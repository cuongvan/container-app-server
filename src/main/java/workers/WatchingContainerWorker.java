/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Event;
import common.ContainerLog;
import docker.DockerAdapter;
import externalapi.AppCallDAO;
import externalapi.models.AppCallResult;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;

@Singleton
public class WatchingContainerWorker {
    private DockerAdapter docker;
    private AppCallDAO appCallDAO;
    
    @Inject
    public WatchingContainerWorker(DockerAdapter dockerAdapter, AppCallDAO appCallDAO) {
        this.docker = dockerAdapter;
        this.appCallDAO = appCallDAO;
    }
    
    public Completable runForever() {
        return docker.watchFinishedContainers()
            .map(Event::getId) // container id
            .subscribeOn(Schedulers.io()) // runs in a dedicated thread
            .observeOn(Schedulers.io())
            .retry()  // retry when exception occurs
            .flatMapCompletable(this::handleFinishedContainer)
            ;
    }
    
    private Completable handleFinishedContainer(String containerId) {
        return Single
            .fromCallable(() -> docker.inspectContainer(containerId))
            .doOnSuccess(this::deleteAllMounted)
            .flatMap(inspect -> Single
                .just(gatherCallResultInfo(inspect, containerId))
                .doOnSuccess(r -> System.out.println("Done: " + r))
                .doOnSuccess(r -> docker.deleteContainer(containerId))
                .doOnSuccess(r -> System.out.println("Deleted"))
                .doOnSuccess(appCallDAO::setCallResult)
                .doOnSuccess(r -> System.out.println("Save to DB"))
            )
            // NOTIFY to CKAN
            .flatMapCompletable(x -> Completable.complete())
            ;
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

    private AppCallResult gatherCallResultInfo(InspectContainerResponse inspect, String containerId) throws IOException {
        int exitCode = inspect.getState().getExitCode();
        boolean success = (exitCode == 0);
        
        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();
        
        ContainerLog log = docker.getContainerLog(inspect.getId());
        return new AppCallResult(containerId, true, duration, log.stdout, log.stderr);
    }
}
