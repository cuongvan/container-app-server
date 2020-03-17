package httpserver.endpoints;

import externalapi.appcall.models.CallDetail;
import externalapi.appcall.AppCallDAO;
import externalapi.appinfo.models.AppInfo;
import httpserver.common.BasicResponse;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/call")
public class AppCallEndpoint {
    @Inject
    private AppCallDAO appCallDAO;
    
    @Path("/")
    @GET
    @Produces("application/json")
    public GetAllResponse getAllCallIds() {
        List<String> callIds = appCallDAO.getAllCallIds();
        return new GetAllResponse(callIds);
    }
    
    private static class GetAllResponse extends BasicResponse {
        public final List<String> call_ids;

        public GetAllResponse(List<String> call_ids) {
            super();
            this.call_ids = call_ids;
        }
    }
    
    @Path("/{callId}")
    @GET
    @Produces("application/json")
    public Response getAppInfo(@PathParam("callId") String callId) {
        CallDetail callDetail = appCallDAO.getById(callId);
        if (callDetail != null) {
            return Response.ok(callDetail).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity(BasicResponse.fail("App call not found")).build();
        }
    }
}
