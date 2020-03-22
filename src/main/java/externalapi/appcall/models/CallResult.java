package externalapi.appcall.models;

public class CallResult {
    public CallStatus callStatus;
    public long elapsedSeconds;
    public String logs;

    public CallResult(CallStatus callStatus, long elapsedSeconds) {
        this.callStatus = callStatus;
        this.elapsedSeconds = elapsedSeconds;
    }
}
