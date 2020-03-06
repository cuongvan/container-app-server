package test_lib.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.command.EventsResultCallback;
import docker.DockerAdapter;
import java.io.IOException;

public class event {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Start");
        try (DockerClient docker = DockerAdapter.newClient()) {
            docker.eventsCmd()
                .withEventFilter("die") // container complete
                .exec(new EventsResultCallback() {
                    @Override
                    public void onNext(Event item) {
                        // String cname = item.getActor().getAttributes().get("name");
                        System.out.println(item.getId());
                    }
                }).awaitCompletion();
        }
        
        System.out.println("Done here");
    }
}
