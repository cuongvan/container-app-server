package docker;

import common.ContainerLog;
import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.*;
import java.io.File;
import java.io.IOException;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Singleton;
//import com.github.dockerjava.api.command.BuildImageResultCallback;
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
    
    public String startContainer(String image) throws IOException {
        try (DockerClient docker = newClient()) {
            CreateContainerCmd cmd = docker.createContainerCmd(image);
            CreateContainerResponse container = cmd.exec();
            docker.startContainerCmd(container.getId()).exec();
            return container.getId();
        }
    }

    //TODO environment variables map
    public String createAndStartContainer(
        String imageName,
        Map<String/*env*/, String /*value*/> envs,
        Map<String /*local path*/, String /*container path*/> mounts) throws IOException
    {
        List<Volume> volumes = new ArrayList<>();
        List<Bind> binds = new ArrayList<>();
        mounts.forEach((localPath, containerPath) -> {
            Volume vol = new Volume(containerPath);
            Bind bind = new Bind(localPath, vol, true);
            volumes.add(vol);
            binds.add(bind);
        });
        
        List<String> envList = envs
            .entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + e.getValue()) // VARIABLE=value
            .collect(toList());
            
        
        try (DockerClient docker = newClient()) {
            CreateContainerResponse container = docker
                .createContainerCmd(imageName)
                .withEnv(envList)
                .withVolumes(volumes)
                .withBinds(binds)
                .exec();
                
            docker.startContainerCmd(container.getId()).exec();
            return container.getId();
        }
    }

    public void startServerApp(String imageName, int hostPort, int imagePort) throws IOException {
        try (DockerClient dockerClient = newClient()) {
            ExposedPort exposedPort = ExposedPort.tcp(imagePort);
            Ports portBindings = new Ports();
            portBindings.bind(exposedPort, Ports.Binding.bindPort(hostPort));

            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
//                .withLabels(labelMap)
                .withExposedPorts(exposedPort)
                .withPortBindings(portBindings)
                .exec();
            dockerClient.startContainerCmd(container.getId()).exec();
        }
    }

    public String buildImage(String path, String imageName) {
        DockerClient docker = newClient();
        try {
            BuildImageResultCallback callback = docker
                .buildImageCmd(new File(path))
                //.withTag(imageName)//TODO: tag
//                .withTag(imageName)//TODO: tag
                .exec(new BuildImageResultCallback() {
                    // DEBUG
                    @Override
                    public void onNext(BuildResponseItem item) {
                        if (item.getStream() != null) {
                            System.out.println(item.getStream().trim());
                        }
                        super.onNext(item);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("********** " + throwable);
                        throwable.printStackTrace();
                        super.onError(throwable);
                    }
                });
            
            return callback.awaitImageId();
        } finally {
            close(docker);
        }
    }
    
    public Single<String> buildImage2(String path) {
        return Single.<String>create(emitter -> {
            DockerClient docker = newClient();
            try {
                BuildImageResultCallback callback = docker
                    .buildImageCmd(new File(path))
                    .exec(new BuildImageResultCallback() {
                        // DEBUG
                        @Override
                        public void onNext(BuildResponseItem item) {
                            super.onNext(item);
                            if (item.getStream() != null) {
                                System.out.println(item.getStream().trim());
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            //System.out.println("***** error " + throwable);
                            //emitter.onError(throwable);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            try {
                                awaitImageId();
                            } catch (Exception ex) {
                                System.out.println(">>>>>>>>>>>>> " + ex);
                            }
                            emitter.onSuccess(awaitImageId());
                        }
                    });
            } catch (Exception ex) {
                System.out.println(">>>>>>>>>>>>> " + ex);
            } finally {
                close(docker);
            }
        });
    }
    
    public InspectContainerResponse inspectContainer(String containerId) {
        DockerClient docker = DockerAdapter.newClient();
        try {
            return docker.inspectContainerCmd(containerId).exec();
        } finally {
            close(docker);
        }
    }
    
    public Observable<Event> watchFinishedContainers() {
        return watchEvents("die") // die = complete
            ;
    }
    
    public Observable<Event> watchEvents(String... filters) {
        return Observable
            .<Event>create(emitter -> {
                try (DockerClient docker = DockerAdapter.newClient()) {
                    docker
                        .eventsCmd()
                        .withEventFilter(filters)
                        .exec(new EventsResultCallback() {
                            @Override
                            public void onNext(Event item) {
                                emitter.onNext(item);
                            }
                        }).awaitCompletion();
                } catch (InterruptedException interrupt) {
                    // when stop server
                    emitter.onComplete();
                } catch (Exception ex) {
                    emitter.onError(ex);
                }
                
            });
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

                    }
                }).awaitCompletion();
        } finally {
            close(docker);
        }
    }
}
