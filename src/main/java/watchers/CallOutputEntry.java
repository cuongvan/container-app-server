package watchers;

public class CallOutputEntry {
    public OutputParamType type;
    public String name;
    public String value;

    @Override
    public String toString() {
        return "CallOutputEntry{" + "type=" + type + ", name=" + name + ", value=" + value + '}';
    }
}
