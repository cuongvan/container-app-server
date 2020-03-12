package unit;

import docker.DockerAdapter;
import externalapi.appcall.AppCallDAO;
import externalapi.appinfo.AppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.AppParamDAO;
import handlers.ExecuteHandler;
import java.io.IOException;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteAnAppTest {
    @Mock DockerAdapter docker;
    @Mock AppInfoDAO appInfoDAO;
    @Mock AppCallDAO appCallDAO;
    @Mock AppParamDAO appParamDAO;
    
    ExecuteHandler handler;

    @Before
    public void setup() {
        handler = new ExecuteHandler(docker, appInfoDAO, appCallDAO, appParamDAO);
    }

    @Test
    public void executeFoundApp() throws IOException {
        when(appInfoDAO.getById("app-id")).thenReturn(appWithImage("app-image"));
        
        HashMap<String, byte[]> files = new HashMap<String, byte[]>() {{
            put("datasetId", "123".getBytes());
            put("dataFile", "file content".getBytes());
        }};
        //when(appParamDAO.getAppParams(appId))
        
        handler.execute("app-id", "user-id", files);
        
        verify(appCallDAO).createNewCall(eq("app-id"), eq("user-id"), anyList(), anyList());
    }

    private static AppInfo appWithImage(String image) {
        return new AppInfo().setImage(image);
    }
}
