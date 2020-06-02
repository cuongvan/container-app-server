/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import com.github.slugify.Slugify;
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
    
    @Inject private DockerAdapter docker;
    @Inject private AppDAO appInfoDAO;
    @Inject private AppCodeVersionDB appCodeVersionDB;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public SuccessResponse createApp(
        @PathParam("appId") String appId,
        @PathParam("codeVersionId") String codeVersionId) throws IOException, Exception {
        
        System.out.println("code version: " + codeVersionId);
        buildApp(appId, codeVersionId);
        return SuccessResponse.OK;
    }
    
    private java.nio.file.Path createRandomDirAt(String root) throws IOException {
        System.out.println("===== cwd " + Paths.get(".").toAbsolutePath());
        System.out.println("=======================" + Paths.get(root).toAbsolutePath());
        java.nio.file.Path parentPath = Paths.get(root).toAbsolutePath().normalize();
        java.nio.file.Path tempDir = Files.createTempDirectory(parentPath, "");
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
            
            java.nio.file.Path buildDir = createRandomDirAt(Constants.DOCKER_BUILD_DIR);
            {
                // unzip code file
                MyFileUtils.unzipStreamToDir(new FileInputStream(codeVersion.codePath), buildDir.resolve(Constants.DOCKER_BUILD_EXTRACE_CODE_DIR).toString());
                
                // copy Dockerfile & more
                String templateDir = Paths.get(Constants.DOCKER_BUILD_TEMPLATE_DIR, appInfo.language.name().toLowerCase()).toString();
                MyFileUtils.copyDirectory(templateDir, buildDir.toString());
            }
            String imageName = imageName(appInfo.appName, codeId);
            
            LOG.info("build started");
            String imageId = docker.buildImage(buildDir.toString(), imageName);
            LOG.info("build done, imageId = {}", imageId);
            
            appCodeVersionDB.updateBuildSuccess(codeId, imageId, imageName);
            LOG.info("Build app image failed success, appId = {}, version = {}", appId, codeId);
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
