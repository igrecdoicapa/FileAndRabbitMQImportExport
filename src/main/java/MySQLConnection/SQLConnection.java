package MySQLConnection;


import Entities.Order;
import Entities.Product;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class SQLConnection {
    private String url;
    private String user;
    private String password;

    public SQLConnection() throws IOException {
        FileInputStream fis = new FileInputStream("src/main/resources/application.properties");
        Properties properties = new Properties();
        properties.load(fis);
        url = properties.getProperty("dbUrl");
        user = properties.getProperty("dbUserName");
        password = properties.getProperty("dbPassword");
    }

    public Order checkOrder(Order jsonOrder) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        /*Retrieving the maximum orderNumber from the db.
        If there are NO orders in the db, the first orderNumber will be 1
        If there are orders in the db, the next orderNumber will be the maximum orderNumber existing + 1*/
        ResultSet maxOrderNumberQ = statement.executeQuery("select max(orderNumber) as maxOrderNumber from orders");
        maxOrderNumberQ.next();
        int newOrderNumber = 1;
        if(maxOrderNumberQ.getString(1) != null) {
            newOrderNumber = Integer.parseInt(maxOrderNumberQ.getString(1)) + 1;
        }
        jsonOrder.setOrderNumber(newOrderNumber);
        /*Looping through all the order items to check if we have stock for each of one.
        If we don't have stock for at least 1 item in the order, the order status will be "INSUFFICIENT_STOCKS"
        If we have stock for each of the order items, the order status will be "RESERVED"*/
        for(Product jsonProduct : jsonOrder.getItems()){
            ResultSet checkStockQ = statement.executeQuery("select stock from products where id = " + jsonProduct.getProduct_id());
            checkStockQ.next();
            int newStock = Integer.parseInt(checkStockQ.getString("stock")) - jsonProduct.getQuantity();
            if (newStock < 0) {
                jsonOrder.setStatus("INSUFFICIENT_STOCKS");
                return jsonOrder;
            }
            jsonProduct.setStock(newStock);
        }
        jsonOrder.setStatus("RESERVED");
        return jsonOrder;
    }
    public void insertOrder(Order jsonOrder) throws IOException, TimeoutException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();

            if(jsonOrder.getStatus() != "INSUFFICIENT_STOCKS") {
                String insertReserved = null;
                for (Product jsonProduct : jsonOrder.getItems()) {
                    insertReserved = "insert into orders (orderNumber, id_product, quantity, client, status) values (" +
                            jsonOrder.getOrderNumber() + "," + jsonProduct.getProduct_id() + "," + jsonProduct.getQuantity()
                            + ",'" + jsonOrder.getClient_name() + "','" + jsonOrder.getStatus()+ "')";
                    statement.execute(insertReserved);
                    /*If the order status is different from "INSUFFICIENT_STOCKS", each product will get the stock quantity diminished*/
                    String updateStock = "update products set stock = " + jsonProduct.getStock() + " where id = " + jsonProduct.getProduct_id();
                    statement.execute(updateStock);
                }
            } else {
                String insertInsufficientStock = null;
                for (Product jsonProduct : jsonOrder.getItems()) {
                    insertInsufficientStock = "insert into orders (orderNumber, id_product, quantity, client, status) values (" +
                            jsonOrder.getOrderNumber() + "," + jsonProduct.getProduct_id() + "," + jsonProduct.getQuantity()
                            + ",'" + jsonOrder.getClient_name() + "','" + jsonOrder.getStatus() + "')";
                    statement.execute(insertInsufficientStock);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Product checkStock(int productId, int quantity) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        ResultSet checkStockQ = statement.executeQuery("select id, stock from products where id = " + productId);
        int tempProduct = -1;
        int tempStock = -1;
        if (checkStockQ.next()) { //Checks if there is a product with that id. If not, product id will stay -1
            tempProduct = checkStockQ.getInt(1);
            tempStock = checkStockQ.getInt(2)+quantity;
        }
        Product jsonProduct = new Product(tempProduct, tempStock);
        return jsonProduct;
    }

    public void insertStock(Product jsonProduct) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        String insertNewProduct = "insert into products (id, stock) values (" + jsonProduct.getProduct_id() + "," +jsonProduct.getStock() + ")";
        statement.execute(insertNewProduct);
    }

    public void updateStock(Product jsonProduct) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        String updateStock = "update products set stock = " +jsonProduct.getStock() + " where id = " + jsonProduct.getProduct_id();
        statement.execute(updateStock);
    }

}
