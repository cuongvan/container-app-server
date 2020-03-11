package handlers;

import common.Consts;
import docker.DockerAdapter;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
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
import utils.MyFileUtils;

public class BuildAppHandler {
    
    private DockerAdapter docker;
    private AppInfoDAO appInfoDAO;
    
    private final Logger LOG = LoggerFactory.getLogger(BuildAppHandler.class);
    
    @Inject
    public BuildAppHandler(DockerAdapter docker, AppInfoDAO appInfoDAO) {
        this.docker = docker;
        this.appInfoDAO = appInfoDAO;
    }
    
    public Completable buildApp(String appId, InputStream codeZipFile) {
        return Single
            .fromCallable(() -> appInfoDAO.getById(appId))
            .flatMapCompletable(appInfo -> buildApp(appInfo, codeZipFile));
    }

    private Completable buildApp(AppInfo appInfo, InputStream codeZipFile) throws IOException {
        String templateDir = Paths.get(Consts.DOCKER_BUILD_TEMPLATE_DIR, appInfo.getLanguage().name().toLowerCase()).toString();
        
        return Single
            .fromCallable(() -> createRandomDirAt(Consts.APP_BUILD_DIR))
            .doOnSuccess(dir -> MyFileUtils.unzipStreamToDir(codeZipFile, dir.resolve(Consts.DOCKER_BUILD_EXTRACE_CODE_DIR).toString()))
            .map(Path::toString)
            .doOnSuccess(dir -> MyFileUtils.copyDirectory(templateDir, dir))
            .doOnTerminate(() -> codeZipFile.close())
            .flatMap(dir -> Single
                .fromCallable(() -> docker.buildImage(dir, appInfo.getImage()))
                //.defer(() -> Single.just(docker.buildImage(dir, appInfo.getImage())))
                .doOnSuccess(imageId -> LOG.info("Build image success: {}", imageId))
                //.doOnComplete(() -> MyFileUtils.deleteDirectory(dir))
                //.doOnError(err -> moveBuildFailBuildDir(appInfo, dir))
            )
            
            .flatMapCompletable(ignore -> Completable.complete())
            ;
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
        Path dest = Paths.get(Consts.APP_BUILD_FAILED_DIR, newFolderName);
        MyFileUtils.moveDirectory(dir, dest.toString());
    }
}
