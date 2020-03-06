/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import httpserver.common.BasicResponse;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.AppCallInfo;
import common.SupportLanguage;
import common.DBHelper;
import docker.DockerAdapter;
import handlers.BuildAppHandler;
import io.reactivex.rxjava3.core.Single;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import io.reactivex.rxjava3.core.Completable;
import externalapi.AppCallDAO;

@Path("/app/new")
public class BuildApp {
    @Inject
    private DockerAdapter docker;
    
    @Inject
    private BuildAppHandler buildAppHandler;
    
    @Inject
    private AppCallDAO appInfoDAO;
    
    public static class NewAppRequest {
        public String appName;
        public SupportLanguage programmingLanguage;

        public static NewAppRequest fromJson(String jsonString) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            return objectMapper.readValue(jsonString, NewAppRequest.class);
        }
    }

    @Path("/batch/{appId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BasicResponse newBatchApp(
        @PathParam("appId") String appId,
        byte[] codeFile
    ) throws IOException, SQLException
    {
        Single
            .fromCallable(() -> appInfoDAO.getById(appId))
            .flatMapCompletable(appInfo -> buildAppHandler.buildApp(appInfo, codeFile))
            .blockingAwait();
        return BasicResponse.OK;
    }

    @Path("/server/{appId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BasicResponse newServerApp(@PathParam("appId") String appId) throws SQLException
    {
        AppCallInfo.ServerAppCallInfo app = DBHelper.retrieveServerAppInfo(appId);
        Completable
            .fromAction(() -> docker.startServerApp(app.image, app.outsidePort, app.imagePort))
            .blockingAwait()
            ;
        return BasicResponse.OK;
    }
}
