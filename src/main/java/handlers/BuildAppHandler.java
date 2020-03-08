package handlers;

import externalapi.appcall.models.BatchAppInfo;
import common.Consts;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import utils.MyFileUtils;

public class BuildAppHandler {
    
    private DockerAdapter docker;
    private AppCallDAO appInfoDAO;
    
    @Inject
    public BuildAppHandler(DockerAdapter docker, AppCallDAO appInfoDAO) {
        this.docker = docker;
        this.appInfoDAO = appInfoDAO;
    }
    
    public Completable buildApp(String appId, byte[] codeZipFile) {
        return Single
            .fromCallable(() -> appInfoDAO.getAppInfoByAppId(appId))
            .flatMapCompletable(appInfo -> buildApp(appInfo, codeZipFile));
    }

    private Completable buildApp(BatchAppInfo appInfo, byte[] codeZipFile) throws IOException {
        String templateDir = Paths.get("docker_build_files", appInfo.getLanguage().name().toLowerCase()).toString();
        
        return Single
            .fromCallable(() -> createRandomDirAt(Consts.APP_BUILD_DIR))
            .doOnSuccess(dir -> MyFileUtils.unzipBytesToDir(codeZipFile, dir))
            .doOnSuccess(dir -> MyFileUtils.copyDirectory(templateDir, dir))
            .flatMapCompletable(dir -> Completable
                .fromAction(() -> docker.buildImage(dir, appInfo.getImageName()))
                .doOnComplete(() -> MyFileUtils.deleteDirectory(dir))
                .doOnError(err -> moveBuildFailBuildDir(appInfo, dir))
            )
            ;
    }

    
    private String createRandomDirAt(String root) throws IOException {
        Path tempDir = Files.createTempDirectory(Paths.get(root), "");
        {
            File codeDir = tempDir.resolve("code").toFile();
            codeDir.mkdir();
        }
        return tempDir.toString();
    }
    
    private void moveBuildFailBuildDir(BatchAppInfo appInfo, String dir) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String newFolderName = String.format("%s-%s-%s", appInfo.getImageName(), appInfo.getLanguage(), dtf.format(now));
        Path dest = Paths.get(Consts.APP_BUILD_FAILED_DIR, newFolderName);
        MyFileUtils.moveDirectory(dir, dest.toString());
    }
}
