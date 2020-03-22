package externalapi.appcall.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CallDetail {
    public String callId;
    public String appId;
    public String userId;
    public long elapsedSeconds;
    public CallStatus callStatus;
    public String logs;
    public String createdAt;
    public List<CallInputEntry> inputs;
    public List<CallOutputEntry> outputs;
}
