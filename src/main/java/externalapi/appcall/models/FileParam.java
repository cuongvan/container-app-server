package externalapi.appcall.models;

import externalapi.appinfo.models.ParamType;

public class FileParam extends CallParam {
    private String filePath;

    public FileParam(String name, String filePath) {
        super(name);
        this.filePath = filePath;
    }

    @Override
    public ParamType getType() {
        return ParamType.FILE;
    }
    
    public String getFilePath() {
        return filePath;
    }
}
