package externalapi;

import externalapi.models.BatchAppInfo;
import externalapi.models.AppCallResult;

public interface AppCallDAO {
    BatchAppInfo getById(String appId);
    void setCallResult(AppCallResult result);
}
