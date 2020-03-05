package test_lib.rxjava;

import io.reactivex.rxjava3.core.Observable;

public class error_in_doOnNext {
    public static void main(String[] args) {
        Observable
            .just(1, 2, 3)
            .doOnNext(System.out::println)
            .doOnNext(n -> {throw new Exception();})
            .blockingLast();
    }
}
