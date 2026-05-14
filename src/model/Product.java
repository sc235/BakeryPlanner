package model;

import java.time.LocalDate;

/**
 * Represents a bakery product (e.g., bread, cake, pastry).
 * Extends WarehouseItem — demonstrates Inheritance and Polymorphism.
 */
public class Product extends WarehouseItem {
    private double price;
    private int quantity;
    private String category;
    private LocalDate expirationDate;

    // Constructor
    public Product(int id, String name, double price, int quantity, String category, LocalDate expirationDate) {
        super(id, name);
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.expirationDate = expirationDate;
    }

    // Getters
    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    // Setters
    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Checks if the product has passed its expiration date.
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    /**
     * Polymorphic implementation of displayInfo().
     */
    @Override
    public void displayInfo() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║          PRODUCT DETAILS             ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║ ID:         %-25d ║%n", getId());
        System.out.printf("║ Name:       %-25s ║%n", getName());
        System.out.printf("║ Category:   %-25s ║%n", category);
        System.out.printf("║ Price:      $%-24.2f ║%n", price);
        System.out.printf("║ Quantity:   %-25d ║%n", quantity);
        System.out.printf("║ Expiration: %-25s ║%n", expirationDate);
        System.out.printf("║ Status:     %-25s ║%n", isExpired() ? "⛔ EXPIRED" : "✅ FRESH");
        System.out.println("╚══════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return "Product{id=" + getId() + ", name='" + getName() + "', price=" + price +
               ", qty=" + quantity + ", cat='" + category + "', exp=" + expirationDate + "}";
    }
}
