package externalapi.appparam;

import externalapi.DBConnectionPool;
import externalapi.appinfo.models.InputFieldType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class AppParamDB {
    @Inject private DBConnectionPool dbConnPool;

    public List<AppParam> getParamsByAppIdAsList(String appId) throws SQLException {
        try (
            Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT name, type, label, description FROM app_param WHERE app_id = ?"))
        {
            stmt.setString(1, appId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<AppParam> result = new ArrayList<>();
                while (rs.next()) {
                    AppParam param = new AppParam();
                    param.appId = appId;
                    param.name = rs.getString("name");
                    param.type = InputFieldType.valueOf(rs.getString("type"));
                    param.label = rs.getString("label");
                    param.description = rs.getString("description");
                    
                    result.add(param);
                }
                return result;
            }
        }
    }
    
    public Map<String, AppParam> getParamsByAppIdAsMap(String appId) throws SQLException {
        try (
            Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT name, type, label, description FROM app_param WHERE app_id = ?"))
        {
            stmt.setString(1, appId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String /*param name*/, AppParam> result = new HashMap<>();
                while (rs.next()) {
                    AppParam param = new AppParam();
                    param.appId = appId;
                    param.name = rs.getString("name");
                    param.type = InputFieldType.valueOf(rs.getString("type"));
                    param.label = rs.getString("label");
                    param.description = rs.getString("description");
                    
                    result.put(param.name, param);
                }
                return result;
            }
        }
    }
}
