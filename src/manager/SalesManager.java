package manager;

import model.Product;
import model.SalesRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages sales records and generates reports.
 * Demonstrates: Encapsulation, working with dates and collections.
 */
public class SalesManager {
    private List<SalesRecord> records;

    // Constructor
    public SalesManager() {
        this.records = new ArrayList<>();
    }

    // Getter
    public List<SalesRecord> getRecords() {
        return records;
    }

    /**
     * Records a new sale transaction.
     */
    public void recordSale(Product product, int quantitySold) {
        double revenue = product.getPrice() * quantitySold;
        int newId = records.size() + 1;
        SalesRecord record = new SalesRecord(newId, product, quantitySold, revenue, LocalDate.now());
        records.add(record);

        // Decrease product quantity
        product.setQuantity(product.getQuantity() - quantitySold);

        System.out.println("  ✓ Sale recorded: " + quantitySold + "x " + product.getName() +
                           " = $" + String.format("%.2f", revenue));
    }

    /**
     * Calculates total revenue between two dates (inclusive).
     */
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        double total = 0;
        for (SalesRecord record : records) {
            LocalDate d = record.getSaleDate();
            if (!d.isBefore(startDate) && !d.isAfter(endDate)) {
                total += record.getRevenue();
            }
        }
        return total;
    }

    /**
     * Calculates today's total sales revenue.
     */
    public double getDailySales() {
        return getTotalRevenue(LocalDate.now(), LocalDate.now());
    }

    /**
     * Generates a formatted sales report.
     */
    public String generateSalesReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════════════════╗\n");
        sb.append("║              📊 SALES REPORT                     ║\n");
        sb.append("╠══════════════════════════════════════════════════╣\n");

        if (records.isEmpty()) {
            sb.append("║  No sales records found.                         ║\n");
        } else {
            double totalRevenue = 0;
            int totalItems = 0;
            for (SalesRecord record : records) {
                sb.append(String.format("║ #%-3d | %-15s | Qty: %-3d | $%-8.2f ║%n",
                        record.getId(), record.getProduct().getName(),
                        record.getQuantitySold(), record.getRevenue()));
                totalRevenue += record.getRevenue();
                totalItems += record.getQuantitySold();
            }
            sb.append("╠══════════════════════════════════════════════════╣\n");
            sb.append(String.format("║ TOTAL: %-5d items          Revenue: $%-10.2f║%n", totalItems, totalRevenue));
        }

        sb.append("╚══════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
}
