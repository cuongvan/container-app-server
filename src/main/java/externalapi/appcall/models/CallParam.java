package externalapi.appcall.models;

import externalapi.appinfo.models.ParamType;

public abstract class CallParam {
    private String name;

    public CallParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    abstract public ParamType getType();
    abstract public String getValue();
}
