/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints.app.create;

import httpserver.common.BasicResponse;
import handlers.BuildAppHandler;
import io.reactivex.rxjava3.core.Single;
import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/app/{appId}/build")
public class BuildApp {
    @Inject
    private BuildAppHandler buildAppHandler;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void sumitCodeFile(
        @Suspended AsyncResponse asyncResponse,
        @PathParam("appId") String appId,
        InputStream codeFile
    ) {
        buildAppHandler
            .buildApp(appId, codeFile)
            .doOnError(err -> System.out.println(err))
            .andThen(Single.just(success()))
            .onErrorReturn(err -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new BasicResponse(err.toString())).build())
            .subscribe(response -> asyncResponse.resume(response));
    }
    
    private static Response success() {
        return Response.accepted(BasicResponse.OK).build();
    }
}
