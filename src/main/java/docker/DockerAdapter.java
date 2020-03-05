package docker;

import common.ContainerLog;
import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class DockerAdapter {
    
    public static DockerClient newClient() {
        return DockerClientBuilder.getInstance().build();
    }
    
    public ContainerLog getContainerLog(String containerId) throws IOException {
        try (DockerClient dockerClient = newClient()) {
            StringBuilder stdoutBuilder = new StringBuilder();
            StringBuilder stderrBuilder = new StringBuilder();
            dockerClient.logContainerCmd(containerId)
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
        }
    }

    public void deleteContainer(String containerId) throws IOException {
        try (DockerClient dockerClient = newClient()) {
            dockerClient.removeContainerCmd(containerId).exec();
        }
    }

    /**
     * @return container's id
     */
    public String startBatchApp(
        String imageName,
        String jsonInputPath,
        String binaryInputPath
    ) throws IOException {
        try (DockerClient docker = newClient()) {
            List<Volume> volumes = new ArrayList<>();
            List<Bind> binds = new ArrayList<>();
            if (jsonInputPath != null) {
                jsonInputPath = Paths.get(jsonInputPath).toAbsolutePath().toString();
                Volume volJson = new Volume("/inputJson");
                volumes.add(volJson);
                Bind bind = new Bind(jsonInputPath, volJson);
                binds.add(bind);
            }
            if (binaryInputPath != null) {
                binaryInputPath = Paths.get(binaryInputPath).toAbsolutePath().toString();
                Volume volBin = new Volume("/inputBinary");
                volumes.add(volBin);
                Bind bind = new Bind(binaryInputPath, volBin);
                binds.add(bind);
            }
            
            
            CreateContainerCmd cmd = docker
                .createContainerCmd(imageName);
//                .withLabels(labelMap);
                
            cmd = cmd
                .withVolumes(volumes)
                .withBinds(binds);
                
//                .withVolumes(volJson)
//                .withBinds(new Bind(jsonInputPath, volJson))
//                .withBinds(new Bind(binaryInputPath, volBin))
//                .exec();
                
            CreateContainerResponse container = cmd.exec();
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

    public void buildImage(String path, String imageName) throws IOException {
        try (DockerClient dockerClient = newClient()) {
            dockerClient.buildImageCmd()
                .withDockerfile(new File(path, "Dockerfile"))
                .withTag(imageName)
                .exec(new BuildImageResultCallback() {
                    // DEBUG
                    @Override
                    public void onNext(BuildResponseItem item) {
                        if (item.getStream() != null) {
                            System.out.println(item.getStream().trim());
                        }
                    }
                })
                .awaitCompletion();
//            appNames.add(imageName);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void pullImage(String image) throws IOException {
        try (DockerClient dockerClient = newClient()) {
//            PullImageCmd pull = dockerClient
//                .pullImageCmd(image)
//                .exec(new PullImageResultCallback() {
//                    @Override
//                    public void onNext(PullResponseItem item) {
//                        super.onNext(item); //To change body of generated methods, choose Tools | Templates.
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        super.onComplete(); //To change body of generated methods, choose Tools | Templates.
//                    }
//
//                });
        }
    }
    
    public InspectContainerResponse inspectContainer(String containerId) throws IOException {
        try (DockerClient docker = DockerAdapter.newClient()) {
            return docker.inspectContainerCmd(containerId).exec();
        }
    }
}
