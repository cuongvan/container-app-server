/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import httpserver.common.BasicResponse;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.AppCallInfo;
import common.BatchAppInfo;
import common.SupportLanguage;
import common.Conf;
import common.DBHelper;
import docker.DockerUtils;
import workers.BuildImageWorker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import utils.MFileUtils;
import workers.ScheduleAppWorker;

@Path("/app/new")
public class BuildApp {

    public static class NewAppRequest {
        public String appName;
        public SupportLanguage programmingLanguage;

        public static NewAppRequest fromJson(String jsonString) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            return objectMapper.readValue(jsonString, NewAppRequest.class);
        }
    }

    @Path("/batch/{appId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BasicResponse newBatchApp(
        @PathParam("appId") String appId,
        InputStream codeFile
    ) throws IOException, SQLException
    {
        BatchAppInfo app = DBHelper.retrieveBatchAppInfo(appId);
        if (app == null)
            return new BasicResponse("app_id not found");
        
        // unzip code immediately, in case fileStream closed() after returning response
        java.nio.file.Path tempDir = Files.createTempDirectory(Paths.get(Conf.Inst.APP_BUILD_DIR), "");
        File codeDir = tempDir.resolve("code").toFile();
        codeDir.mkdir();
        MFileUtils.unzipStreamToDir(codeFile, codeDir);
        
        // send build image to executors
        BuildImageWorker.submitBuildTask(app, tempDir.toFile());

        return BasicResponse.OK;
    }

    @Path("/server/{appId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public BasicResponse newServerApp(@PathParam("appId") String appId) throws SQLException
    {
        AppCallInfo.ServerAppCallInfo app = DBHelper.retrieveServerAppInfo(appId);
        ScheduleAppWorker.Instance.submit(
            app,
            () -> DockerUtils.startServerApp(app.image, app.outsidePort, app.imagePort));
        return BasicResponse.OK;
    }
}
