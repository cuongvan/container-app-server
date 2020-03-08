/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalapi.appcall.models;

import externalapi.appinfo.models.SupportLanguage;

/**
 *
 * @author cuong
 */
public class BatchAppInfo {
    private final String appId;
    private final String imageName;
    private final SupportLanguage language;

    public BatchAppInfo(String appId, String imageName, SupportLanguage language) {
        this.appId = appId;
        this.imageName = imageName;
        this.language = language;
    }

    public String getAppId() {
        return appId;
    }

    public String getImageName() {
        return imageName;
    }

    public SupportLanguage getLanguage() {
        return language;
    }
}
