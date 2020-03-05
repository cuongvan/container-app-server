package test_lib.jersey.all;

import javax.inject.Singleton;

@Singleton
public class SingletonData {
    public SingletonData() {
        System.out.println("Singleton created");
    }
}
