package httpserver.endpoints;

import common.Config;
import helpers.SystemStats;
import java.util.concurrent.LinkedBlockingDeque;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CheckAndStartDockerContainerThread extends Thread {
    @Inject private Config config;
    @Inject private SystemStats systemStats;
    private final LinkedBlockingDeque<Runnable> tasks = new LinkedBlockingDeque<>();
    private final Logger logger = LoggerFactory.getLogger(CheckAndStartDockerContainerThread.class);
    
    public void submitTask(Runnable task) {
        tasks.addLast(task);
    }

    @Override
    public void run() {
        logger.info("thread started");
        for(;;) {
            try {
                Runnable task = tasks.takeFirst();
                
                double freeMemMB = systemStats.getFreePhysicalMemoryMB();
                    
                if (freeMemMB >= config.minFreeRamExecMB) {
                    task.run();
                } else {
                    logger.info("Memory is low: " + freeMemMB + ". Resubmit a Docker run task");
                    tasks.putFirst(task); // put back task
                    Thread.sleep(5000);   // wait for other tasks to complete then run this
                }
            } catch (InterruptedException ignore) {
                ignore.printStackTrace();
            }
        }
    }
}