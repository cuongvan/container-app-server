package handlers;

import common.Constants;
import docker.DockerAdapter;
import externalapi.appinfo.AppDAO;
import externalapi.appinfo.models.AppDetail;
import externalapi.appinfo.models.AppStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import helpers.MyFileUtils;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;

@Singleton
public class BuildAppHandler {

    @Inject    
    private DockerAdapter docker;
    
    @Inject
    private AppDAO appInfoDAO;
    
    private final Logger LOG = LoggerFactory.getLogger(BuildAppHandler.class);
    
    public void buildApp(String appId, byte[] codeZipFile) {
        try {
            AppDetail appInfo = appInfoDAO.getById(appId);
            String templateDir = Paths.get(Constants.DOCKER_BUILD_TEMPLATE_DIR, appInfo.getLanguage().name().toLowerCase()).toString();
            Path dir = createRandomDirAt(Constants.DOCKER_BUILD_DIR);
            try {
                MyFileUtils.unzipBytesToDir(codeZipFile, dir.resolve(Constants.DOCKER_BUILD_EXTRACE_CODE_DIR).toString());
                MyFileUtils.copyDirectory(templateDir, dir.toString());
                String imageId = docker.buildImage(dir.toString(), appInfo.getImage());
                updateAppBuildDone(appId, imageId);
                LOG.info("Image built: " + imageId);
            } finally {
                FileUtils.forceDelete(dir.toFile());
            }
        } catch (Exception ex) {
            LOG.info("Build app image failed, appId = {}, {}", appId, ex);
            ex.printStackTrace();
            updateAppBuildFailed(appId);
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
    
    private void updateAppBuildDone(String appId, String imageId) {
        appInfoDAO.updateImageId(appId, imageId, AppStatus.BUILD_DONE);
    }
    
    private void updateAppBuildFailed(String appId) {
        appInfoDAO.updateImageId(appId, null, AppStatus.BUILD_FAILED);
    }
}
