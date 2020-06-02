package externalapi;


import common.Config;
import java.sql.Connection;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.dbcp2.BasicDataSource;

@Singleton
public class DBConnectionPool {
    
    private BasicDataSource dataSource;

    @Inject
    public DBConnectionPool(Config config) throws SQLException {
        if (dataSource != null)
            return;
        dataSource = new BasicDataSource();
        dataSource.setUrl(config.JDBC_CONNECTION_STRING);
        dataSource.setUsername(config.DB_USER);
        dataSource.setPassword(config.DB_PASSWORD);
        dataSource.setMinIdle(1);
        dataSource.setMaxIdle(5); // only 1 thread do the insert
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setAutoCommitOnReturn(true);
        
        // try opening connection to avoid invalid username/password/db at start
        try (Connection connection = dataSource.getConnection()) {
            
        }
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
