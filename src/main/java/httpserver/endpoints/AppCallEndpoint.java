package httpserver.endpoints;

import externalapi.appcall.AppCallDAO;
import httpserver.common.BasicResponse;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;

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
            super("");
            this.call_ids = call_ids;
        }
    }
}
