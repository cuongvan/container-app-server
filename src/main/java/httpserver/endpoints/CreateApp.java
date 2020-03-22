/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.appinfo.models.AppDetail;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.InputFieldType;
import handlers.BuildAppHandler;
import handlers.CreateAppHandler;
import httpserver.common.FailedResponse;
import httpserver.common.SuccessResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/app/create")
public class CreateApp {
    @Inject
    private CreateAppHandler createAppHandler;
    @Inject
    private BuildAppHandler buildAppHandler;
    
    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createApp(
        @FormDataParam("app_info") byte[] appInfo,
        @FormDataParam("code_file") byte [] codeFile,
        @FormDataParam("avatar_file") byte[] avatarFile
    ) throws IOException, Exception
    {
        AppInfo_ request;
        try {
            request = new ObjectMapper().readValue(appInfo, AppInfo_.class);
        } catch (Throwable exec) {
            exec.printStackTrace();
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FailedResponse(exec.toString()))
                .build();
        }
        AppDetail app = translate(request);
        
        String appId = createAppHandler.createApp(app, codeFile, avatarFile);
        
        CompletableFuture
            .runAsync(() -> buildAppHandler.buildApp(appId, codeFile));
        
        return Response
            .status(Response.Status.CREATED)
            .entity(new CreateAppResponse(appId))
            .build();
    }

    public static AppDetail translate(AppInfo_ request) {
        AppDetail app = new AppDetail()
            .setAppName(request.appName)
            .setSlugName(request.slugName)
            .setImage(request.image)
            .setOwner(request.owner)
            .setOrganization(request.organization)
            .setDescription(request.description)
            .setLanguage(request.language)
            .setType(AppType.BATCH)//EXTRA: remove field
            ;
        
        for (AppParam_ p : request.params) {
            app.addParam(new AppParam(p.name, p.type, p.label, p.description));
        }
        
        return app;
    }
    
    
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class AppInfo_ {
        public String appName;
        public String slugName;
        public String image;
        public String owner;
        public String organization;
        public String description;
        public SupportLanguage language;
        public List<AppParam_> params;
    }
    
    public static class AppParam_ {
        public String name;
        public InputFieldType type;
        public String label;
        public String description;
    }
    
    public static class CreateAppResponse extends SuccessResponse {
        public final String appId;

        public CreateAppResponse(String appId) {
            this.appId = appId;
        }
    }
}
