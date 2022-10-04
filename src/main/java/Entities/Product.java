package Entities;

public class Product {
    private int product_id;
    private int quantity;
    private int stock;

    public Product() {
    }

    public Product(int product_id, int stock) {
        this.product_id = product_id;
        this.stock = stock;
    }

    public Product(int product_id, int quantity, int stock) {
        this.product_id = product_id;
        this.quantity = quantity;
        this.stock = stock;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
