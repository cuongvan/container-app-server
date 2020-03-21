package externalapi.appinfo.models;

import java.util.Objects;

public class AppParam {
    public final String name;
    public final InputFieldType type;
    public final String label;
    public final String description;

    public AppParam(String name, InputFieldType type, String label, String description) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.description = description;
    }

    @Override
    public String toString() {
        return "AppParam{" + "name=" + name + ", type=" + type + ", label=" + label + ", description=" + description + '}';
    }
}
