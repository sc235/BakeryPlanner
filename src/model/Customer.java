package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bakery customer who can place orders.
 * Demonstrates: Encapsulation, Association (has-a relationship with Order).
 */
public class Customer {
    private int id;
    private String name;
    private String phone;
    private List<Order> orderHistory;

    // Constructor
    public Customer(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.orderHistory = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Creates and places a new order for this customer.
     * @return the newly created Order
     */
    public Order placeOrder() {
        int orderId = orderHistory.size() + 1;
        Order newOrder = new Order(orderId);
        orderHistory.add(newOrder);
        return newOrder;
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', phone='" + phone +
               "', totalOrders=" + orderHistory.size() + "}";
    }
}
