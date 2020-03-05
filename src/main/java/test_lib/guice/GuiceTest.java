package test_lib.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javax.inject.Singleton;

public class GuiceTest {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }
        });
        
        {
            NotSingleton i1 = injector.getInstance(NotSingleton.class);
            NotSingleton i2 = injector.getInstance(NotSingleton.class);
        }
        
        {
            injector.getInstance(SingletonCls.class);
            injector.getInstance(SingletonCls.class);
            injector.getInstance(SingletonCls.class);
        }
    }
    
    static class NotSingleton {
        public NotSingleton() {
            System.out.println("NotSingleton created");
        }
    }
    
    @Singleton
    static class SingletonCls {
        public SingletonCls() {
            System.out.println("Singleton created");
        }
    }
}
