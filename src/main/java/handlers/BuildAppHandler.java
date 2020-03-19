package handlers;

import common.Constants;
import docker.DockerAdapter;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppStatus;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import helpers.MyFileUtils;
import javax.inject.Singleton;

@Singleton
public class BuildAppHandler {

    @Inject    
    private DockerAdapter docker;
    
    @Inject
    private AppInfoDAO appInfoDAO;
    
    private final Logger LOG = LoggerFactory.getLogger(BuildAppHandler.class);
    
    public void buildApp(String appId, byte[] codeZipFile) {
        try {
            AppInfo appInfo = appInfoDAO.getById(appId);
            String templateDir = Paths.get(Constants.DOCKER_BUILD_TEMPLATE_DIR, appInfo.getLanguage().name().toLowerCase()).toString();
            Path dir = createRandomDirAt(Constants.DOCKER_BUILD_DIR);
            MyFileUtils.unzipBytesToDir(codeZipFile, dir.resolve(Constants.DOCKER_BUILD_EXTRACE_CODE_DIR).toString());
            MyFileUtils.copyDirectory(templateDir, dir.toString());
            String imageId = docker.buildImage(dir.toString(), appInfo.getImage());
            updateAppBuildDone(appId, imageId);
            LOG.info("Image built: " + imageId);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.info("Build app image failed, appId = {}, {}", appId, ex);
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
    
    private void moveBuildFailBuildDir(AppInfo appInfo, String dir) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String newFolderName = String.format("%s-%s-%s", appInfo.getImage(), appInfo.getLanguage(), dtf.format(now));
        Path dest = Paths.get(Constants.DOCKER_BUILD_FAILED_DIR, newFolderName);
        MyFileUtils.moveDirectory(dir, dest.toString());
    }
    
     private void updateAppBuildDone(String appId, String imageId) {
        appInfoDAO.updateImageId(appId, imageId, AppStatus.BUILD_DONE);
    }
    
    private void updateAppBuildFailed(String appId) {
        appInfoDAO.updateImageId(appId, null, AppStatus.BUILD_FAILED);
    }
}
