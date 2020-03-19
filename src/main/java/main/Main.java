package main;

import com.google.inject.*;
import httpserver.WebServiceServer;
import org.slf4j.*;
import watchers.ContainerFinishWatcher;
import externalapi.DBConnectionPool;
import helpers.*;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Server server;
    private static Injector injector;
    
    public static void main(String... args) throws Exception {
        MyFileUtils.createRequiredDirs();
        injector = initialize();
        {
            ContainerFinishWatcher watchingContainerWorker = injector.getInstance(ContainerFinishWatcher.class);
            watchingContainerWorker.runForever();
        }
        
        server = WebServiceServer.createServer(injector);
        server.start();
        logger.info("Application started");
    }
    
    public static Injector initialize() {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DBConnectionPool.class).toProvider(() -> DBConnectionPool.getInstance());
            }
        });
    }

    public static void stop() throws Exception {
        logger.info("Stop server");
        server.stop();
        injector.getInstance(ContainerFinishWatcher.class).stop();
    }
}
