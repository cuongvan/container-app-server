/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import common.ContainerLog;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.AppCallResult;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WatchingContainerWorker {
    
    private final Logger LOG = LoggerFactory.getLogger(WatchingContainerWorker.class);
    
    private DockerAdapter docker;
    private AppCallDAO appCallDAO;
    private ExecutorService executor;
    
    @Inject
    public WatchingContainerWorker(DockerAdapter dockerAdapter, AppCallDAO appCallDAO) {
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
                // continue
            }
        }
    }
    
    private void handleFinishedContainer(String containerId) {
        try {
            InspectContainerResponse inspect = docker.inspectContainer(containerId);
            deleteAllMounted(inspect);
            AppCallResult r = gatherCallResultInfo(inspect, containerId);
            appCallDAO.updateFinishedAppCall(r);
        } finally {
            docker.deleteContainer(containerId);
        }
    }
    private void deleteAllMounted(InspectContainerResponse inspect) {
        inspect.getMounts()
            .stream()
            .map(mount -> mount.getSource())
            .map(path -> new File(path))
            .forEach(inputFile -> {
                try {
                    FileUtils.forceDelete(inputFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
    }

    private AppCallResult gatherCallResultInfo(InspectContainerResponse inspect, String containerId) {
        int exitCode = inspect.getState().getExitCode();
        boolean success = (exitCode == 0);
        
        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();
        
        ContainerLog log = docker.getContainerLog(inspect.getId());
        return new AppCallResult(containerId, success, duration, log.stdout, log.stderr);
    }
    
    public void stop() {
        LOG.info("Stop now");
        executor.shutdown();
    }
}
