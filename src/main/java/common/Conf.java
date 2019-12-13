/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author cuong
 */
public class Conf {
    public static final int HTTP_PORT = 5000;
    
    public static final String APP_BUILD_DIR = "./app_builds";
    public static final String APP_BUILD_FAILED_DIR = "./app_builds/_failed";
    
    public static final long COMMAND_STATUS_CHECK_INTERVAL = 1;
    
    public static final String POSTGRES_HOST = "localhost:5432";
    public static final String POSTGRES_USER = "ckan_default";
    public static final String POSTGRES_PASS = "123456";
    public static final String POSTGRES_DATABASE = "ckan_default";
}
