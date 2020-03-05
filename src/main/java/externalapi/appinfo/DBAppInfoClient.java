package externalapi.appinfo;

import common.AppType;
import common.BatchAppInfo;
import common.SupportLanguage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.Main;

public class DBAppInfoClient implements BatchAppInfoDAO {

    @Override
    public BatchAppInfo getById(String appId) {
        String query = "SELECT image, language FROM app_info WHERE type = ? AND app_id = ?";
        try (
            Connection conn = Main.singletonDBConnectionPool().getConnection();
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
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
