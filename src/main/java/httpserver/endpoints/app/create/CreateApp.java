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
        System.out.println(">>>>>>>>>>>> Received request: " + request);
        AppInfo app = new AppInfo()
            .setAppName(request.appName)
            .setAvatarUrl(request.avatarUrl)
            .setSlugName(request.slugName)
            .setImage(request.image)
            .setOwner(request.owner)
            .setDescription(request.description)
            .setLanguage(request.language)
            .setType(AppType.BATCH)//EXTRA: remove field
            ;
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
