/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import com.github.slugify.Slugify;
import common.Config;
import docker.DockerAdapter;
import externalapi.AppCodeVersion;
import externalapi.AppCodeVersionDB;
import externalapi.BuildStatus;
import externalapi.appinfo.AppDAO;
import externalapi.appinfo.models.AppDetail;
import externalapi.appinfo.models.AppLanguage;
import helpers.MyFileUtils;
import httpserver.common.BaseResponse;
import httpserver.common.FailedResponse;
import httpserver.common.SuccessResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/app/{appId}/{codeVersionId}/build")
@Singleton
public class BuildAppVersion {

    private static final Logger LOG = LoggerFactory.getLogger(BuildAppVersion.class);
    
    @Inject private Config config;
    @Inject private DockerAdapter docker;
    @Inject private AppDAO appInfoDAO;
    @Inject private AppCodeVersionDB appCodeVersionDB;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public BaseResponse createApp(
        @PathParam("appId") String appId,
        @PathParam("codeVersionId") String codeVersionId) throws IOException, Exception {
        
        try {
            buildApp(appId, codeVersionId);
            return SuccessResponse.OK;
        } catch (Exception e) {
            System.out.println("message " + e.getMessage());
            return new FailedResponse(e.getMessage());
        }
    }
    

    public void buildApp(String appId, String codeId) {
        try {
            AppDetail appInfo = appInfoDAO.getById(appId);
            AppCodeVersion codeVersion = appCodeVersionDB.getById(codeId);
            
            File buildDir = new File(config.dockerBuildDir, codeVersion.codeId);
            buildDir.mkdir();
            
            // unzip code file
            MyFileUtils.unzipStreamToDir(new FileInputStream(codeVersion.codePath), buildDir);

            // copy Dockerfile & template files
            FileUtils.copyDirectory(templateDirNameFor(appInfo.language), buildDir);
            String imageName = imageName(appInfo.appName, codeId);
            
            long started = System.currentTimeMillis();
            StringBuilder buildLog = new StringBuilder();
            String imageId = null;
            try {
                imageId = docker.buildImage(buildDir.toString(), imageName, buildLog);
            } catch (Exception e) {
                LOG.warn("build failed, build log:\n{}", buildLog.toString());
                throw new BuildAppFailedException(e.getMessage(), e);
            }
            long buildTime = System.currentTimeMillis() - started;
            
            
            appCodeVersionDB.updateBuildSuccess(codeId, imageId, imageName);
            LOG.info("Build app image success, appId = {}, version = {}, time = {} seconds", appId, codeId, buildTime / 1_000);
            FileUtils.forceDelete(buildDir);
        } catch (IOException | SQLException ex) {
            LOG.warn("Build app image failed, appId = {}, {}", appId, ex);
            ex.printStackTrace();
            try {
                appCodeVersionDB.updateBuildFailure(codeId);
            } catch (SQLException ex1) {
                LOG.info("Failed to update image build status to {}, appId = {}, {}", BuildStatus.BUILD_FAILED, appId, ex);
            }
            
            throw new BuildAppFailedException(ex.getMessage(), ex);
        }
    }
    
    
    private File templateDirNameFor(AppLanguage language) {
        return new File(config.dockerBuildTemplateDir, language.name().toLowerCase());
    }
    
    private static String imageName(String appName, String codeId) {
        return new Slugify().slugify(appName) + ":" + codeId;
    }

    private static class BuildAppFailedException extends RuntimeException {

        public BuildAppFailedException(String string, Throwable thrwbl) {
            super(string, thrwbl);
        }

        public BuildAppFailedException(Throwable thrwbl) {
            super(thrwbl);
        }
    }
}
