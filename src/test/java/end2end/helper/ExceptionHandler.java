/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package end2end.helper;

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
        
        return Response
            .status(500)
            .type(MediaType.APPLICATION_JSON)
            .entity(e.toString())
            .build();
    }
}
