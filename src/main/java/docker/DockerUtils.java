/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package docker;

import common.RunningContainer;
import common.ContainerLog;
import com.fasterxml.uuid.Generators;
import com.github.dockerjava.api.*;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.*;
import com.github.dockerjava.core.command.*;
import java.io.File;
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

    static final int COMMAND_STATUS_CHECK_INTERVAL_SEC = 5;

    static DockerClient dockerClient = DockerClientBuilder.getInstance().build();

    private static Set<String> appNames;

    private static String TAG_POSTFIX = "v1";

    public static boolean appExists(String appName) {
        return appNames.contains(appName);
    }

    public static ContainerLog getContainerLog(String containerId) {
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
        }
    }
    
    public static void deleteContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId);
    }

    public static void init() {
        appNames = dockerClient.listImagesCmd().exec().stream()
            .flatMap(image -> Stream.of(image.getRepoTags()))
            .filter(name -> name.endsWith(TAG_POSTFIX))
            .map(name -> name.substring(0, name.length() - TAG_POSTFIX.length() - 1))
            .collect(Collectors.toCollection(() -> ConcurrentHashMap.newKeySet()));
        logger.info("apps: {}", appNames);
    }

    public static String newCallId() {
        UUID uuidv11 = Generators.timeBasedGenerator().generate();
        return uuidv11.toString();
    }

    private static String appNameToImageName(String appName) {
        return appName + ":" + TAG_POSTFIX;
    }

    public static RunningContainer startContainer(String appName, String callId, String input) {
        CreateContainerResponse container = dockerClient.createContainerCmd(appNameToImageName(appName))
            .withName(callId)
            .withEnv("INPUT=" + input)
            .exec();
        dockerClient.startContainerCmd(container.getId()).exec();

        return new RunningContainer(appName, callId);
    }

    public static void buildImage(String path, String appName) {
        try {
            dockerClient.buildImageCmd()
                .withDockerfile(new File(path, "Dockerfile"))
                .withTag(appNameToImageName(appName))
                .exec(new BuildImageResultCallback() {
                    // DEBUG
                    @Override
                    public void onNext(BuildResponseItem item) {
                        if (item.getStream() != null)
                            System.out.println(item.getStream().trim());
                    }
                })
                .awaitCompletion();
            appNames.add(appName);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
