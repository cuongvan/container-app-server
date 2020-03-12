package check3rdparties.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import static docker.DockerAdapter.newClient;
import org.junit.Test;

public class RunContainerFromImageId {

    @Test
    public void test() {
        DockerClient docker = newClient();
        CreateContainerCmd cmd = docker.createContainerCmd("319e9f006cce");
        CreateContainerResponse container = cmd.exec();
        docker.startContainerCmd(container.getId()).exec();
    }
}
