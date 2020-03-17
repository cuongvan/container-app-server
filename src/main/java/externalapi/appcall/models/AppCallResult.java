package externalapi.appcall.models;

public class AppCallResult {
    private final String appCallId;
    private final CallStatus callStatus;
    private final long elapsedSeconds;
    private final String output;

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
    
    public String getAppCallId() {
        return appCallId;
    }

    public CallStatus getCallStatus() {
        return CallStatus.SUCCESS;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public String getOutput() {
        return output;
    }
}
