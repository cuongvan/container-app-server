package externalapi.db;

import common.AppType;
import common.DBConnectionPool;
import externalapi.models.BatchAppInfo;
import common.SupportLanguage;
import externalapi.AppCallDAO;
import externalapi.models.AppCallResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;

public class DBAppInfoClient implements AppCallDAO {

    private DBConnectionPool dbPool;

    @Inject
    public DBAppInfoClient(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }
    
    @Override
    public BatchAppInfo getById(String appId) {
        String query = "SELECT image, language FROM app_info WHERE type = ? AND app_id = ?";
        try (
            Connection conn = dbPool.getConnection();
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

    @Override
    public void setCallResult(AppCallResult callResult) {
        String query = "SELECT call_id, duration, status, stdout, stderr, container_id FROM app_call WHERE container_id = ? FOR UPDATE";
        try (Connection conn = dbPool.getNonAutoCommitConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE))
            {
                stmt.setString(1, callResult.getContainerId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }
                    
                    String callId = rs.getString("call_id");
                    rs.updateLong("duration", callResult.getDuration());
                    {
                        String status = callResult.isSuccess() ? "Success" : "Fail";
                        rs.updateString("status", status);
                    }
                    rs.updateString("stdout", callResult.getStdout());
                    rs.updateString("stderr", callResult.getStderr());
                    rs.updateNull("container_id");
                    rs.updateRow();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
