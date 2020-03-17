package unittests;

import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import handlers.CreateAppHandler;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CreateAppTest {
    @Mock AppInfoDAO appInfoDAO;
    
    @Test
    public void handlerCallsCollaborators() throws IOException {
        CreateAppHandler handler = new CreateAppHandler(appInfoDAO);
        AppInfo app = AppInfo.builder()
            .build();
        
        String appId = handler.createApp(app, new byte[0]);
        
        verify(appInfoDAO).createApp(eq(appId), any());
    }
    
    @Test
    public void createAnAppWithCorrectArguments() throws IOException {
        CreateAppHandler handler = new CreateAppHandler(appInfoDAO);
        AppInfo app = AppInfo.builder()
            .build();
        
        String appId = handler.createApp(app, new byte[0]);
        
        ArgumentCaptor<AppInfo> appCaptor = ArgumentCaptor.forClass(AppInfo.class);
        verify(appInfoDAO).createApp(anyString(), appCaptor.capture());
        String codePath = appCaptor.getValue().getCodePath();
        assertNotNull(codePath);
        File codeFile = new File(codePath);
        codeFile.deleteOnExit();
        assertTrue("code file should exists", codeFile.exists());
    }
}
