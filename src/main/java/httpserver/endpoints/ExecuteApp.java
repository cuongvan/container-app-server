/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import common.AppCallInfo.*;
import common.Conf;
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
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author cuong
 */

@Path("/execute")
@Debugging
public class ExecuteApp {
    @Path("/batch/{callId}")
    @POST
//    @Consumes(MediaType.WILDCARD)
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
            File temp = File.createTempFile("aaa", "bbb", new File(Conf.Inst.APP_INPUT_FILES_DIR));
            try (FileOutputStream jsonStream = new FileOutputStream(temp)) {
                IOUtils.copy(jsonInput, jsonStream);
            }
            jsonFile = temp.getAbsolutePath();
        }
        String binFile = null;
        if (callInfo.hasBinaryInput && binaryInput != null) {
            File temp = File.createTempFile("aaa", "bbb", new File(Conf.Inst.APP_INPUT_FILES_DIR));
            try (FileOutputStream binStream = new FileOutputStream(temp)) {
                IOUtils.copy(binaryInput, binStream);
            }
            binFile = temp.getAbsolutePath();
        }

        // really start container
        String containerId = DockerUtils.startBatchApp(callInfo.image, jsonFile, binFile);
        System.out.println("Started, container_id = " + containerId);

        // update call status
        try (
            Connection conn = DBConnectionPool.getConnection();
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
