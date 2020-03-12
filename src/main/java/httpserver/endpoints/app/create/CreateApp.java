/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints.app.create;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import httpserver.common.BasicResponse;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppType;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/app/create")
public class CreateApp {
    
    @Inject
    private AppInfoDAO appInfoDAO;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newBatchApp(
        CreateAppRequest request
    ) throws IOException, SQLException
    {
        AppInfo app = AppInfo.builder()
            .withAppName(request.appName)
            .withAvatarUrl(request.avatarUrl)
            .withSlugName(request.slugName)
            .withImage(request.image)
            .withOwner(request.owner)
            .withDescription(request.description)
            .withLanguage(request.language)
            .withType(AppType.BATCH)//EXTRA: remove field
            .build();
        String appId = appInfoDAO.createApp(app);
        return Response
            .status(Response.Status.CREATED)
            .entity(new CreateAppResponse(appId))
            .build();
    }
    
    
    @JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
    public static class CreateAppRequest {
        public String appName;
        public String avatarUrl;
        public String slugName;
        public String image;
        public String owner;
        public String description;
        public SupportLanguage language;
    }
    
    public static class CreateAppResponse extends BasicResponse {
        public final String appId;

        public CreateAppResponse(String appId) {
            super("");
            this.appId = appId;
        }
    }
}
