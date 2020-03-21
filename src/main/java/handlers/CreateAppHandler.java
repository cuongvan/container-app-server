package handlers;

import common.Constants;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppStatus;
import helpers.MiscHelper;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;

public class CreateAppHandler {
    @Inject
    private AppInfoDAO appInfoDAO;
    
    public String createApp(AppInfo app, byte[] codeFile, byte[] avatarFile) throws IOException {
        String appId = MiscHelper.newId();
        
        Path codePath = codePath(appId);
        Files.write(codePath, codeFile);
        app.setCodePath(codePath.toString());
        
        Path avatarPath = avatarPath(appId);
        app.setAvatarPath(avatarPath.toString());
        if (avatarFile != null) {
            Files.write(avatarPath, avatarFile);
        } else {
            String defaultAvatarPath = Constants.APP_DEFAULT_AVATAR_PATH;
            FileUtils.copyFile(new File(defaultAvatarPath), avatarPath.toFile());
        }
        
        app.setAppStatus(AppStatus.BUILDING);
        appInfoDAO.createApp(appId, app);
        
        return appId;
    }
    
    private static Path codePath(String appId) {
        String filename = format("%s.zip", appId);
        return Paths.get(Constants.APP_CODE_FILES_DIR, filename).toAbsolutePath().normalize();
    }
    
    private static Path avatarPath(String appId) {
        String filename = format("%s.jpg", appId);
        return Paths.get(Constants.APP_AVATARS_DIR, filename).toAbsolutePath().normalize();
    }
}
