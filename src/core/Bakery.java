package core;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Central class representing the bakery itself.
 * Holds all products, ingredients, orders, and recipes.
 * Demonstrates: Composition (has-a relationships), Collections.
 */
public class Bakery {
    private List<Product> products;
    private List<Ingredient> ingredients;
    private List<Order> orders;
    private List<Recipe> recipes;

    // Constructor
    public Bakery() {
        this.products = new ArrayList<>();
        this.ingredients = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.recipes = new ArrayList<>();
    }

    // ───────────── Product Operations ─────────────

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public Product getProductById(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // ───────────── Ingredient Operations ─────────────

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Ingredient getIngredientById(int id) {
        for (Ingredient i : ingredients) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    // ───────────── Order Operations ─────────────

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Order getOrderById(int id) {
        for (Order o : orders) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }

    // ───────────── Recipe Operations ─────────────

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public String toString() {
        return "Bakery{products=" + products.size() + ", ingredients=" + ingredients.size() +
               ", orders=" + orders.size() + ", recipes=" + recipes.size() + "}";
    }
}
