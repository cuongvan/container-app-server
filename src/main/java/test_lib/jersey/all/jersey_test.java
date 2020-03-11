package test_lib.jersey.all;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class jersey_test {

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new GuiceModule());
        ResourceConfig conf = new GuiceResourceConfig(injector);
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        JdkHttpServerFactory.createHttpServer(baseUri, conf);

        System.out.println("Started");
    }

    public static class GuiceResourceConfig extends ResourceConfig {

        public GuiceResourceConfig(Injector injector) {
            packages(true, jersey_test.class.getPackage().getName());
            register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
            register(new SetupGuiceHK2Bridge(injector));

        }
    }
    
    static class SetupGuiceHK2Bridge implements ContainerLifecycleListener {
        private Injector injector;

        public SetupGuiceHK2Bridge(Injector injector) {
            this.injector = injector;
        }
        
        @Override
        public void onStartup(Container container) {
            ServiceLocator serviceLocator = container.getApplicationHandler().getInjectionManager().getInstance(ServiceLocator.class);
            GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
            GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
            guiceBridge.bridgeGuiceInjector(injector);
        }

        @Override
        public void onReload(Container cntnr) {
        }

        @Override
        public void onShutdown(Container cntnr) {
        }
    }

    static class MyApplicationBinder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(new SingletonData()).to(SingletonData.class);
        }
    }

    private static class GuiceModule extends AbstractModule {
        @Override
        protected void configure() {
            //bind(MyData.class).toProvider(() -> new MyData());
            //bind(MyData.class).to
        }
    }
}
