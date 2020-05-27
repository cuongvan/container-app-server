package externalapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppCodeVersionDB {

    @Inject
    private DBConnectionPool dbConnPool;

    public AppCodeVersion getById(String codeId) throws SQLException {
        try (
            Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT code_path FROM app_code_version WHERE code_id = ?"))
        {
            stmt.setString(1, codeId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                AppCodeVersion result = new AppCodeVersion();
                result.codeId = codeId;
                result.codePath = rs.getString("code_path");
                return result;
            }
        }
    }

    public void updateBuildSuccess(String codeId, String imageId, String imageName) throws SQLException {
        try (
            Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE app_code_version SET image_id = ?, image = ?, build_status = ? WHERE code_id = ?"))
        {
            stmt.setString(1, imageId);
            stmt.setString(2, imageName);
            stmt.setString(3, BuildStatus.BUILD_DONE.name());
            stmt.setString(4, codeId);
            stmt.executeUpdate();
        }
    }

    public void updateBuildFailure(String codeId) throws SQLException {
        try (
            Connection connection = dbConnPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE app_code_version SET build_status = ? WHERE code_id = ?"))
        {
            stmt.setString(1, BuildStatus.BUILD_FAILED.name());
            stmt.setString(2, codeId);
            stmt.executeUpdate();
        }
    }
}
