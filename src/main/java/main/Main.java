package main;

import com.google.inject.*;
import externalapi.appcall.DBAppCallDAO;
import httpserver.WebServiceServer;
import org.slf4j.*;
import watchers.ContainerFinishWatcher;
import externalapi.appcall.*;
import externalapi.DBConnectionPool;
import externalapi.appinfo.*;
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
                bind(DBConnectionPool.class).toProvider(() -> DBHelper.getPool());
                bind(AppInfoDAO.class).to(DBAppInfoDAO.class);
                bind(AppCallDAO.class).to(DBAppCallDAO.class);
            }
        });
    }

    public static void stop() throws Exception {
        logger.info("Stop server");
        server.stop();
        injector.getInstance(ContainerFinishWatcher.class).stop();
    }
}
