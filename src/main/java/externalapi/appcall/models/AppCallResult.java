package externalapi.appcall.models;

public class AppCallResult {
    private final String appCallId;
    private final boolean success;
    private final long elapsedSeconds;
    private final String output;

    public AppCallResult(String appCallId, boolean success, long elapsedSeconds, String output) {
        this.appCallId = appCallId;
        this.success = success;
        this.elapsedSeconds = elapsedSeconds;
        this.output = output;
    }
    

    @Override
    public String toString() {
        return "AppCallResult{" + "appCallId=" + appCallId + ", success=" + success + ", elapsedSeconds=" + elapsedSeconds + ", output=" + output + '}';
    }

    public String getAppCallId() {
        return appCallId;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public String getOutput() {
        return output;
    }
}
