package externalapi.appcall.models;

public class KeyValueParam extends CallParam {
    private String value;

    public KeyValueParam(String name, String value) {
        super(name);
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
