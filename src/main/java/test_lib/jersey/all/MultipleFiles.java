package test_lib.jersey.all;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.server.ContainerRequest;

@Path("/files")
public class MultipleFiles {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@Context ContainerRequest request) {
        String contentLength = request.getHeaderString(HttpHeaders.CONTENT_LENGTH);
        if (contentLength != null && Integer.parseInt(contentLength) != 0) {
            FormDataMultiPart multiPart = request.readEntity(FormDataMultiPart.class);
            System.out.println(multiPart.getFields());
            multiPart.getFields().forEach((field, body) -> {
                FormDataBodyPart s = body.get(0);
                BodyPartEntity b = (BodyPartEntity) s.getEntity();
                //System.out.println(b.getInputStream());
            });
            return "" + multiPart.getFields();
        }
        return "no body";
    }
    
//    public String post(FormDataMultiPart body) {
//        System.out.println(body.getFields());
//        return String.valueOf(body.getFields());
//    }
}
