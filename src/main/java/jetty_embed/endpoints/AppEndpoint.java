/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jetty_embed.endpoints;

import common.SupportLanguage;
import common.Conf;
import docker.DockerUtils;
import docker.ImageBuildingWorker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import utils.MFileUtils;

@Path("/app")
public class AppEndpoint {
    
    
    public static class NewAppRequest {
        public String appName;
        public SupportLanguage programmingLanguage;
    }
    
    public static class MResponse {
        public String error; // no error means accepted

        public MResponse(String error) {
            this.error = error;
        }
    }
    

    @Path("/new")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public MResponse newApp(
        @FormDataParam("codeFile") InputStream fileStream,
        @FormDataParam("body") FormDataBodyPart jsonRequest) throws IOException
    {
        jsonRequest.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        NewAppRequest request = jsonRequest.getValueAs(NewAppRequest.class);
        
        if (DockerUtils.appExists(request.appName)) {
            return new MResponse("app name already exists");
        }
        
        // BONUS: prevent concurrent creating same app name (HARD)
        
        // unzip code, incase fileStream closed() after returning response
        java.nio.file.Path tempDir = Files.createTempDirectory(Paths.get(Conf.APP_BUILD_DIR), "");
        File codeDir = tempDir.resolve("code").toFile();
        codeDir.mkdir();
        MFileUtils.unzipStreamToDir(fileStream, codeDir);
        
        // send build image to executors
        ImageBuildingWorker.submitBuildTask(request.appName, request.programmingLanguage, tempDir.toFile());
        
        return new MResponse("");
    }
    
    // TODO GET, DELETE
}
