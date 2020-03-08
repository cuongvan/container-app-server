package externalapi.appcall.models;

public class ServerAppCallInfo {

    private final String appId;
    private final String image;
    private final int imagePort;
    private final int hostPort;

    public ServerAppCallInfo(String appId, String image, String portMap) {
        this.appId = appId;
        this.image = image;
        String[] ports = portMap.split(":");
        hostPort = Integer.parseInt(ports[0]);
        imagePort = Integer.parseInt(ports[1]);
    }

    public String getAppId() {
        return appId;
    }

    public String getImage() {
        return image;
    }

    public int getImagePort() {
        return imagePort;
    }

    public int getHostPort() {
        return hostPort;
    }
}
