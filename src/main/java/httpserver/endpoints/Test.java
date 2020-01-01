/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.endpoints;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author cuong
 */
@Path("/test")
public class Test {
    @GET
    public String aaa(@QueryParam("bool") boolean val) {
        return Boolean.toString(val);
    }
    
    @Path("/post")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String newApp(
        @FormDataParam("json") InputStream jsonInput,
        @FormDataParam("binary") InputStream binaryInput
    ) throws IOException
    {
        return jsonInput + " " + binaryInput;
    }
    
    @Path("/stream")
    @POST
    public String bbb(InputStream what) {
        return "in: " + what;
    }
}
