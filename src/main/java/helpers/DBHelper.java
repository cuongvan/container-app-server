package helpers;

import externalapi.DBConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
    private static class ConnectionPoolHolder {
        private static final DBConnectionPool pool = new DBConnectionPool();
    }
    
    public static DBConnectionPool getPool() {
        return ConnectionPoolHolder.pool;
    }

    public static void createTables() {
        createAppInfoTable();
        createAppParamTable();
        createAppCallTable();
        createCallParamTable();
    }
    
    private static void createAppInfoTable() {
        update(
            "CREATE TABLE IF NOT EXISTS app_info (\n" +
            "    app_id TEXT PRIMARY KEY,\n" +
            "    app_name TEXT NOT NULL,\n" +
            "    ava_url TEXT,\n" +
            "    type TEXT,\n" +
            "    slug_name TEXT,\n" +
            "    code_path TEXT,\n" +
            "    image TEXT,\n" +
            "    image_id TEXT,\n" +
            "    owner TEXT,\n" +
            "    description TEXT,\n" +
            "    language TEXT,\n" +
            "    status TEXT\n" +
            ");"
        );
    }
    private static void createAppParamTable() {
        update(
            "CREATE TABLE IF NOT EXISTS app_param (\n" +
            "    app_id TEXT REFERENCES app_info(app_id) ON DELETE CASCADE,\n" +
            "    name TEXT NOT NULL,\n" +
            "    type TEXT NOT NULL,\n" +
            "    label TEXT NOT NULL,\n" +
            "    description TEXT,\n" +
            "    PRIMARY KEY(app_id, name)\n" +
            ");"
        );
    }
    
    private static void createAppCallTable() {
        update(
            "CREATE TABLE IF NOT EXISTS app_call (\n" +
            "    call_id TEXT PRIMARY KEY,\n" +
            "    app_id TEXT NOT NULL,\n" +
            "    user_id TEXT,\n" +
            "    elapsed_seconds BIGINT,\n" +
            "    call_status TEXT,\n" +
            "    output TEXT\n" +
            ");"
        );
    }
    
    private static void createCallParamTable() {
        update(
            "CREATE TABLE IF NOT EXISTS call_param(\n" +
            "    call_id TEXT REFERENCES app_call(call_id) ON DELETE CASCADE,\n" +
            "    param_name TEXT NOT NULL,\n" +
            "    text_value TEXT,\n" +
            "    file_path TEXT,\n" +
            "    PRIMARY KEY(call_id, param_name)\n" +
            ")"
        );
    }
    
    private static void update(String query) {
        try (
            Connection conn = getPool().getConnection();
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static void truncateTables() {
        update("TRUNCATE call_param, app_call, app_param, app_info");
    }
    
    public static void dropTables() {
        update("DROP TABLE IF EXISTS app_info, app_param, app_call, call_param");
    }
    
    public static void close(Connection connection) {
        if (connection != null)
            try {
                connection.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void rollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException ex1) {
            ex1.printStackTrace();
        }
    }
}
