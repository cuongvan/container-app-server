/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author cuong
 */
public class AppCallInfo {
    public final AppType type;

    public AppCallInfo(AppType type) {
        this.type = type;
    }

    public static class BatchAppCallInfo extends AppCallInfo {
        public final String callId;
        public final String image;
        public final boolean hasJsonInput;
        public final boolean hasBinaryInput;

        public BatchAppCallInfo(String callId, String image, boolean hasJsonInput, boolean hasBinaryInput) {
            super(AppType.Batch);
            this.callId = callId;
            this.image = image;
            this.hasJsonInput = hasJsonInput;
            this.hasBinaryInput = hasBinaryInput;
        }
    }

    public static class ServerAppCallInfo extends AppCallInfo {
        public final String appId;
        public final String image;
        public final int imagePort;
        public final int outsidePort;

        public ServerAppCallInfo(String appId, String image, String portMap) {
            super(AppType.Server);
            this.appId = appId;
            this.image = image;
            String[] ports = portMap.split(":");
            outsidePort = Integer.parseInt(ports[0]);
            imagePort = Integer.parseInt(ports[1]);
        }
    }
    
}
