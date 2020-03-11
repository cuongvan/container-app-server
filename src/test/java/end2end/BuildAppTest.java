package end2end;

import helpers.TestConstants;
import static helpers.TestHelper.*;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import java.io.File;
import java.io.IOException;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BuildAppTest {
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
    public void create_and_build_app_returns_202_accepted() throws IOException {
        String appId = createNewApp();
        
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
