/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import com.google.inject.Injector;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.*;
import org.glassfish.jersey.servlet.*;
import httpserver.helpers.SetupGuiceHK2Bridge;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

/**
 *
 * @author cuong
 */
public class WebServiceServer {
    private Injector guiceInjector;

    public WebServiceServer(Injector guiceInjector) {
        this.guiceInjector = guiceInjector;
    }
    
    public void start() throws Exception {
        Server server = new Server();
        {
            ServerConnector connector = new ServerConnector(server);
            connector.setHost("0.0.0.0");
            connector.setPort(5001);
            server.setConnectors(new Connector[]{connector});
        }
        
        {
            ServletContextHandler ctx = new ServletContextHandler();
            ctx.addServlet(new ServletHolder(new ServletContainer(new MyApplication(guiceInjector))), "/*");
            server.setHandler(ctx);
        }
        
        server.start();
    }
    
    public static class MyApplication extends ResourceConfig {
        public MyApplication(Injector guiceInjector) {
            packages(true, "httpserver");
            register(LoggingFeature.class);
            register(org.glassfish.jersey.jackson.JacksonFeature.class);
            register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
            register(new SetupGuiceHK2Bridge(guiceInjector));
        }
        
        public void tieInjectorToLocator(ServiceLocator aServiceLocator, Injector guiceInjector) {
            GuiceIntoHK2Bridge guiceBridge = aServiceLocator.getService(GuiceIntoHK2Bridge.class);
            guiceBridge.bridgeGuiceInjector(guiceInjector);
        }
    }
}
