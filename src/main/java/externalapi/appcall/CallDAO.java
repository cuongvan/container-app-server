package externalapi.appcall;

import externalapi.appcall.models.CallResult;
import externalapi.DBConnectionPool;
import externalapi.appcall.models.CallDetail;
import externalapi.appcall.models.CallInputEntry;
import externalapi.appcall.models.CallStatus;
import externalapi.appinfo.models.InputFieldType;
import helpers.DBHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import externalapi.appcall.models.CallOutputEntry;
import externalapi.appcall.models.OutputFieldType;

@Singleton
public class CallDAO {
    private DBConnectionPool dbPool;
    
    public static final String ANONYMOUS_USER = null;
    
    @Inject
    public CallDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }

    public void createNewCall(String callId, String appId, String userId, List<CallInputEntry> callParams) throws SQLException {
        try (Connection conn = dbPool.getNonAutoCommitConnection()) {
            
            insertAppCallRow(conn, callId, appId, userId);
            
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO call_input (call_id, name, type, value) VALUES (?, ?, ?, ?)")) {

                for (CallInputEntry p : callParams) {
                    stmt.setString(1, callId);
                    stmt.setString(2, p.name);
                    stmt.setString(3, p.type.name());
                    stmt.setString(4, p.value);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
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
    
    public void updateCallResult(String callId, CallResult callResult, List<CallOutputEntry> fields) throws SQLException {
        Connection conn = dbPool.getNonAutoCommitConnection();
        try {
            try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE app_call SET elapsed_seconds = ?, call_status = ?, logs = ? WHERE call_id = ?")) {
                stmt.setLong(1, callResult.elapsedSeconds);
                stmt.setString(2, callResult.callStatus.name());
                stmt.setString(3, callResult.logs);
                stmt.setString(4, callId);
                stmt.executeUpdate();
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO call_output(call_id, name, type, value) VALUES (?, ?, ?, ?)")) {
                for (CallOutputEntry field : fields) {
                    stmt.setString(1, callId);
                    stmt.setString(2, field.name);
                    stmt.setString(3, field.type.name());
                    stmt.setString(4, field.value);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        }
    }


    public void updateStartedAppCall(String callId, String containerId) throws SQLException {
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
        }
    }

    public List<String> getAllCallIds() throws SQLException {
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
        }
    }

    public CallDetail getById(String callId) throws SQLException {
        Connection connection = dbPool.getNonAutoCommitConnection();
        try {
            CallDetail callDetail = new CallDetail();
            
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM app_call WHERE call_id = ?")) {
                stmt.setString(1, callId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next())
                        return null;
                    
                    callDetail.appId = rs.getString("app_id");
                    callDetail.userId = rs.getString("user_id");
                    callDetail.elapsedSeconds = rs.getLong("elapsed_seconds");
                    callDetail.callStatus = CallStatus.valueOf(rs.getString("call_status"));
                    callDetail.logs = rs.getString("logs");
                    Timestamp time = rs.getTimestamp("created_at");
                    callDetail.createdAt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(time.toLocalDateTime());
                }
            }
            
            List<CallInputEntry> inputs = new ArrayList<>();
            try (PreparedStatement stmt2 = connection.prepareStatement(
                "SELECT name, type, value FROM call_input WHERE call_id = ?")) {
                stmt2.setString(1, callId);
                try (ResultSet rs = stmt2.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        InputFieldType type = InputFieldType.valueOf(rs.getString("type"));
                        String value = rs.getString("value");
                        CallInputEntry input = new CallInputEntry(type, name, value);
                        inputs.add(input);
                    }
                }
            }
            
            callDetail.outputs = new ArrayList<>();
            try (PreparedStatement stmt2 = connection.prepareStatement(
                "SELECT name, type, value FROM call_output WHERE call_id = ?")) {
                stmt2.setString(1, callId);
                try (ResultSet rs = stmt2.executeQuery()) {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        OutputFieldType type = OutputFieldType.valueOf(rs.getString("type"));
                        String value = rs.getString("value");
                        
                        CallOutputEntry output = new CallOutputEntry(type, name, value);
                        callDetail.outputs.add(output);
                    }
                }
            }
            
            connection.commit();
            return callDetail;
        } catch (SQLException ex) {
            DBHelper.rollback(connection);
            throw ex;
        } finally {
            DBHelper.close(connection);
        }
    }
}
