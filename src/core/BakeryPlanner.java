package core;

import manager.*;
import model.*;

import java.util.List;

/**
 * Core class coordinating the Bakery Planner system.
 * Demonstrates: Facade Pattern (simplified interface to complex subsystem).
 */
public class BakeryPlanner {
    private Bakery bakery;
    private ProductManager productManager;
    private InventoryManager inventoryManager;
    private OrderTracker orderTracker;
    private CustomerManager customerManager;
    private SalesManager salesManager;
    private RecommendationEngine recommendationEngine;
    private ProductionOptimizer productionOptimizer;
    private DatabaseManager databaseManager;

    // Constructor
    public BakeryPlanner() {
        this.bakery = new Bakery();
        this.productManager = new ProductManager();
        this.inventoryManager = new InventoryManager();
        this.orderTracker = new OrderTracker();
        this.customerManager = new CustomerManager();
        this.salesManager = new SalesManager();
        this.recommendationEngine = new RecommendationEngine();
        this.productionOptimizer = new ProductionOptimizer();
        this.databaseManager = new DatabaseManager();
    }

    // Getters
    public Bakery getBakery() {
        return bakery;
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public OrderTracker getOrderTracker() {
        return orderTracker;
    }

    public CustomerManager getCustomerManager() {
        return customerManager;
    }

    public SalesManager getSalesManager() {
        return salesManager;
    }

    public RecommendationEngine getRecommendationEngine() {
        return recommendationEngine;
    }

    public ProductionOptimizer getProductionOptimizer() {
        return productionOptimizer;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    // ═══════════════ Orchestration Methods ═══════════════

    /**
     * Displays production plan based on current inventory and recipes.
     */
    public void selectProduction(Bakery bakery) {
        productionOptimizer.displayProductionPlan(bakery);
    }

    /**
     * Orchestrates inventory checking.
     */
    public void checkInventory(Bakery bakery) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║            📦 INVENTORY STATUS CHECK              ║");
        System.out.println("╠══════════════════════════════════════════════════╣");

        for (Ingredient i : bakery.getIngredients()) {
            System.out.printf("║ - %-15s: %3d units %-15s ║%n",
                    i.getName(), i.getStock(),
                    i.isLowStock() ? "⚠️ LOW STOCK" : "✅ OK");
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    /**
     * Orchestrates order accessing.
     */
    public void accessOrders(Bakery bakery) {
        System.out.println("\n  All Orders:");
        if (bakery.getOrders().isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Order o : bakery.getOrders()) {
                System.out.println("    " + o);
            }
        }
    }

    /**
     * Checks and displays expired products.
     */
    public void checkExpiredProducts(Bakery bakery) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║           ⏰ EXPIRED PRODUCTS CHECK              ║");
        System.out.println("╠══════════════════════════════════════════════════╣");

        List<Product> expired = productManager.getExpiredProducts(bakery);
        if (expired.isEmpty()) {
            System.out.println("║  ✓ No expired products found!                    ║");
        } else {
            System.out.println("║  ⚠ The following products have expired:          ║");
            for (Product p : expired) {
                System.out.printf("║    - %-15s (Expired: %-12s)      ║%n",
                        p.getName(), p.getExpirationDate());
            }
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    /**
     * Persists all current data to the SQLite database.
     */
    public void persistData() {
        databaseManager.saveAllData(bakery, salesManager.getRecords(),
                customerManager.getAllCustomers());
    }

    /**
     * Loads all data from the SQLite database.
     */
    public void loadFromDatabase() {
        databaseManager.loadAllData(bakery);
    }
}
