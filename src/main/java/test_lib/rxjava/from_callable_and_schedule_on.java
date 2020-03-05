package test_lib.rxjava;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class from_callable_and_schedule_on {
    public static void main(String[] args) {
        Single
            .fromCallable(() -> createObject())
            .subscribeOn(Schedulers.io())
            .blockingGet()
            ;
    }
    
    static Object createObject() {
        System.out.println("Thread: " + Thread.currentThread().getName());
        return new Object();
    }
}
