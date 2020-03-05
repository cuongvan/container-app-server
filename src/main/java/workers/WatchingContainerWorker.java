/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.command.EventsResultCallback;
import docker.DockerAdapter;
import java.util.concurrent.*;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchingContainerWorker {
    private static Logger logger = LoggerFactory.getLogger(WatchingContainerWorker.class);
    
    // TODO remove RunningContainer queue?
    private ContainerFinishWorker containerFinishWorker;
    private final Executor executor;

    @Inject
    public WatchingContainerWorker(ContainerFinishWorker containerFinishWorker) {
        this.containerFinishWorker = containerFinishWorker;
        executor = Executors.newSingleThreadExecutor();
    }
    
    public void startWatching() {
        executor.execute(() -> {
            while (true) {
                try {
                    watchForever();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.info("Retrying...");
                }
            }
        });
    }
    
    private void watchForever() throws Exception {
        try (DockerClient docker = DockerAdapter.newClient()) {
            docker.eventsCmd()
                .withEventFilter("die") // container complete
                .exec(new EventsResultCallback() {
                    @Override
                    public void onNext(Event item) {
                        // String cname = item.getActor().getAttributes().get("name");
                        containerFinishWorker.submitFinishContainer(item.getId());
                        logger.info("Container finished: {}", item.getId());
                    }
                }).awaitCompletion();
        }
    }
}
