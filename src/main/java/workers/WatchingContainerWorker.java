/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.DockerClient;
import common.RunningContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
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
    private final Set<String> runningContainersNames;
    private final Executor executor;


    public WatchingContainerWorker() {
        executor = Executors.newSingleThreadExecutor();
        runningContainersNames = ConcurrentHashMap.newKeySet();
        
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
    
    public void submitNewRunningContainer(String callId) {
        runningContainersNames.add(callId);
    }
    
    private void watchForever() throws Exception {
        final String STOP_EVENT = "die";
        DockerClientPool.Instance.useClient(client -> {
            client.eventsCmd()
                .withEventFilter(STOP_EVENT) // container complete
                .withLabelFilter(Conf.Inst.CKAN_APP_CONTAINER_LABEL)
                .exec(new EventsResultCallback() {
                    @Override
                    public void onNext(Event item) {
                        String cname = item.getActor().getAttributes().get("name");
                        if (runningContainersNames.remove(cname)) {
                            ContainerFinishWorker.submitFinishContainer(cname);
                            logger.info("Container finised: {}", cname);
                        }
                    }
                }).awaitCompletion();
        });
    }
}
