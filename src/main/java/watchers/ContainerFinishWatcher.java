/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import common.Constants;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.CallStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ContainerFinishWatcher {
    
    private final Logger LOG = LoggerFactory.getLogger(ContainerFinishWatcher.class);
    
    private DockerAdapter docker;
    private AppCallDAO appCallDAO;
    private ExecutorService executor;
    
    @Inject
    public ContainerFinishWatcher(DockerAdapter dockerAdapter, AppCallDAO appCallDAO) {
        this.docker = dockerAdapter;
        this.appCallDAO = appCallDAO;
        executor = Executors.newSingleThreadExecutor();
    }
    
    public void runForever() {
        executor.submit(() -> loopTask());
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
        
        try {
            AppCallResult r = gatherCallResultInfo(inspect);
            appCallDAO.updateFinishedAppCall(r);
            LOG.info("App call {} finished: {}", r.getAppCallId(), r);
        } finally {
            docker.deleteContainer(containerId);
        }
    }

    private AppCallResult gatherCallResultInfo(InspectContainerResponse inspect) {
        Map<String, String> labels = inspect.getConfig().getLabels();
        String callId = labels.get(Constants.CONTAINER_ID_LABEL_KEY);
        int exitCode = inspect.getState().getExitCode();
        CallStatus status = (exitCode == 0) ? CallStatus.SUCCESS : CallStatus.FAILED;
        
        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();
        
        String output = docker.getContainerLog(inspect.getId());
        return new AppCallResult(callId, status, duration, output);
    }
    
    public void stop() {
        LOG.info("Stop now");
        executor.shutdown();
    }
}
