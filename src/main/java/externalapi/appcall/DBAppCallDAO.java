package externalapi.appcall;

import externalapi.appcall.AppCallDAO;
import externalapi.appcall.models.AppCallResult;
import externalapi.appcall.models.BatchAppCallResult;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appparam.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DBAppCallDAO implements AppCallDAO {
    private DBConnectionPool dbPool;
    
    public static final String ANONYMOUS_USER = null;
    
    @Inject
    public DBAppCallDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }

    @Override
    public void createNewCall(String callId, String appId, String userId, List<KeyValueParam> keyValueParams, List<FileParam> fileParams) {
        try (Connection conn = dbPool.getNonAutoCommitConnection()) {
            insertAppCallRow(conn, callId, appId, userId);
            for (KeyValueParam p : keyValueParams)
                insertTextCallParam(conn, callId, p);
            for (FileParam p : fileParams)
                insertFileCallParam(conn, callId, p);
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void insertAppCallRow(final Connection conn, String callId, String appId, String userId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement (
            "INSERT INTO app_call (call_id, app_id, user_id) VALUES (?, ?, ?)")) {
            stmt.setString(1, callId);
            stmt.setString(2, appId);
            stmt.setString(3, userId);
            int nrows = stmt.executeUpdate();
        }
    }

    private void insertTextCallParam(Connection conn, String callId, KeyValueParam param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO call_param (call_id, param_name, text_value) VALUES (?, ?, ?)")) {
            
            stmt.setString(1, callId);
            stmt.setString(2, param.getName());
            stmt.setString(3, param.getValue());
            stmt.executeUpdate();
        }
    }
    
    private void insertFileCallParam(Connection conn, String callId, FileParam param) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO call_param (call_id, param_name, file_path) VALUES (?, ?, ?)")) {
            
            stmt.setString(1, callId);
            stmt.setString(2, param.getName());
            stmt.setString(3, param.getFilePath());
            stmt.executeUpdate();
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

    //@Override
    public BatchAppCallResult getCallInfoByCallId(String callId) {
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
//                return new BatchAppCallInfo(
//                    callId,
//                    r.getString("image"),
//                    r.getBoolean("json_input"),
//                    r.getBoolean("binary_input")
//                );
                return null;
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
