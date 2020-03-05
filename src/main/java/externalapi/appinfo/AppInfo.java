package externalapi.appinfo;

import common.SupportLanguage;

public class AppInfo {
    public final String appId;
    public final String imageName;
    public final SupportLanguage language;

    public AppInfo(String appId, String imageName, SupportLanguage language) {
        this.appId = appId;
        this.imageName = imageName;
        this.language = language;
    }
}
