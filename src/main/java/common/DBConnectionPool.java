package common;


import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cuong
 */
public class DBConnectionPool {
    private static BasicDataSource dataSource;
    public static void init() {
        dataSource = new BasicDataSource();
        dataSource.setUrl(
            String.format("jdbc:postgresql://%s/%s", Conf.Inst.POSTGRES_HOST, Conf.Inst.POSTGRES_DATABASE));
        dataSource.setUsername(Conf.Inst.POSTGRES_USER);
        dataSource.setPassword(Conf.Inst.POSTGRES_PASS);
        dataSource.setMinIdle(1);
        dataSource.setMaxIdle(1); // only 1 thread do the insert
        dataSource.setMaxOpenPreparedStatements(1);
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
