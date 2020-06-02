package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    
    public final int port;
    public final String dataDir;
    public final String dockerBuildDir;
    public final String dockerBuildTemplateDir;
    public final String DB_HOST;
    public final String DB_USER;
    public final String DB_PASSWORD;
    public final String DB_DATABASE;
    public final String JDBC_CONNECTION_STRING;
    
    public Config(Properties props) {
        port = Integer.parseInt(props.getProperty("port"));
        dataDir= props.getProperty("data.dir");
        dockerBuildDir = Paths.get(dataDir, "docker-builds").toString();
        dockerBuildTemplateDir = Paths.get("templates", "docker_build").toAbsolutePath().toString();
        
        DB_HOST = props.getProperty("database.hostport");
        DB_USER = props.getProperty("database.username");
        DB_PASSWORD = props.getProperty("database.password");
        DB_DATABASE = props.getProperty("database.schema");
        JDBC_CONNECTION_STRING = String.format("jdbc:postgresql://%s/%s", DB_HOST, DB_DATABASE);
    }
    
    public static Config loadConfig() {
        try {
            Properties defaultConfig = new Properties();
            InputStream defaultConfigFile = Config.class.getResourceAsStream("/config.properties");
            defaultConfig.load(defaultConfigFile);
            Properties customConfig = new Properties();
            if (System.getProperty("config.file") != null) {
                customConfig.load(new FileInputStream(System.getProperty("config.file")));
            }
            
            Properties finalProperties = new Properties();
            finalProperties.putAll(defaultConfig);
            finalProperties.putAll(customConfig);
            finalProperties.putAll(System.getProperties());
            return new Config(finalProperties);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load config", ex);
        }
    }
}
