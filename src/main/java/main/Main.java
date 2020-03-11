package main;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import common.AppConfig;
import externalapi.appcall.db.DBAppCallDAO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import httpserver.WebServiceServer;
import org.slf4j.*;
import workers.WatchingContainerWorker;
import externalapi.appcall.AppCallDAO;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.db.DBAppInfoDAO;
import externalapi.appparam.models.AppParamDAO;
import externalapi.appparam.models.db.DBAppParamDAO;
import externalapi.db.DBConnectionPool;
import helpers.DBHelper;

/**
 *
 * @author cuong
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String... args) throws Exception {
        DBHelper.createTables();
        Injector injector = initialize();
        {
            WatchingContainerWorker watchingContainerWorker = injector.getInstance(WatchingContainerWorker.class);
            watchingContainerWorker.runForever().subscribe();
        }
        
        WebServiceServer server = new WebServiceServer(injector);
        server.start();
        logger.info("Application started");
    }
    
    public static Injector initialize() {
        Injector injector = Guice.createInjector(new AppModule());
        return injector;
    }
    
    // dependency injection
    public static class AppModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(AppConfig.class).toProvider(() -> AppConfig.Inst);
            bind(DBConnectionPool.class).toProvider(() -> DBHelper.getPool());
            bind(AppInfoDAO.class).to(DBAppInfoDAO.class);
            bind(AppParamDAO.class).to(DBAppParamDAO.class);
            bind(AppCallDAO.class).to(DBAppCallDAO.class);
        }
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

