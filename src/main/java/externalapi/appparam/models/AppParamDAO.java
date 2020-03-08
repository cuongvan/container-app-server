/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalapi.appparam.models;

import java.util.List;

/**
 *
 * @author cuong
 */
public interface AppParamDAO {
    void updateParams(String appId, List<AppParam> params);
    List<AppParam> getAppParams(String appId);
}
