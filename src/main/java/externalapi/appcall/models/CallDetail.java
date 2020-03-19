package externalapi.appcall.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CallDetail {
    private String callId;
    private String appId;
    private String userId;
    private long elapsedSeconds;
    private CallStatus callStatus;
    private String output;
    private List<CallParam> params = new ArrayList<>();
    
    public CallDetail withCallId(String callId) {
        this.callId = callId;
        return this;
    }

    public CallDetail withAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public CallDetail withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public CallDetail withElapsedSeconds(long elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
        return this;
    }

    public CallDetail withCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
        return this;
    }

    public CallDetail withOutput(String output) {
        this.output = output;
        return this;
    }

    public CallDetail addCallParam(CallParam param) {
        params.add(param);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////
    
    public String getCallId() {
        return callId;
    }

    public String getAppId() {
        return appId;
    }

    public String getUserId() {
        return userId;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public String getOutput() {
        return output;
    }

    public List<CallParam> getParams() {
        return params;
    }
}
