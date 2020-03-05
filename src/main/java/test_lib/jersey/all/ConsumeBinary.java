package test_lib.jersey.all;

import javax.ws.rs.*;

@Path("binary")
public class ConsumeBinary {
    @POST
    public String post(byte[] body) {
        return "Body size: " + body.length;
    }
}
