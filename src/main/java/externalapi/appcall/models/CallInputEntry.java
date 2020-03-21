package externalapi.appcall.models;

import externalapi.appinfo.models.ParamType;

public class CallInputEntry {
    public final ParamType type;
    public final String name;
    public final String value;

    public CallInputEntry(ParamType type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
