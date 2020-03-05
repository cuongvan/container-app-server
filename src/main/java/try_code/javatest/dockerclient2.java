/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package try_code.javatest;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;

/**
 *
 * @author cuong
 */
public class dockerclient2 {

    public static void main(String[] args) throws DockerException, InterruptedException {
        
        final DockerClient docker = DefaultDockerClient.builder().uri("unix:///var/run/docker.sock").build();;
        ContainerConfig conf = ContainerConfig.builder()
            .image("random:ckanapp")
            .build();
        final ContainerCreation creation = docker.createContainer(conf);
        final String id = creation.id();

        final ContainerInfo info = docker.inspectContainer(id);

        docker.startContainer(id);
        docker.close();
    }
}
