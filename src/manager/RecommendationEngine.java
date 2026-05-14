package manager;

import model.Customer;
import model.Order;
import model.Product;
import model.SalesRecord;
import java.util.*;

/**
 * Provides product recommendations and trend analysis.
 * Demonstrates: Advanced algorithms, working with maps and sorting.
 */
public class RecommendationEngine {

    /**
     * Recommends products for a customer based on their order history
     * and top-selling items.
     */
    public List<Product> recommendForCustomer(Customer customer, SalesManager salesManager) {
        // Get products the customer has already ordered
        Set<Integer> purchasedIds = new HashSet<>();
        for (Order order : customer.getOrderHistory()) {
            for (Product p : order.getProducts()) {
                purchasedIds.add(p.getId());
            }
        }

        // Get top-selling products the customer hasn't purchased yet
        List<Product> topSelling = getTopSelling(salesManager, 10);
        List<Product> recommendations = new ArrayList<>();
        for (Product p : topSelling) {
            if (!purchasedIds.contains(p.getId())) {
                recommendations.add(p);
            }
        }
        return recommendations;
    }

    /**
     * Gets the top N best-selling products based on sales records.
     */
    public List<Product> getTopSelling(SalesManager salesManager, int limit) {
        // Count total quantity sold per product
        Map<Product, Integer> salesCount = new HashMap<>();
        for (SalesRecord record : salesManager.getRecords()) {
            salesCount.merge(record.getProduct(), record.getQuantitySold(), Integer::sum);
        }

        // Sort by quantity sold (descending)
        List<Map.Entry<Product, Integer>> sorted = new ArrayList<>(salesCount.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        List<Product> topProducts = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            topProducts.add(sorted.get(i).getKey());
        }
        return topProducts;
    }

    /**
     * Suggests combo products that pair well with a given product
     * (same category or frequently bought together).
     */
    public List<Product> suggestCombo(Product product, SalesManager salesManager) {
        // Find products frequently sold on the same date as this product
        Set<Product> combos = new HashSet<>();
        for (SalesRecord record : salesManager.getRecords()) {
            if (record.getProduct().getId() == product.getId()) {
                // Find other products sold on the same date
                for (SalesRecord other : salesManager.getRecords()) {
                    if (other.getProduct().getId() != product.getId()
                            && other.getSaleDate().equals(record.getSaleDate())) {
                        combos.add(other.getProduct());
                    }
                }
            }
        }
        return new ArrayList<>(combos);
    }

    /**
     * Predicts sales trends by analyzing the sales growth of each product.
     * @return a map of product name → trend description
     */
    public Map<String, String> predictTrend(SalesManager salesManager) {
        Map<String, Integer> productSales = new HashMap<>();
        for (SalesRecord record : salesManager.getRecords()) {
            productSales.merge(record.getProduct().getName(), record.getQuantitySold(), Integer::sum);
        }

        Map<String, String> trends = new HashMap<>();
        for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
            if (entry.getValue() > 20) {
                trends.put(entry.getKey(), "📈 High Demand — Consider increasing production");
            } else if (entry.getValue() > 10) {
                trends.put(entry.getKey(), "📊 Steady — Maintain current production");
            } else {
                trends.put(entry.getKey(), "📉 Low Demand — Consider promotions or reducing production");
            }
        }
        return trends;
    }
}
