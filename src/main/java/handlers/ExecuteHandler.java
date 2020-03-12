package handlers;

import common.Consts;
import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.models.AppParam;
import externalapi.appparam.AppParamDAO;
import externalapi.appparam.models.ParamType;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import javax.inject.Inject;

public class ExecuteHandler {
    
    private DockerAdapter docker;
    private AppInfoDAO appInfoDAO;
    private AppCallDAO appCallDAO;
    private AppParamDAO appParamDAO;

    @Inject
    public ExecuteHandler(DockerAdapter docker, AppInfoDAO appInfoDAO, AppCallDAO appCallDAO, AppParamDAO appParamDAO) {
        this.docker = docker;
        System.out.println("dao: " + appInfoDAO);
        this.appInfoDAO = appInfoDAO;
        this.appCallDAO = appCallDAO;
        this.appParamDAO = appParamDAO;
    }


    public String execute(String appId, String userId, Map<String, byte[]> files) throws IOException {
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
            
        List<FileParam> fileParams = new ArrayList<>();
        for (AppParam appParam : appParams) {
            if (appParam.getType() == ParamType.FILE)
                fileParams.add(processFileParam("aabbccdd", appParam, files.get(appParam.getName())));
        }
        
        String callId = appCallDAO.createNewCall(appId, userId, keyValueParams, fileParams);
        
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
    
    private KeyValueParam processKeyValueParam(AppParam appParam, byte[] fileContent) {
        String value = new String(fileContent);
        return new KeyValueParam(appParam.getName(), value);
    }
    private FileParam processFileParam(String callId, AppParam appParam, byte[] fileContent) throws IOException {
        String filename = format("%s-%s", callId, appParam.getName());
        Path filePath = Paths.get(Consts.APP_INPUT_FILES_DIR, filename);
        
        Files.write(filePath, fileContent);
        return new FileParam(appParam.getName(), filePath.toString());
    }
    
    private String fileParamMountPath(String paramName) {
        return Paths.get(Consts.FILES_MOUNT_DIR, paramName).toString();
    }
}
