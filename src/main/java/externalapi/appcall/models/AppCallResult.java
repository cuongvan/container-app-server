package externalapi.appcall.models;

public class AppCallResult {
    public final String appCallId;
    public final CallStatus callStatus;
    public final long elapsedSeconds;
    public final String output;

    public AppCallResult(String appCallId, CallStatus callStatus, long elapsedSeconds, String output) {
        this.appCallId = appCallId;
        this.callStatus = callStatus;
        this.elapsedSeconds = elapsedSeconds;
        this.output = output;
    }

    @Override
    public String toString() {
        return "AppCallResult{" + "appCallId=" + appCallId + ", callStatus=" + callStatus + ", elapsedSeconds=" + elapsedSeconds + ", output=" + output + '}';
    }
}
