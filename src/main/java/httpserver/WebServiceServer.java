/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import com.google.inject.Injector;
import common.Constants;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.*;
import org.glassfish.jersey.servlet.*;
import httpserver.helpers.SetupGuiceHK2Bridge;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

/**
 *
 * @author cuong
 */
public class WebServiceServer {
    public static Server createServer(Injector guiceInjector) throws Exception {
        Server server = new Server();
        {
            ServerConnector connector = new ServerConnector(server);
            //connector.setHost("0.0.0.0"); // all ips: can access from internet
            connector.setHost("127.0.0.1"); // can only access from same host
            connector.setPort(Constants.SERVER_PORT);
            server.setConnectors(new Connector[]{connector});
        }
        
        {
            ServletContextHandler ctx = new ServletContextHandler();
            ctx.addServlet(new ServletHolder(new ServletContainer(new MyApplication(guiceInjector))), "/*");
            server.setHandler(ctx);
        }
        
        return server;
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
