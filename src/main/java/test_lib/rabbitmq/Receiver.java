package test_lib.rabbitmq;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

public class Receiver {

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

            DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery delivery) throws IOException {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println("> " + message);
                }
            };

            CancelCallback cancelCallback = new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException {

                }
            };
            
            System.out.println("receiving");
            channel.basicConsume(QUEUE, true, deliverCallback, cancelCallback);
            System.in.read();
        }
    }
}
