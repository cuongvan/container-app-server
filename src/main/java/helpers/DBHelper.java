package helpers;

import common.AppConfig;
import externalapi.db.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
    private static class ConnectionPoolHolder {
        private static final DBConnectionPool pool = new DBConnectionPool(AppConfig.Inst);
    }
    
    public static DBConnectionPool getPool() {
        return ConnectionPoolHolder.pool;
    }
    
    //public static DBConnectionPool pool = new DBConnectionPool(AppConfig.Inst);
    
    public interface UseStatement {
        void accept(Statement stmt) throws SQLException;
    }
    
    public interface UsePreparedStatement {
        void accept(PreparedStatement stmt) throws SQLException;
    }
    
    public static void useStmt(UseStatement use) {
        try (
            Connection conn = getPool().getConnection();
            Statement stmt = conn.createStatement();
        ) {
            use.accept(stmt);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    public static void prepareStmt(String query, UsePreparedStatement use) {
        try (
            Connection conn = getPool().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            use.accept(stmt);
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
    
    public static void createTables() {
        createAppInfoTable();
        createAppParamTable();
        createAppCallTable();
        createCallParamTable();
    }
    
    
    
    
    private static void createAppInfoTable() {
        query(
            "CREATE TABLE IF NOT EXISTS app_info (\n" +
            "	app_id TEXT PRIMARY KEY,\n" +
            "	app_name TEXT NOT NULL,\n" +
            "	ava_url TEXT,\n" +
            "	type TEXT,\n" +
            "    slug_name TEXT,\n" +
            "	image TEXT,\n" +
            "	owner TEXT,\n" +
            "    description TEXT,\n" +
            "	host_port int,\n" +
            "    container_port int,\n" +
            "	language TEXT,\n" +
            "    status TEXT\n" +
            ");"
        );
    }
    private static void createAppParamTable() {
        query(
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
        query(
            "CREATE TABLE IF NOT EXISTS app_call (\n" +
            "    call_id TEXT PRIMARY KEY,\n" +
            "    app_id TEXT NOT NULL,\n" +
            "    user_id TEXT,\n" +
            "    elapsed_seconds BIGINT,\n" +
            "    output TEXT\n" +
            ");"
        );
    }
    
    private static void createCallParamTable() {
        query(
            "CREATE TABLE IF NOT EXISTS call_param(\n" +
            "    call_id TEXT REFERENCES app_call(call_id) ON DELETE CASCADE,\n" +
            "    param_name TEXT NOT NULL,\n" +
            "    text_value TEXT,\n" +
            "    file_path TEXT,\n" +
            "    PRIMARY KEY(call_id, param_name)\n" +
            ")"
        );
    }
    
    private static void query(String query) {
        useStmt(stmt -> stmt.executeUpdate(query));
    }
    
    public static void clearAllRows() {
        query("DELETE FROM call_param");
        query("DELETE FROM app_call");
        query("DELETE FROM app_param");
        query("DELETE FROM app_info");
    }
    
    public static void dropTables() {
        query("DROP TABLE IF EXISTS app_info, app_param, app_call, call_param");
    }
}
