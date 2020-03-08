/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import handlers.ExecuteHandler;
import httpserver.common.BasicResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import httpserver.providers.Debugging;
import javax.inject.Inject;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author cuong
 */

@Path("/execute")
@Debugging
public class ExecuteApp {
    @Inject
    private ExecuteHandler handler;
        
    @Path("/batch/{callId}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(
        @PathParam("callId") String callId,
        @FormDataParam("json") byte[] jsonInput,
        @FormDataParam("binary") byte[] fileInput)
    {
        handler
            .execute(callId, jsonInput, fileInput)
            .blockingAwait()
            ;
        return Response
            .ok(new BasicResponse(""))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
