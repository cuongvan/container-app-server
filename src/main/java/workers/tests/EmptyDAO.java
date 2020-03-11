package workers.tests;

import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import java.util.List;

public class EmptyDAO implements AppCallDAO {

    @Override
    public void createNewCall(String callId, String appId, String userId, List<KeyValueParam> keyValueParams, List<FileParam> fileParams) {
    }

    @Override
    public void updateStartedAppCall(String callId, String containerId) {
    }

    @Override
    public void updateFinishedAppCall(AppCallResult result) {
    }
}
