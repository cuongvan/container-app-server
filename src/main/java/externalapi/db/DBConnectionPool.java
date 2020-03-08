package externalapi.db;


import common.AppConfig;
import java.sql.Connection;
import java.sql.SQLException;
import javax.inject.Inject;
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
    private BasicDataSource dataSource;
    
    @Inject
    public DBConnectionPool(AppConfig appConfig) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(
            String.format("jdbc:postgresql://%s/%s", appConfig.POSTGRES_HOST, appConfig.POSTGRES_DATABASE));
        dataSource.setUsername(appConfig.POSTGRES_USER);
        dataSource.setPassword(appConfig.POSTGRES_PASS);
        dataSource.setMinIdle(1);
        dataSource.setMaxIdle(5); // only 1 thread do the insert
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setEnableAutoCommitOnReturn(true);
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
