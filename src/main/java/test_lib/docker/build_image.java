package test_lib.docker;

import docker.DockerAdapter;
import io.reactivex.rxjava3.core.Single;

public class build_image {
    public static void main(String[] args) throws Exception {
        DockerAdapter docker = new DockerAdapter();
        Single<String> single;
        //docker.buildImage("./tmp/builds/1123213714368793687", "123");
        //Single<String> single = docker.buildImage2("./tmp/builds/1123213714368793687");
        single = Single.fromCallable(() -> docker.buildImage("./tmp/builds/1123213714368793687", "123"));
            
//        single = Single.fromCallable(() -> {
//            Thread.sleep(5000);
//            return "111";
//        });
        single.subscribe(
            id -> System.out.println("Success: " + id),
            //err -> err.printStackTrace()
            err -> System.out.println(err)
        );
        Thread.sleep(5000);
    }
}
