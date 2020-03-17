package httpserver.endpoints;

import externalapi.appcall.models.CallDetail;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.CallParam;
import externalapi.appcall.models.FileParam;
import httpserver.common.BasicResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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
    
    @Path("/{callId}/{fileParamName}")
    @GET
    public Response getCodeFile(@PathParam("callId") String callId, @PathParam("fileParamName") String fileParamName) {
        CallDetail callDetail = appCallDAO.getById(callId);
        
        if (callDetail == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(BasicResponse.fail("App call not found"))
                .type("application/json")
                .build();
        }
        
        Optional<CallParam> paramOpt = callDetail.getParams().stream()
            .filter(p -> p.getName().equals(fileParamName))
            .findFirst();
        
        if (!paramOpt.isPresent()) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .type("application/json")
                .entity(BasicResponse.fail("Param not found"))
                .build();
        }
        
        CallParam param = paramOpt.get();
        if (!(param instanceof FileParam)) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .type("application/json")
                .entity(BasicResponse.fail("Not a file param"))
                .build();
        }
        
        String filePath = ((FileParam) param).getFilePath();
        try {
            byte[] file = Files.readAllBytes(Paths.get(filePath));
            return Response
                .ok(file)
                .type("application/octet-stream")
                .build();
        } catch (IOException ex) {
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type("application/json")
                .entity(BasicResponse.fail("Input file not found. It may have been deleted"))
                .build();
        }
    }
}
