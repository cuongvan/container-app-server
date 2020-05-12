package test_lib.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class Sender {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("mustang.rmq.cloudamqp.com");
        factory.setUsername("bpnkxscx");
        factory.setVirtualHost("bpnkxscx");
        factory.setPassword("HJsvGjpmQdDrJiVuw5w36F1lWr63sEkR");
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            String QUEUE = "test";
            channel.queueDeclare(QUEUE, true, false, false, Collections.emptyMap());
            channel.basicPublish("", QUEUE, null, "hello from sender".getBytes(UTF_8));
            System.out.println("sent");
        }
    }
}
