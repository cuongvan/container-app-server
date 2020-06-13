package httpserver.endpoints.execute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.Config;
import common.Constants;
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
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.*;

@Path("/app/{appId}/{codeId}/execute")
@Singleton
public class ExecuteApp {

    public static final String CONTAINER_LABEL_CALL_ID = "ckan.callid";
    public static final String CONTAINER_LABEL_APP_ID = "ckan.appid";
    public static final String CONTAINER_LABEL_CODE_VERSION = "ckan.codeversion";
    
    @Inject private Config config;
    
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
        
        String ckanData = new ObjectMapper().writeValueAsString(containerEnvs(emptyList()));
        
        docker.createAndStartContainer(
            codeVersion.imageId,
            singletonMap("CKANAPP_DATA", ckanData),
            emptyMap(),
            labels(callId, appId, codeId));
        
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

    @Inject private DockerAdapter docker;
    @Inject private AppParamDB appParamDB;
    @Inject private CallParamDB callParamDB;
    @Inject private AppCodeVersionDB appCodeVersionDB;
    @Inject private CallDAO appCallDAO;
    

    private void execute(String appId, String codeId, String userId, String callId, FormDataMultiPart body) throws SQLException, IOException {
        AppCodeVersion codeVersion = appCodeVersionDB.getById(codeId);
        Map<String, AppParam> declaredParams = appParamDB.getParamsByAppIdAsMap(appId);
        
        List<CallParam> actualParams = new ArrayList<>();
        
        Set<String> submittedParams = body.getFields().keySet();
        for (String param : submittedParams) {
            try (InputStream paramValue = body.getField(param).getEntityAs(InputStream.class)) {
                CallParam callParam = new CallParam();
                callParam.name = param;
                callParam.type = declaredParams.get(param).type;
                
                if (callParam.type != InputFieldType.FILE)
                    callParam.value = IOUtils.toString(paramValue, UTF_8);
                else {
                    FileUtils.copyInputStreamToFile(paramValue, inputFilePath(callId, param));
                    callParam.value = inputFilePath(callId, param).toString();
                }
                
                actualParams.add(callParam);
            }
        }
        
        appCallDAO.insertCall(callId, appId, userId);
        callParamDB.insertParams(callId, actualParams);
        
        String ckanData = new ObjectMapper().writeValueAsString(containerEnvs(actualParams));
        
        docker.createAndStartContainer(
            codeVersion.imageId,
            singletonMap("CKANAPP_DATA", ckanData),
            mounts(actualParams),
            labels(callId, appId, codeId));
    }

    private Map<String, String> labels(String callId, String appId, String codeId) {
        Map<String, String> labels = new HashMap<>();
        labels.put(CONTAINER_LABEL_CALL_ID, callId);
        labels.put(CONTAINER_LABEL_APP_ID, appId);
        labels.put(CONTAINER_LABEL_CODE_VERSION, codeId);
        return labels;
    }

    private Map<String, String> mounts(List<CallParam> actualParams) {
        // execute docker
        Map<String, String> mounts = new HashMap<>();
        for (CallParam param : actualParams) {
            if (param.type == InputFieldType.FILE) {
                mounts.put(param.value, fileParamMountPath(param.name));
            }
        }
        return mounts;
    }

    private File inputFilePath(String callId, String param) throws IOException {
        String filename = format("%s-%s", callId, param);
        File writtenFile = new File(config.appInputFilesDir, filename).getCanonicalFile();
        return writtenFile;
    }
    

    private String fileParamMountPath(String paramName) {
        return Paths.get(Constants.CONTAINER_INPUT_FILES_MOUNT_DIR, paramName).toString();
    }
    
    private static class ExecuteResponseSuccess extends SuccessResponse {
        public final String callId;

        public ExecuteResponseSuccess(String callId) {
            this.callId = callId;
        }
    }
    
    private Map<String, Object> containerEnvs(List<CallParam> callParams) throws JsonProcessingException {
        Map<String, Object> envs = new HashMap<>();
        List<ContainerParam> containerParams = callParams.stream().map(ContainerParam::fromInputEntry).collect(toList());
        envs.put("params", containerParams);
        envs.put("ckan_site", "http://192.168.100.10:5000");
        return envs;
    }
    
    // value has real type, not just strings
    private static class ContainerParam {
        public final String name;
        public final String type;
        public final Object value;

        public ContainerParam(String name, InputFieldType type, Object value) {
            this.name = name;
            this.type = type.name().toLowerCase();
            this.value = value;
        }

        public static ContainerParam fromInputEntry(CallParam entry) {
            try {
                return new ContainerParam(entry.name, entry.type, parseValue(entry.type, entry.value));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        private static final ObjectMapper mapper = new ObjectMapper();

        private static Object parseValue(InputFieldType type, String value) throws JsonProcessingException {
            switch (type) {
                case TEXT: return value;
                case TEXT_LIST: return mapper.readValue(value, new TypeReference<List<String>>(){});
                case NUMBER: return Double.parseDouble(value);
                case NUMBER_LIST: return mapper.readValue(value, new TypeReference<List<Double>>() {});
                case BOOLEAN: return Boolean.parseBoolean(value);
                case FILE: return value;
                default: throw new AssertionError(type.name());
            }
        }
    }
}
