/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package docker;

import common.DBPool;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.ContainerLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.*;

/**
 *
 * @author cuong
 */
public class ContainerFinishHandler {

    private static Logger logger = LoggerFactory.getLogger(ContainerFinishHandler.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void submitFinishContainer(String appName, String callId, InspectContainerResponse inspect) {
        executor.submit(() -> {
            ContainerLog log = DockerUtils.getContainerLog(inspect.getId());
            DockerUtils.deleteContainer(callId);

            Instant t1 = Instant.parse(inspect.getState().getStartedAt());
            Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
            long duration = Duration.between(t1, t2).getSeconds();

            // get container's output
//            logger.info("{} => {}", callId, log);

            // write database
            logger.warn("NOT IMPLEMENTED: Write output to database");
            try (Connection conn = DBPool.getConn(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO application_result(call_id, duration, stdout, stderr) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, callId);
                stmt.setLong(2, duration);
                stmt.setString(3, log.stdout);
                stmt.setString(4, log.stderr);
                int nrows = stmt.executeUpdate();
                if (nrows == 1) {
                    logger.info("Inserted result: {}", callId);
                }
                if (nrows != 1) {
                    logger.warn("Failed to insert data to database: {}", callId);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // notify user
            logger.warn("NOT IMPLEMENTED: Notify CKAN");

            // delete container
        });
    }
}
