package httpserver.endpoints;

import externalapi.appinfo.AppDAO;
import externalapi.appinfo.models.AppDetail;
import handlers.exceptions.AppNotFound;
import httpserver.common.FailedResponse;
import httpserver.common.SuccessResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/app")
public class AppEndpoint {
    @Inject
    private AppDAO appDAO;
    
    @Path("/")
    @GET
    @Produces("application/json")
    public GetAllResponse getAllApps() throws SQLException {
        List<String> params = appDAO.getAllAppIds();
        return new GetAllResponse(params);
    }
    
    private static class GetAllResponse extends SuccessResponse {
        public final List<String> app_ids;

        public GetAllResponse(List<String> app_ids) {
            this.app_ids = app_ids;
        }
    }
    
    @Path("/{appId}")
    @GET
    @Produces("application/json")
    public Response getAppInfo(@PathParam("appId") String appId) throws SQLException {
        AppDetail appInfo = appDAO.getById(appId);
        if (appInfo != null) {
            return Response
                .ok(new GetAppResponse(appInfo)).build();
        } else {
            return Response
                .status(Status.NOT_FOUND)
                .entity(new FailedResponse("App not found"))
                .build();
        }
    }
    
    private static class GetAppResponse extends SuccessResponse {
        public final AppDetail appDetail;

        public GetAppResponse(AppDetail appDetail) {
            this.appDetail = appDetail;
        }
    }
    
    @Path("/{appId}/codefile")
    @GET
    public Response getCodeFile(@PathParam("appId") String appId) throws IOException, SQLException {
        AppDetail appInfo = appDAO.getById(appId);
        if (appInfo != null) {
            String codePath = appInfo.getCodePath();
            byte[] codeFile = Files.readAllBytes(Paths.get(codePath));
            return Response.ok(codeFile).type("application/octet-stream").build();
        } else {
            return Response
                .status(Status.NOT_FOUND)
                .type("application/json")
                .entity(new FailedResponse("App not found"))
                .build();
        }
    }
    
    @Path("/{appId}/avatar")
    @GET
    public Response getAvatarFile(@PathParam("appId") String appId) throws IOException, SQLException {
        AppDetail appInfo = appDAO.getById(appId);
        if (appInfo != null) {
            String avatarPath = appInfo.getAvatarPath();
            byte[] file = Files.readAllBytes(Paths.get(avatarPath));
            return Response
                .ok(file, "application/octet-stream")
                .build();
        } else {
            return Response
                .status(Status.NOT_FOUND)
                .type("application/json")
                .entity(new FailedResponse("App not found"))
                .build();
        }
    }
    
    @Path("/{appId}")
    @DELETE
    public Response deleteApp(@PathParam("appId") String appId) throws SQLException {
        try {
            appDAO.deleteById(appId);
            return Response
                .ok(new SuccessResponse(), "application/json")
                .build();
        } catch (AppNotFound ex) {
            return Response
                .status(Status.NOT_FOUND)
                .type("application/json")
                .entity(new FailedResponse("App not found"))
                .build();
        }
    }
}
