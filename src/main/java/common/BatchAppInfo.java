/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author cuong
 */
public class BatchAppInfo {
    public final String appId;
    public final String imageName;
    public final SupportLanguage language;

    public BatchAppInfo(String appId, String imageName, SupportLanguage language) {
        this.appId = appId;
        this.imageName = imageName;
        this.language = language;
    }
}
