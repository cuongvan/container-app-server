package main;

import com.google.inject.*;
import common.Config;
import httpserver.WebServiceServer;
import org.slf4j.*;
import watchers.ContainerFinishWatcher;
import externalapi.DBConnectionPool;
import helpers.*;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Injector injector;
    
    public static void main(String... args) throws Exception {
        MyFileUtils.createRequiredDirs();
        injector = initialize();
        injector.getInstance(DBConnectionPool.class).init();
        {
            ContainerFinishWatcher watchingContainerWorker = injector.getInstance(ContainerFinishWatcher.class);
            watchingContainerWorker.runForever();
        }
        
        WebServiceServer server = injector.getInstance(WebServiceServer.class);
        server.start();
    }
    
    public static Injector initialize() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DBConnectionPool.class).asEagerSingleton();
                bind(Config.class).toProvider(Config::loadConfig).asEagerSingleton(); // already covers in(Singleton.class)
            }
        });
    }
}
