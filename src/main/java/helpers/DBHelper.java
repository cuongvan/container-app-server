package helpers;

import java.sql.Connection;
import java.sql.SQLException;

public class DBHelper {
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
