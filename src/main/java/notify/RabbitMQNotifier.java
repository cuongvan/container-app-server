package notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import externalapi.appcall.CallDAO;
import externalapi.appcall.models.CallDetail;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RabbitMQNotifier {

    @Inject private CallDAO appCallDAO;
    
    private static String QUEUE = "call_done";
    private final Connection connection;
    private final Channel channel;
    
    ObjectMapper mapper = new ObjectMapper();
    
    public RabbitMQNotifier() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("mustang.rmq.cloudamqp.com");
        factory.setUsername("bpnkxscx");
        factory.setVirtualHost("bpnkxscx");
        factory.setPassword("HJsvGjpmQdDrJiVuw5w36F1lWr63sEkR");
        
        connection = factory.newConnection();
        channel = connection.createChannel();
        createQueue();
    }

    private void createQueue() throws IOException {
        channel.queueDeclare(QUEUE, true, false, false, null);
    }
    
    private void sendNotification(byte[] data) throws IOException {
        channel.basicPublish("", QUEUE, null, data);
    }
    
    

    public void notifyExecuteDone(String callId) throws Exception {
        CallDetail callDetail = appCallDAO.getById(callId);
        byte[] sentBytes = mapper.writeValueAsBytes(callDetail);
        sendNotification(sentBytes);
    }
}
