package handlers;

import common.Constants;
import docker.DockerAdapter;
import externalapi.appinfo.AppDAO;
import externalapi.appinfo.models.AppDetail;
import externalapi.appinfo.models.AppStatus;
import externalapi.appinfo.models.SupportLanguage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import helpers.MyFileUtils;
import java.sql.SQLException;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;

@Singleton
public class BuildAppHandler {

    @Inject    
    private DockerAdapter docker;
    
    @Inject
    private AppDAO appInfoDAO;
    
    private final Logger LOG = LoggerFactory.getLogger(BuildAppHandler.class);
    
    public void buildApp(String appId, SupportLanguage language, byte[] codeZipFile) {
        try {
            AppDetail appInfo = appInfoDAO.getById(appId);
            String templateDir = Paths.get(Constants.DOCKER_BUILD_TEMPLATE_DIR, appInfo.getLanguage().name().toLowerCase()).toString();
            Path buildDir = createRandomDirAt(Constants.DOCKER_BUILD_DIR);
            MyFileUtils.unzipBytesToDir(codeZipFile, buildDir.resolve(Constants.DOCKER_BUILD_EXTRACE_CODE_DIR).toString());
            MyFileUtils.copyDirectory(templateDir, buildDir.toString());
            String imageId = docker.buildImage(buildDir.toString(), appInfo.getImage());
            updateAppBuildDone(appId, imageId);
            LOG.info("Image built: " + imageId);
            FileUtils.forceDelete(buildDir.toFile());
        } catch (Exception ex) {
            LOG.info("Build app image failed, appId = {}, {}", appId, ex);
            ex.printStackTrace();
            try {
                updateAppBuildFailed(appId);
            } catch (SQLException ex1) {
                LOG.info("Failed to update image build status to {}, appId = {}, {}", AppStatus.BUILD_FAILED, appId, ex);
                ex.printStackTrace();
            }
        }
    }

    private Path createRandomDirAt(String root) throws IOException {
        Path tempDir = Files.createTempDirectory(Paths.get(root), "");
        {
            File codeDir = tempDir.resolve("code").toFile();
            codeDir.mkdir();
        }
        return tempDir;
    }
    
    private void updateAppBuildDone(String appId, String imageId) throws SQLException {
        appInfoDAO.updateImageId(appId, imageId, AppStatus.BUILD_DONE);
    }
    
    private void updateAppBuildFailed(String appId) throws SQLException {
        appInfoDAO.updateImageId(appId, null, AppStatus.BUILD_FAILED);
    }
}
