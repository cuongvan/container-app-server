package docker;

import common.ContainerLog;
import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.*;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import javax.inject.Singleton;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import common.AppConfig;
import static java.util.stream.Collectors.toList;

@Singleton
public class DockerAdapter {
    
    public static DockerClient newClient() {
        return DockerClientBuilder.getInstance().build();
    }
    
    private static void close(DockerClient client) {
        try {
            client.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public ContainerLog getContainerLog(String containerId) {
        DockerClient docker = newClient();
        try {
            StringBuilder stdoutBuilder = new StringBuilder();
            StringBuilder stderrBuilder = new StringBuilder();
            docker.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        String s = new String(item.getPayload());
                        if (item.getStreamType() == StreamType.STDOUT) {
                            stdoutBuilder.append(s);
                        } else if (item.getStreamType() == StreamType.STDERR) {
                            stderrBuilder.append(s);
                        }
                    }
                }).awaitCompletion();
            return new ContainerLog(stdoutBuilder.toString(), stderrBuilder.toString());
        } catch (InterruptedException ignore) {
            throw new IllegalStateException("Should not happend");
        } finally {
            close(docker);
        }
    }

    public void deleteContainer(String containerId) {
        DockerClient docker = newClient();
        try {
            docker.removeContainerCmd(containerId).exec();
        } finally {
            close(docker);
        }
    }
    
    public String startContainer(String image) {
        DockerClient docker = newClient();
        try {
            CreateContainerCmd cmd = docker.createContainerCmd(image);
            CreateContainerResponse container = cmd.exec();
            docker.startContainerCmd(container.getId()).exec();
            return container.getId();
        } finally {
            close(docker);
        }
    }

    //TODO environment variables map
    public String createAndStartContainer(
        String imageName,
        Map<String/*env*/, String /*value*/> envs,
        Map<String /*local path*/, String /*container path*/> mounts)
    {
        List<Volume> volumes = new ArrayList<>();
        List<Bind> binds = new ArrayList<>();
        mounts.forEach((localPath, containerPath) -> {
            Volume vol = new Volume(containerPath);
            Bind bind = new Bind(localPath, vol);
            volumes.add(vol);
            binds.add(bind);
        });
        
        List<String> envList = envs
            .entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + e.getValue()) // VARIABLE=value
            .collect(toList());
        
        System.out.println(mounts);
        
        DockerClient docker = newClient();
        try {
            CreateContainerResponse container = docker
                .createContainerCmd(imageName)
                .withEnv(envList)
                .withVolumes(volumes)
                //@SuppressWarnings("deprecation")
                .withBinds(binds)
                .exec();
                
            docker.startContainerCmd(container.getId()).exec();
            return container.getId();
        } finally {
            close(docker);
        }
    }

    public void startServerApp(String imageName, int hostPort, int imagePort) {
        DockerClient docker = newClient();
        try {
            ExposedPort exposedPort = ExposedPort.tcp(imagePort);
            Ports portBindings = new Ports();
            portBindings.bind(exposedPort, Ports.Binding.bindPort(hostPort));

            CreateContainerResponse container = docker.createContainerCmd(imageName)
                //.withLabels(labelMap)
                .withExposedPorts(exposedPort)
                //@SuppressWarnings("deprecation")
                .withPortBindings(portBindings)
                .exec();
            docker.startContainerCmd(container.getId()).exec();
        } finally {
            close(docker);
        }
    }

    public String buildImage(String path, String imageName) {
        DockerClient docker = newClient();
        try {
            BuildImageResultCallback callback = docker
                .buildImageCmd(new File(path))
                //.withTag(imageName)//TODO: tag
                .exec(new BuildImageResultCallback() {
                    // DEBUG
                    @Override
                    public void onNext(BuildResponseItem item) {
//                        if (item.getStream() != null) {
//                            System.out.println(item.getStream().trim());
//                        }
                        super.onNext(item);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        super.onError(throwable);
                    }
                });
            
            return callback.awaitImageId();
        } finally {
            close(docker);
        }
    }
    
    public InspectContainerResponse inspectContainer(String containerId) {
        DockerClient docker = DockerAdapter.newClient();
        try {
            return docker.inspectContainerCmd(containerId).exec();
        } finally {
            close(docker);
        }
    }
    
    public void watchContainersFinish(Consumer<String/*containerId*/> handler) throws InterruptedException {
        DockerClient docker = DockerAdapter.newClient();
        try {
            docker
                .eventsCmd()
                .withEventFilter("die")
                .exec(new EventsResultCallback() {
                    @Override
                    public void onNext(Event item) {
                        handler.accept(item.getId());
                    }
                }).awaitCompletion();
        } finally {
            close(docker);
        }
    }
}
