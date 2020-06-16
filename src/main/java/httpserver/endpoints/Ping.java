package httpserver.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/ping")
public class Ping {
    
    @GET
    public void ping() {
    }
}
