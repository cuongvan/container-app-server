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
public class Conf {
    public static final Conf Inst = readConfigFile();
    public final int HTTP_PORT;

    public final String APP_BUILD_DIR = "./app_builds";
    public final String APP_BUILD_FAILED_DIR = "./app_builds/_failed";

    public final long COMMAND_STATUS_CHECK_INTERVAL = 1;

    public final String POSTGRES_HOST;
    public final String POSTGRES_USER;
    public final String POSTGRES_PASS;
    public final String POSTGRES_DATABASE;

    public Conf(int HTTP_PORT, String POSTGRES_HOST, String POSTGRES_USER, String POSTGRES_PASS, String POSTGRES_DATABASE) {
        this.HTTP_PORT = HTTP_PORT;
        this.POSTGRES_HOST = POSTGRES_HOST;
        this.POSTGRES_USER = POSTGRES_USER;
        this.POSTGRES_PASS = POSTGRES_PASS;
        this.POSTGRES_DATABASE = POSTGRES_DATABASE;
    }
    
    public static void init() {
        
    }
    
    public static void main(String[] args) {
        readConfigFile();
    }

    private static Conf readConfigFile() {
        try (InputStream input = Conf.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }

            prop.load(input);
            
            return new Conf(
                Integer.parseInt(prop.getProperty("http.port")),
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
