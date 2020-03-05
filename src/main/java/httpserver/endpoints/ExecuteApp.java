/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import httpserver.common.BasicResponse;
import common.AppCallInfo.*;
import common.AppConfig;
import common.DBConnectionPool;
import common.DBHelper;
import docker.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import httpserver.Debugging;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author cuong
 */

@Path("/execute")
@Debugging
public class ExecuteApp {
    
    @Inject DockerAdapter docker;
    @Inject DBConnectionPool connectionPool;
    
    @Path("/batch/{callId}")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(
        @PathParam("callId") String callId,
        @FormDataParam("json") InputStream jsonInput,
        @FormDataParam("binary") InputStream binaryInput) throws SQLException, IOException
    {
        //TODO get appName, imageName from database
        BatchAppCallInfo callInfo = DBHelper.retrieveBatchCallInfo(callId);
        
        // prepare input files to mount to container
        String jsonFile = null;
        if (callInfo.hasJsonInput && jsonInput != null) {
            File temp = File.createTempFile("aaa", "bbb", new File(AppConfig.Inst.APP_INPUT_FILES_DIR));
            try (FileOutputStream jsonStream = new FileOutputStream(temp)) {
                IOUtils.copy(jsonInput, jsonStream);
            }
            jsonFile = temp.getAbsolutePath();
        }
        String binFile = null;
        if (callInfo.hasBinaryInput && binaryInput != null) {
            File temp = File.createTempFile("aaa", "bbb", new File(AppConfig.Inst.APP_INPUT_FILES_DIR));
            try (FileOutputStream binStream = new FileOutputStream(temp)) {
                IOUtils.copy(binaryInput, binStream);
            }
            binFile = temp.getAbsolutePath();
        }

        // really start container
        String containerId = docker.startBatchApp(callInfo.image, jsonFile, binFile);
        System.out.println("Started, container_id = " + containerId);

        // update call status
        try (
            Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE app_call SET status = ?, container_id = ? WHERE call_id = ?");
        ){
            stmt.setString(1, "Started");
            stmt.setString(2, containerId);
            stmt.setString(3, callId);
            int nrows = stmt.executeUpdate();
            if (nrows == 1) {
                return Response
                    .ok(new BasicResponse(""))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            } else {
                return Response
                    .ok(new BasicResponse("Fail to update container_id in the databse"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
            }
        }
    }
}
