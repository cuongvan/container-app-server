package try_code.jersey.application_object;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class main {

    public static void main(String[] args) throws Exception {
        ResourceConfig conf = new MyApplication();
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        JdkHttpServerFactory.createHttpServer(baseUri, conf);

        System.out.println("Started");
    }

    public static class MyApplication extends ResourceConfig {
        public MyApplication() {
            packages(true, main.class.getPackage().getName());
            register(new MyApplicationBinder());
        }
    }
    
    static class MyApplicationBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(MyData.class).to(MyData.class);
            bind("Cuong").to(String.class);
        }
    }
}
