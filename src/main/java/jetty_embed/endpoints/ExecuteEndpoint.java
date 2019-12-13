/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jetty_embed.endpoints;

import workers.PollingContainerStatusWorker;
import docker.*;
import common.RunningContainer;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import jetty_embed.Debugging;

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
    public MResponse execute(@PathParam("appName") String appName, String input) {
        if (!DockerUtils.appExists(appName)) {
            return new MResponse("app not exists", "");
        }
        
        String callId = DockerUtils.newCallId();
        RunningContainer rc = DockerUtils.startContainer(appName, callId, input);
        PollingContainerStatusWorker.submitNewRunningContainer(rc);
        return new MResponse("", callId);
    }
}
