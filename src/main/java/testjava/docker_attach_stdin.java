/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjava;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author cuong
 */
public class docker_attach_stdin {

    public static void main(String[] args) throws InterruptedException, IOException {
        DockerClient docker = DockerClientBuilder.getInstance().build();
        Volume inputFileVol = new Volume("/inputfile");
        Volume dumpVol = new Volume("/dump");
        CreateContainerResponse container = docker.createContainerCmd("b3984985d873")
            .withVolumes(inputFileVol, dumpVol)
            .withBinds(new Bind("/tmp/input2", inputFileVol), new Bind("/tmp/input", dumpVol))
            .withEnv("CKAN_URL=http://192.168.100.16:5000")
            .exec();
        System.out.println("Container ID: " + container.getId());
        docker.startContainerCmd(container.getId())
            .exec();
    }
}
