package externalapi.appinfo;

import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
import java.util.List;

public interface AppInfoDAO {
    String /*appId*/ createApp(AppInfo app);
    AppInfo getById(String appId);
    void deleteById(String appId);
    
    void updateImageId(String appId, String imageId);
    
    void updateParams(String appId, List<AppParam> params);
    List<AppParam> getAppParams(String appId);
}
