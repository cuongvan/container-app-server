/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import com.github.dockerjava.api.DockerClient;
import common.DBConnectionPool;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.ContainerLog;
import common.DBHelper;
import common.DockerClientPool;
import docker.DockerUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import notifications.Event;
import notifications.EventType;
import notifications.Status;
import org.apache.commons.io.FileUtils;
import org.slf4j.*;
import utils.HttpUtil;

/**
 *
 * @author cuong
 */
public class ContainerFinishWorker {

    private static Logger LOGGER = LoggerFactory.getLogger(ContainerFinishWorker.class);
    private static ExecutorService executor;
    
    public static void init() {
        executor = Executors.newFixedThreadPool(5);
    }

    public static void submitFinishContainer(String containerId) {
        executor.submit(() -> handleFinishContainer(containerId));
    }
    public static void handleFinishContainer(String containerId) {
        DockerClient docker =  DockerClientPool.Instance.borrowClient();
        InspectContainerResponse inspect;
        
        try {
            inspect = docker.inspectContainerCmd(containerId).exec();
        } finally {
            DockerClientPool.Instance.returnObject(docker);
        }
        
        // delete all mounted input files
        inspect.getMounts()
            .stream()
            .map(mount -> mount.getSource())
            .map(path -> new File(path))
            .forEach(inputFile -> {
                try {
                    FileUtils.forceDelete(inputFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LOGGER.warn("Fail to delete input file for call {}", containerId);
                }
            });
        
        int exitCode = inspect.getState().getExitCode();
        String callStatus = exitCode == 0 ? "Success" : "Fail";
            
        
        // get logs
        ContainerLog log = DockerUtils.getContainerLog(inspect.getId());
        DockerUtils.deleteContainer(containerId);
        
        Instant t1 = Instant.parse(inspect.getState().getStartedAt());
        Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
        long duration = Duration.between(t1, t2).getSeconds();

        LOGGER.info("Finished container: {}", containerId);
        
        String callId = null;
        
        // write database
        try (Connection conn = DBConnectionPool.getConnection()) {
            
            // get the call_id to notify CKAN
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT call_id FROM app_call WHERE container_id = ?"))
            {
//                stmt.setString(1, DBHelper.APP_CALL_TABLE);
                stmt.setString(1, containerId);
                try (ResultSet r = stmt.executeQuery()) {
                    if (!r.next()) {
                        throw new IllegalStateException("Cannot find call_id");
                    }
                }
            }
            
            // update app output & delete container_id
            try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE app_call SET duration = ?, status = ?, stdout = ?, stderr = ?, container_id = NULL WHERE container_id = ?"))
            {
//                stmt.setString(1, DBHelper.APP_CALL_TABLE);
                stmt.setLong(1, duration);
                stmt.setString(2, callStatus);
                stmt.setString(3, log.stdout);
                stmt.setString(4, log.stderr);
                stmt.setString(5, containerId);
                int nrows = stmt.executeUpdate();
                if (nrows == 1) {
                    LOGGER.info("Inserted result: {}", containerId);
                } else {
                    LOGGER.warn("Failed to insert data to database: {}. Container not belongs to CKAN", containerId);
                }
            }
            
        } catch (SQLException ex) {
            LOGGER.warn("SQL exception for call: {}", containerId);
            ex.printStackTrace();
        }
        
        // notify CKAN
        if (callId != null) {
            try {
                HttpUtil.post("http://localhost:5000/notify/batch/" + callId, new Event(EventType.Execute, Status.Success));
            } catch (IOException ex) {
                ex.printStackTrace();
                LOGGER.error("Fail to notify CKAN, call_id = {}", callId);
            }
        }
    }
}
