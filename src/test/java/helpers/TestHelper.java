package helpers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import static helpers.TestConstants.BASE_URI_PORT;
import java.io.File;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class TestHelper {
    public static JsonObject sampleApp() {
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
        
        return app;
    }
    
    public static String createNewAppWithExistingCodeFile(JsonObject app) {
        return createNewApp(app, new File("./example_apps/python/hello-world/code.zip"));
    }
    
    public static String createNewApp(JsonObject app, File codeFile) {
        FormDataMultiPart multiPart = new FormDataMultiPart()
            .field("app_info", new Gson().toJson(app))
            .field("code_file", codeFile, MediaType.APPLICATION_OCTET_STREAM_TYPE)
            ;
        
        Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
        Response response = client.target(BASE_URI_PORT + "/app/create")
            .request()
            .post(Entity.entity(multiPart, multiPart.getMediaType()));
        
        JsonObject json = readJson(response);
        return json.get("app_id").getAsString();
    }
    
    public static JsonObject readJson(Response response) {
        String s = response.readEntity(String.class);
        return new Gson().fromJson(s, JsonObject.class);
    }
}
