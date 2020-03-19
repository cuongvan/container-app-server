package handlers;

import common.Constants;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.ParamType;
import helpers.MiscHelper;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import javax.inject.Inject;
import notify.Notifier;

public class ExecuteHandler {
    
    private DockerAdapter docker;
    private AppInfoDAO appInfoDAO;
    private AppCallDAO appCallDAO;

    @Inject
    public ExecuteHandler(DockerAdapter docker, AppInfoDAO appInfoDAO, AppCallDAO appCallDAO) {
        this.docker = docker;
        this.appInfoDAO = appInfoDAO;
        this.appCallDAO = appCallDAO;
    }


    public String execute(String appId, String userId, Map<String, byte[]> files) throws IOException, AppNotBuiltYet {
        AppInfo appInfo = appInfoDAO.getById(appId);
        if (appInfo.getImageId() == null)
            throw new AppNotBuiltYet();
        
        String callId = MiscHelper.newId();
        
        List<KeyValueParam> keyValueParams = appInfo.getParams()
            .stream()
            .filter(p -> p.getType() == ParamType.KEY_VALUE)
            .map(p -> processKeyValueParam(p, files.get(p.getName())))
            .collect(toList());
            
        List<FileParam> fileParams = new ArrayList<>();
        for (AppParam appParam : appInfo.getParams()) {
            if (appParam.getType() == ParamType.FILE)
                fileParams.add(processFileParam(callId, appParam, files.get(appParam.getName())));
        }
        
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
        
        Map<String, String> labels = new HashMap<String, String>() {{
            put(Constants.CONTAINER_ID_LABEL_KEY, callId);
        }};
        
        docker.createAndStartContainer(appInfo.getImageId(), environments, mounts, labels);
        return callId;
    }
    
    private KeyValueParam processKeyValueParam(AppParam appParam, byte[] fileContent) {
        String value = new String(fileContent);
        return new KeyValueParam(appParam.getName(), value);
    }
    private FileParam processFileParam(String callId, AppParam appParam, byte[] fileContent) throws IOException {
        String filename = format("%s-%s", callId, appParam.getName());
        Path filePath = Paths.get(Constants.APP_INPUT_FILES_DIR, filename).toAbsolutePath().normalize();
        
        Files.write(filePath, fileContent);
        return new FileParam(appParam.getName(), filePath.toString());
    }
    
    private String fileParamMountPath(String paramName) {
        return Paths.get(Constants.CONTAINER_INPUT_FILES_MOUNT_DIR, paramName).toString();
    }
}
