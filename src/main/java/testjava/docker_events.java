/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjava;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.EventsResultCallback;
import java.io.IOException;

/**
 *
 * @author cuong
 */
public class docker_events {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("started");
        DockerClient client = DockerClientBuilder.getInstance().build();
        client.eventsCmd()
//            .withEventFilter("die")
            .exec(new EventsResultCallback(){
            @Override
            public void onNext(Event item) {
                System.out.println(item);
                super.onNext(item); //To change body of generated methods, choose Tools | Templates.
            }
        }).awaitCompletion().close();
    }
}
