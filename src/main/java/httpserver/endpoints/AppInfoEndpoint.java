package httpserver.endpoints;

import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import httpserver.common.BasicResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/app")
public class AppInfoEndpoint {
    @Inject
    private AppInfoDAO appInfoDAO;
    
    @Path("/")
    @GET
    @Produces("application/json")
    public GetAllResponse getAllApps() {
        List<String> params = appInfoDAO.getAllAppIds();
        return new GetAllResponse(params);
    }
    
    private static class GetAllResponse extends BasicResponse {
        public final List<String> app_ids;

        public GetAllResponse(List<String> app_ids) {
            super("");
            this.app_ids = app_ids;
        }
    }
    
    @Path("/{appId}")
    @GET
    @Produces("application/json")
    public Response getAppInfo(@PathParam("appId") String appId) {
        AppInfo appInfo = appInfoDAO.getById(appId);
        if (appInfo != null) {
            return Response.ok(appInfo).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity(BasicResponse.fail("App not found")).build();
        }
    }
    
    @Path("/{appId}/codefile")
    @GET
    public Response getCodeFile(@PathParam("appId") String appId) throws IOException {
        AppInfo appInfo = appInfoDAO.getById(appId);
        if (appInfo != null) {
            String codePath = appInfo.getCodePath();
            byte[] codeFile = Files.readAllBytes(Paths.get(codePath));
            return Response.ok(codeFile).type("application/octet-stream").build();
        } else {
            return Response
                .status(Status.NOT_FOUND)
                .type("application/json")
                .entity(BasicResponse.fail("App not found"))
                .build();
        }
    }
}
