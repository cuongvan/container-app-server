/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_lib.jersey.all;

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
public class ExceptionHandler implements ExceptionMapper<Exception> {
    static Logger logger = LoggerFactory.getLogger("Exception handler");
    
    public static class ErrorResponse {
        public String error;
    }
    
    @Override
    public Response toResponse(Exception e) {
        e.printStackTrace();
        return Response.ok(e.toString()).build();
    }
}
