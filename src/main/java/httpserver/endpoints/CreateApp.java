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
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.ParamType;
import handlers.BuildAppHandler;
import handlers.CreateAppHandler;
import httpserver.common.FailedResponse;
import httpserver.common.SuccessResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
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
        } catch (Exception exec) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FailedResponse(exec.toString()))
                .build();
        }
        AppInfo app = translate(request);
        
        String appId = createAppHandler.createApp(app, codeFile, avatarFile);
        buildAppHandler.buildApp(appId, new ByteArrayInputStream(codeFile));
        return Response
            .status(Response.Status.CREATED)
            .entity(new CreateAppResponse(appId))
            .build();
    }

    public static AppInfo translate(AppInfo_ request) {
        AppInfo app = new AppInfo()
            .withAppName(request.appName)
            .withSlugName(request.slugName)
            .withImage(request.image)
            .withOwner(request.owner)
            .withDescription(request.description)
            .withLanguage(request.language)
            .withType(AppType.BATCH)//EXTRA: remove field
            ;
        
        for (AppParam_ p : request.params) {
            app.addParam(AppParam.builder()
                .withName(p.name)
                .withType(ParamType.valueOf(p.type.name()))
                .withLabel(p.label)
                .withDescription(p.description)
                .build()
            );
        }
        
        return app;
    }
    
    
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class AppInfo_ {
        public String appName;
        public String slugName;
        public String image;
        public String owner;
        public String description;
        public SupportLanguage language;
        public List<AppParam_> params;
    }
    
    public static class AppParam_ {
        public String name;
        public ParamType type;
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
