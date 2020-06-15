package externalapi.appinfo;

import externalapi.appinfo.models.AppDetail;
import externalapi.BuildStatus;
import externalapi.appinfo.models.AppLanguage;
import externalapi.DBConnectionPool;
import externalapi.appinfo.models.AppStatus;
import handlers.exceptions.AppNotFound;
import java.sql.*;
import javax.inject.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AppDAO {
    @Inject private DBConnectionPool dbConnPool;
    
    public AppDetail getById(String appId) throws SQLException {
        try (
            Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT app_name, language, app_status FROM app_info WHERE app_id = ?"))
        {
            stmt.setString(1, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                AppDetail result = new AppDetail();
                result.appId = appId;
                result.appName = rs.getString("app_name");
                result.language = AppLanguage.fromString(rs.getString("language"));
                result.status = AppStatus.valueOf(rs.getString("app_status"));
                return result;
            }
        }
    }

    public void deleteById(String appId) throws AppNotFound, SQLException {
        String query = "DELETE FROM app_info WHERE app_id = ?";
        try (Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, appId);
            int n = stmt.executeUpdate();
            if (n == 0)
                throw new AppNotFound();
        }
    }
    
    public void updateImageId(String appId, String imageId, BuildStatus appStatus) throws SQLException {
        String query = "UPDATE app_info SET image_id = ?, app_status = ? WHERE app_id = ?";
        try (Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, imageId);
            stmt.setString(2, appStatus.name());
            stmt.setString(3, appId);
            stmt.executeUpdate();
        }
    }
    
    public List<String> getAllAppIds() throws SQLException {
        String query = "SELECT app_id FROM app_info";
        try (Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("app_id"));
            }
            
            return ids;
        }
    }
}
