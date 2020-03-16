package end2end;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import externalapi.appinfo.DBAppInfoDAO;
import helpers.DBHelper;
import helpers.TestConstants;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import java.io.File;
import java.io.IOException;
import static org.hamcrest.Matchers.equalTo;
import org.junit.*;
import static helpers.TestHelper.createNewAppWithExistingCodeFile;


public class ExecuteAppTest {
    
    DBAppInfoDAO appInfoDAO = new DBAppInfoDAO(DBHelper.getPool());
    
    @BeforeClass
    public static void setup() throws Exception {
        RestAssured.baseURI = TestConstants.BASE_URI;
        RestAssured.port = TestConstants.PORT;
    }
    
    @Test
    public void execute_app_returns_201_created() throws Exception {
        JsonObject app = new JsonObject();
        app.addProperty("app_name", "Anonymize data file");
        app.addProperty("language", "PYTHON");
        {
            JsonArray params = new JsonArray();
            {
                JsonObject param = new JsonObject();
                param.addProperty("name", "algorithm");
                param.addProperty("type", "KEY_VALUE");
                param.addProperty("label", "Algorithm");
                param.addProperty("description", "Algorithm to anonymize dataset");
                params.add(param);
            }
            {
                JsonObject param = new JsonObject();
                param.addProperty("name", "file");
                param.addProperty("type", "FILE");
                param.addProperty("label", "File to anonymize");
                param.addProperty("description", "A csv data file");
                params.add(param);
            }
            app.add("params", params);
        }
        String appId = createNewAppWithExistingCodeFile(app);
        
        given()
            .contentType("multipart/form-data")
            .multiPart("algorithm", "k-anomity")
            .multiPart("file", tempFile())
        .when()
            .post("/app/{appId}/execute?userId=1111", appId)
        .then()
            .body("error", equalTo(""))
            .statusCode(202)
        ;
    }
    
    public static File tempFile() throws IOException {
        File file = File.createTempFile("file", "");
        file.deleteOnExit();
        return file;
    }
}
