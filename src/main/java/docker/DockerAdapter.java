package docker;

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
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import common.Config;
import helpers.MyFileUtils;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;

@Singleton
public class DockerAdapter {
    
    @Inject private Config config;
    
    public static DockerClient newClient() {
        DockerClientConfig custom = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("tcp://docker.somewhere.tld:2375")
            .withDockerTlsVerify(false)
            .build();

        return DockerClientBuilder.getInstance().build();
    }
    
    private static void close(DockerClient client) {
        try {
            client.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getContainerLog(String containerId) {
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
            
            stdoutBuilder.append(stderrBuilder);
            return stdoutBuilder.toString();
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

    public String createAndStartContainer(
        String imageName,
        Map<String/*env*/, String /*value*/> envs,
        Map<String /*local path*/, String /*container path*/> mounts,
        Map<String, String> labels
    ) {
        List<Volume> volumes = new ArrayList<>();
        List<Bind> binds = new ArrayList<>();
        mounts.forEach((localPath, containerPath) -> {
            Volume vol = new Volume(containerPath);
            Bind bind = new Bind(localPath, vol, AccessMode.ro);
            volumes.add(vol);
            binds.add(bind);
        });
        
        List<String> envList = envs
            .entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + e.getValue()) // VARIABLE=value
            .collect(toList());
        
        DockerClient docker = newClient();
        try {
            long maxRamBytes = config.maxContainerRamMB * 1024 * 1024;
            CreateContainerResponse container = docker
                .createContainerCmd(imageName)
                .withEnv(envList)
                .withVolumes(volumes)
                .withLabels(labels)
                .withCpuShares(1024)
                .withCpusetCpus(config.appExecCpuset)
                .withCapDrop(Capability.ALL)
                .withMemory(maxRamBytes)
                .withMemorySwap(maxRamBytes)
                .withBinds(binds)
                .exec();
            
            // ~ --cpus=1
            UpdateContainerResponse r = docker.updateContainerCmd(container.getId())
                .withCpuQuota(1_000_000)
                .withCpuPeriod(1_000_000)
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

    public String buildImage(String path, String imageName, StringBuilder buildLog) {
        DockerClient docker = newClient();
        try {
            BuildImageResultCallback callback = docker
                .buildImageCmd(new File(path))
                .withTag(imageName)
                .exec(new BuildImageResultCallback() {
                    // DEBUG
                    @Override
                    public void onNext(BuildResponseItem item) {
                        if (item.getStream() != null) {
                            if (true)
                                System.out.println(item.getStream());
                            buildLog.append(item.getStream());
                        }
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
    
    public void copyDirectory(String containerId, String containerDirPath, File hostPath) throws IOException, DockerOutputPathNotFound {
        DockerClient docker = DockerAdapter.newClient();
        try {
            
            File tempDir = Files.createTempDirectory("").toFile();
            
            try (InputStream tarStream = docker
                .copyArchiveFromContainerCmd(containerId, containerDirPath)
                .exec()) {
                MyFileUtils.untar(tarStream, tempDir);
            }
            
            String untaredRootDirName = Paths.get(containerDirPath).getFileName().toString();
            File untaredRootDir = new File(tempDir, untaredRootDirName);
            FileUtils.moveDirectory(untaredRootDir, hostPath);
            tempDir.delete();
        } catch(NotFoundException ex) {
            throw new DockerOutputPathNotFound(ex);
        } finally {
            close(docker);
        }
    }
    
    public static class DockerOutputPathNotFound extends Exception {
        public DockerOutputPathNotFound(Throwable ex) {
            super(ex);
        }
    }
}
