package externalapi.appparam;

import externalapi.appinfo.models.InputFieldType;


public class AppParam {
    public String appId;
    public String name;
    public InputFieldType type;
    public String label;
    public String description;
    
    
    @Override
    public String toString() {
        return "AppParam{" + "name=" + name + ", type=" + type + ", label=" + label + ", description=" + description + '}';
    }
}
