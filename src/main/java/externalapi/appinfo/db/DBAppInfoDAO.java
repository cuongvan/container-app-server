package externalapi.appinfo.db;

import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppStatus;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.db.DBConnectionPool;
import java.sql.*;
import javax.inject.*;

@Singleton
public class DBAppInfoDAO implements AppInfoDAO {
    private DBConnectionPool dbPool;

    @Inject
    public DBAppInfoDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }
    

    @Override
    public void createApp(AppInfo app) {
        String query = "INSERT INTO app_info(\n" +
            "app_id, app_name, ava_url, type, slug_name, image, owner, description, language, status)\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, app.getAppId());
            stmt.setString(2, app.getAppName());
            stmt.setString(3, app.getAvatarUrl());
            stmt.setString(4, app.getType().name());
            stmt.setString(5, app.getSlugName());
            stmt.setString(6, app.getImage());
            stmt.setString(7, app.getOwner());
            stmt.setString(8, app.getDescription());
            stmt.setString(9, app.getLanguage().name());
            stmt.setString(10, AppStatus.CREATED.name());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AppInfo getById(String appId) {
        String query = "SELECT app_id, app_name, ava_url, type, slug_name, image, "
            + "owner, description, host_port, container_port, language, status\n" 
            + "FROM app_info WHERE app_id = ?";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next())
                    return null;
                return new AppInfo()
                    .setAppId(appId)
                    .setAppName(rs.getString("app_name"))
                    .setAvatarUrl(rs.getString("ava_url"))
                    .setType(AppType.valueOf(rs.getString("type")))
                    .setSlugName(rs.getString("slug_name"))
                    .setImage(rs.getString("image"))
                    .setOwner(rs.getString("owner"))
                    .setDescription(rs.getString("description"))
                    .setLanguage(SupportLanguage.valueOf(rs.getString("language")))
                    .setStatus(AppStatus.valueOf(rs.getString("status")))
                    ;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
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
    
    private void template() {
        String query = "";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
