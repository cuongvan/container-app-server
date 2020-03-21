package watchers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallOutputEntry {
    public final OutputFieldType type;
    public final String name;
    public final String value;

    public CallOutputEntry(
        @JsonProperty("type") OutputFieldType type,
        @JsonProperty("name") String name,
        @JsonProperty("value") String value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "CallOutputEntry{" + "type=" + type + ", name=" + name + ", value=" + value + '}';
    }
}
