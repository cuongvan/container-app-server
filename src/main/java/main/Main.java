package main;

import com.google.inject.*;
import common.Config;
import externalapi.AppCodeVersion;
import externalapi.AppCodeVersionDB;
import httpserver.WebServiceServer;
import org.slf4j.*;
import watchers.ContainerFinishWatcher;
import externalapi.DBConnectionPool;
import helpers.*;
import java.nio.file.Paths;

public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Injector injector;
    
    public static void main(String... args) throws Exception {
        MyFileUtils.createRequiredDirs();
        injector = initialize();
        injector.getInstance(DBConnectionPool.class).init();
//        System.out.println(Paths.get(".").toAbsolutePath());
//        if (true) return;
        AppCodeVersionDB db = injector.getInstance(AppCodeVersionDB.class);
//        AppCodeVersion r = db.getById("2a4a617b-2302-446f-816e-08645e4e50d2");
//        System.out.println(r.codePath);
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
