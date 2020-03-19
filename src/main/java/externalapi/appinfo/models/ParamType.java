package externalapi.appinfo.models;

public enum ParamType {
    TEXT("ckan.input.text"),
    TEXT_LIST("ckan.input.testlist"),
    
    NUMBER("ckan.input.number"),
    NUMBER_LIST("ckan.input.numberlist"),
    
    BOOLEAN("ckan.input.boolean"),
    
    FILE(null);
    
    
    public final String prefix;

    private ParamType(String prefix) {
        this.prefix = prefix;
    }
}
