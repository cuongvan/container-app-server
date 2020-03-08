package externalapi.appcall;

import externalapi.appcall.models.BatchAppCallInfo;
import externalapi.appcall.models.AppCallResult;

public interface AppCallDAO {
    BatchAppCallInfo getCallInfoByCallId(String callId);
    void updateStartedAppCall(String callId, String containerId);
    void updateFinishedAppCall(AppCallResult result);
}
