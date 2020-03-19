package externalapi.appcall;

import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.CallDetail;
import externalapi.appcall.models.CallParam;
import java.util.List;

public interface AppCallDAO {
    void createNewCall(String callId, String appId, String userId, List<CallParam> callParams);
    void updateStartedAppCall(String callId, String containerId);
    void updateFinishedAppCall(AppCallResult result);
    List<String> getAllCallIds();
    CallDetail getById(String callId);
}
