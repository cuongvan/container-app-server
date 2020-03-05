/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import common.BatchAppInfo;
import common.AppConfig;
import docker.DockerAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import notifications.Event;
import notifications.EventType;
import notifications.Status;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExecutorUtil;
import utils.HttpUtil;

public class BuildImageWorker {
    private static Logger logger = LoggerFactory.getLogger(BuildImageWorker.class);
    private Executor executor = ExecutorUtil.newExecutor(0, 3);
    
    @Inject
    private DockerAdapter docker;
    
    public void submitBuildTask(BatchAppInfo app, File buildDir) {
        Runnable r = () -> {
            try {
                logger.info("Build app started: {}", app.image);
                Path dockerBuildFilesDir = Paths.get("docker_build_files", app.language.name().toLowerCase());
                FileUtils.copyDirectory(dockerBuildFilesDir.toFile(), buildDir);
                docker.buildImage(buildDir.toString(), app.image);
                logger.info("Build app done: {}", app.image);
//                FileUtils.deleteDirectory(buildDir);
                HttpUtil.post("http://localhost:5000/notify/batch/" + app.appId,
                    new Event(EventType.Build, Status.Success));
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    logger.warn("Build app failed, app={} language={}, buildDir={}, exeception={}-{}",
                        app.image, app.language, buildDir, e.toString(), e.getMessage());
                        
                    // move to fail directory for later diagnosing
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    String newFolderName = String.format("%s-%s-%s", app.image, app.language, dtf.format(now));
                    FileUtils.moveDirectory(buildDir, new File(AppConfig.Inst.APP_BUILD_FAILED_DIR, newFolderName));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        
        executor.execute(r);
    }
}
