package test_lib.jersey.all;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/form")
public class FormPath {

    @Path("/file1")
    @POST
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@FormDataParam("file") byte[] file) {
        return "Received file length: " + file.length;
    }
    
    @Path("/file2")
    @POST
    @Consumes("*/*")
    public String file2(
        @FormDataParam("file1") byte[] file1,
        @FormDataParam("file2") byte[] file2
    ) {
        return "file 1: " + file1 + ", file2: " + file2;
    }
}
