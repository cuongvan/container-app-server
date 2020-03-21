/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import handlers.exceptions.AppBuildFailed;
import handlers.exceptions.AppNotDoneBuilding;
import handlers.exceptions.AppNotFound;
import handlers.ExecuteHandler;
import httpserver.common.FailedResponse;
import httpserver.common.SuccessResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.media.multipart.*;

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
    ) throws IOException, SQLException 
    {
        Map<String, byte[]> fields = getFieldsContent(body);
        try {
            String callId = handler.execute(appId, userId, fields);
            return Response
                .ok(new ExecuteResponseSuccess(callId))
                .status(Status.ACCEPTED)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (AppNotFound ex) {
            return Response
                .ok(new FailedResponse("App not found"))
                .status(Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (AppNotDoneBuilding ex) {
            return Response
                .ok(new FailedResponse("App has not been built yet"))
                .status(Status.SERVICE_UNAVAILABLE)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (AppBuildFailed ex) {
            return Response
                .ok(new FailedResponse("App build failed. Cannot execute"))
                .status(Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }
    
    private static Map<String, byte[]> getFieldsContent(FormDataMultiPart body) {
        Map<String, byte[]> map = new HashMap<>();
        
        Set<String> fields = body.getFields().keySet();
        for (String field : fields) {
            FormDataBodyPart fieldContent = body.getField(field);
            byte[] content = fieldContent.getEntityAs(byte[].class);
            map.put(field, content);
        }
        
        return map;
    }
    
    private static class ExecuteResponseSuccess extends SuccessResponse {
        public final String callId;

        public ExecuteResponseSuccess(String callId) {
            this.callId = callId;
        }
    }
}
