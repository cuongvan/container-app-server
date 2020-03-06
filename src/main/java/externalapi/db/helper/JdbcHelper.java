package externalapi.db.helper;

import common.DBConnectionPool;
import java.sql.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JdbcHelper {
    
    @Inject
    private DBConnectionPool dbPool;
    
    public <T> T query(String query, SetParamsFunction setParams, StatementFunction<T> mapStatement) throws SQLException {
        try (
            Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ){
            setParams.accept(stmt);
            return mapStatement.apply(stmt);
        }
    }
    
    public void query(String query, SetParamsFunction setParams, StatementConsumer stmtConsumer) throws SQLException {
        try (
            Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ){
            setParams.accept(stmt);
            stmtConsumer.accept(stmt);
        }
    }
    
    
    public <T> T select(String query, SetParamsFunction setParams, ResultSetFunction<T> mapValue) throws SQLException {
        try (
            Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ){
            setParams.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapValue.apply(rs);
            }
        }
    }

    public int update(String query, SetParamsFunction setParams) throws SQLException {
        try (
            Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ){
            setParams.accept(stmt);
            return stmt.executeUpdate();
        }
    }
}
