package handlers;

import common.Consts;
import externalapi.db.DBConnectionPool;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class ExecuteHandler {
    
    @Inject DockerAdapter docker;
    @Inject DBConnectionPool connectionPool;
    @Inject AppCallDAO dao;

    public Completable execute(String callId, @Nullable byte[] jsonInput, @Nullable byte[] fileInput) {
        byte[] jsonInputNotNull = (jsonInput == null) ? jsonInput : "{}".getBytes();
        byte[] fileInputNotNull = (fileInput == null) ? fileInput : new byte[0];
        
        Path jsonPath = jsonInputPath(callId);
        Path filePath = fileInputPath(callId);
        return Single
            .fromCallable(() -> dao.getCallInfoByCallId(callId)) // check if exists first
            .doOnSuccess(ignore -> Files.write(jsonPath, jsonInputNotNull))
            .doOnSuccess(ignore -> Files.write(filePath, fileInputNotNull))
            .map(callInfo -> {
                Map<String, String> mounts = new HashMap<>();
                mounts.put(jsonPath.toString(), Consts.JSON_MOUNT_PATH);
                mounts.put(filePath.toString(), Consts.FILE_MOUNT_PATH);
                return docker.createAndStartContainer(callInfo.getImageName(), mounts);
            })
            .doOnSuccess(containerId -> dao.updateStartedAppCall(callId, containerId))
            .subscribeOn(Schedulers.io())
            .concatMapCompletable(ignore -> Completable.complete())
            ;
    }
    
    private Path jsonInputPath(String callId) {
        return Paths.get(Consts.APP_INPUT_FILES_DIR, callId + ".json").toAbsolutePath();
    }
    
    private Path fileInputPath(String callId) {
        return Paths.get(Consts.APP_INPUT_FILES_DIR, callId + ".file").toAbsolutePath();
    }
}
