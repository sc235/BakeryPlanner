package manager;

import core.Bakery;
import model.Customer;
import model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks and manages order statuses and history.
 * Demonstrates: Single Responsibility Principle, Association.
 */
public class OrderTracker {

    /**
     * Updates the status of an order (e.g., "Pending" → "In Progress" → "Completed").
     */
    public void updateStatus(Order order, String newStatus) {
        String oldStatus = order.getStatus();
        order.setStatus(newStatus);
        System.out.println("  ✓ Order #" + order.getId() + " status: " + oldStatus + " → " + newStatus);
    }

    /**
     * Gets all orders with a specific status from the bakery.
     */
    public List<Order> getOrdersByStatus(Bakery bakery, String status) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : bakery.getOrders()) {
            if (order.getStatus().equalsIgnoreCase(status)) {
                filtered.add(order);
            }
        }
        return filtered;
    }

    /**
     * Gets the full order history for a specific customer.
     */
    public List<Order> getOrderHistory(Customer customer) {
        return customer.getOrderHistory();
    }

    /**
     * Sends a notification to a customer about their order status.
     */
    public void notifyCustomer(Order order, Customer customer) {
        System.out.println("  📧 Notification sent to " + customer.getName() +
                           " (" + customer.getPhone() + "): Order #" + order.getId() +
                           " is now '" + order.getStatus() + "'");
    }
}
