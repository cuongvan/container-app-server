/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jetty_embed.jerseycomponents;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 *
 * @author cuong
 */

@Provider
public class UnknownExceptionHandler implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        e.printStackTrace();
        String exec = e.toString() + "\n" + e.getMessage() + "\n";
        return Response.status(500).entity(exec).build();
    }
}
