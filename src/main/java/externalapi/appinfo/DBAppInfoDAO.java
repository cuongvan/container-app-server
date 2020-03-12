package externalapi.appinfo;

import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppStatus;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.DBConnectionPool;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.ParamType;
import java.sql.*;
import javax.inject.*;
import helpers.MiscHelper;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class DBAppInfoDAO implements AppInfoDAO {
    private DBConnectionPool dbPool;

    @Inject
    public DBAppInfoDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }
    

    @Override
    public String createApp(AppInfo app) {
        String appId = MiscHelper.newId();
        String query = "INSERT INTO app_info(\n" +
            "app_id, app_name, ava_url, type, slug_name, image, owner, description, language, status)\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, appId);
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
            return appId;
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
                return AppInfo.builder()
                    .withAppId(appId)
                    .withAppName(rs.getString("app_name"))
                    .withAvatarUrl(rs.getString("ava_url"))
                    .withType(AppType.valueOf(rs.getString("type")))
                    .withSlugName(rs.getString("slug_name"))
                    .withImage(rs.getString("image"))
                    .withOwner(rs.getString("owner"))
                    .withDescription(rs.getString("description"))
                    .withLanguage(SupportLanguage.valueOf(rs.getString("language")))
                    .withStatus(AppStatus.valueOf(rs.getString("status")))
                    .build()
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
    
    @Override
    public void updateParams(String appId, List<AppParam> params) {
        String query = "INSERT INTO public.app_param(\n" +
                        "	app_id, name, type, label, description)\n" +
                        "	VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = dbPool.getNonAutoCommitConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            for (AppParam param : params) {
                stmt.setString(1, appId);
                stmt.setString(2, param.getName());
                stmt.setString(3, param.getType().name());
                stmt.setString(4, param.getLabel());
                stmt.setString(5, param.getDescription());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public List<AppParam> getAppParams(String appId) {
        String query = "SELECT name, type, label, description\n" +
                       "FROM app_param WHERE app_id = ?";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<AppParam> params = new ArrayList<>();
                while (rs.next()) {
                    params.add(new AppParam()
                        .setName(rs.getString("name"))
                        .setType(ParamType.valueOf(rs.getString("type")))
                        .setLabel(rs.getString("label"))
                        .setDescription(rs.getString("description"))
                    );
                }
                return params;
            }
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
