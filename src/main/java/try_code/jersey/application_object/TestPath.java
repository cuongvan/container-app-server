package try_code.jersey.application_object;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/")
public class TestPath {
    @Inject MyData data;
    @Inject String data2;
    
    
    @GET
    @Produces("text/plain")
    public String get() {
        return "Data: " + data.toString() + " - " + data2;
    }
}
