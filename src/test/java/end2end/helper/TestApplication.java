package end2end.helper;

import common.AppConfig;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.DBAppCallDAO;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.DBAppInfoDAO;
import externalapi.appparam.AppParamDAO;
import externalapi.appparam.DBAppParamDAO;
import externalapi.DBConnectionPool;
import handlers.BuildAppHandler;
import javax.inject.Singleton;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class TestApplication extends ResourceConfig {

    public TestApplication() {
        register(ExceptionHandler.class);
        register(new TestBinder());
    }

    public static class TestBinder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(AppConfig.Inst).to(AppConfig.class);
            bind(DBConnectionPool.class).to(DBConnectionPool.class).in(Singleton.class);
            bind(DBAppInfoDAO.class).to(AppInfoDAO.class);
            bind(DBAppParamDAO.class).to(AppParamDAO.class);
            bind(DBAppCallDAO.class).to(AppCallDAO.class);
            bind(BuildAppHandler.class).to(BuildAppHandler.class);
            bind(DockerAdapter.class).to(DockerAdapter.class);
        }
    }

}
