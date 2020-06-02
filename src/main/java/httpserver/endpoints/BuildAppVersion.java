/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import com.github.slugify.Slugify;
import common.Config;
import common.Constants;
import docker.DockerAdapter;
import externalapi.AppCodeVersion;
import externalapi.AppCodeVersionDB;
import externalapi.BuildStatus;
import externalapi.appinfo.AppDAO;
import externalapi.appinfo.models.AppDetail;
import helpers.MyFileUtils;
import httpserver.common.SuccessResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public SuccessResponse createApp(
        @PathParam("appId") String appId,
        @PathParam("codeVersionId") String codeVersionId) throws IOException, Exception {
        
        buildApp(appId, codeVersionId);
        return SuccessResponse.OK;
    }
    
    private java.nio.file.Path createRandomDirAt(String root) throws IOException {
        java.nio.file.Path tempDir = Files.createTempDirectory(Paths.get(root), "");
        {
            File codeDir = tempDir.resolve("code").toFile().getCanonicalFile();
            codeDir.mkdir();
        }
        return tempDir;
    }

    public void buildApp(String appId, String codeId) {
        try {
            AppDetail appInfo = appInfoDAO.getById(appId);
            AppCodeVersion codeVersion = appCodeVersionDB.getById(codeId);
            
            java.nio.file.Path buildDir = createRandomDirAt(config.dockerBuildDir);
            {
                // unzip code file
                MyFileUtils.unzipStreamToDir(new FileInputStream(codeVersion.codePath), buildDir.resolve("code").toString());
                
                // copy Dockerfile & more
                String templateDir = Paths.get(Constants.DOCKER_BUILD_TEMPLATE_DIR, appInfo.language.name().toLowerCase()).toString();
                MyFileUtils.copyDirectory(templateDir, buildDir.toString());
            }
            String imageName = imageName(appInfo.appName, codeId);
            
            long started = System.currentTimeMillis();
            String imageId = docker.buildImage(buildDir.toString(), imageName);
            long buildTime = System.currentTimeMillis() - started;
            
            appCodeVersionDB.updateBuildSuccess(codeId, imageId, imageName);
            LOG.info("Build app image success, appId = {}, version = {}, time = {} seconds", appId, codeId, buildTime / 1_000);
            FileUtils.forceDelete(buildDir.toFile());
        } catch (IOException | SQLException ex) {
            LOG.warn("Build app image failed, appId = {}, {}", appId, ex);
            ex.printStackTrace();
            try {
                appCodeVersionDB.updateBuildFailure(codeId);
            } catch (SQLException ex1) {
                LOG.info("Failed to update image build status to {}, appId = {}, {}", BuildStatus.BUILD_FAILED, appId, ex);
            }
            
            throw new BuildAppFailedException(ex);
        }
    }
    
    private static String imageName(String appName, String codeId) {
        return new Slugify().slugify(appName) + ":" + codeId;
    }

    private static class BuildAppFailedException extends RuntimeException {

        public BuildAppFailedException(Throwable thrwbl) {
            super(thrwbl);
        }
    }
}
