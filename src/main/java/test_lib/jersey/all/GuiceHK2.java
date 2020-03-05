package test_lib.jersey.all;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("guice")
public class GuiceHK2 {
    @Inject MyData data;
    
    @GET
    public String get() {
        return "data: " + data;
    }
}
