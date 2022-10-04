import XMLReader.ReadXML;
import RabbitMQ.MQConsumer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Run {
    public static void main(String args[]) throws FileNotFoundException {

        Runnable drawRunnable = new Runnable() {
            public void run() {
                try {
                    MQConsumer mqConsumer = new MQConsumer();
                    ReadXML readXML = new ReadXML();
                    readXML.readStock(); //used for reading the XML file
                    mqConsumer.orderConsume(); //used for reading from the "ORDERS" RabbitMQ queue
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        };

        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(drawRunnable , 0, 10, TimeUnit.SECONDS);
    }
}
