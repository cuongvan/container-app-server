/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import common.DBConnectionPool;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.Conf;
import common.ContainerLog;
import common.DockerClientPool;
import docker.DockerUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.FileUtils;
import org.slf4j.*;

/**
 *
 * @author cuong
 */
public class ContainerFinishWorker {

    private static Logger logger = LoggerFactory.getLogger(ContainerFinishWorker.class);
    private static ExecutorService executor;
    
    public static void init() {
        executor = Executors.newFixedThreadPool(5);
    }

    public static void submitFinishContainer(String callId) {
        executor.submit(() -> {
            InspectContainerResponse[] inspects = new InspectContainerResponse[1];
            try {
                DockerClientPool.Instance.useClient(client -> {
                    // can inspect container id or name. use name here
                    inspects[0] = client.inspectContainerCmd(callId).exec();
                });
            } catch (Exception ex) {
                logger.warn("?");
                ex.printStackTrace();
                return;
            }
            
            InspectContainerResponse inspect = inspects[0];
            ContainerLog log = DockerUtils.getContainerLog(inspect.getId());
            DockerUtils.deleteContainer(callId);
            try {
                FileUtils.forceDelete(new File(Conf.Inst.APP_INPUT_FILES_DIR, callId));
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.warn("Fail to delete input file for call {}", callId);
            }

            Instant t1 = Instant.parse(inspect.getState().getStartedAt());
            Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
            long duration = Duration.between(t1, t2).getSeconds();

            logger.info("Finished container: {}", callId);

            // write database
            try (
                Connection conn = DBConnectionPool.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO application_result(call_id, duration, stdout, stderr) VALUES (?, ?, ?, ?)")
             ) {
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
                logger.warn("SQL exception for call: {}", callId);
                ex.printStackTrace();
            }

            // notify user
            logger.warn("NOT IMPLEMENTED: Notify CKAN");
        });
    }
}
