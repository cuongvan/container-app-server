package handlers;

import common.Constants;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.CallParam;
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
            switch (param.type) {
                case TEXT:
                    environments.put(keyValueEnvName(param.name), param.value);
                    break;
                case NUMBER:
                    break;
                case FILE:
                    mounts.put(param.value, fileParamMountPath(param.name));
                    break;
                default:
                    throw new AssertionError(param.type.name());
                
            }
        }
        
        environments.put("CKAN_HOST", "http://localhost:5000");
        
        Map<String, String> labels = new HashMap<String, String>() {{
            put(Constants.CONTAINER_ID_LABEL_KEY, callId);
        }};
        
        docker.createAndStartContainer(appInfo.getImageId(), environments, mounts, labels);
        return callId;
    }

    private static String keyValueEnvName(String paramName) {
        return format("%s.%s", Constants.CONTAINER_ENV_TEXT_PARAM_PREFIX, paramName);
    }
    
    private CallParam processParam(String callId, AppParam appParam, byte[] fileContent) throws IOException {
        String value = "";
        switch (appParam.getType()) {
            case TEXT:
            case NUMBER:
            {
                value = new String(fileContent);
                break;
            }
            case FILE: {
                String filename = format("%s-%s", callId, appParam.getName());
                Path filePath = Paths.get(Constants.APP_INPUT_FILES_DIR, filename).toAbsolutePath().normalize();
                Files.write(filePath, fileContent);
                value = filePath.toString();
                break;
            }
            default:
                value = new String(fileContent);
                //throw new AssertionError(appParam.getType().name());
        }
        
        return new CallParam(appParam.getType(), appParam.getName(), value);
    }
    
    private String fileParamMountPath(String paramName) {
        return Paths.get(Constants.CONTAINER_INPUT_FILES_MOUNT_DIR, paramName).toString();
    }
}
