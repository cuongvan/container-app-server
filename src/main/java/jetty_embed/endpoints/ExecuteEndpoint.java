/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jetty_embed.endpoints;

import common.Conf;
import workers.PollingContainerStatusWorker;
import docker.*;
import common.RunningContainer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import jetty_embed.Debugging;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author cuong
 */

@Path("/execute/{appName}")
@Debugging
public class ExecuteEndpoint {
    public static class MResponse {

        public String error;
        public String callId;

        public MResponse() {
            super();
        }

        public MResponse(String error, String callId) {
            super();
            this.error = error;
            this.callId = callId;
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public MResponse execute(@PathParam("appName") String appName, InputStream body) throws FileNotFoundException, IOException {
        if (!DockerUtils.appExists(appName)) {
            return new MResponse("app not exists", "");
        }
        
        String callId = DockerUtils.newCallId();
        String inputFile = Conf.Inst.APP_INPUT_FILES_DIR + "/" + callId;
        try (FileOutputStream file = new FileOutputStream(inputFile)) {
            IOUtils.copy(body, file);
        }
        
        RunningContainer rc = DockerUtils.startContainer(appName, callId, inputFile);
        PollingContainerStatusWorker.submitNewRunningContainer(rc);
        return new MResponse("", callId);
    }
}
