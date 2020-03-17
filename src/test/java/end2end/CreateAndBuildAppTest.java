package end2end;

import com.google.gson.*;
import helpers.TestConstants;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import java.io.*;
import org.junit.*;

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
        
        given()
            .contentType("multipart/form-data")
            .multiPart("app_info", new Gson().toJson(app))
            .multiPart("code_file", new File("./example_apps/python/hello-world/code.zip"))
        .when()
            .post("/app/create")
        .then()
            .statusCode(201)
        ;
    }
}
