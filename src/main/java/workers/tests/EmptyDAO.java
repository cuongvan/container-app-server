package workers.tests;

import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.BatchAppCallInfo;

public class EmptyDAO implements AppCallDAO {
    @Override
    public void updateFinishedAppCall(AppCallResult result) {
        
    }

    @Override
    public BatchAppCallInfo getCallInfoByCallId(String callId) {
        return null;
    }

    @Override
    public void updateStartedAppCall(String callId, String containerId) {
    }
}
