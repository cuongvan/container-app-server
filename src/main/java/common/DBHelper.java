/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author cuong
 */
public class DBHelper {
    public static final String APP_INFO_TABLE = "app_info";
    public static final String APP_CALL_TABLE = "app_call";
    
    public static BatchAppInfo retrieveBatchAppInfo(String appId) throws SQLException {
        String query = "SELECT image, language FROM app_info WHERE type = ? AND app_id = ?";
        try (
            Connection conn = DBConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ){
            stmt.setString(1, AppType.Batch.name());
            stmt.setString(2, appId);
            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    return null;
                } else {
                    return new BatchAppInfo(
                        appId,
                        r.getString("image"),
                        SupportLanguage.valueOf(r.getString("language"))
                    );
                }
            }
        }
    }
    
    public static AppCallInfo.ServerAppCallInfo retrieveServerAppInfo(String appId) throws SQLException {
        try (
            Connection conn = DBConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT image, port2port FROM app_info WHERE type = ? AND app_id = ?");
        ){
            stmt.setString(1, AppType.Server.name());
            stmt.setString(2, appId);
            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    return null;
                }
                return new AppCallInfo.ServerAppCallInfo(
                    appId,
                    r.getString("image"),
                    r.getString("port2port")
                );
            }
        }
    }
    
    public static AppCallInfo.BatchAppCallInfo retrieveBatchCallInfo(String callId) throws SQLException {
        try (
            Connection conn = DBConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT image, json_input, binary_input FROM app_info, app_call "
                    + "WHERE call_id = ? AND app_info.app_id = app_call.app_id");
        ){
//            stmt.setString(1, DBHelper.APP_INFO_TABLE);
//            stmt.setString(2, DBHelper.APP_CALL_TABLE);
            stmt.setString(1, callId);
            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    throw new IllegalStateException("call_id not found in app_info");
                }
                return new AppCallInfo.BatchAppCallInfo(
                    callId,
                    r.getString("image"),
                    r.getBoolean("json_input"),
                    r.getBoolean("binary_input")
                );
            }
        }
    }
}
