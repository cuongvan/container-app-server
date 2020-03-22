/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.Constants;
import docker.DockerAdapter;
import externalapi.appcall.CallDAO;
import externalapi.appcall.models.CallOutputEntry;
import externalapi.appcall.models.CallResult;
import externalapi.appcall.models.CallStatus;
import externalapi.appcall.models.OutputFieldType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
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
        
        List<CallOutputEntry> callOutputs;
        try {
            try {
                File outputDir = copyContainerOutput(containerId, callId);
                callOutputs = getNormalOutputFields(outputDir);

                getFileOutputs(outputDir)
                    .map(outputFile -> 
                        new CallOutputEntry(OutputFieldType.FILE, outputFile.getName(), outputFile.getAbsolutePath()))
                    .forEach(callOutputs::add);
                
            } catch (DockerAdapter.DockerOutputPathNotFound ex) {
                LOG.info("Cannot initialize /outputs in app container. Client code failed before initialization. Check docker logs!");
                callOutputs = Collections.emptyList();
                ex.printStackTrace();
            }
            
            CallResult r = gatherCallResultInfo(inspect);
            appCallDAO.updateCallResult(callId, r, callOutputs);
            LOG.info("App call {} finished: {}", callId, r);
        } catch (IOException ex) {
            LOG.info("Failed copy output data out of container, callID = {}", callId);
            String containerLog = docker.getContainerLog(containerId);
            System.out.println("Failed container logs: ");
            System.out.println(containerLog);
            ex.printStackTrace();
        } catch (SQLException ex) {
            LOG.info("Failed to insert result to DB, callID = {}", callId);
            ex.printStackTrace();
        } finally {
            docker.deleteContainer(containerId);
        }
    }

    private String getAppCallId(InspectContainerResponse inspect) {
        Map<String, String> labels = inspect.getConfig().getLabels();
        String callId = labels.get(Constants.CONTAINER_ID_LABEL_KEY);
        return callId;
    }
    
    private File copyContainerOutput(String containerId, String callId) throws IOException, DockerAdapter.DockerOutputPathNotFound {
        File dest = new File(Constants.APP_OUTPUT_FILES_DIR, callId);
        docker.copyDirectory(containerId, Constants.CONTAINER_OUTPUT_FILES_DIR, dest);
        return dest;
    }
    
    private List<CallOutputEntry> getNormalOutputFields(File outputDir) throws IOException {
        File metadataFile = new File(outputDir, Constants.CONTAINER_OUTPUT_FILE_RELATIVE_PATH);
        FileInputStream in = new FileInputStream(metadataFile);
        return OBJECT_READER.readValue(in);
    }
    
    
    private Stream<File> getFileOutputs(File outputDir) {
        File binaryFilesDir = new File(outputDir, Constants.CONTAINER_OUTPUT_BINARY_FILES_RELATIVE_PATH);
        
        return Arrays.stream(binaryFilesDir.listFiles())
            .map(File::toPath)
            .map(Path::toAbsolutePath)
            .map(Path::normalize)
            .map(Path::toFile);
    }
    
    private CallResult gatherCallResultInfo(InspectContainerResponse inspect) {
        int exitCode = inspect.getState().getExitCode();
        CallStatus status = (exitCode == 0) ? CallStatus.SUCCESS : CallStatus.FAILED;
        
        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();
        
        return new CallResult(status, duration);
    }
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectReader OBJECT_READER = OBJECT_MAPPER.readerFor(new TypeReference<List<CallOutputEntry>>() {});
}
