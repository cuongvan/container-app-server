package end2end;

import com.google.gson.JsonObject;
import static end2end.helper.Helper.*;
import end2end.helper.TestApplication;
import httpserver.endpoints.app.create.CreateApp;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.test.JerseyTest;
import static org.junit.Assert.*;
import org.junit.Test;


public class CreateAndBuildAppTests extends JerseyTest {

    @Override
    protected Application configure() {
        return new TestApplication() {{
            register(CreateApp.class);
        }};
    }
    
    @Test
    public void create_app_returns_201_created() throws Exception {
        Response response = target("/app/create").request().post(
            Entity.json("{\n" +
            "    \"app_name\": \"Test app\",\n" +
            "    \"language\": \"PYTHON\"\n" +
            "}"));
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        JsonObject json = readJson(response);
        assertTrue(json.has("app_id"));
    }
}
