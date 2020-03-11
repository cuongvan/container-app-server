/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author cuong
 */
public class AppConfig {
    public enum RunningMode { DEBUG, TEST, PRODUCTION }
    public static RunningMode RUNNING_MODE = RunningMode.DEBUG;
    
    public static final AppConfig Inst = readConfigFile();
    public final int HTTP_PORT = 5001;

    public final String APP_BUILD_DIR = "./tmp/builds";
    public final String APP_BUILD_FAILED_DIR = "./tmp/builds-failed";
    public final String APP_INPUT_FILES_DIR = "./tmp/input-files";

    // use to filter containers related to this server
//    public final String CKAN_APP_CONTAINER_LABEL = "ckanapp";

    public final String POSTGRES_HOST;
    public final String POSTGRES_USER;
    public final String POSTGRES_PASS;
    public final String POSTGRES_DATABASE;

    public AppConfig(String POSTGRES_HOST, String POSTGRES_USER, String POSTGRES_PASS, String POSTGRES_DATABASE) {
        this.POSTGRES_HOST = POSTGRES_HOST;
        this.POSTGRES_USER = POSTGRES_USER;
        this.POSTGRES_PASS = POSTGRES_PASS;
        this.POSTGRES_DATABASE = POSTGRES_DATABASE;
    }
    
    public static void init() {
        
    }
    
    private static AppConfig readConfigFile() {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }

            prop.load(input);
            
            return new AppConfig(
                prop.getProperty("postgres.host"),
                prop.getProperty("postgres.user"),
                prop.getProperty("postgres.pass"),
                prop.getProperty("postgres.db"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading config.properties");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing config.properties");
        }
    }
}
