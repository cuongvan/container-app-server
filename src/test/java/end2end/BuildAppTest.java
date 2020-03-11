package end2end;

import com.google.gson.JsonObject;
import static end2end.helper.Helper.readJson;
import end2end.helper.TestApplication;
import httpserver.endpoints.app.create.*;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BuildAppTest extends JerseyTest {
    @Override
    protected Application configure() {
        return new TestApplication() {{
            register(CreateApp.class);
            register(BuildApp.class);
        }};
    }
    
    @Test
    public void create_and_build_app_returns_202_accepted() throws IOException {
        String appId = createApp(this);
        Response response = target(format("/app/%s/build", appId)).request().post(
            Entity.entity(
                Files.readAllBytes(Paths.get("./example_apps/python/hello-world/code.zip")),
                MediaType.APPLICATION_OCTET_STREAM));
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        // assert notification
    }
    
    public static String createApp(JerseyTest test) {
        Response response = test.target("/app/create").request().post(
            Entity.json("{\n" +
            "    \"app_name\": \"Test app\",\n" +
            "    \"language\": \"PYTHON\"\n" +
            "}"));
        JsonObject json = readJson(response);
        return json.get("app_id").getAsString();
    }
}
