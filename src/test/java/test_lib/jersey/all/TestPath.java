package test_lib.jersey.all;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/")
public class TestPath {
    @Inject SingletonData data;
    
    
    @GET
    @Produces("text/plain")
    public String get() {
        return "Data: " + data.toString();
    }
}
