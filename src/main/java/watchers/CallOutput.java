package watchers;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CallOutput {
    public List<CallOutputEntry> fields;

    @Override
    public String toString() {
        return "CallOutput{fields=\n" + fields + '}';
    }
}
