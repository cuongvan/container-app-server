/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import common.DBConnectionPool;
import docker.DockerAdapter;
import externalapi.appinfo.DBAppInfoClient;
import handlers.BuildAppHandler;
import main.Main;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.*;
import org.glassfish.jersey.servlet.*;
import org.slf4j.*;
import externalapi.appinfo.BatchAppInfoDAO;

/**
 *
 * @author cuong
 */
public class WebServiceServer {
    static Logger logger = LoggerFactory.getLogger(WebServiceServer.class);
    public static Server start() throws Exception {
        Server server = new Server();
        {
            ServerConnector connector = new ServerConnector(server);
            connector.setHost("0.0.0.0");
            connector.setPort(5001);
            server.setConnectors(new Connector[]{connector});
        }
        
        {
            ServletContextHandler ctx = new ServletContextHandler();
            ctx.addServlet(new ServletHolder(new ServletContainer(new MyApplication())), "/*");
            server.setHandler(ctx);
        }
        
        server.start();
        logger.info("HTTP server started");
        return server;
    }
    
    public static class MyApplication extends ResourceConfig {
        public MyApplication() {
            packages(true, "httpserver");
            register(new JerseyInjection());
            
            register(LoggingFeature.class);
            register(org.glassfish.jersey.jackson.JacksonFeature.class);
            register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
        }
    }
    
    // dependency injection
    public static class JerseyInjection extends AbstractBinder {
        @Override
        protected void configure() {
            bind(DockerAdapter.class).to(DockerAdapter.class);
            bind(Main.singletonDBConnectionPool()).to(DBConnectionPool.class);
            bind(BuildAppHandler.class).to(BuildAppHandler.class);
            bind(DBAppInfoClient.class).to(BatchAppInfoDAO.class);
        }
    }
}
