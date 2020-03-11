package externalapi.appcall.models;

public class FileParam extends CallParam {
    private String filePath;

    public FileParam(String name, String filePath) {
        super(name);
        this.filePath = filePath;
    }
    
    public String getFilePath() {
        return filePath;
    }
}
