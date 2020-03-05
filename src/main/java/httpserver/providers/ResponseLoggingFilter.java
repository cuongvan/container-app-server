/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.providers;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cuong
 */
//@Debugging
@Provider
public class ResponseLoggingFilter implements ContainerResponseFilter {
    static Logger logger = LoggerFactory.getLogger(ResponseLoggingFilter.class);

    @Override
    public void filter(
        ContainerRequestContext req, 
        ContainerResponseContext res) throws IOException
    {
        logger.info("{} /{} {} - {}",
            req.getMethod(),
            req.getUriInfo().getPath(),
            req.getHeaders().get("Content-Type"),
            res.getStatus());
    }
}