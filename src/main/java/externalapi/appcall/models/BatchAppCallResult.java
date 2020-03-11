package externalapi.appcall.models;

public class BatchAppCallResult {

    private String callId;
    private String appId;
    private String userId;
    private long elapsedSeconds;
    private String output;

    public String getCallId() {
        return callId;
    }

    public BatchAppCallResult setCallId(String callId) {
        this.callId = callId;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public BatchAppCallResult setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public BatchAppCallResult setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public BatchAppCallResult setElapsedSeconds(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public BatchAppCallResult setOutput(String output) {
        this.output = output;
        return this;
    }
}
