package externalapi.appinfo;

import common.BatchAppInfo;

public interface BatchAppInfoDAO {
    BatchAppInfo getById(String appId);
}
