package externalapi.appcall.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CallDetail {
    public final String callId;
    public final String appId;
    public final String userId;
    public final long elapsedSeconds;
    public final CallStatus callStatus;
    public final List<CallParam> params;

    public CallDetail(String callId, String appId, String userId, long elapsedSeconds, CallStatus callStatus, List<CallParam> params) {
        this.callId = callId;
        this.appId = appId;
        this.userId = userId;
        this.elapsedSeconds = elapsedSeconds;
        this.callStatus = callStatus;
        this.params = params;
    }
}
