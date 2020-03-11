package end2end.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.ws.rs.core.Response;

public class Helper {
    public static JsonObject readJson(Response response) {
        String s = response.readEntity(String.class);
        return new Gson().fromJson(s, JsonObject.class);
    }
}
