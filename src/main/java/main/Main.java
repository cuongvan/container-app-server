package main;

import com.google.inject.*;
import common.Config;
import httpserver.WebServiceServer;
import org.slf4j.*;
import watchers.ContainerFinishWatcher;
import externalapi.DBConnectionPool;
import httpserver.endpoints.CheckAndStartDockerContainerThread;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Injector injector;
    
    public static void main(String... args) throws Exception {
        injector = createInjector();
        
        initSystem(injector.getInstance(Config.class));
        injector.getInstance(ContainerFinishWatcher.class).runForever();
        injector.getInstance(CheckAndStartDockerContainerThread.class).start();
        
        WebServiceServer server = injector.getInstance(WebServiceServer.class);
        server.start();
    }
    
    private static Injector createInjector() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DBConnectionPool.class).asEagerSingleton();
                bind(Config.class).toProvider(Config::loadConfig).asEagerSingleton(); // already covers in(Singleton.class)
            }
        });
    }
    
    private static void initSystem(Config config) throws IOException {
        createDirectory(new File(config.dataDir), "data directory");
        createDirectory(new File(config.dockerBuildDir), "Docker build directory");
        createDirectory(new File(config.appInputFilesDir), "app call input files directory");
        createDirectory(new File(config.appOutputFilesDir), "app call output files directory");
    }
    
    private static void createDirectory(File directory, String logging) throws IOException {
        if (!directory.isDirectory()) {
            logger.info("Create " + logging + " at " + directory);
            FileUtils.forceMkdir(directory);
        }
    }
}
