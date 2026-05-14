import core.*;
import manager.*;
import model.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Main entry point — Interactive console menu for the Bakery Planner.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static BakeryPlanner planner;
    private static int nextProductId = 1;
    private static int nextIngredientId = 1;
    private static int nextCustomerId = 1;
    private static int nextOrderId = 1;

    public static void main(String[] args) {
        planner = new BakeryPlanner();
        planner.loadFromDatabase(); // Load existing data
        if (planner.getBakery().getProducts().isEmpty() && planner.getBakery().getIngredients().isEmpty()) {
            loadSampleData(); // Only load sample data if DB is empty
        }

        // Auto-save all data to SQLite on app exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n  💾 Auto-saving data to SQLite...");
            planner.persistData();
            System.out.println("  ✓ All data saved. Goodbye!");
        }));

        // Launch the Premium Swing GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui.BakeryDashboard dashboard = new gui.BakeryDashboard(planner);
            dashboard.setVisible(true);
        });
    }

    // ═══════════════════ MENUS ═══════════════════

    private static void printWelcome() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║                                                  ║");
        System.out.println("║         🧁  BAKERY PLANNER SYSTEM  🧁           ║");
        System.out.println("║                                                  ║");
        System.out.println("║     Manage your bakery with ease and style!      ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void printMainMenu() {
        System.out.println("\n┌──────────────── MAIN MENU ────────────────┐");
        System.out.println("│  1. 🍞 Manage Products                    │");
        System.out.println("│  2. 📦 Manage Inventory                   │");
        System.out.println("│  3. 📋 Manage Orders                      │");
        System.out.println("│  4. 👥 Manage Customers                   │");
        System.out.println("│  5. 📊 View Sales Report                  │");
        System.out.println("│  6. 🏭 View Production Plan               │");
        System.out.println("│  7. 💡 View Recommendations               │");
        System.out.println("│  8. ⏰ Check Expired Products             │");
        System.out.println("│  0. 🚪 Exit                               │");
        System.out.println("└───────────────────────────────────────────┘");
    }

    // ─────────── 1. PRODUCT MANAGEMENT ───────────

    private static void manageProducts() {
        System.out.println("\n┌────────── PRODUCT MANAGEMENT ──────────┐");
        System.out.println("│  1. Add Product                         │");
        System.out.println("│  2. View All Products                   │");
        System.out.println("│  3. Remove Product                      │");
        System.out.println("│  4. Update Price                        │");
        System.out.println("│  5. Filter by Category                  │");
        System.out.println("│  0. Back                                │");
        System.out.println("└─────────────────────────────────────────┘");

        int choice = readInt("Enter your choice: ");
        Bakery bakery = planner.getBakery();
        ProductManager pm = planner.getProductManager();

        switch (choice) {
            case 1:
                System.out.print("  Product name: ");
                String name = scanner.nextLine();
                double price = readDouble("  Price: $");
                int qty = readInt("  Quantity: ");
                System.out.print("  Category (Bread/Cake/Pastry): ");
                String cat = scanner.nextLine();
                int days = readInt("  Days until expiration: ");
                Product p = new Product(nextProductId++, name, price, qty, cat,
                        LocalDate.now().plusDays(days));
                pm.addProduct(bakery, p);
                break;
            case 2:
                System.out.println("\n  All Products:");
                if (bakery.getProducts().isEmpty()) {
                    System.out.println("  (empty)");
                } else {
                    for (Product prod : bakery.getProducts()) {
                        prod.displayInfo();
                    }
                }
                break;
            case 3:
                int removeId = readInt("  Product ID to remove: ");
                pm.removeProduct(bakery, removeId);
                break;
            case 4:
                int updateId = readInt("  Product ID to update: ");
                Product target = bakery.getProductById(updateId);
                if (target != null) {
                    double newPrice = readDouble("  New price: $");
                    pm.updatePrice(target, newPrice);
                } else {
                    System.out.println("  ✗ Product not found.");
                }
                break;
            case 5:
                System.out.print("  Category to filter: ");
                String filterCat = scanner.nextLine();
                List<Product> filtered = pm.filterByCategory(bakery, filterCat);
                System.out.println("  Found " + filtered.size() + " product(s):");
                for (Product fp : filtered) {
                    System.out.println("    " + fp);
                }
                break;
            case 0: break;
            default: System.out.println("  ✗ Invalid choice.");
        }
    }

    // ─────────── 2. INVENTORY MANAGEMENT ───────────

    private static void manageInventory() {
        System.out.println("\n┌────────── INVENTORY MANAGEMENT ──────────┐");
        System.out.println("│  1. View Inventory Status                 │");
        System.out.println("│  2. Add Ingredient                        │");
        System.out.println("│  3. Restock Ingredient                    │");
        System.out.println("│  4. View Low Stock Alerts                 │");
        System.out.println("│  0. Back                                  │");
        System.out.println("└───────────────────────────────────────────┘");

        int choice = readInt("Enter your choice: ");
        Bakery bakery = planner.getBakery();
        InventoryManager im = planner.getInventoryManager();

        switch (choice) {
            case 1:
                planner.checkInventory(bakery);
                break;
            case 2:
                System.out.print("  Ingredient name: ");
                String name = scanner.nextLine();
                int stock = readInt("  Initial stock: ");
                int threshold = readInt("  Minimum stock threshold: ");
                Ingredient ing = new Ingredient(nextIngredientId++, name, stock, threshold);
                bakery.addIngredient(ing);
                System.out.println("  ✓ Ingredient added: " + name);
                break;
            case 3:
                System.out.println("  Available ingredients:");
                for (Ingredient i : bakery.getIngredients()) {
                    System.out.println("    [" + i.getId() + "] " + i.getName() + " (stock: " + i.getStock() + ")");
                }
                int id = readInt("  Ingredient ID to restock: ");
                Ingredient target = bakery.getIngredientById(id);
                if (target != null) {
                    int amount = readInt("  Amount to add: ");
                    im.restockIngredient(target, amount);
                } else {
                    System.out.println("  ✗ Ingredient not found.");
                }
                break;
            case 4:
                List<String> alerts = im.generateStockAlert(bakery);
                if (alerts.isEmpty()) {
                    System.out.println("  ✓ All ingredients are well-stocked!");
                } else {
                    for (String alert : alerts) {
                        System.out.println("  " + alert);
                    }
                }
                break;
            case 0: break;
            default: System.out.println("  ✗ Invalid choice.");
        }
    }

    // ─────────── 3. ORDER MANAGEMENT ───────────

    private static void manageOrders() {
        System.out.println("\n┌────────── ORDER MANAGEMENT ──────────┐");
        System.out.println("│  1. Place New Order                    │");
        System.out.println("│  2. View All Orders                    │");
        System.out.println("│  3. Update Order Status                │");
        System.out.println("│  4. Record Sale                        │");
        System.out.println("│  0. Back                               │");
        System.out.println("└────────────────────────────────────────┘");

        int choice = readInt("Enter your choice: ");
        Bakery bakery = planner.getBakery();
        OrderTracker ot = planner.getOrderTracker();

        switch (choice) {
            case 1:
                // Show available customers
                List<Customer> customers = planner.getCustomerManager().getAllCustomers();
                if (customers.isEmpty()) {
                    System.out.println("  ✗ No customers registered. Add a customer first.");
                    return;
                }
                System.out.println("  Select customer:");
                for (Customer c : customers) {
                    System.out.println("    [" + c.getId() + "] " + c.getName());
                }
                int custId = readInt("  Customer ID: ");
                Customer cust = planner.getCustomerManager().findCustomerById(custId);
                if (cust == null) {
                    System.out.println("  ✗ Customer not found.");
                    return;
                }

                Order order = new Order(nextOrderId++);
                boolean addingProducts = true;
                while (addingProducts) {
                    System.out.println("  Available products:");
                    for (Product prod : bakery.getProducts()) {
                        System.out.println("    [" + prod.getId() + "] " + prod.getName() +
                                " - $" + String.format("%.2f", prod.getPrice()) +
                                " (qty: " + prod.getQuantity() + ")");
                    }
                    int prodId = readInt("  Add product ID (0 to finish): ");
                    if (prodId == 0) {
                        addingProducts = false;
                    } else {
                        Product prod = bakery.getProductById(prodId);
                        if (prod != null && prod.getQuantity() > 0) {
                            order.addProduct(prod);
                            System.out.println("  ✓ Added " + prod.getName() + " to order.");
                        } else {
                            System.out.println("  ✗ Product not found or out of stock.");
                        }
                    }
                }

                if (!order.getProducts().isEmpty()) {
                    bakery.addOrder(order);
                    cust.getOrderHistory().add(order);
                    System.out.println("  ✓ Order #" + order.getId() + " placed! Total: $" +
                            String.format("%.2f", order.getTotalPrice()));
                    ot.notifyCustomer(order, cust);
                } else {
                    System.out.println("  ✗ Order cancelled — no products added.");
                    nextOrderId--;
                }
                break;
            case 2:
                planner.accessOrders(bakery);
                break;
            case 3:
                if (bakery.getOrders().isEmpty()) {
                    System.out.println("  No orders to update.");
                    return;
                }
                for (Order o : bakery.getOrders()) {
                    System.out.println("    " + o);
                }
                int orderId = readInt("  Order ID to update: ");
                Order targetOrder = bakery.getOrderById(orderId);
                if (targetOrder != null) {
                    System.out.print("  New status (Pending/In Progress/Completed/Cancelled): ");
                    String status = scanner.nextLine();
                    ot.updateStatus(targetOrder, status);
                } else {
                    System.out.println("  ✗ Order not found.");
                }
                break;
            case 4:
                if (bakery.getProducts().isEmpty()) {
                    System.out.println("  No products available.");
                    return;
                }
                System.out.println("  Available products:");
                for (Product prod : bakery.getProducts()) {
                    System.out.println("    [" + prod.getId() + "] " + prod.getName() +
                            " ($" + String.format("%.2f", prod.getPrice()) + ", qty: " + prod.getQuantity() + ")");
                }
                int saleId = readInt("  Product ID: ");
                Product saleProd = bakery.getProductById(saleId);
                if (saleProd != null) {
                    int saleQty = readInt("  Quantity sold: ");
                    if (saleQty <= saleProd.getQuantity()) {
                        planner.getSalesManager().recordSale(saleProd, saleQty);
                    } else {
                        System.out.println("  ✗ Insufficient stock! Available: " + saleProd.getQuantity());
                    }
                } else {
                    System.out.println("  ✗ Product not found.");
                }
                break;
            case 0: break;
            default: System.out.println("  ✗ Invalid choice.");
        }
    }

    // ─────────── 4. CUSTOMER MANAGEMENT ───────────

    private static void manageCustomers() {
        System.out.println("\n┌────────── CUSTOMER MANAGEMENT ──────────┐");
        System.out.println("│  1. Add Customer                         │");
        System.out.println("│  2. View All Customers                   │");
        System.out.println("│  3. Search Customer                      │");
        System.out.println("│  4. Remove Customer                      │");
        System.out.println("│  0. Back                                 │");
        System.out.println("└──────────────────────────────────────────┘");

        int choice = readInt("Enter your choice: ");
        CustomerManager cm = planner.getCustomerManager();

        switch (choice) {
            case 1:
                System.out.print("  Customer name: ");
                String name = scanner.nextLine();
                System.out.print("  Phone number: ");
                String phone = scanner.nextLine();
                cm.addCustomer(new Customer(nextCustomerId++, name, phone));
                break;
            case 2:
                List<Customer> all = cm.getAllCustomers();
                if (all.isEmpty()) {
                    System.out.println("  No customers registered.");
                } else {
                    System.out.println("\n  Registered Customers:");
                    for (Customer c : all) {
                        System.out.println("    " + c);
                    }
                }
                break;
            case 3:
                System.out.print("  Search name: ");
                String query = scanner.nextLine();
                List<Customer> results = cm.searchByName(query);
                if (results.isEmpty()) {
                    System.out.println("  No customers found matching '" + query + "'");
                } else {
                    for (Customer c : results) {
                        System.out.println("    " + c);
                    }
                }
                break;
            case 4:
                int removeId = readInt("  Customer ID to remove: ");
                cm.removeCustomer(removeId);
                break;
            case 0: break;
            default: System.out.println("  ✗ Invalid choice.");
        }
    }

    // ─────────── 5-8. VIEW OPERATIONS ───────────

    private static void viewSalesReport() {
        System.out.println(planner.getSalesManager().generateSalesReport());
    }

    private static void viewProduction() {
        planner.selectProduction(planner.getBakery());

        // Also show restocking suggestions
        List<String> suggestions = planner.getProductionOptimizer().suggestRestocking(planner.getBakery());
        if (!suggestions.isEmpty()) {
            System.out.println("\n  📋 Restocking Suggestions:");
            for (String s : suggestions) {
                System.out.println("    " + s);
            }
        }
    }

    private static void viewRecommendations() {
        SalesManager sm = planner.getSalesManager();
        RecommendationEngine re = planner.getRecommendationEngine();

        System.out.println("\n┌────────── RECOMMENDATIONS ──────────┐");

        // Top selling
        List<Product> topSelling = re.getTopSelling(sm, 5);
        System.out.println("│  🏆 Top Selling Products:             │");
        if (topSelling.isEmpty()) {
            System.out.println("│    No sales data yet.                 │");
        } else {
            for (Product p : topSelling) {
                System.out.println("│    - " + p.getName() + " ($" + String.format("%.2f", p.getPrice()) + ")");
            }
        }

        // Trends
        Map<String, String> trends = re.predictTrend(sm);
        System.out.println("│                                       │");
        System.out.println("│  📈 Market Trends:                    │");
        if (trends.isEmpty()) {
            System.out.println("│    No trend data available yet.       │");
        } else {
            for (Map.Entry<String, String> entry : trends.entrySet()) {
                System.out.println("│    " + entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println("└───────────────────────────────────────┘");
    }

    private static void viewExpiredProducts() {
        planner.checkExpiredProducts(planner.getBakery());
    }

    // ═══════════════ SAMPLE DATA ═══════════════

    private static void loadSampleData() {
        Bakery bakery = planner.getBakery();
        ProductManager pm = planner.getProductManager();
        CustomerManager cm = planner.getCustomerManager();

        // Sample Ingredients
        Ingredient flour = new Ingredient(nextIngredientId++, "Flour", 100, 20);
        Ingredient sugar = new Ingredient(nextIngredientId++, "Sugar", 80, 15);
        Ingredient butter = new Ingredient(nextIngredientId++, "Butter", 50, 10);
        Ingredient eggs = new Ingredient(nextIngredientId++, "Eggs", 200, 30);
        Ingredient yeast = new Ingredient(nextIngredientId++, "Yeast", 8, 10);  // Low stock!
        bakery.addIngredient(flour);
        bakery.addIngredient(sugar);
        bakery.addIngredient(butter);
        bakery.addIngredient(eggs);
        bakery.addIngredient(yeast);

        // Sample Products
        Product baguette = new Product(nextProductId++, "Baguette", 3.50, 25, "Bread", LocalDate.now().plusDays(3));
        Product croissant = new Product(nextProductId++, "Croissant", 2.75, 40, "Pastry", LocalDate.now().plusDays(2));
        Product chocCake = new Product(nextProductId++, "Chocolate Cake", 24.99, 10, "Cake", LocalDate.now().plusDays(5));
        Product sourdough = new Product(nextProductId++, "Sourdough", 5.00, 15, "Bread", LocalDate.now().plusDays(4));
        Product eclair = new Product(nextProductId++, "Eclair", 4.50, 20, "Pastry", LocalDate.now().minusDays(1)); // Expired!
        pm.addProduct(bakery, baguette);
        pm.addProduct(bakery, croissant);
        pm.addProduct(bakery, chocCake);
        pm.addProduct(bakery, sourdough);
        pm.addProduct(bakery, eclair);

        // Sample Recipes
        Recipe baguetteRecipe = new Recipe(baguette);
        baguetteRecipe.addIngredient(flour, 3);
        baguetteRecipe.addIngredient(yeast, 1);
        baguetteRecipe.addIngredient(sugar, 1);
        bakery.addRecipe(baguetteRecipe);

        Recipe cakeRecipe = new Recipe(chocCake);
        cakeRecipe.addIngredient(flour, 4);
        cakeRecipe.addIngredient(sugar, 5);
        cakeRecipe.addIngredient(butter, 3);
        cakeRecipe.addIngredient(eggs, 6);
        bakery.addRecipe(cakeRecipe);

        // Sample Customers
        cm.addCustomer(new Customer(nextCustomerId++, "Alice Martin", "555-0101"));
        cm.addCustomer(new Customer(nextCustomerId++, "Bob Johnson", "555-0202"));
        cm.addCustomer(new Customer(nextCustomerId++, "Claire Dupont", "555-0303"));

        // Sample Orders
        List<Customer> allCustomers = cm.getAllCustomers();
        Order order1 = new Order(nextOrderId++);
        order1.addProduct(baguette); order1.addProduct(croissant);
        order1.setStatus("Completed");
        bakery.addOrder(order1);
        allCustomers.get(0).getOrderHistory().add(order1);

        Order order2 = new Order(nextOrderId++);
        order2.addProduct(chocCake);
        order2.setStatus("In Progress");
        bakery.addOrder(order2);
        allCustomers.get(1).getOrderHistory().add(order2);

        Order order3 = new Order(nextOrderId++);
        order3.addProduct(sourdough); order3.addProduct(eclair); order3.addProduct(croissant);
        order3.setStatus("Pending");
        bakery.addOrder(order3);
        allCustomers.get(2).getOrderHistory().add(order3);

        // Sample Sales
        SalesManager sm = planner.getSalesManager();
        sm.recordSale(baguette, 5);
        sm.recordSale(croissant, 12);
        sm.recordSale(chocCake, 3);
        sm.recordSale(croissant, 8);

        System.out.println("\n  ✓ Sample data loaded successfully!\n");
    }

    // ═══════════════ UTILITY METHODS ═══════════════

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("  Please enter a valid number: ");
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("  Please enter a valid number: ");
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }
}
