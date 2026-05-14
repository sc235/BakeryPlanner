package model;

/**
 * Abstract base class for all items stored in the bakery warehouse.
 * Demonstrates: Abstraction, Encapsulation
 */
public abstract class WarehouseItem {
    private int id;
    private String name;

    // Constructor
    public WarehouseItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Abstract method to display item information.
     * Must be implemented by all subclasses (Polymorphism).
     */
    public abstract void displayInfo();

    @Override
    public String toString() {
        return "WarehouseItem{id=" + id + ", name='" + name + "'}";
    }
}
