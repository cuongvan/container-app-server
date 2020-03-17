package common;

public class Constants {
    public static final int SERVER_PORT = 5001;
    public static final String DB_HOST = "localhost:5432";
    public static final String DB_USER = "ckan_default";
    public static final String DB_PASSWORD = "ckan_default";
    public static final String DB_DATABASE = "ckan_default";
    public static final String JDBC_CONNECTION_STRING = String.format("jdbc:postgresql://%s/%s", DB_HOST, DB_DATABASE);
    
    public static final String DOCKER_BUILD_TEMPLATE_DIR = "docker_build_files";
    public static final String DOCKER_BUILD_DIR = "../ckanapp/builds";
    public static final String DOCKER_BUILD_FAILED_DIR = "../ckanapp/builds-failed";
    public static final String DOCKER_BUILD_EXTRACE_CODE_DIR = "code";
    
    public static final String APP_CODE_FILES_DIR = "../ckanapp/codes";
    public static final String APP_INPUT_FILES_DIR = "../ckanapp/inputs";
    
    public static final String CONTAINER_INPUT_FILES_MOUNT_DIR = "/files";
    public static final String CONTAINER_ID_LABEL_KEY = "ckan.callid";
}
