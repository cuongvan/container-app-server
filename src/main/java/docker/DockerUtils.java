package docker;

import common.ContainerLog;
import com.fasterxml.uuid.Generators;
import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.*;
import common.DockerClientPool;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import org.slf4j.*;

public class DockerUtils {

    private static Logger logger = LoggerFactory.getLogger(DockerUtils.class);
    private static Set<String> appNames;
//    private static final String TAG_POSTFIX = "ckanapp";

    public static boolean appExists(String appName) {
        return appNames.contains(appName);
    }

    public static ContainerLog getContainerLog(String containerId) {
        DockerClient dockerClient = DockerClientPool.Instance.borrowClient();
        try {
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
        } catch (InterruptedException e) {
            throw new IllegalThreadStateException("Should not happend");
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }

    public static void deleteContainer(String containerId) {
        DockerClient dockerClient = DockerClientPool.Instance.borrowClient();
        try {
            dockerClient.removeContainerCmd(containerId).exec();
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }

    public static void init() {
        DockerClient dockerClient = DockerClientPool.Instance.borrowClient();
        try {
//            appNames = dockerClient.listImagesCmd().exec().stream()
//                .flatMap(image -> Stream.of(image.getRepoTags()))
//                .filter(name -> name.endsWith(TAG_POSTFIX))
//                .map(name -> name.substring(0, name.length() - TAG_POSTFIX.length() - 1))
//                .collect(Collectors.toCollection(() -> ConcurrentHashMap.newKeySet()));
            logger.info("Initial apps: {}", appNames);
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }

    }

    public static String newCallId() {
        UUID uuidv11 = Generators.timeBasedGenerator().generate();
        return uuidv11.toString();
    }

//    private static String appNameToImageName(String appName) {
//        return appName + ":" + TAG_POSTFIX;
//    }

//    private static final Map<String, String> labelMap = Collections.singletonMap(Conf.Inst.CKAN_APP_CONTAINER_LABEL, "");

    /**
     * @return container's id
     */
    public static String startBatchApp(
        String imageName,
        String jsonInputPath,
        String binaryInputPath
    ) {
        DockerClient docker = DockerClientPool.Instance.borrowClient();
        try {
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
        } finally {
            DockerClientPool.Instance.returnClient(docker);
        }
    }

    public static void startServerApp(
        String imageName, int hostPort, int imagePort) {
        DockerClient dockerClient = DockerClientPool.Instance.borrowClient();
        try {
            ExposedPort exposedPort = ExposedPort.tcp(imagePort);
            Ports portBindings = new Ports();
            portBindings.bind(exposedPort, Ports.Binding.bindPort(hostPort));

            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
//                .withLabels(labelMap)
                .withExposedPorts(exposedPort)
                .withPortBindings(portBindings)
                .exec();
            dockerClient.startContainerCmd(container.getId()).exec();
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }

    public static void buildImage(String path, String imageName) {
        DockerClient dockerClient = DockerClientPool.Instance.borrowClient();
        try {
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
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }
    
    public static void pullImage(String image) {
        DockerClient dockerClient = DockerClientPool.Instance.borrowClient();
        try {
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
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }
}
