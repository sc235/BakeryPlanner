package manager;

import model.Customer;
import model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages customers: adding, removing, searching.
 * Demonstrates: Encapsulation, working with collections.
 */
public class CustomerManager {
    private List<Customer> customers;

    // Constructor
    public CustomerManager() {
        this.customers = new ArrayList<>();
    }

    /**
     * Adds a new customer.
     */
    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("  ✓ Customer added: " + customer.getName());
    }

    /**
     * Removes a customer by ID.
     */
    public void removeCustomer(int customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            customers.remove(customer);
            System.out.println("  ✓ Customer removed: " + customer.getName());
        } else {
            System.out.println("  ✗ Customer with ID " + customerId + " not found.");
        }
    }

    /**
     * Finds a customer by their ID.
     */
    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Searches for customers by name (partial match).
     */
    public List<Customer> searchByName(String name) {
        List<Customer> results = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(c);
            }
        }
        return results;
    }

    /**
     * Returns all registered customers.
     */
    public List<Customer> getAllCustomers() {
        return customers;
    }

    /**
     * Gets the total number of customers.
     */
    public int getCustomerCount() {
        return customers.size();
    }

    /**
     * Sends a notification to a customer about their order.
     * @param order the order to notify about
     * @param customer the customer to notify
     */
    public void notifyCustomer(Order order, Customer customer) {
        System.out.println("  📧 Notification sent to " + customer.getName() +
                           " (" + customer.getPhone() + "): Order #" + order.getId() +
                           " is now '" + order.getStatus() + "'");
    }
}
