package try_code.javatest;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.EventsResultCallback;
import docker.DockerAdapter;
import java.io.IOException;

public class docker {
    public void mount() throws InterruptedException, IOException {
        DockerClient docker = DockerAdapter.newClient();
        InspectContainerResponse inspect = docker.inspectContainerCmd("3e1918db6a23757f7936f9cdbba3a4bf9b92b85873aa0714263154e67d2ab5ed").exec();
        inspect.getMounts().stream()
            .map(mount -> mount.getSource())
            .forEach(x -> System.out.println(x));
        docker.close();
    }
    
    public void events() throws InterruptedException, IOException {
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
    public void docker_attach_stdin() {
        DockerClient docker = DockerClientBuilder.getInstance().build();
        Volume inputFileVol = new Volume("/inputfile");
        Volume dumpVol = new Volume("/dump");
        CreateContainerResponse container = docker.createContainerCmd("b3984985d873")
            .withVolumes(inputFileVol, dumpVol)
            .withBinds(new Bind("/tmp/input2", inputFileVol), new Bind("/tmp/input", dumpVol))
            .withEnv("CKAN_URL=http://192.168.100.16:5000")
            .exec();
        System.out.println("Container ID: " + container.getId());
        docker.startContainerCmd(container.getId())
            .exec();
    }
}
