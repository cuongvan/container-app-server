package helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import static helpers.TestConstants.BASE_URI_PORT;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class TestHelper {
    public static String createNewApp() {
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI_PORT + "/app/create").request().post(
            Entity.json("{\"app_name\": \"Test app\", \"language\": \"PYTHON\"}"));
        JsonObject json = readJson(response);
        return json.get("app_id").getAsString();
    }
    
    public static JsonObject readJson(Response response) {
        String s = response.readEntity(String.class);
        return new Gson().fromJson(s, JsonObject.class);
    }
}
