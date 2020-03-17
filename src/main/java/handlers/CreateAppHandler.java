package handlers;

import common.Constants;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import helpers.MiscHelper;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;

public class CreateAppHandler {
    
    private AppInfoDAO appInfoDAO;
    @Inject
    public CreateAppHandler(AppInfoDAO appInfoDAO) {
        this.appInfoDAO = appInfoDAO;
    }
    
    public String createApp(AppInfo app, byte[] codeFile) throws IOException {
        String appId = MiscHelper.newId();
        Path codePath = codePath(appId);
        System.out.println(">> code path: " + codePath);
        Files.write(codePath, codeFile);
        
        app = AppInfo.builder(app).withCodePath(codePath.toString()).build();
        
        appInfoDAO.createApp(appId, app);
        return appId;
    }
    
    private static Path codePath(String appId) {
        String filename = format("%s.zip", appId);
        return Paths.get(Constants.APP_CODE_FILES_DIR, filename).toAbsolutePath().normalize();
    }
}
