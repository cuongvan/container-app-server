/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.command.EventsResultCallback;
import common.Conf;
import common.DockerClientPool;
import java.util.Set;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchingContainerWorker {
    private static Logger logger = LoggerFactory.getLogger(WatchingContainerWorker.class);
    public static final WatchingContainerWorker Singleton = new WatchingContainerWorker();

    // TODO remove RunningContainer queue?
    private final Executor executor;

    public WatchingContainerWorker() {
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
        DockerClientPool.Instance.useClient(client -> {
            client.eventsCmd()
                .withEventFilter("die") // container complete
                .exec(new EventsResultCallback() {
                    @Override
                    public void onNext(Event item) {
                        // String cname = item.getActor().getAttributes().get("name");
                        ContainerFinishWorker.submitFinishContainer(item.getId());
                        logger.info("Container finished: {}", item.getId());
                    }
                }).awaitCompletion();
        });
    }
}
