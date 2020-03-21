package externalapi.appcall.models;

public class CallResult {
    public final CallStatus callStatus;
    public final long elapsedSeconds;

    public CallResult(CallStatus callStatus, long elapsedSeconds) {
        this.callStatus = callStatus;
        this.elapsedSeconds = elapsedSeconds;
    }
}
