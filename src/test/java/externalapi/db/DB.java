package externalapi.db;

import common.AppConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    public static DBConnectionPool pool = new DBConnectionPool(AppConfig.Inst);
    
    public interface UseStatement {
        void accept(Statement stmt) throws SQLException;
    }
    
    public interface UsePreparedStatement {
        void accept(PreparedStatement stmt) throws SQLException;
    }
    
    public static void useStmt(UseStatement use) {
        try (
            Connection conn = pool.getConnection();
            Statement stmt = conn.createStatement();
        ) {
            use.accept(stmt);
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
    
    public static void prepareStmt(String query, UsePreparedStatement use) {
        try (
            Connection conn = pool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            use.accept(stmt);
        } catch (SQLException ex) {
            throw new RuntimeException();
        }
    }
    
    public static void createTables() {
        useStmt(stmt -> {
           stmt.executeUpdate("CREATE TABLE IF NOT EXISTS app_info (\n" +
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
                ")");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS app_call (\n" +
                "    call_id TEXT PRIMARY KEY,\n" +
                "    app_id TEXT NOT NULL,\n" +
                "    user_id TEXT,\n" +
                "    input_id TEXT,\n" +
                "    output_id TEXT,\n" +
                "    duration bigint,\n" +
                "    container_id TEXT,\n" +
                "    stdout TEXT,\n" +
                "    stderr TEXT\n" +
                ")"); 
        });
    }
    
    public static void clearAllRows() {
        useStmt(stmt -> {
            stmt.executeUpdate("DELETE FROM app_info");
            stmt.executeUpdate("DELETE FROM app_call");
        });
    }
    
    public static void dropTables() {
        useStmt(stmt -> {
            stmt.executeUpdate("DROP TABLE IF EXISTS app_info");
            stmt.executeUpdate("DROP TABLE IF EXISTS app_call");
        });
    }
}
