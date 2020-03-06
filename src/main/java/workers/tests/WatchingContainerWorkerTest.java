/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers.tests;

import docker.DockerAdapter;
import externalapi.AppCallDAO;
import java.io.IOException;
import workers.WatchingContainerWorker;

/**
 *
 * @author cuong
 */
public class WatchingContainerWorkerTest {
    static DockerAdapter docker = new DockerAdapter();
    static AppCallDAO dao = new EmptyDAO();
    static WatchingContainerWorker worker = new WatchingContainerWorker(docker, dao);

    public static void watchContainersFinish() throws InterruptedException, IOException {
        worker.runForever().subscribe();
        for (int i = 0; i < 10; i++) {
            String id = docker.startContainer("aaa");
            System.out.println("Created: " + id);
        }
        Thread.sleep(1000);
    }
    
    public static void main(String[] args) throws Exception {
        watchContainersFinish();
    }
}
