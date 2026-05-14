package model;

/**
 * Represents a raw ingredient used in bakery recipes.
 * Extends WarehouseItem — demonstrates Inheritance and Polymorphism.
 */
public class Ingredient extends WarehouseItem {
    private int stock;
    private int minStockThreshold;

    // Constructor
    public Ingredient(int id, String name, int stock, int minStockThreshold) {
        super(id, name);
        this.stock = stock;
        this.minStockThreshold = minStockThreshold;
    }

    // Getters
    public int getStock() {
        return stock;
    }

    public int getMinStockThreshold() {
        return minStockThreshold;
    }

    // Setters
    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setMinStockThreshold(int minStockThreshold) {
        this.minStockThreshold = minStockThreshold;
    }

    /**
     * Updates stock by adding or subtracting a given amount.
     * @param amount positive to add, negative to subtract
     */
    public void updateStock(int amount) {
        this.stock += amount;
        if (this.stock < 0) {
            this.stock = 0;
        }
    }

    /**
     * Checks if the current stock is below the minimum threshold.
     * @return true if stock is low
     */
    public boolean isLowStock() {
        return stock < minStockThreshold;
    }

    /**
     * Polymorphic implementation of displayInfo().
     */
    @Override
    public void displayInfo() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║        INGREDIENT DETAILS            ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║ ID:         %-25d ║%n", getId());
        System.out.printf("║ Name:       %-25s ║%n", getName());
        System.out.printf("║ Stock:      %-25d ║%n", stock);
        System.out.printf("║ Min Stock:  %-25d ║%n", minStockThreshold);
        System.out.printf("║ Low Stock:  %-25s ║%n", isLowStock() ? "⚠ YES" : "✓ NO");
        System.out.println("╚══════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return "Ingredient{id=" + getId() + ", name='" + getName() + "', stock=" + stock +
               ", threshold=" + minStockThreshold + "}";
    }
}
