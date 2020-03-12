package externalapi.appcall;

import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import java.util.List;

public interface AppCallDAO {
    String /*callId*/ createNewCall(String appId, String userId, List<KeyValueParam> keyValueParams, List<FileParam> fileParams);
    void updateStartedAppCall(String callId, String containerId);
    void updateFinishedAppCall(AppCallResult result);
}
