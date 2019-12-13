/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jetty_embed;

import common.Conf;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.*;
import org.glassfish.jersey.servlet.*;
import org.slf4j.*;

/**
 *
 * @author cuong
 */
public class HttpServer {
    static Logger logger = LoggerFactory.getLogger(HttpServer.class);
    public static Server start() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("0.0.0.0");
        connector.setPort(Conf.Inst.HTTP_PORT);
        server.setConnectors(new Connector[]{connector});
        
        ResourceConfig conf = new ResourceConfig();
        conf.packages(true, HttpServer.class.getPackage().toString());
        conf.register(LoggingFeature.class);
        conf.register(org.glassfish.jersey.jackson.JacksonFeature.class);
        conf.register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
        
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ctx.addServlet(new ServletHolder(new ServletContainer(conf)), "/*");
        server.setHandler(ctx);
        server.start();
        logger.info("HTTP server started");
        return server;
    }
}
