package externalapi.appinfo;

import externalapi.appinfo.models.AppDetail;
import externalapi.appinfo.models.AppStatus;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.DBConnectionPool;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.ParamType;
import externalapi.appinfo.models.SysStatus;
import helpers.DBHelper;
import java.sql.*;
import javax.inject.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AppInfoDAO {
    private DBConnectionPool dbPool;

    @Inject
    public AppInfoDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }
    

    public void insertApp(String appId, AppDetail app) {
        String insertAppInfo = "INSERT INTO app_info(" +
            "app_id, app_name, avatar_path, type, slug_name, code_path, image, owner, description, language, app_status, sys_status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String insertAppParams = "INSERT INTO app_param(app_id, name, type, label, description) " +
            " VALUES (?, ?, ?, ?, ?);";
        
        Connection connection = null;
        try {
            connection = dbPool.getNonAutoCommitConnection();
            try (PreparedStatement stmt = connection.prepareStatement(insertAppInfo)) {
                stmt.setString(1, appId);
                stmt.setString(2, app.getAppName());
                stmt.setString(3, app.getAvatarPath());
                stmt.setString(4, app.getType().name());
                stmt.setString(5, app.getSlugName());
                stmt.setString(6, app.getCodePath());
                stmt.setString(7, app.getImage());
                stmt.setString(8, app.getOwner());
                stmt.setString(9, app.getDescription());
                stmt.setString(10, app.getLanguage().name());
                stmt.setString(11, app.getAppStatus().name());
                stmt.setString(12, app.getSysStatus().name());
                stmt.executeUpdate();
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(insertAppParams)) {
                for (AppParam param : app.getParams()) {
                    stmt.setString(1, appId);
                    stmt.setString(2, param.name);
                    stmt.setString(3, param.type.name());
                    stmt.setString(4, param.label);
                    stmt.setString(5, param.description);
                    stmt.addBatch();
                    stmt.executeBatch();
                }
            }
                
            connection.commit();
        } catch (SQLException ex) {
            DBHelper.rollback(connection);
            throw new RuntimeException(ex);
        } finally {
            DBHelper.close(connection);
        }
    }

    public AppDetail getById(String appId) {
        String selectAppInfo = "SELECT * FROM app_info WHERE app_id = ?";
        
        String selectAppParams = "SELECT name, type, label, description\n" +
                       "FROM app_param WHERE app_id = ?";
        
        Connection connection = null;
        AppDetail app = new AppDetail();
        try {
            connection = dbPool.getNonAutoCommitConnection();
            try (PreparedStatement stmt = connection.prepareStatement(selectAppInfo)) {
                stmt.setString(1, appId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next())
                        return null;
                    app
                        .setAppId(appId)
                        .setAppName(rs.getString("app_name"))
                        .setAvatarPath(rs.getString("avatar_path"))
                        .setType(AppType.valueOf(rs.getString("type")))
                        .setSlugName(rs.getString("slug_name"))
                        .setCodePath(rs.getString("code_path"))
                        .setImage(rs.getString("image"))
                        .setImageId(rs.getString("image_id"))
                        .setOwner(rs.getString("owner"))
                        .setDescription(rs.getString("description"))
                        .setLanguage(SupportLanguage.valueOf(rs.getString("language")))
                        .setAppStatus(AppStatus.valueOf(rs.getString("app_status")))
                        .setSysStatus(SysStatus.valueOf(rs.getString("sys_status")))
                        .setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime())
                        ;
                }
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(selectAppParams)) {
                stmt.setString(1, appId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        app.addParam(new AppParam(
                            rs.getString("name"),
                            ParamType.valueOf(rs.getString("type")),
                            rs.getString("label"),
                            rs.getString("description")));
                    }
                }
            }
            
            connection.commit();
            return app;
        } catch (SQLException ex) {
            DBHelper.rollback(connection);
            throw new RuntimeException(ex);
        } finally {
            DBHelper.close(connection);
        }
    }

    public void deleteById(String appId) {
        String query = "DELETE FROM app_info WHERE app_id = ?";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, appId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void updateImageId(String appId, String imageId, AppStatus appStatus) {
        String query = "UPDATE app_info SET image_id = ?, app_status = ? WHERE app_id = ?";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, imageId);
            stmt.setString(2, appStatus.name());
            stmt.setString(3, appId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public List<String> getAllAppIds() {
        String query = "SELECT app_id FROM app_info";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("app_id"));
            }
            
            return ids;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
