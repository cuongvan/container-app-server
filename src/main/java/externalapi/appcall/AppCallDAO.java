package externalapi.appcall;

import externalapi.appcall.models.BatchAppCallInfo;
import externalapi.appcall.models.BatchAppInfo;
import externalapi.appcall.models.AppCallResult;

public interface AppCallDAO {
    BatchAppInfo getAppInfoByAppId(String appId);
    
    BatchAppCallInfo getCallInfoByCallId(String callId);
    void updateStartedAppCall(String callId, String containerId);
    
    void updateFinishedAppCall(AppCallResult result);
}
