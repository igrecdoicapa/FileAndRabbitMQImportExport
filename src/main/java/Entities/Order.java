package Entities;

import java.util.List;

public class Order {
    private String client_name;
    private List<Product> items;
    private int orderNumber;
    private String status;

    public Order() {
    }

    public Order(String client_name, List<Product> items, int orderNumber, String status) {
        this.client_name = client_name;
        this.items = items;
        this.orderNumber = orderNumber;
        this.status = status;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
