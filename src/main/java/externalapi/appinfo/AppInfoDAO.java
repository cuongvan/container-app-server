package externalapi.appinfo;

import externalapi.appinfo.models.AppInfo;

public interface AppInfoDAO {
    void createApp(AppInfo app);
    AppInfo getById(String appId);
    void deleteById(String appId);
}
