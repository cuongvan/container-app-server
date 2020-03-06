package workers.tests;

import externalapi.AppCallDAO;
import externalapi.models.AppCallResult;
import externalapi.models.BatchAppInfo;

public class EmptyDAO implements AppCallDAO {

    @Override
    public BatchAppInfo getById(String appId) {
        return null;
    }

    @Override
    public void setCallResult(AppCallResult result) {
        
    }
}
