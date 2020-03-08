/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import externalapi.appinfo.models.AppType;
import externalapi.appcall.models.ServerAppCallInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.Main;

/**
 *
 * @author cuong
 */
public class DBHelper {
    public static final String APP_INFO_TABLE = "app_info";
    public static final String APP_CALL_TABLE = "app_call";
    
    public static ServerAppCallInfo retrieveServerAppInfo(String appId) throws SQLException {
        try (
            Connection conn = Main.singletonDBConnectionPool().getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT image, port2port FROM app_info WHERE type = ? AND app_id = ?");
        ){
            stmt.setString(1, AppType.SERVER.name());
            stmt.setString(2, appId);
            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    return null;
                }
                return new ServerAppCallInfo(
                    appId,
                    r.getString("image"),
                    r.getString("port2port")
                );
            }
        }
    }
}
