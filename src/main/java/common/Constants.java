package common;

public class Constants {
    public static final int HTTP_PORT = 5001;
    public static final String DB_HOST = "localhost:5432";
    public static final String DB_USER = "ckan_default";
    public static final String DB_PASSWORD = "ckan_default";
    public static final String DB_DATABASE = "ckan_default";
    public static final String JDBC_CONNECTION_STRING = String.format("jdbc:postgresql://%s/%s", DB_HOST, DB_DATABASE);
    
    public static final String DOCKER_BUILD_TEMPLATE_DIR = "docker_build_files";
    public static final String DOCKER_BUILD_EXTRACE_CODE_DIR = "code";
    public static final String APP_BUILD_DIR = "./tmp/builds";
    public static final String APP_BUILD_FAILED_DIR = "./tmp/builds-failed";
    public static final String APP_INPUT_FILES_DIR = "./tmp/input-files";
    public static final String JSON_MOUNT_PATH = "/inputJson";
    public static final String FILE_MOUNT_PATH = "/inputBinary";
    public static final String FILES_MOUNT_DIR = "/files";
    
    // container labels
    public static final String CONTAINER_ID_LABEL_KEY = "ckan.callid";
}
