package end2end;

import externalapi.appinfo.DBAppInfoDAO;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.ParamType;
import helpers.DBHelper;
import helpers.TestConstants;
import static helpers.TestHelper.createNewApp;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import main.Main;
import org.junit.*;


public class ExecuteAppTest {
    
    DBAppInfoDAO appInfoDAO = new DBAppInfoDAO(DBHelper.getPool());
    
    @BeforeClass
    public static void setup() throws Exception {
        RestAssured.baseURI = TestConstants.BASE_URI;
        RestAssured.port = TestConstants.PORT;
        Main.main();
    }
    
    @AfterClass
    public static void teardown() throws Exception {
        Main.stop();
    }
    
    @Test
    public void create_app_returns_201_created() throws Exception {
        String appId = createNewApp();
        List<AppParam> params = Arrays.asList(
            AppParam.builder()
                .setName("algorithm")
                .setType(ParamType.KEY_VALUE)
                .setLabel("Algorithm").build(),
            AppParam.builder()
                .setName("file2anonymize")
                .setType(ParamType.FILE)
                .setLabel("File to anonymize").build());
        
        appInfoDAO.updateParams(appId, params);
        
        File file = File.createTempFile("file", "");
        file.deleteOnExit();
        
        given()
            .contentType("multipart/form-data")
            .multiPart("algorithm", "k-anomity")
            .multiPart("file2anonymize", file)
        .when()
            .post("/app/{appId}/execute?userId=1111", appId)
        .then()
            .statusCode(201)
        ;
    }
}
