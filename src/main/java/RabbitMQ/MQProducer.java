package RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQProducer {
    ConnectionFactory factory = new ConnectionFactory();
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    public MQProducer() throws IOException, TimeoutException {
    }

    public void orderResponse(String message){
        try {
            channel.queueDeclare("ORDERS_RESPONSE", false, false, false, null);
            channel.basicPublish("","ORDERS_RESPONSE", false, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
