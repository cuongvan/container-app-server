/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package docker;

import common.RunningContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import static docker.DockerUtils.COMMAND_STATUS_CHECK_INTERVAL_SEC;
import static docker.DockerUtils.dockerClient;
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
                COMMAND_STATUS_CHECK_INTERVAL_SEC,
                COMMAND_STATUS_CHECK_INTERVAL_SEC,
                TimeUnit.SECONDS);
    }

    private static void poll() {
        int toCheck = runningContainers.size();
        for (int i = 0; i < toCheck; i++) {
            RunningContainer container = runningContainers.poll();
            InspectContainerResponse inspect
                    = dockerClient.inspectContainerCmd(container.callId).exec();

            // Finished container
            if (inspect.getState().getRunning() == true) {
                runningContainers.offer(container);
            } else {
                ContainerFinishHandler.submitFinishContainer(container.appName, container.callId, inspect);
            }
        }
    }
}
