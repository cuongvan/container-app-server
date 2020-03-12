package externalapi.appparam.models;

import externalapi.appparam.models.AppParam;
import externalapi.appparam.AppParamDAO;
import externalapi.appparam.models.ParamType;
import externalapi.appparam.DBConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DBAppParamDAO implements AppParamDAO {
    private DBConnectionPool dbPool;

    @Inject
    public DBAppParamDAO(DBConnectionPool dbPool) {
        this.dbPool = dbPool;
    }
    
    @Override
    public void updateParams(String appId, List<AppParam> params) {
        String query = "INSERT INTO public.app_param(\n" +
                        "	app_id, name, type, label, description)\n" +
                        "	VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = dbPool.getNonAutoCommitConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            for (AppParam param : params) {
                stmt.setString(1, appId);
                stmt.setString(2, param.getName());
                stmt.setString(3, param.getType().name());
                stmt.setString(4, param.getLabel());
                stmt.setString(5, param.getDescription());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<AppParam> getAppParams(String appId) {
        String query = "SELECT name, type, label, description\n" +
                       "FROM app_param WHERE app_id = ?";
        try (Connection connection = dbPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
        ) {
            stmt.setString(1, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<AppParam> params = new ArrayList<>();
                while (rs.next()) {
                    params.add(new AppParam()
                        .setName(rs.getString("name"))
                        .setType(ParamType.valueOf(rs.getString("type")))
                        .setLabel(rs.getString("label"))
                        .setDescription(rs.getString("description"))
                    );
                }
                return params;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
