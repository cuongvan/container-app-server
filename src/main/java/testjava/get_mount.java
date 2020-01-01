/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjava;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import common.DockerClientPool;
import java.io.IOException;

/**
 *
 * @author cuong
 */
public class get_mount {
    public static void main(String[] args) throws IOException {
        DockerClient docker = DockerClientPool.Instance.borrowClient();
        InspectContainerResponse inspect = docker.inspectContainerCmd("3e1918db6a23757f7936f9cdbba3a4bf9b92b85873aa0714263154e67d2ab5ed").exec();
        inspect.getMounts().stream()
            .map(mount -> mount.getSource())
            .forEach(x -> System.out.println(x));
        docker.close();
    }
}
