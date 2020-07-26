/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.Config;
import httpserver.endpoints.ContainerPaths;
import docker.DockerAdapter;
import externalapi.appcall.CallDAO;
import externalapi.appcall.models.CallOutputEntry;
import externalapi.appcall.models.CallResult;
import externalapi.appcall.models.CallStatus;
import externalapi.appcall.models.OutputFieldType;
import externalapi.appinfo.AppDAO;
import externalapi.appinfo.models.AppDetail;
import externalapi.appinfo.models.AppStatus;
import httpserver.endpoints.ExecuteApp;
import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ContainerFinishWatcher {

    private final Logger LOG = LoggerFactory.getLogger(ContainerFinishWatcher.class);

    @Inject
    private DockerAdapter docker;

    @Inject
    private AppDAO appDAO;
    @Inject
    private CallDAO callDAO;
    @Inject
    private Config config;

//    @Inject private RabbitMQNotifier notifier;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void runForever() {
        LOG.info("started");
        executor.submit(() -> loopTask());
    }

    public void stop() {
        LOG.info("Stop now");
        executor.shutdown();
    }

    private void loopTask() {
        while (true)
            try {
                docker.watchContainersFinish(containerId -> handleFinishedContainer(containerId));
            } catch (InterruptedException ex) {
                // stop server
                break;
            } catch (Exception ex) {
                LOG.warn("Exception: {}", ex);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex1) {
                    ex1.printStackTrace();
                }
            }
    }

    private void handleFinishedContainer(String containerId) {
        InspectContainerResponse inspect = docker.inspectContainer(containerId);
        Map<String, String> labels = inspect.getConfig().getLabels();
        if (!labels.containsKey(ExecuteApp.CALL_ID_LABEL))
            // container not belong to ckan
            return;

        CallResult callResult = gatherCallResultInfo(inspect);
        String callId = getCallId(inspect);
        String appId = getAppId(inspect);
        List<CallOutputEntry> callOutputs = Collections.emptyList();
        try {
            AppDetail appDetail = appDAO.getById(appId);

            // app deleted?
            if (appDetail == null)
                return;

            if (appDetail.status == AppStatus.DEBUG)
                callResult.logs = docker.getContainerLog(containerId);

            try {
                File outputDir = copyContainerOutput(containerId, callId);
                
                // populate with file params
                callOutputs = Arrays.stream(outputDir.listFiles())
                    .map(this::outputParamOf)
                    .filter(x -> x != null)
                    .collect(toList());
            } catch (DockerAdapter.DockerOutputPathNotFound ex) {
                LOG.info("Cannot initialize /outputs in app container. Client code failed before initialization. Check docker logs!");
                LOG.info("Docker logs: " + docker.getContainerLog(containerId));
                ex.printStackTrace();
            } catch (IOException ex) {
                LOG.info("Failed copy output data out of container, callID = {}", callId);
                String containerLog = docker.getContainerLog(containerId);
                System.out.println("Failed container logs: ");
                System.out.println(containerLog);
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            LOG.info("Failed to retrive app detail of appId = {}", appId);
            ex.printStackTrace();
        } finally {
            try {
                callDAO.updateCallResult(callId, callResult, callOutputs);
                LOG.info("App call {} finished: {}", callId, callResult);
                //docker.deleteContainer(containerId);
                //notifier.notifyExecuteDone(callId);
            } catch (SQLException ex) {
                LOG.info("Failed to insert result to DB, callID = {}", callId);
                ex.printStackTrace();
            } catch (Exception ex) {
                LOG.info("Failed to notify rabbitmq queue");
                ex.printStackTrace();
            }
        }
    }

    private String getCallId(InspectContainerResponse inspect) {
        Map<String, String> labels = inspect.getConfig().getLabels();
        String callId = labels.get(ExecuteApp.CALL_ID_LABEL);
        return callId;
    }

    private String getAppId(InspectContainerResponse inspect) {
        Map<String, String> labels = inspect.getConfig().getLabels();
        String appId = labels.get(ExecuteApp.APP_ID_LABEL);
        return appId;
    }

    private File copyContainerOutput(String containerId, String callId) throws IOException, DockerAdapter.DockerOutputPathNotFound {
        File dest = new File(config.appOutputFilesDir, callId);
        //dest.mkdir(); // do not create!
        try {
            docker.copyDirectory(containerId, ContainerPaths.OUTPUT_FILES_DIR, dest);
        } catch (DockerAdapter.DockerOutputPathNotFound ex) {
        }
        return dest;
    }

    private CallResult gatherCallResultInfo(InspectContainerResponse inspect) {
        CallStatus status;
        boolean oom = inspect.getState().getOOMKilled();
        if (oom) {
            status = CallStatus.OUT_OF_MEMORY;
        } else {
            int exitCode = inspect.getState().getExitCode();
            if (exitCode == 0)
                status = CallStatus.SUCCESS;
            else if (exitCode == 124)
                status = CallStatus.TIMEOUT;
            else 
                status = CallStatus.FAILED;
        }

        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();

        return new CallResult(status, duration);
    }

    private CallOutputEntry outputParamOf(File outputFile) {
        String param = FilenameUtils.getBaseName(outputFile.getName());
        String path = outputFile.getAbsolutePath();
        String extension = FilenameUtils.getExtension(outputFile.getName()).toLowerCase();

        try {
            switch (extension) {
                case "txt":
                case "text": return new CallOutputEntry(OutputFieldType.TEXT, param, readFile(outputFile));
                case "boolean": return new CallOutputEntry(OutputFieldType.BOOLEAN, param, readFile(outputFile)); 
                case "double": return new CallOutputEntry(OutputFieldType.DOUBLE, param, readFile(outputFile)); 
                case "integer": return new CallOutputEntry(OutputFieldType.INTEGER, param, readFile(outputFile)); 
                case "list": return new CallOutputEntry(OutputFieldType.LIST, param, readFile(outputFile)); 
                
                // files
                case "csv": return new CallOutputEntry(OutputFieldType.CSV, param, path);
                case "jpg":
                case "jpeg": return new CallOutputEntry(OutputFieldType.JPG, param, path);
                case "png": return new CallOutputEntry(OutputFieldType.PNG, param, path);
                case "json": return new CallOutputEntry(OutputFieldType.JSON, param, path);
                case "xslx": return new CallOutputEntry(OutputFieldType.XSLX, param, path);
                
                // unknown
                default: return new CallOutputEntry(OutputFieldType.FILE, param, path);
            }
        } catch (IOException ex) {
            return null;
        }
        
        
    }

    private String readFile(File f) throws IOException {
        return FileUtils.readFileToString(f, UTF_8);
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}
