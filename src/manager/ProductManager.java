package manager;

import core.Bakery;
import model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages product operations within the bakery.
 * Demonstrates: Single Responsibility Principle, working with collections.
 */
public class ProductManager {

    /**
     * Adds a product to the bakery's inventory.
     */
    public void addProduct(Bakery bakery, Product product) {
        bakery.addProduct(product);
        System.out.println("  ✓ Product added: " + product.getName());
    }

    /**
     * Removes a product from the bakery by its ID.
     */
    public void removeProduct(Bakery bakery, int productId) {
        Product product = bakery.getProductById(productId);
        if (product != null) {
            bakery.getProducts().remove(product);
            System.out.println("  ✓ Product removed: " + product.getName());
        } else {
            System.out.println("  ✗ Product with ID " + productId + " not found.");
        }
    }

    /**
     * Updates the price of a product.
     */
    public void updatePrice(Product product, double newPrice) {
        double oldPrice = product.getPrice();
        product.setPrice(newPrice);
        System.out.println("  ✓ Price updated for " + product.getName() +
                           ": $" + String.format("%.2f", oldPrice) + " → $" + String.format("%.2f", newPrice));
    }

    /**
     * Returns a list of all expired products in the bakery.
     */
    public List<Product> getExpiredProducts(Bakery bakery) {
        List<Product> expired = new ArrayList<>();
        for (Product p : bakery.getProducts()) {
            if (p.isExpired()) {
                expired.add(p);
            }
        }
        return expired;
    }

    /**
     * Filters products by category.
     */
    public List<Product> filterByCategory(Bakery bakery, String category) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : bakery.getProducts()) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }
}
