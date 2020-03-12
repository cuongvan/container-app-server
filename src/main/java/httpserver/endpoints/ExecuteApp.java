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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import javax.inject.Inject;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/**
 *
 * @author cuong
 */

@Path("/app/{appId}/execute")
public class ExecuteApp {
    @Inject
    private ExecuteHandler handler;
        
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(
        @PathParam("appId") String appId,
        @QueryParam("userId") String userId,
        FormDataMultiPart body
    ){
        Map<String, List<FormDataBodyPart>> allFiles = body.getFields();
        // get first file of each key
        Map<String, FormDataBodyPart> files = allFiles
            .entrySet()
            .stream()
            .collect(toMap(
                Map.Entry::getKey,
                e -> e.getValue().get(0)
            ));
        
        try {
            String callId = handler.execute(appId, userId, files);
            return Response
                .ok(new ExecuteResponseSuccess(callId))
                .status(HttpStatus.ACCEPTED_202)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (IOException ex) {
            throw new RuntimeException(ex); 
        }
    }
    
    private static class ExecuteResponseSuccess extends BasicResponse {
        public final String callId;

        public ExecuteResponseSuccess(String callId) {
            super("");
            this.callId = callId;
        }
    }
}
