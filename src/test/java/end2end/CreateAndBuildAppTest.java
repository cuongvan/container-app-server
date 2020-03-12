package end2end;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import helpers.TestConstants;
import static helpers.TestHelper.*;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import java.io.File;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

public class CreateAndBuildAppTest {
    @BeforeClass
    public static void setup() throws Exception {
        RestAssured.baseURI = TestConstants.BASE_URI;
        RestAssured.port = TestConstants.PORT;
    }
    
    @Test
    public void create_and_build_app_returns_202_accepted() throws IOException {
        JsonObject app = new JsonObject();
        app.addProperty("app_name", "Test app");
        app.addProperty("language", "PYTHON");
        {
            JsonArray params = new JsonArray();
            {
                JsonObject param = new JsonObject();
                param.addProperty("name", "userToken");
                param.addProperty("type", "KEY_VALUE");
                param.addProperty("label", "Your API token");
                param.addProperty("description", "Your CKAN API token, used to access your dataset");
            }
            app.add("params", params);
        }
        String appId = createNewAppWithExistingCodeFile(app);
        
        given()
            .contentType("application/octet-stream")
            .body(new File("./example_apps/python/hello-world/code.zip"))
        .when()
            .post("/app/{appId}/build", appId)
        .then()
            .statusCode(202)
        ;
    }
}
