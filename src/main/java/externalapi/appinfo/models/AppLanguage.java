package externalapi.appinfo.models;

public enum AppLanguage {
    PYTHON_36("python-3.6"),
    PYTHON_27("python-2.7"),
    JAVA_8_JAR("java-8-jar"),
    JAVA_8_GRADLE("java-8-gradle"),
    NODEJS_14("nodejs-14");
    
    // name of the template directory
    public final String templateDir;

    private AppLanguage(String templateDir) {
        this.templateDir = templateDir;
    }
    
    
    public static AppLanguage fromString(String str) {
        for (AppLanguage language : values()) {
            if (language.name().equalsIgnoreCase(str))
                return language;
        }
        
        throw new IllegalArgumentException("Unknown language: " + str);
    }
}
