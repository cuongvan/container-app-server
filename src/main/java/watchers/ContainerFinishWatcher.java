/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.Constants;
import docker.DockerAdapter;
import externalapi.appcall.CallDAO;
import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.CallStatus;
import static io.netty.util.CharsetUtil.UTF_8;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ContainerFinishWatcher {
    
    private final Logger LOG = LoggerFactory.getLogger(ContainerFinishWatcher.class);
    
    private DockerAdapter docker;
    private CallDAO appCallDAO;
    private ExecutorService executor;
    
    @Inject
    public ContainerFinishWatcher(DockerAdapter dockerAdapter, CallDAO appCallDAO) {
        this.docker = dockerAdapter;
        this.appCallDAO = appCallDAO;
        executor = Executors.newSingleThreadExecutor();
    }
    
    public void runForever() {
        executor.submit(() -> loopTask());
    }
    
    public void stop() {
        LOG.info("Stop now");
        executor.shutdown();
    }
    
    private void loopTask() {
        while (true) {
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
    }
    
    private void handleFinishedContainer(String containerId) {
        InspectContainerResponse inspect = docker.inspectContainer(containerId);
        Map<String, String> labels = inspect.getConfig().getLabels();
        if (!labels.containsKey(Constants.CONTAINER_ID_LABEL_KEY)) {
            // container not belong to ckan
            return;
        }
        
        String callId = getAppCallId(inspect);
        try {
            AppCallResult r = gatherCallResultInfo(inspect);
            File outputDir = copyContainerOutput(containerId, callId);
            
            CallOutput callOutput = getCallOutput(outputDir);
            System.out.println(">>> " + callOutput);
            
            appCallDAO.updateFinishedAppCall(callId, r);
            LOG.info("App call {} finished: {}", callId, r);
        } catch (IOException ex) {
            LOG.info("Failed copy output data out of container, callID = {}", callId);
            ex.printStackTrace();
        } catch (SQLException ex) {
            LOG.info("Failed to insert result to DB, callID = {}", callId);
        } finally {
            docker.deleteContainer(containerId);
        }
    }

    private AppCallResult gatherCallResultInfo(InspectContainerResponse inspect) {
        int exitCode = inspect.getState().getExitCode();
        CallStatus status = (exitCode == 0) ? CallStatus.SUCCESS : CallStatus.FAILED;
        
        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();
        
        String output = docker.getContainerLog(inspect.getId());
        return new AppCallResult(status, duration, output, null);
    }

    private String getAppCallId(InspectContainerResponse inspect) {
        Map<String, String> labels = inspect.getConfig().getLabels();
        String callId = labels.get(Constants.CONTAINER_ID_LABEL_KEY);
        return callId;
    }
    
    private File copyContainerOutput(String containerId, String callId) throws IOException {
        File dest = new File(Constants.APP_OUTPUT_FILES_DIR, callId);
        docker.copyDirectory(containerId, Constants.CONTAINER_OUTPUT_FILES_DIR, dest);
        return dest;
    }
    
    private CallOutput getCallOutput(File outputDir) throws IOException {
        File metadataFile = new File(outputDir, Constants.OUTPUT_FILE_RELATIVE_PATH);
        //System.out.println(FileUtils.readFileToString(metadataFile, UTF_8));
        
        FileInputStream in = new FileInputStream(metadataFile);
        return OBJECT_READER.readValue(in);
    }
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectReader OBJECT_READER = OBJECT_MAPPER.readerFor(CallOutput.class);
}
