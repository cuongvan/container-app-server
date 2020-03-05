package httpserver.helpers;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class SetupGuiceHK2Bridge implements ContainerLifecycleListener {

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
