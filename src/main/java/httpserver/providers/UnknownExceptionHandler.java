/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.providers;

import httpserver.common.FailedResponse;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cuong
 */

@Provider
public class UnknownExceptionHandler implements ExceptionMapper<Throwable> {
    static Logger logger = LoggerFactory.getLogger("Exception handler");
    
    @Override
    public Response toResponse(Throwable e) {
        int status;
        if (e instanceof SQLException) {
            status = 500;
            e.printStackTrace();
        }
        else if (e instanceof NotFoundException) {
            status = 404;
        }
        else if (e instanceof NotAllowedException) {
            status = 404;
        }
        else if (e instanceof IOException) {
            status = 500;
            e.printStackTrace();
        }
        else {
            status = 500;
            e.printStackTrace();
        }
        
        return Response
            .status(status)
            .type(MediaType.APPLICATION_JSON)
            .entity(new FailedResponse(e.toString()))
            .build();
    }
}
