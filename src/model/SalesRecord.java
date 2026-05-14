package model;

import java.time.LocalDate;

/**
 * Represents a record of a sale transaction.
 * Demonstrates: Encapsulation, Association with Product.
 */
public class SalesRecord {
    private int id;
    private Product product;
    private int quantitySold;
    private double revenue;
    private LocalDate saleDate;

    // Constructor
    public SalesRecord(int id, Product product, int quantitySold, double revenue, LocalDate saleDate) {
        this.id = id;
        this.product = product;
        this.quantitySold = quantitySold;
        this.revenue = revenue;
        this.saleDate = saleDate;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getRevenue() {
        return revenue;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    @Override
    public String toString() {
        return "SalesRecord{id=" + id + ", product='" + product.getName() +
               "', qty=" + quantitySold + ", revenue=$" + String.format("%.2f", revenue) +
               ", date=" + saleDate + "}";
    }
}
