package check3rdparties.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import static docker.DockerAdapter.newClient;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class StartContainerNoLabels {
    @Test
    public void test() {
        DockerClient docker = newClient();
        Map<String, String> labels = new HashMap<String, String>() {{
            put("my.name", "Cuong");
        }};
        
        CreateContainerResponse create = docker.createContainerCmd("319e9f006cce")
            //.withLabels(labels)
            .exec();
        docker.startContainerCmd(create.getId())
            .exec();
        InspectContainerResponse inspect = docker.inspectContainerCmd(create.getId()).exec();
        System.out.println(inspect.getConfig().getLabels());
        assertNotNull(inspect.getConfig().getLabels());
    }
}
