package RabbitMQ;

import Entities.Order;
import com.fasterxml.jackson.databind.JsonNode;
import Entities.JSON;
import MySQLConnection.SQLConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class MQConsumer {
    SQLConnection sqlConnection = new SQLConnection();
    ConnectionFactory factory = new ConnectionFactory();
    Connection connection = factory.newConnection();

    Channel channel = connection.createChannel();
    public MQConsumer() throws IOException, TimeoutException {
    }

    public void orderConsume() throws IOException {
        channel.queueDeclare("ORDERS", false, false, false, null);
        channel.basicConsume("ORDERS", true, (s, delivery) ->{
            String message = new String(delivery.getBody(), "UTF-8");
            JsonNode node = JSON.parse(message);
            Order jsonOrder = JSON.fromJson(node, Order.class);

            try {
                Order afterCheckjsonOrder = sqlConnection.checkOrder(jsonOrder);
                sqlConnection.insertOrder(afterCheckjsonOrder);
                //The order response will be sent to "ORDERS_RESPONSE" RabbitMQ queue
                String orderResponse = "{\"order_id\":" + afterCheckjsonOrder.getOrderNumber() + ",\"order_status\":\""
                        + afterCheckjsonOrder.getStatus() + "\",\"error_message\":\"(error here if applies)\"}";
                MQProducer mqProducer = new MQProducer();
                System.out.println(orderResponse);
                mqProducer.orderResponse(orderResponse);//Here is where the order response is being place to the queue
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }, s -> {

        });
    }

}
