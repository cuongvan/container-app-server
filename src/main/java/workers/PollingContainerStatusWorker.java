/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.DockerClient;
import common.RunningContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import common.Conf;
import common.DockerClientPool;
import java.util.concurrent.*;

public class PollingContainerStatusWorker {

    private static ConcurrentLinkedQueue<RunningContainer> runningContainers = new ConcurrentLinkedQueue<>();
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    public static void submitNewRunningContainer(RunningContainer container) {
        runningContainers.add(container);
    }

    public static void init() {
        runningContainers = new ConcurrentLinkedQueue<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(4);
        scheduledExecutorService.scheduleWithFixedDelay(PollingContainerStatusWorker::poll,
            Conf.COMMAND_STATUS_CHECK_INTERVAL,
            Conf.COMMAND_STATUS_CHECK_INTERVAL,
            TimeUnit.SECONDS);
    }

    private static void poll() {
        DockerClient dockerClient = DockerClientPool.Instance.getClient();
        try {
            int toCheck = runningContainers.size();
            for (int i = 0; i < toCheck; i++) {
                RunningContainer container = runningContainers.poll();
                InspectContainerResponse inspect
                    = dockerClient.inspectContainerCmd(container.callId).exec();

                // Finished container
                if (inspect.getState().getRunning() == true) {
                    runningContainers.offer(container);
                } else {
                    ContainerFinishWorker.submitFinishContainer(container.appName, container.callId, inspect);
                }
            }
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }
}
