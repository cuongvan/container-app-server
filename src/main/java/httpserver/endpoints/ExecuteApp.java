package httpserver.endpoints;

import common.Config;
import docker.DockerAdapter;
import externalapi.AppCodeVersion;
import externalapi.AppCodeVersionDB;
import externalapi.appcall.CallDAO;
import externalapi.callparam.CallParam;
import externalapi.appparam.AppParam;
import externalapi.appinfo.models.InputFieldType;
import externalapi.appparam.AppParamDB;
import externalapi.callparam.CallParamDB;
import helpers.MiscHelper;
import httpserver.common.SuccessResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import static java.util.Collections.emptyList;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.*;

@Path("/app/{appId}/{codeId}/execute")
@Singleton
public class ExecuteApp {

    public static final String CALL_ID_LABEL = "ckan.callid";
    public static final String APP_ID_LABEL = "ckan.appid";
    public static final String CODE_VERSION_LABEL = "ckan.codeversion";
    
    @Inject private Config config;
    @Inject private CheckAndStartDockerContainerThread startContainerThread;
    
    @Inject private DockerAdapter docker;
    @Inject private AppParamDB appParamDB;
    @Inject private CallParamDB callParamDB;
    @Inject private AppCodeVersionDB appCodeVersionDB;
    @Inject private CallDAO appCallDAO;
    
    @Path("/empty")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public ExecuteResponseSuccess executeNoParams(
        @PathParam("appId") String appId,
        @PathParam("codeId") String codeId,
        @QueryParam("userId") String userId
    ) throws IOException, SQLException {
        String callId = MiscHelper.newId();
        AppCodeVersion codeVersion = appCodeVersionDB.getById(codeId);
        
        appCallDAO.insertCall(callId, appId, userId);
        
        submitTaskToScheduler(appId, codeId, callId, codeVersion.imageId, emptyList());
        
        return new ExecuteResponseSuccess(callId);
    }
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ExecuteResponseSuccess executeWithParams(
        @PathParam("appId") String appId,
        @PathParam("codeId") String codeId,
        @QueryParam("userId") String userId,
        FormDataMultiPart body
    ) throws IOException, SQLException {
        String callId = MiscHelper.newId();
        execute(appId, codeId, userId, callId, body);
        return new ExecuteResponseSuccess(callId);
    }

    

    private void execute(String appId, String codeId, String userId, String callId, FormDataMultiPart body) throws SQLException, IOException {
        AppCodeVersion codeVersion = appCodeVersionDB.getById(codeId);
        Map<String, AppParam> declaredParams = appParamDB.getParamsByAppIdAsMap(appId);
        
        List<CallParam> inputParams = new ArrayList<>();
        
        Set<String> submittedParams = body.getFields().keySet();
        for (String param : submittedParams) {
            try (InputStream paramValue = body.getField(param).getEntityAs(InputStream.class)) {
                CallParam callParam = new CallParam();
                callParam.name = param;
                callParam.type = declaredParams.get(param).type;
                
                File inputFileOnDisk = inputParamFile(callId, param);
                if (callParam.type != InputFieldType.FILE) {
                    callParam.value = IOUtils.toString(paramValue, UTF_8);
                    FileUtils.writeStringToFile(inputFileOnDisk, callParam.value, UTF_8);
                } else {
                    FileUtils.copyInputStreamToFile(paramValue, inputFileOnDisk);
                    callParam.value = inputFileOnDisk.toString();
                }
                
                inputParams.add(callParam);
            }
        }
        
        appCallDAO.insertCall(callId, appId, userId);
        callParamDB.insertParams(callId, inputParams);
        
        
        submitTaskToScheduler(appId, codeId, callId, codeVersion.imageId, inputParams);
    }
    
    private void submitTaskToScheduler(
        String appId, String codeId, String callId, 
        String imageId, List<CallParam> inputParams) throws IOException
    {
        Map<String, String> mounts = mounts(callId, inputParams);
        startContainerThread.submitTask(()-> docker.createAndStartContainer(
            imageId,
            containerEnvs(),
            mounts,
            labels(callId, appId, codeId)));
    }

    private Map<String, String> labels(String callId, String appId, String codeId) {
        Map<String, String> labels = new HashMap<>();
        labels.put(CALL_ID_LABEL, callId);
        labels.put(APP_ID_LABEL, appId);
        labels.put(CODE_VERSION_LABEL, codeId);
        return labels;
    }

    /**
     * Mount mọi file input, kể cả kiểu FILE và kiểu khác
     * Mục đích: để app dễ đọc input hơn mà không cần dùng tới thư viện
     * Vấn đề: sinh ra nhiều file trong thư mục lưu trữ
     */
    private Map<String, String> mounts(String callId, List<CallParam> actualParams) throws IOException {
        // execute docker
        Map<String, String> mounts = new HashMap<>();
        for (CallParam param : actualParams)
            mounts.put(inputParamFile(callId, param.name).toString(), fileParamMountPath(param.name));
        return mounts;
    }

    private File inputParamFile(String callId, String param) throws IOException {
        String filename = format("%s-%s", callId, param);
        File writtenFile = new File(config.appInputFilesDir, filename).getCanonicalFile();
        return writtenFile;
    }
    

    private static String fileParamMountPath(String fileParamName) {
        return Paths.get(ContainerPaths.INPUT_FILES_MOUNT_DIR, fileParamName).toString();
    }
    
    private static class ExecuteResponseSuccess extends SuccessResponse {
        public final String callId;

        public ExecuteResponseSuccess(String callId) {
            this.callId = callId;
        }
    }
    
    
    private Map<String, String> containerEnvs() {
        Map<String, String> envs = new HashMap<>();
        envs.put("CKAN_HOST", config.ckanHost);
        return envs;
    }
}
