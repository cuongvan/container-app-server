package externalapi.appinfo;

import externalapi.appinfo.models.AppInfo;

public interface AppInfoDAO {
    String /*appId*/ createApp(AppInfo app);
    AppInfo getById(String appId);
    void deleteById(String appId);
}
