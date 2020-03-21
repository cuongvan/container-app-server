package externalapi.appcall.models;

import externalapi.appinfo.models.InputFieldType;

public class CallInputEntry {
    public final InputFieldType type;
    public final String name;
    public final String value;

    public CallInputEntry(InputFieldType type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
}
