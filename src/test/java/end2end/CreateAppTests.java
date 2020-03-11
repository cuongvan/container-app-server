package end2end;

import common.AppConfig;
import helpers.TestConstants;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import main.Main;
import static org.hamcrest.Matchers.hasKey;
import org.junit.*;


public class CreateAppTests {
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
        given()
            .contentType("application/json")
            .body("{\"app_name\": \"Test app\", \"language\": \"PYTHON\"}")
        .when()
            .post("/app/create")
        .then()
            .statusCode(201)
            .body("$", hasKey("app_id"))
        ;
    }
}
