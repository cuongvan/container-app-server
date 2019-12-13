package common;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cuong
 */
public class DBPool {
    public static Connection getConn() throws SQLException {
        Connection conn = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/ckan_default", "ckan_default", "123456");
        conn.setAutoCommit(true);
        return conn;
    }
}
