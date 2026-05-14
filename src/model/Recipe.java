package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a recipe that links a Product to its required Ingredients.
 * Demonstrates: Composition, Map collections.
 */
public class Recipe {
    private Product product;
    private Map<Ingredient, Integer> ingredientsNeeded;

    // Constructor
    public Recipe(Product product) {
        this.product = product;
        this.ingredientsNeeded = new HashMap<>();
    }

    // Constructor with ingredients
    public Recipe(Product product, Map<Ingredient, Integer> ingredientsNeeded) {
        this.product = product;
        this.ingredientsNeeded = new HashMap<>(ingredientsNeeded);
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public Map<Ingredient, Integer> getIngredientsNeeded() {
        return ingredientsNeeded;
    }

    // Setters
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Adds an ingredient requirement to this recipe.
     * @param ingredient the ingredient required
     * @param quantity the amount needed
     */
    public void addIngredient(Ingredient ingredient, int quantity) {
        ingredientsNeeded.put(ingredient, quantity);
    }

    /**
     * Calculates the required ingredients for a given number of batches.
     * @param batches the number of batches to produce
     * @return a map of ingredients and their required quantities
     */
    public Map<Ingredient, Integer> calculateRequiredIngredients(int batches) {
        Map<Ingredient, Integer> required = new HashMap<>();
        for (Map.Entry<Ingredient, Integer> entry : ingredientsNeeded.entrySet()) {
            required.put(entry.getKey(), entry.getValue() * batches);
        }
        return required;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recipe for: ").append(product.getName()).append("\n");
        sb.append("  Ingredients needed:\n");
        for (Map.Entry<Ingredient, Integer> entry : ingredientsNeeded.entrySet()) {
            sb.append("    - ").append(entry.getKey().getName())
              .append(": ").append(entry.getValue()).append(" units\n");
        }
        return sb.toString();
    }
}
