package externalapi.appcall.models;

import externalapi.appinfo.models.ParamType;

public class CallParam {
    public final ParamType type;
    public final String name;
    public final String value;

    public CallParam(ParamType type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
