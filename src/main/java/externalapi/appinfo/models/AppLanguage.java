package externalapi.appinfo.models;

public enum AppLanguage {
    Python_36,
    Java_8,
    JavaScript;
    
    public static AppLanguage fromString(String str) {
        for (AppLanguage language : values()) {
            if (language.name().equalsIgnoreCase(str))
                return language;
        }
        
        throw new IllegalArgumentException("Unknown language: " + str);
    }
}
