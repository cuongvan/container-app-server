package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class Config {
    
    public final int port;
    
    public final String dataDir;
    public final String dockerBuildDir;
    public final String dockerBuildTemplateDir;
    public final String appInputFilesDir;
    public final String appOutputFilesDir;
    
    
    public final String databaseHost;
    public final String databaseUser;
    public final String databasePassword;
    public final String databaseSchema;
    public final String jdbcConnectionString;
    
    public Config(Properties props) {
        Map<String, String> env = System.getenv();
        port = Integer.parseInt(props.getProperty("port"));
        
        dataDir = env.getOrDefault("DATA_DIR", props.getProperty("data.dir"));
        dockerBuildDir = Paths.get(dataDir, "docker-builds").toString();
        dockerBuildTemplateDir = Paths.get("templates", "docker_build").toAbsolutePath().toString();
        appInputFilesDir = Paths.get(dataDir, "input-files").toString();
        appOutputFilesDir = Paths.get(dataDir, "output-files").toString();
        
        databaseHost = props.getProperty("database.hostport");
        databaseUser = props.getProperty("database.username");
        databasePassword = props.getProperty("database.password");
        databaseSchema = props.getProperty("database.schema");
        jdbcConnectionString = String.format("jdbc:postgresql://%s/%s", databaseHost, databaseSchema);
    }
    
    public static Config loadConfig() {
        try {
            Properties props = new Properties();
            try (InputStream is = Config.class.getResourceAsStream("/config.properties")) {
                props.load(is);
            }
            
            if (System.getProperty("config.file") != null) {
                try (InputStream is = new FileInputStream(System.getProperty("config.file"))) {
                    props.load(is);
                }
            }
            
            props.putAll(System.getProperties());
            
            return new Config(props);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load config", ex);
        }
    }
}
