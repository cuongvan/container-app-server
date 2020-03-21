package test_lib.docker;

import com.github.dockerjava.api.DockerClient;
import docker.DockerAdapter;
import helpers.MyFileUtils;
import java.io.IOException;
import java.io.InputStream;

public class CopyFileOutofContainer {

    public static void main(String[] args) throws IOException {
//        DockerClient docker = DockerAdapter.newClient();
//        InputStream in = docker.copyArchiveFromContainerCmd("01e0997fd3b5", "/output").exec();
//        MyFileUtils.untar(in, "/ram/app-outputs/12345");
    }
}
