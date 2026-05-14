package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order containing multiple products.
 * Demonstrates: Encapsulation, Aggregation (contains Products).
 */
public class Order {
    private int id;
    private List<Product> products;
    private String status;
    private LocalDate date;
    private Customer customer;

    // Constructors
    public Order(int id) {
        this.id = id;
        this.products = new ArrayList<>();
        this.status = "Pending";
        this.date = LocalDate.now();
    }

    public Order(int id, Customer customer, LocalDate date, String status) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.status = status;
        this.products = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Adds a product to this order.
     * @param product the product to add
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
     * Calculates the total price of all products in this order.
     * @return the total price
     */
    public double getTotalPrice() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", status='" + status + "', date=" + date +
               ", items=" + products.size() + ", total=$" + String.format("%.2f", getTotalPrice()) + "}";
    }
}
