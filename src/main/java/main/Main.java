package main;

import common.Conf;
import docker.DockerUtils;
import docker.PollingContainerStatusWorker;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import jetty_embed.HttpServer;
import org.slf4j.*;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        createAppBuildDirs();
        DockerUtils.init();
        HttpServer.start();
        PollingContainerStatusWorker.init();
    }
    
    public static void createAppBuildDirs(){
        if (Files.notExists(Paths.get(Conf.APP_BUILD_DIR))) {
            new File(Conf.APP_BUILD_DIR).mkdirs();
            logger.info("App build directory {} created", Conf.APP_BUILD_DIR);
        }
        
        if (Files.notExists(Paths.get(Conf.APP_BUILD_FAILED_DIR))) {
            new File(Conf.APP_BUILD_FAILED_DIR).mkdirs();
            logger.info("Failed app builds directory {} created", Conf.APP_BUILD_FAILED_DIR);
        }
    }
}

