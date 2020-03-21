package httpserver.endpoints;

import externalapi.appcall.models.CallDetail;
import externalapi.appcall.CallDAO;
import externalapi.appcall.models.CallParam;
import externalapi.appinfo.models.ParamType;
import httpserver.common.FailedResponse;
import httpserver.common.SuccessResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/call")
public class AppCallEndpoint {
    @Inject
    private CallDAO appCallDAO;
    
    @Path("/")
    @GET
    @Produces("application/json")
    public GetAllResponse getAllCallIds() throws SQLException {
        List<String> callIds = appCallDAO.getAllCallIds();
        return new GetAllResponse(callIds);
    }
    
    private static class GetAllResponse extends SuccessResponse {
        public final List<String> call_ids;

        public GetAllResponse(List<String> call_ids) {
            super();
            this.call_ids = call_ids;
        }
    }
    
    @Path("/{callId}")
    @GET
    @Produces("application/json")
    public Response getAppInfo(@PathParam("callId") String callId) throws SQLException {
        CallDetail callDetail = appCallDAO.getById(callId);
        if (callDetail != null) {
            return Response
                .ok(new GetAppResponse(callDetail))
                .build();
        } else {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new FailedResponse("App call not found"))
                .build();
        }
    }
    
    private static class GetAppResponse extends SuccessResponse {
        public final CallDetail callDetail;

        public GetAppResponse(CallDetail callDetail) {
            this.callDetail = callDetail;
        }
    }
    
    @Path("/{callId}/{fileParamName}")
    @GET
    public Response getCodeFile(
        @PathParam("callId") String callId,
        @PathParam("fileParamName") String fileParamName) throws SQLException {
        CallDetail callDetail = appCallDAO.getById(callId);
        
        if (callDetail == null) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new FailedResponse("App call not found"))
                .type("application/json")
                .build();
        }
        
        Optional<CallParam> paramOpt = callDetail.params.stream()
            .filter(p -> p.name.equals(fileParamName))
            .findFirst();
        
        if (!paramOpt.isPresent()) {
            return Response
                .status(Response.Status.NOT_FOUND)
                .type("application/json")
                .entity(new FailedResponse("Param not found"))
                .build();
        }
        
        CallParam param = paramOpt.get();
        if (!(param.type == ParamType.FILE)) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .type("application/json")
                .entity(new FailedResponse("Not a file param"))
                .build();
        }
        
        String filePath = param.value;
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
                .entity(new FailedResponse("Input file not found. It may have been deleted"))
                .build();
        }
    }
}
