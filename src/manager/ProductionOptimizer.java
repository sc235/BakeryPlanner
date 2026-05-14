package manager;

import core.Bakery;
import model.*;
import java.util.*;

/**
 * Optimizes production planning based on inventory and demand.
 * Demonstrates: Complex algorithms, working with Maps and business logic.
 */
public class ProductionOptimizer {

    /**
     * Plans production by checking which recipes can be made
     * with current ingredient stock.
     * @return a map of product name → max producible quantity
     */
    public Map<String, Integer> planProduction(Bakery bakery) {
        Map<String, Integer> plan = new HashMap<>();

        for (Recipe recipe : bakery.getRecipes()) {
            int maxBatches = Integer.MAX_VALUE;
            Map<Ingredient, Integer> needed = recipe.getIngredientsNeeded();

            for (Map.Entry<Ingredient, Integer> entry : needed.entrySet()) {
                Ingredient ingredient = entry.getKey();
                int requiredPerBatch = entry.getValue();
                if (requiredPerBatch > 0) {
                    int possibleBatches = ingredient.getStock() / requiredPerBatch;
                    maxBatches = Math.min(maxBatches, possibleBatches);
                }
            }

            if (maxBatches == Integer.MAX_VALUE) {
                maxBatches = 0;
            }

            plan.put(recipe.getProduct().getName(), maxBatches);
        }

        return plan;
    }

    /**
     * Displays production plan to console.
     */
    public void displayProductionPlan(Bakery bakery) {
        Map<String, Integer> plan = planProduction(bakery);
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║            🏭 PRODUCTION PLAN                      ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        if (plan.isEmpty()) {
            System.out.println("║  No recipes available or missing ingredients.    ║");
        } else {
            for (Map.Entry<String, Integer> entry : plan.entrySet()) {
                System.out.printf("║ - %-20s: max %4d batches         ║%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    /**
     * Suggests which ingredients need restocking based on recipe demands.
     * @return list of restocking suggestions
     */
    public List<String> suggestRestocking(Bakery bakery) {
        List<String> suggestions = new ArrayList<>();

        for (Ingredient ingredient : bakery.getIngredients()) {
            if (ingredient.isLowStock()) {
                int deficit = ingredient.getMinStockThreshold() * 2 - ingredient.getStock();
                suggestions.add("Restock " + ingredient.getName() +
                               ": current=" + ingredient.getStock() +
                               ", suggested order=" + Math.max(deficit, ingredient.getMinStockThreshold()));
            }
        }

        return suggestions;
    }

    /**
     * Estimates future demand based on past sales data.
     * @return map of product name → estimated daily demand
     */
    public Map<String, Double> estimateDemand(SalesManager salesManager) {
        Map<String, Integer> totalSales = new HashMap<>();
        Map<String, Integer> daysCounted = new HashMap<>();

        for (SalesRecord record : salesManager.getRecords()) {
            String name = record.getProduct().getName();
            totalSales.merge(name, record.getQuantitySold(), Integer::sum);
            daysCounted.merge(name, 1, Integer::sum);
        }

        Map<String, Double> demand = new HashMap<>();
        for (String product : totalSales.keySet()) {
            double avgDaily = (double) totalSales.get(product) / daysCounted.get(product);
            demand.put(product, Math.round(avgDaily * 10.0) / 10.0);
        }

        return demand;
    }

    /**
     * Prioritizes recipes by their estimated batch yield.
     * @param recipes list of recipes to evaluate
     * @param targetOutput desired total output
     * @return the recommended batch count
     */
    public int prioritizeBatchList(List<Recipe> recipes, int targetOutput) {
        if (recipes.isEmpty()) return 0;
        return (int) Math.ceil((double) targetOutput / recipes.size());
    }

    /**
     * Optimizes the batch size for a given recipe to meet target output.
     * @param recipe the recipe to optimize
     * @param targetOutput the desired number of units
     * @return the recommended batch count
     */
    public int optimizeBatchSize(Recipe recipe, int targetOutput) {
        Map<Ingredient, Integer> needed = recipe.getIngredientsNeeded();
        if (needed.isEmpty()) return targetOutput;

        int maxBatches = Integer.MAX_VALUE;
        for (Map.Entry<Ingredient, Integer> entry : needed.entrySet()) {
            int perBatch = entry.getValue();
            if (perBatch > 0) {
                maxBatches = Math.min(maxBatches, entry.getKey().getStock() / perBatch);
            }
        }
        if (maxBatches == Integer.MAX_VALUE) maxBatches = 0;
        return Math.min(targetOutput, maxBatches);
    }
}
