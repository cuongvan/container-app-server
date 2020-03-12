/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import httpserver.common.BasicResponse;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.ParamType;
import handlers.BuildAppHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/app/create")
public class CreateApp {
    
    @Inject
    private AppInfoDAO appInfoDAO;
    @Inject
    private BuildAppHandler buildAppHandler;
    
    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newBatchApp(
        @FormDataParam("app_info") byte[] appInfo,
        @FormDataParam("code_file") byte [] codeFile
    ) throws IOException, SQLException
    {
        AppInfo_ request = new ObjectMapper().readValue(appInfo, AppInfo_.class);
        AppInfo app = translate(request);
        
        String appId = appInfoDAO.createApp(app);
        buildAppHandler.buildApp(appId, new ByteArrayInputStream(codeFile));
        
        return Response
            .status(Response.Status.CREATED)
            .entity(new CreateAppResponse(appId))
            .build();
    }

    private AppInfo translate(AppInfo_ request) {
        AppInfo.Builder builder = AppInfo.builder()
            .withAppName(request.appName)
            .withAvatarUrl(request.avatarUrl)
            .withSlugName(request.slugName)
            .withImage(request.image)
            .withOwner(request.owner)
            .withDescription(request.description)
            .withLanguage(request.language)
            .withType(AppType.BATCH)//EXTRA: remove field
            ;
        
        for (AppParam_ p : request.params) {
            builder.addParam(AppParam.builder()
                .withName(p.name)
                .withType(ParamType.valueOf(p.type.name()))
                .withLabel(p.label)
                .withDescription(p.description)
                .build()
            );
        }
        
        return builder.build();
    }
    
    
    @JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
    private static class AppInfo_ {
        public String appName;
        public String avatarUrl;
        public String slugName;
        public String image;
        public String owner;
        public String description;
        public SupportLanguage language;
        public List<AppParam_> params;
    }
    
    private static class AppParam_ {
        public String name;
        public ParamType_ type;
        public String label;
        public String description;
    }
    
    private enum ParamType_ {
        KEY_VALUE, FILE
    }
    
    public static class CreateAppResponse extends BasicResponse {
        public final String appId;

        public CreateAppResponse(String appId) {
            super("");
            this.appId = appId;
        }
    }
}
