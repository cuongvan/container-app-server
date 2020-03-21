package externalapi.appcall;

import externalapi.appcall.models.AppCallResult;
import externalapi.DBConnectionPool;
import externalapi.appcall.models.CallDetail;
import externalapi.appcall.models.CallParam;
import externalapi.appcall.models.CallStatus;
import externalapi.appinfo.models.ParamType;
import helpers.DBHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CallDAO {
    private DBConnectionPool dbPool;
    
    public static final String ANONYMOUS_USER = null;
    
    @Inject
    public CallDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }

    public void createNewCall(String callId, String appId, String userId, List<CallParam> callParams) {
        try (Connection conn = dbPool.getNonAutoCommitConnection()) {
            
            insertAppCallRow(conn, callId, appId, userId);
            
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO call_param (call_id, name, type, value) VALUES (?, ?, ?, ?)")) {

                for (CallParam p : callParams) {
                    stmt.setString(1, callId);
                    stmt.setString(2, p.name);
                    stmt.setString(3, p.type.name());
                    stmt.setString(4, p.value);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void insertAppCallRow(Connection conn, String callId, String appId, String userId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement (
            "INSERT INTO app_call (call_id, app_id, user_id, call_status) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, callId);
            stmt.setString(2, appId);
            stmt.setString(3, userId);
            stmt.setString(4, CallStatus.STARTED.name());
            stmt.executeUpdate();
        }
    }
    
    public void updateFinishedAppCall(AppCallResult callResult) {
        //String query = "SELECT call_id, elapsed_seconds, call_status, output FROM app_call WHERE call_id = ? FOR UPDATE";
        String query = "UPDATE app_call SET elapsed_seconds = ?, call_status = ?, output = ? WHERE call_id = ?";
        try (Connection conn = dbPool.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query))
            {
                stmt.setLong(1, callResult.elapsedSeconds);
                stmt.setString(2, callResult.callStatus.name());
                stmt.setString(3, callResult.output);
                stmt.setString(4, callResult.appCallId);
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }


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

    public List<String> getAllCallIds() {
        String query = "SELECT call_id FROM app_call";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
        ) {
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("call_id"));
            }
            
            return ids;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public CallDetail getById(String callId) {
        Connection connection = null;
        try {
            connection = dbPool.getNonAutoCommitConnection();
            
            
            String appId;
            String userId;
            long elapsed;
            CallStatus callStatus;
            String output;
            List<CallParam> params = new ArrayList<>();
            
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT call_id, app_id, user_id, elapsed_seconds, call_status, output "
                + "FROM app_call WHERE call_id = ?")) {
                stmt.setString(1, callId);
                try (ResultSet callRs = stmt.executeQuery()) {
                    if (!callRs.next())
                        return null;
                    
                    appId = callRs.getString("app_id");
                    userId = callRs.getString("user_id");
                    elapsed = callRs.getLong("elapsed_seconds");
                    callStatus = CallStatus.valueOf(callRs.getString("call_status"));
                    output = callRs.getString("output");
                }
                
                try (PreparedStatement stmt2 = connection.prepareStatement(
                    "SELECT name, type, value FROM call_param WHERE call_id = ?")) {
                    stmt2.setString(1, callId);
                    try (ResultSet rs = stmt2.executeQuery()) {
                        while (rs.next()) {
                            String name = rs.getString("name");
                            ParamType type = ParamType.valueOf(rs.getString("type"));
                            String value = rs.getString("value");
                            CallParam param = new CallParam(type, name, value);
                            params.add(param);
                        }
                    }
                }

                connection.commit();
                return new CallDetail(callId, appId, userId, elapsed, callStatus, output, params);
            }
        } catch (SQLException ex) {
            DBHelper.rollback(connection);
            throw new RuntimeException(ex);
        } finally {
            DBHelper.close(connection);
        }
    }
}
