package externalapi.appcall.models;

public class BatchAppCallInfo {

    private final String callId;
    private final String imageName;
    private final boolean hasJsonInput;
    private final boolean hasBinaryInput;

    public BatchAppCallInfo(String callId, String image, boolean hasJsonInput, boolean hasBinaryInput) {
        this.callId = callId;
        this.imageName = image;
        this.hasJsonInput = hasJsonInput;
        this.hasBinaryInput = hasBinaryInput;
    }

    public String getCallId() {
        return callId;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isHasJsonInput() {
        return hasJsonInput;
    }

    public boolean isHasBinaryInput() {
        return hasBinaryInput;
    }
}
