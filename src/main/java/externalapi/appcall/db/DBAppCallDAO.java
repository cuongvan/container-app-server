package externalapi.appcall.db;

import externalapi.appinfo.models.AppType;
import externalapi.appcall.models.BatchAppInfo;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.BatchAppCallInfo;
import externalapi.db.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;

public class DBAppCallDAO implements AppCallDAO {
    private DBConnectionPool dbPool;
    
    @Inject
    public DBAppCallDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }
    
    @Override
    public BatchAppInfo getAppInfoByAppId(String appId) {
        String query = "SELECT image, language FROM app_info WHERE type = ? AND app_id = ?";
        try (
            Connection conn = dbPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ){
            stmt.setString(1, AppType.BATCH.name());
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
    public void updateFinishedAppCall(AppCallResult callResult) {
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

    @Override
    public BatchAppCallInfo getCallInfoByCallId(String callId) {
        String query = "SELECT image, json_input, binary_input FROM app_info, app_call "
                    + "WHERE call_id = ? AND app_info.app_id = app_call.app_id";
        try (
            Connection conn = dbPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ){
            stmt.setString(1, callId);
            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    throw new IllegalStateException("call_id not found in app_info");
                }
                return new BatchAppCallInfo(
                    callId,
                    r.getString("image"),
                    r.getBoolean("json_input"),
                    r.getBoolean("binary_input")
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateStartedAppCall(String callId, String containerId) {
        String query = "UPDATE app_call SET status = ?, container_id = ? WHERE call_id = ?";
        // update call status
        try (
            Connection conn = dbPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setString(1, "Started");
            stmt.setString(2, containerId);
            stmt.setString(3, callId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } 
    }
}
