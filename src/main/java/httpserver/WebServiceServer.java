/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import com.google.inject.Injector;
import common.Config;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.*;
import org.glassfish.jersey.servlet.*;
import httpserver.helpers.SetupGuiceHK2Bridge;
import javax.inject.Inject;
import main.Main;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cuong
 */
public class WebServiceServer {
    
    @Inject private Config config;
    @Inject private Injector guiceInjector;
    
    private Server server;
    
    private static Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public void start() throws Exception {
        server = new Server();
        {
            ServerConnector connector = new ServerConnector(server);
            //connector.setHost("0.0.0.0"); // all ips: can access from internet
            connector.setHost("127.0.0.1"); // can only access from same host
            connector.setPort(config.port);
            server.setConnectors(new Connector[]{connector});
        }

        {
            ServletContextHandler ctx = new ServletContextHandler();
            ctx.addServlet(new ServletHolder(new ServletContainer(new MyApplication(guiceInjector))), "/*");
            server.setHandler(ctx);
        }
        server.start();
        LOG.info("Server listening at port " + config.port);
    }

    public static class MyApplication extends ResourceConfig {
        public MyApplication(Injector guiceInjector) {
            packages(true, "httpserver");
            register(LoggingFeature.class);
            register(JacksonFeature.class);
            register(MultiPartFeature.class);
            register(new SetupGuiceHK2Bridge(guiceInjector));
        }
        
        public void tieInjectorToLocator(ServiceLocator aServiceLocator, Injector guiceInjector) {
            GuiceIntoHK2Bridge guiceBridge = aServiceLocator.getService(GuiceIntoHK2Bridge.class);
            guiceBridge.bridgeGuiceInjector(guiceInjector);
        }
    }
}
