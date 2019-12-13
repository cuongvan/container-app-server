/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import common.Conf;
import common.SupportLanguage;
import docker.DockerUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageBuildingWorker {
    private static Logger logger = LoggerFactory.getLogger(ImageBuildingWorker.class);
    private static Executor executor = new ThreadPoolExecutor(
        0, 3, // maximum 3 builds
        60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    
    public static void submitBuildTask(String appName, SupportLanguage language, File buildDir) {
        Runnable r = () -> {
            try {
                logger.info("Build app started: {}", appName);
//                String dockerfileName = language.name().toLowerCase() + ".Dockerfile";
//                File dockerfile = new File("Dockerfiles", dockerfileName);
//                FileUtils.copyFile(dockerfile, new File(buildDir, "Dockerfile"));
                Path dockerBuildFilesDir = Paths.get("docker_build_files", language.name().toLowerCase());
                FileUtils.copyDirectory(dockerBuildFilesDir.toFile(), buildDir);
                DockerUtils.buildImage(buildDir.toString(), appName);
                logger.info("Build app done: {}", appName);
//                FileUtils.deleteDirectory(buildDir);
                
                // TODO notify CKAN
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    logger.warn("Build app failed, app={} language={}, buildDir={}, exeception={}-{}",
                        appName, language, buildDir, e.toString(), e.getMessage());
                        
                    // move to fail directory for later diagnosing
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String newFolderName = String.format("%s-%s-%s", appName, language, dtf.format(now));
                    FileUtils.moveDirectory(buildDir, new File(Conf.APP_BUILD_FAILED_DIR, newFolderName));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        
        executor.execute(r);
    }
}
