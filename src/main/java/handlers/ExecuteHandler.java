package handlers;

import common.Constants;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.CallParam;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
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
import static java.util.stream.Collectors.toMap;
import javax.inject.Inject;

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


    public String execute(String appId, String userId, Map<String, byte[]> files) throws IOException, AppNotBuiltYet, AppNotFound {
        AppInfo appInfo = appInfoDAO.getById(appId);
        if (appInfo == null)
            throw new AppNotFound();
        
        if (appInfo.getImageId() == null)
            throw new AppNotBuiltYet();
        
        String callId = MiscHelper.newId();
        
        List<CallParam> callParams = new ArrayList<>();
        for (AppParam appParam : appInfo.getParams()) {
            callParams.add(processParam(callId, appParam, files.get(appParam.getName())));
        }
        
        appCallDAO.createNewCall(callId, appId, userId, callParams);
        
        // execute docker
        Map<String, String> environments = new HashMap<>();
        Map<String, String> mounts = new HashMap<>();;
        
        for (CallParam param : callParams) {
            if (param instanceof KeyValueParam) {
                environments.put(keyValueEnvName((KeyValueParam) param), param.getValue());
            } else if (param instanceof FileParam) {
                mounts.put(((FileParam) param).getFilePath(), param.getValue());
            } else {
                
            }
        }
        
        environments.put("CKAN_HOST", "http://localhost:5000");
        
        Map<String, String> labels = new HashMap<String, String>() {{
            put(Constants.CONTAINER_ID_LABEL_KEY, callId);
        }};
        
        docker.createAndStartContainer(appInfo.getImageId(), environments, mounts, labels);
        return callId;
    }

    private static String keyValueEnvName(KeyValueParam p) {
        return format("%s.%s", Constants.CONTAINER_ENV_TEXT_PARAM_PREFIX, p.getName());
    }
    
    private CallParam processParam(String callId, AppParam appParam, byte[] fileContent) throws IOException {
        switch (appParam.getType()) {
            case TEXT: {
                String value = new String(fileContent);
                return new KeyValueParam(appParam.getName(), value);
            }
            case FILE: {
                String filename = format("%s-%s", callId, appParam.getName());
                Path filePath = Paths.get(Constants.APP_INPUT_FILES_DIR, filename).toAbsolutePath().normalize();

                Files.write(filePath, fileContent);
                return new FileParam(appParam.getName(), filePath.toString());
            }
            default:
                throw new AssertionError(appParam.getType().name());
        }
    }
    
    private String fileParamMountPath(String paramName) {
        return Paths.get(Constants.CONTAINER_INPUT_FILES_MOUNT_DIR, paramName).toString();
    }
}
