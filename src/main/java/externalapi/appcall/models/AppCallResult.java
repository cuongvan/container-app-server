package externalapi.appcall.models;

import java.util.List;

public class AppCallResult {
    public final CallStatus callStatus;
    public final long elapsedSeconds;
    public final String output;
    public final List<CallOutputEntry> outputs;

    public AppCallResult(CallStatus callStatus, long elapsedSeconds, String output, List<CallOutputEntry> outputs) {
        this.callStatus = callStatus;
        this.elapsedSeconds = elapsedSeconds;
        this.output = output;
        this.outputs = outputs;
    }
    
    @Override
    public String toString() {
        return "AppCallResult{" + ", callStatus=" + callStatus + ", elapsedSeconds=" + elapsedSeconds + ", output size=" + output.length() + '}';
    }
}
