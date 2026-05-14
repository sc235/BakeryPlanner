package manager;

import core.Bakery;
import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database interactions with SQLite.
 * Demonstrates: JDBC, Persistence, Exception Handling.
 * 
 * SQLite stores the database as a single file (bakery.db) in the project root.
 * No server installation or credentials required!
 */
public class DatabaseManager {
    // SQLite — just a file path, no server needed
    private static final String URL = "jdbc:sqlite:bakery.db";

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite driver
        } catch (ClassNotFoundException e) {
            System.err.println("  ✗ SQLite JDBC driver not found! Make sure sqlite-jdbc.jar is in the classpath.");
        }
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ═══════════════ AUTO-CREATE TABLES ═══════════════

    /**
     * Creates all tables if they don't already exist.
     * Called once on startup — makes the app fully portable.
     */
    private void initializeDatabase() {
        String[] createStatements = {
            // 1. Products
            """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                quantity INTEGER NOT NULL,
                category TEXT,
                expiration_date TEXT
            )
            """,
            // 2. Ingredients
            """
            CREATE TABLE IF NOT EXISTS ingredients (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                stock INTEGER NOT NULL,
                min_stock_threshold INTEGER NOT NULL
            )
            """,
            // 3. Customers
            """
            CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                phone TEXT
            )
            """,
            // 4. Orders
            """
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY,
                customer_id INTEGER REFERENCES customers(id),
                status TEXT DEFAULT 'Pending',
                order_date TEXT DEFAULT (date('now'))
            )
            """,
            // 5. Order Items
            """
            CREATE TABLE IF NOT EXISTS order_items (
                order_id INTEGER REFERENCES orders(id),
                product_id INTEGER REFERENCES products(id),
                PRIMARY KEY (order_id, product_id)
            )
            """,
            // 6. Recipes
            """
            CREATE TABLE IF NOT EXISTS recipes (
                id INTEGER PRIMARY KEY,
                product_id INTEGER UNIQUE REFERENCES products(id)
            )
            """,
            // 7. Recipe Ingredients
            """
            CREATE TABLE IF NOT EXISTS recipe_ingredients (
                recipe_id INTEGER REFERENCES recipes(id),
                ingredient_id INTEGER REFERENCES ingredients(id),
                quantity_needed INTEGER NOT NULL,
                PRIMARY KEY (recipe_id, ingredient_id)
            )
            """,
            // 8. Sales Records
            """
            CREATE TABLE IF NOT EXISTS sales_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER REFERENCES products(id),
                quantity_sold INTEGER NOT NULL,
                revenue REAL NOT NULL,
                sale_date TEXT DEFAULT (date('now'))
            )
            """
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : createStatements) {
                stmt.execute(sql);
            }
            System.out.println("  ✓ SQLite database initialized (bakery.db).");
        } catch (SQLException e) {
            System.err.println("  ✗ Error initializing database: " + e.getMessage());
        }
    }

    // ═══════════════ SAVING DATA ═══════════════

    public void saveProducts(List<Product> products) {
        String sql = "INSERT OR REPLACE INTO products (id, name, price, quantity, category, expiration_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Product p : products) {
                pstmt.setInt(1, p.getId());
                pstmt.setString(2, p.getName());
                pstmt.setDouble(3, p.getPrice());
                pstmt.setInt(4, p.getQuantity());
                pstmt.setString(5, p.getCategory());
                pstmt.setString(6, p.getExpirationDate().toString()); // Store as TEXT
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  ✓ Products saved to database.");
        } catch (SQLException e) {
            System.err.println("  ✗ Error saving products: " + e.getMessage());
        }
    }

    public void saveIngredients(List<Ingredient> ingredients) {
        String sql = "INSERT OR REPLACE INTO ingredients (id, name, stock, min_stock_threshold) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Ingredient i : ingredients) {
                pstmt.setInt(1, i.getId());
                pstmt.setString(2, i.getName());
                pstmt.setInt(3, i.getStock());
                pstmt.setInt(4, i.getMinStockThreshold());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  ✓ Ingredients saved to database.");
        } catch (SQLException e) {
            System.err.println("  ✗ Error saving ingredients: " + e.getMessage());
        }
    }

    public void saveCustomers(List<Customer> customers) {
        String sql = "INSERT OR REPLACE INTO customers (id, name, phone) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Customer c : customers) {
                pstmt.setInt(1, c.getId());
                pstmt.setString(2, c.getName());
                pstmt.setString(3, c.getPhone());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  ✓ Customers saved to database.");
        } catch (SQLException e) {
            System.err.println("  ✗ Error saving customers: " + e.getMessage());
        }
    }

    public void saveOrders(List<Order> orders) {
        String sqlOrder = "INSERT OR REPLACE INTO orders (id, status, order_date) VALUES (?, ?, ?)";
        String sqlItem = "INSERT OR REPLACE INTO order_items (order_id, product_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder);
             PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {

            for (Order o : orders) {
                pstmtOrder.setInt(1, o.getId());
                pstmtOrder.setString(2, o.getStatus());
                pstmtOrder.setString(3, o.getDate().toString());
                pstmtOrder.addBatch();
            }
            pstmtOrder.executeBatch();

            for (Order o : orders) {
                for (Product p : o.getProducts()) {
                    pstmtItem.setInt(1, o.getId());
                    pstmtItem.setInt(2, p.getId());
                    pstmtItem.addBatch();
                }
            }
            pstmtItem.executeBatch();
            System.out.println("  ✓ Orders saved to database.");
        } catch (SQLException e) {
            System.err.println("  ✗ Error saving orders: " + e.getMessage());
        }
    }

    public void saveSalesRecords(List<SalesRecord> records) {
        // Clear old records and re-insert to avoid duplicates
        String deleteSql = "DELETE FROM sales_records";
        String sql = "INSERT INTO sales_records (product_id, quantity_sold, revenue, sale_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             Statement delStmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            delStmt.execute(deleteSql);

            for (SalesRecord r : records) {
                pstmt.setInt(1, r.getProduct().getId());
                pstmt.setInt(2, r.getQuantitySold());
                pstmt.setDouble(3, r.getRevenue());
                pstmt.setString(4, r.getSaleDate().toString()); // Store as TEXT
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  ✓ Sales records saved to database.");
        } catch (SQLException e) {
            System.err.println("  ✗ Error saving sales: " + e.getMessage());
        }
    }

    // ═══════════════ LOADING DATA ═══════════════

    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("category"),
                    LocalDate.parse(rs.getString("expiration_date"))
                );
                products.add(p);
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error loading products: " + e.getMessage());
        }
        return products;
    }

    public List<Ingredient> loadIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ingredient i = new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getInt("min_stock_threshold")
                );
                ingredients.add(i);
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error loading ingredients: " + e.getMessage());
        }
        return ingredients;
    }

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer c = new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone")
                );
                customers.add(c);
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error loading customers: " + e.getMessage());
        }
        return customers;
    }

    /**
     * Loads orders and their linked products from the database.
     */
    public List<Order> loadOrders(List<Product> products) {
        List<Order> orders = new ArrayList<>();
        String sqlOrders = "SELECT * FROM orders";
        String sqlItems = "SELECT product_id FROM order_items WHERE order_id = ?";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlOrders)) {

            while (rs.next()) {
                Order o = new Order(rs.getInt("id"));
                o.setStatus(rs.getString("status"));
                String dateStr = rs.getString("order_date");
                if (dateStr != null) o.setDate(LocalDate.parse(dateStr));

                // Load order items
                try (PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
                    pstmt.setInt(1, o.getId());
                    ResultSet itemRs = pstmt.executeQuery();
                    while (itemRs.next()) {
                        int productId = itemRs.getInt("product_id");
                        for (Product p : products) {
                            if (p.getId() == productId) {
                                o.addProduct(p);
                                break;
                            }
                        }
                    }
                }
                orders.add(o);
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error loading orders: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Loads sales records from the database.
     */
    public List<SalesRecord> loadSalesRecords(List<Product> products) {
        List<SalesRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM sales_records";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                Product product = null;
                for (Product p : products) {
                    if (p.getId() == productId) { product = p; break; }
                }
                if (product != null) {
                    SalesRecord r = new SalesRecord(
                        rs.getInt("id"), product,
                        rs.getInt("quantity_sold"),
                        rs.getDouble("revenue"),
                        LocalDate.parse(rs.getString("sale_date"))
                    );
                    records.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error loading sales records: " + e.getMessage());
        }
        return records;
    }

    /**
     * Saves recipes and their ingredient mappings to the database.
     */
    public void saveRecipes(List<Recipe> recipes) {
        String sqlRecipe = "INSERT OR REPLACE INTO recipes (id, product_id) VALUES (?, ?)";
        String sqlClearIngs = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";
        String sqlIng = "INSERT OR REPLACE INTO recipe_ingredients (recipe_id, ingredient_id, quantity_needed) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmtRecipe = conn.prepareStatement(sqlRecipe);
             PreparedStatement pstmtClear = conn.prepareStatement(sqlClearIngs);
             PreparedStatement pstmtIng = conn.prepareStatement(sqlIng)) {

            int recipeId = 1;
            for (Recipe recipe : recipes) {
                pstmtRecipe.setInt(1, recipeId);
                pstmtRecipe.setInt(2, recipe.getProduct().getId());
                pstmtRecipe.executeUpdate();

                // Clear old ingredient mappings and re-insert
                pstmtClear.setInt(1, recipeId);
                pstmtClear.executeUpdate();

                for (java.util.Map.Entry<Ingredient, Integer> entry : recipe.getIngredientsNeeded().entrySet()) {
                    pstmtIng.setInt(1, recipeId);
                    pstmtIng.setInt(2, entry.getKey().getId());
                    pstmtIng.setInt(3, entry.getValue());
                    pstmtIng.executeUpdate();
                }
                recipeId++;
            }
            System.out.println("  ✓ Recipes saved to database.");
        } catch (SQLException e) {
            System.err.println("  ✗ Error saving recipes: " + e.getMessage());
        }
    }

    /**
     * Loads recipes and their ingredient mappings from the database.
     */
    public List<Recipe> loadRecipes(List<Product> products, List<Ingredient> ingredients) {
        List<Recipe> recipes = new ArrayList<>();
        String sqlRecipes = "SELECT * FROM recipes";
        String sqlIngs = "SELECT ingredient_id, quantity_needed FROM recipe_ingredients WHERE recipe_id = ?";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlRecipes)) {

            while (rs.next()) {
                int recipeId = rs.getInt("id");
                int productId = rs.getInt("product_id");
                Product product = null;
                for (Product p : products) {
                    if (p.getId() == productId) { product = p; break; }
                }
                if (product == null) continue;

                Recipe recipe = new Recipe(product);

                try (PreparedStatement pstmt = conn.prepareStatement(sqlIngs)) {
                    pstmt.setInt(1, recipeId);
                    ResultSet ingRs = pstmt.executeQuery();
                    while (ingRs.next()) {
                        int ingId = ingRs.getInt("ingredient_id");
                        int qtyNeeded = ingRs.getInt("quantity_needed");
                        for (Ingredient ing : ingredients) {
                            if (ing.getId() == ingId) {
                                recipe.addIngredient(ing, qtyNeeded);
                                break;
                            }
                        }
                    }
                }
                recipes.add(recipe);
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error loading recipes: " + e.getMessage());
        }
        return recipes;
    }

    /**
     * Loads ALL data from the database into the Bakery and returns sales records.
     */
    public void loadAllData(Bakery bakery) {
        List<Product> products = loadProducts();
        List<Ingredient> ingredients = loadIngredients();
        List<Customer> customers = loadCustomers();

        bakery.getProducts().addAll(products);
        bakery.getIngredients().addAll(ingredients);
        bakery.getOrders().addAll(loadOrders(products));
        bakery.getRecipes().addAll(loadRecipes(products, ingredients));

        System.out.println("  ✓ All data loaded from SQLite (" +
            products.size() + " products, " + ingredients.size() + " ingredients, " +
            bakery.getOrders().size() + " orders, " + bakery.getRecipes().size() + " recipes).");
    }

    /**
     * Loads sales records (needs products already loaded).
     */
    public List<SalesRecord> loadSalesRecordsFromDB(List<Product> products) {
        return loadSalesRecords(products);
    }

    /**
     * Saves ALL data to the database.
     */
    public void saveAllData(Bakery bakery, List<SalesRecord> salesRecords, List<Customer> customers) {
        saveProducts(bakery.getProducts());
        saveIngredients(bakery.getIngredients());
        saveCustomers(customers);
        saveOrders(bakery.getOrders());
        saveRecipes(bakery.getRecipes());
        saveSalesRecords(salesRecords);
        System.out.println("  ✓ All data saved to SQLite.");
    }
}
