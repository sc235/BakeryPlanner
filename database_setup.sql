-- Bakery Planner Database Schema (SQLite)
-- Note: Tables are auto-created by DatabaseManager on startup.
-- This file is provided as documentation / manual reference.

-- 1. Products Table
CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    quantity INTEGER NOT NULL,
    category TEXT,
    expiration_date TEXT
);

-- 2. Ingredients Table
CREATE TABLE IF NOT EXISTS ingredients (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    stock INTEGER NOT NULL,
    min_stock_threshold INTEGER NOT NULL
);

-- 3. Customers Table
CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT
);

-- 4. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(id),
    status TEXT DEFAULT 'Pending',
    order_date TEXT DEFAULT (date('now'))
);

-- 5. Order Items (Link between Orders and Products)
CREATE TABLE IF NOT EXISTS order_items (
    order_id INTEGER REFERENCES orders(id),
    product_id INTEGER REFERENCES products(id),
    PRIMARY KEY (order_id, product_id)
);

-- 6. Recipes Table
CREATE TABLE IF NOT EXISTS recipes (
    id INTEGER PRIMARY KEY,
    product_id INTEGER UNIQUE REFERENCES products(id)
);

-- 7. Recipe Ingredients (Link between Recipes and Ingredients)
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_id INTEGER REFERENCES recipes(id),
    ingredient_id INTEGER REFERENCES ingredients(id),
    quantity_needed INTEGER NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_id)
);

-- 8. Sales Records Table
CREATE TABLE IF NOT EXISTS sales_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_id INTEGER REFERENCES products(id),
    quantity_sold INTEGER NOT NULL,
    revenue REAL NOT NULL,
    sale_date TEXT DEFAULT (date('now'))
);
