/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_lib.jersey.all;

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
    
    @Override
    public Response toResponse(Exception e) {
        e.printStackTrace();
        return Response.ok(e.toString()).build();
    }
}
