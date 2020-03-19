package externalapi;


import common.Constants;
import java.sql.Connection;
import java.sql.SQLException;
import javax.inject.Singleton;
import org.apache.commons.dbcp2.BasicDataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cuong
 */
@Singleton
public class DBConnectionPool {
    
    private static final DBConnectionPool instance = new DBConnectionPool();
    
    public static DBConnectionPool getInstance() {
        return instance;
    }
    
    private BasicDataSource dataSource;
    
    private DBConnectionPool() {
        dataSource = new BasicDataSource();
        dataSource.setUrl(Constants.JDBC_CONNECTION_STRING);
        dataSource.setUsername(Constants.DB_USER);
        dataSource.setPassword(Constants.DB_PASSWORD);
        dataSource.setMinIdle(1);
        dataSource.setMaxIdle(5); // only 1 thread do the insert
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setAutoCommitOnReturn(true);
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public Connection getNonAutoCommitConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }
    
    public void close() {
        try {
            dataSource.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
