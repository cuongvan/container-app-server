package handlers;

import common.Consts;
import externalapi.appparam.DBConnectionPool;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.models.AppParam;
import externalapi.appparam.AppParamDAO;
import externalapi.appparam.models.ParamType;
import io.reactivex.rxjava3.core.Completable;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import helpers.MiscHelper;

public class ExecuteHandler {
    
    @Inject DockerAdapter docker;
    @Inject AppInfoDAO appInfoDAO;
    @Inject AppCallDAO appCallDAO;
    @Inject AppParamDAO appParamDAO;

    public ExecuteHandler(DockerAdapter docker, DBConnectionPool connectionPool, AppInfoDAO appInfoDAO, AppCallDAO appCallDAO, AppParamDAO appParamDAO) {
        this.docker = docker;
        this.appInfoDAO = appInfoDAO;
        this.appCallDAO = appCallDAO;
        this.appParamDAO = appParamDAO;
    }

    public String execute(String appId, String userId, Map<String, FormDataBodyPart> files) throws IOException {
        AppInfo appInfo = appInfoDAO.getById(appId);
        String image = appInfo.getImage();
        if (image == null) {
            
        }
        
        List<AppParam> appParams = appParamDAO.getAppParams(appId);

        List<KeyValueParam> keyValueParams = appParams
            .stream()
            .filter(p -> p.getType() == ParamType.KEY_VALUE)
            .map(p -> processKeyValueParam(p, files.get(p.getName())))
            .collect(toList());
            
        List<FileParam> fileParams = appParams
            .stream()
            .filter(p -> p.getType() == ParamType.FILE)
            .map(p -> processFileParam("aabbccdd", p, files.get(p.getName())))
            .collect(toList());
        
        String callId = MiscHelper.newId();
        
        appCallDAO.createNewCall(callId, appId, userId, keyValueParams, fileParams);
        
        // execute docker
        Map<String, String> environments = keyValueParams
            .stream()
            .collect(toMap(
                KeyValueParam::getName,
                KeyValueParam::getValue
            ));
        environments.put("CKAN_HOST", "http://localhost:5000");
        
        Map<String, String> mounts = fileParams
            .stream()
            .collect(toMap(
                FileParam::getFilePath,
                p -> fileParamMountPath(p.getName())
            ));
        
        String imageName = appInfo.getImage();
        docker.createAndStartContainer(imageName, environments, mounts);
        return callId;
    }
    
    private KeyValueParam processKeyValueParam(AppParam appParam, FormDataBodyPart bodyPart) {
        String value = bodyPart.getValue();
        bodyPart.cleanup();
        return new KeyValueParam(appParam.getName(), value);
    }
    private FileParam processFileParam(String callId, AppParam appParam, FormDataBodyPart bodyPart) {
        String filename = format("%s-%s", callId, appParam.getName());
        Path filePath = Paths.get(Consts.APP_INPUT_FILES_DIR, filename);
        
        try (InputStream fileStream = bodyPart.getEntityAs(InputStream.class)) {
            Files.copy(fileStream, filePath);
            return new FileParam(appParam.getName(), filePath.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            bodyPart.cleanup();
        }
    }
    
    private String fileParamMountPath(String paramName) {
        return Paths.get(Consts.FILES_MOUNT_DIR, paramName).toString();
    }

    public Completable execute(String callId, @Nullable byte[] jsonInput, @Nullable byte[] fileInput) {
        byte[] jsonInputNotNull = (jsonInput == null) ? jsonInput : "{}".getBytes();
        byte[] fileInputNotNull = (fileInput == null) ? fileInput : new byte[0];
        
        Path jsonPath = jsonInputPath(callId);
        Path filePath = fileInputPath(callId);
        return null;
//        return Single
//            .fromCallable(() -> dao.getCallInfoByCallId(callId)) // check if exists first
//            .doOnSuccess(ignore -> Files.write(jsonPath, jsonInputNotNull))
//            .doOnSuccess(ignore -> Files.write(filePath, fileInputNotNull))
//            .map(callInfo -> {
//                Map<String, String> mounts = new HashMap<>();
//                mounts.put(jsonPath.toString(), Consts.JSON_MOUNT_PATH);
//                mounts.put(filePath.toString(), Consts.FILE_MOUNT_PATH);
//                return docker.createAndStartContainer(callInfo.getImageName(), mounts);
//            })
//            .doOnSuccess(containerId -> dao.updateStartedAppCall(callId, containerId))
//            .subscribeOn(Schedulers.io())
//            .concatMapCompletable(ignore -> Completable.complete())
//            ;
    }
    
    private Path jsonInputPath(String callId) {
        return Paths.get(Consts.APP_INPUT_FILES_DIR, callId + ".json").toAbsolutePath();
    }
    
    private Path fileInputPath(String callId) {
        return Paths.get(Consts.APP_INPUT_FILES_DIR, callId + ".file").toAbsolutePath();
    }
}
