/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class UnknownExceptionHandler implements ExceptionMapper<Exception> {
    static Logger logger = LoggerFactory.getLogger("Exception handler");
    
    public static class ErrorResponse {
        public String error;
    }
    
    @Override
    public Response toResponse(Exception e) {
        e.printStackTrace();
        ErrorResponse r = new ErrorResponse();
        int status;
        if (e instanceof SQLException) {
            status = 500;
            r.error = String.format("SQL exception: %s", e.getMessage());
        }
        else if (e instanceof NotFoundException) {
            status = 404;
            r.error = "Invalid path";
        }
        else if (e instanceof NotAllowedException) {
            status = 404;
            r.error = "Method not allowed";
        }
        else if (e instanceof IOException) {
            status = 500;
            r.error = "IOException: " + e.getMessage();
        }
        else {
            status = 500;
            r.error = String.format("Unknown error: %s: %s", e, e.getMessage());
        }
        
        return Response
            .status(status)
            .type(MediaType.APPLICATION_JSON)
            .entity(r)
            .build();
    }
}
