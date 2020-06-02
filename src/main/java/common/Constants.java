package common;

public class Constants {
    public static final String DOCKER_BUILD_TEMPLATE_DIR = "./templates/docker_build";
    
    public static final String APP_INPUT_FILES_DIR = "../ckanapp/inputs";
    public static final String CONTAINER_INPUT_FILES_MOUNT_DIR = "/files";
    
    public static final String APP_OUTPUT_FILES_DIR = "../ckanapp/outputs";
    public static final String CONTAINER_OUTPUT_FILES_DIR = "/outputs";
    public static final String CONTAINER_OUTPUT_FILE_RELATIVE_PATH = "output.json"; // /outputs/output.json
    public static final String CONTAINER_OUTPUT_BINARY_FILES_RELATIVE_PATH = "files"; // /outputs/files/
}
