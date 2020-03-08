package main;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import common.AppConfig;
import externalapi.db.DBConnectionPool;
import externalapi.appcall.db.DBAppCallDAO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import httpserver.WebServiceServer;
import org.slf4j.*;
import workers.WatchingContainerWorker;
import externalapi.appcall.AppCallDAO;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
//        String s = """
//                   DROP TABLE IF EXISTS app_info;
//                   DROP TABLE IF EXISTS app_call;
//                   """;
//        System.out.println(s);
        Injector injector = initialize();
        {
            WatchingContainerWorker watchingContainerWorker = injector.getInstance(WatchingContainerWorker.class);
            watchingContainerWorker.runForever().subscribe();
        }
        
//        createAppBuildDirs();
        WebServiceServer server = new WebServiceServer(injector);
        server.start();
    }
    
    private static Injector initialize() {
        Injector injector = Guice.createInjector(new AppModule());
        return injector;
    }
    
    // dependency injection
    public static class AppModule extends AbstractModule {
        @Override
        protected void configure() {
            AppConfig config = AppConfig.Inst;
            bind(AppConfig.class).toProvider(() -> config);
            bind(DBConnectionPool.class).toProvider(() -> singletonDBConnectionPool());
            bind(AppCallDAO.class).to(DBAppCallDAO.class);
        }
    }
    
    private static final AppConfig appConfig = AppConfig.Inst;
    private static DBConnectionPool dbPool = new DBConnectionPool(appConfig);
    
    public static DBConnectionPool singletonDBConnectionPool() {
        return dbPool;
    }
    
    public static void createAppBuildDirs(){
        if (Files.notExists(Paths.get(AppConfig.Inst.APP_BUILD_DIR))) {
            new File(AppConfig.Inst.APP_BUILD_DIR).mkdirs();
        }
        
        if (Files.notExists(Paths.get(AppConfig.Inst.APP_BUILD_FAILED_DIR))) {
            new File(AppConfig.Inst.APP_BUILD_FAILED_DIR).mkdirs();
        }
        
        if (Files.notExists(Paths.get(AppConfig.Inst.APP_INPUT_FILES_DIR))) {
            new File(AppConfig.Inst.APP_INPUT_FILES_DIR).mkdirs();
        }
    }
}

