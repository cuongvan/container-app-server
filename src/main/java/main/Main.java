package main;

import common.Conf;
import common.DBConnectionPool;
import common.DockerClientPool;
import docker.DockerUtils;
import workers.PollingContainerStatusWorker;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import jetty_embed.HttpServer;
import org.apache.commons.io.FileUtils;
import org.slf4j.*;
import workers.ContainerFinishWorker;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        DockerClientPool.init();
        DockerUtils.init();
        DBConnectionPool.init();
        PollingContainerStatusWorker.init();
        ContainerFinishWorker.init();
        
        createAppBuildDirs();
        HttpServer.start();
    }
    
    public static void createAppBuildDirs(){
        if (Files.notExists(Paths.get(Conf.Inst.APP_BUILD_DIR))) {
            new File(Conf.Inst.APP_BUILD_DIR).mkdirs();
        }
        
        if (Files.notExists(Paths.get(Conf.Inst.APP_BUILD_FAILED_DIR))) {
            new File(Conf.Inst.APP_BUILD_FAILED_DIR).mkdirs();
        }
        
        if (Files.notExists(Paths.get(Conf.Inst.APP_INPUT_FILES_DIR))) {
            new File(Conf.Inst.APP_INPUT_FILES_DIR).mkdirs();
        }
    }
}

