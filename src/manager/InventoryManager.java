package manager;

import core.Bakery;
import model.Ingredient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the inventory of ingredients in the bakery.
 * Demonstrates: Encapsulation, working with collections and maps.
 */
public class InventoryManager {

    /**
     * Checks for ingredients that are below their minimum stock threshold.
     * @return list of low-stock ingredients
     */
    public List<Ingredient> checkLowStock(Bakery bakery) {
        List<Ingredient> lowStock = new ArrayList<>();
        for (Ingredient ingredient : bakery.getIngredients()) {
            if (ingredient.isLowStock()) {
                lowStock.add(ingredient);
            }
        }
        return lowStock;
    }

    /**
     * Restocks an ingredient by a given amount.
     */
    public void restockIngredient(Ingredient ingredient, int amount) {
        int oldStock = ingredient.getStock();
        ingredient.updateStock(amount);
        System.out.println("  ✓ Restocked " + ingredient.getName() +
                           ": " + oldStock + " → " + ingredient.getStock() + " units");
    }

    /**
     * Generates a list of stock alert messages for low-stock ingredients.
     */
    public List<String> generateStockAlert(Bakery bakery) {
        List<String> alerts = new ArrayList<>();
        for (Ingredient ingredient : bakery.getIngredients()) {
            if (ingredient.isLowStock()) {
                alerts.add("⚠ LOW STOCK: " + ingredient.getName() +
                           " — Current: " + ingredient.getStock() +
                           " | Minimum: " + ingredient.getMinStockThreshold());
            }
        }
        return alerts;
    }

    /**
     * Returns a summary map of all ingredient stocks.
     */
    public Map<String, Integer> getStockSummary(Bakery bakery) {
        Map<String, Integer> summary = new HashMap<>();
        for (Ingredient ingredient : bakery.getIngredients()) {
            summary.put(ingredient.getName(), ingredient.getStock());
        }
        return summary;
    }
}
