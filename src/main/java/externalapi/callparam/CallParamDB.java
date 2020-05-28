package externalapi.callparam;

import externalapi.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class CallParamDB {
    @Inject private DBConnectionPool dbConnPool;

    public void insertParams(String callId, List<CallParam> callParams) throws SQLException {
        try (Connection conn = dbConnPool.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO call_input (call_id, name, type, value) VALUES (?, ?, ?, ?)")) {

                for (CallParam p : callParams) {
                    stmt.setString(1, callId);
                    stmt.setString(2, p.name);
                    stmt.setString(3, p.type.name());
                    stmt.setString(4, p.value);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
}
