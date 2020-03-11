/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints.app.create;

import httpserver.common.BasicResponse;
import handlers.BuildAppHandler;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/app/{appId}/build")
public class BuildApp {
    @Inject
    private BuildAppHandler buildAppHandler;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sumitCodeFile(
        @PathParam("appId") String appId,
        InputStream codeFile
    ) throws IOException {
        buildAppHandler.buildApp(appId, codeFile);
        return Response.accepted(BasicResponse.OK).build();
    }
}
