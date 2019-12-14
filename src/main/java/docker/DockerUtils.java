package docker;

import common.RunningContainer;
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
import java.util.concurrent.*;
import java.util.stream.*;
import org.slf4j.*;

/**
 *
 * @author cuong
 */
public class DockerUtils {

    private static Logger logger = LoggerFactory.getLogger(DockerUtils.class);
    private static Set<String> appNames;
    private static final String TAG_POSTFIX = "v1";

    public static boolean appExists(String appName) {
        return appNames.contains(appName);
    }

    public static ContainerLog getContainerLog(String containerId) {
        DockerClient dockerClient = DockerClientPool.Instance.getClient();
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
            throw new RuntimeException("Should not happend");
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }

    public static void deleteContainer(String containerId) {
        DockerClient dockerClient = DockerClientPool.Instance.getClient();
        try {
            dockerClient.removeContainerCmd(containerId);
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }

    public static void init() {
        DockerClient dockerClient = DockerClientPool.Instance.getClient();
        try {
            appNames = dockerClient.listImagesCmd().exec().stream()
                .flatMap(image -> Stream.of(image.getRepoTags()))
                .filter(name -> name.endsWith(TAG_POSTFIX))
                .map(name -> name.substring(0, name.length() - TAG_POSTFIX.length() - 1))
                .collect(Collectors.toCollection(() -> ConcurrentHashMap.newKeySet()));
            logger.info("Initial apps: {}", appNames);
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }

    }

    public static String newCallId() {
        UUID uuidv11 = Generators.timeBasedGenerator().generate();
        return uuidv11.toString();
    }

    private static String appNameToImageName(String appName) {
        return appName + ":" + TAG_POSTFIX;
    }

    public static RunningContainer startContainer(String appName, String callId, String inputFile) {
        inputFile = Paths.get(inputFile).toAbsolutePath().toString();
        DockerClient dockerClient = DockerClientPool.Instance.getClient();
        try {
            Volume vol = new Volume("/input"); // container's path
            CreateContainerResponse container = dockerClient.createContainerCmd(appNameToImageName(appName))
                .withName(callId)
                .withVolumes(vol)
                .withBinds(new Bind(inputFile, vol))
                .exec();
            dockerClient.startContainerCmd(container.getId()).exec();

            return new RunningContainer(appName, callId);
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }

    public static void buildImage(String path, String appName) {
        DockerClient dockerClient = DockerClientPool.Instance.getClient();
        try {
            dockerClient.buildImageCmd()
                .withDockerfile(new File(path, "Dockerfile"))
                .withTag(appNameToImageName(appName))
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
            appNames.add(appName);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            DockerClientPool.Instance.returnClient(dockerClient);
        }
    }
}
