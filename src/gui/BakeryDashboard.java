package gui;

import core.BakeryPlanner;
import core.Bakery;
import manager.*;
import model.*;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Premium GUI Dashboard for the Bakery Planner.
 * Features: gradient backgrounds, animated sidebar, styled tables,
 * stat cards with icons, and a modern dark/gold aesthetic.
 */
public class BakeryDashboard extends JFrame {

    private BakeryPlanner planner;

    // ── Premium Color Palette ──
    private static final Color BG_DARK     = new Color(22, 22, 30);
    private static final Color BG_CARD     = new Color(30, 31, 42);
    private static final Color BG_SIDEBAR  = new Color(16, 16, 24);
    private static final Color BG_HOVER    = new Color(42, 43, 58);
    private static final Color BG_ACTIVE   = new Color(55, 48, 80);
    private static final Color ACCENT_GOLD = new Color(234, 179, 72);
    private static final Color ACCENT_PURPLE = new Color(138, 92, 246);
    private static final Color ACCENT_GREEN  = new Color(52, 211, 153);
    private static final Color ACCENT_ROSE   = new Color(244, 114, 182);
    private static final Color ACCENT_BLUE   = new Color(96, 165, 250);
    private static final Color TEXT_PRIMARY   = new Color(240, 240, 245);
    private static final Color TEXT_SECONDARY = new Color(148, 150, 168);
    private static final Color BORDER_COLOR   = new Color(50, 52, 70);
    private static final Color TABLE_ROW_ALT  = new Color(26, 27, 38);
    private static final Color TABLE_ROW_HOVER = new Color(45, 46, 62);

    private JPanel contentArea;
    private JPanel sidebarNotificationPanel;
    private JButton activeNavButton;
    private String activePage = "Dashboard";

    public BakeryDashboard(BakeryPlanner planner) {
        this.planner = planner;

        setTitle("🧁 Bakery Planner — Premium Dashboard");
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_DARK);
        contentArea.setBorder(new EmptyBorder(28, 28, 28, 28));
        contentArea.add(buildDashboardView(), BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);
        
        refreshSidebarNotifications();
        showStartupAlerts();
    }

    // ═══════════════════════════════════════
    //                SIDEBAR
    // ═══════════════════════════════════════

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // ── Logo ──
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        logoPanel.setBackground(BG_SIDEBAR);
        logoPanel.setBorder(new EmptyBorder(28, 0, 28, 0));
        logoPanel.setMaximumSize(new Dimension(240, 80));
        JLabel logo = new JLabel("🧁");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        JLabel brand = new JLabel("BakeryPlanner");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 19));
        brand.setForeground(ACCENT_GOLD);
        logoPanel.add(logo);
        logoPanel.add(brand);
        sidebar.add(logoPanel);

        // ── Separator ──
        sidebar.add(createSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // ── Section Label ──
        sidebar.add(createSectionLabel("MENU"));

        // ── Nav Items ──
        String[][] navItems = {
            {"📊", "Dashboard"},
            {"🍞", "Products"},
            {"📦", "Inventory"},
            {"📋", "Orders"},
            {"👥", "Customers"},
            {"📈", "Sales"},
            {"🧑‍🍳", "Recipes"},
        };
        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0], item[1]);
            if (item[1].equals("Dashboard")) {
                activeNavButton = btn;
                btn.setBackground(BG_ACTIVE);
            }
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        sidebar.add(Box.createVerticalGlue());

        // ── Bottom Actions ──
        sidebar.add(createSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton syncBtn = createNavButton("💾", "Sync to SQLite");
        syncBtn.addActionListener(e -> {
            planner.persistData();
            showToast("Data synced to SQLite ✓");
        });
        sidebar.add(syncBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Smart Notifications Panel ──
        sidebarNotificationPanel = buildSidebarNotificationPanel();
        sidebar.add(sidebarNotificationPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));

        return sidebar;
    }

    private void showStartupAlerts() {
        int expiredCount = 0;
        for (Product p : planner.getBakery().getProducts()) { if (p.isExpired()) expiredCount++; }
        int lowStockCount = 0;
        for (Ingredient i : planner.getBakery().getIngredients()) { if (i.isLowStock()) lowStockCount++; }

        if (expiredCount > 0 || lowStockCount > 0) {
            String msg = "Welcome! You have urgent tasks:\n";
            if (expiredCount > 0) msg += "• " + expiredCount + " products are EXPIRED.\n";
            if (lowStockCount > 0) msg += "• " + lowStockCount + " ingredients are LOW STOCK.\n";
            msg += "\nPlease check the Alerts panel.";
            
            JOptionPane.showMessageDialog(this, msg, "Smart Bakery Notifications", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshSidebarNotifications() {
        if (sidebarNotificationPanel != null) {
            sidebarNotificationPanel.removeAll();
            
            // Calculate Alerts
            int expiredCount = 0;
            for (Product p : planner.getBakery().getProducts()) { if (p.isExpired()) expiredCount++; }
            int lowStockCount = 0;
            for (Ingredient i : planner.getBakery().getIngredients()) { if (i.isLowStock()) lowStockCount++; }

            if (expiredCount > 0 || lowStockCount > 0) {
                JLabel title = new JLabel("SMART ALERTS");
                title.setFont(new Font("Segoe UI", Font.BOLD, 10));
                title.setForeground(TEXT_SECONDARY);
                title.setAlignmentX(Component.LEFT_ALIGNMENT);
                sidebarNotificationPanel.add(title);
                sidebarNotificationPanel.add(Box.createRigidArea(new Dimension(0, 8)));

                if (expiredCount > 0) {
                    JLabel exp = new JLabel("🛑 " + expiredCount + " Expired Items");
                    exp.setForeground(ACCENT_ROSE);
                    exp.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    exp.setAlignmentX(Component.LEFT_ALIGNMENT);
                    sidebarNotificationPanel.add(exp);
                }
                if (lowStockCount > 0) {
                    sidebarNotificationPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                    JLabel low = new JLabel("⚠️ " + lowStockCount + " Low Stock");
                    low.setForeground(ACCENT_GOLD);
                    low.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    low.setAlignmentX(Component.LEFT_ALIGNMENT);
                    sidebarNotificationPanel.add(low);
                }
            }
            sidebarNotificationPanel.revalidate();
            sidebarNotificationPanel.repaint();
        }
    }

    private JPanel buildSidebarNotificationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SIDEBAR);
        panel.setBorder(new EmptyBorder(0, 15, 0, 15));
        panel.setMinimumSize(new Dimension(240, 60));
        panel.setMaximumSize(new Dimension(240, 120));
        return panel;
    }

    private JButton createNavButton(String icon, String label) {
        JButton btn = new JButton(icon + "   " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getBackground();
                if (getModel().isRollover() && bg != BG_ACTIVE) bg = BG_HOVER;
                g2.setColor(bg);
                g2.fillRoundRect(8, 0, getWidth() - 16, getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 22, 0, 0));
        btn.setMaximumSize(new Dimension(240, 42));
        btn.setPreferredSize(new Dimension(240, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btn != activeNavButton) btn.setForeground(TEXT_PRIMARY); btn.repaint(); }
            public void mouseExited(MouseEvent e) { if (btn != activeNavButton) btn.setForeground(TEXT_SECONDARY); btn.repaint(); }
        });

        btn.addActionListener(e -> navigateTo(label, btn));
        return btn;
    }

    private void navigateTo(String page, JButton btn) {
        activePage = page;

        if (activeNavButton != null) {
            activeNavButton.setBackground(BG_SIDEBAR);
            activeNavButton.setForeground(TEXT_SECONDARY);
        }
        activeNavButton = btn;
        btn.setBackground(BG_ACTIVE);
        btn.setForeground(ACCENT_GOLD);

        contentArea.removeAll();
        refreshSidebarNotifications();
        switch (page) {
            case "Dashboard":  contentArea.add(buildDashboardView()); break;
            case "Products":   contentArea.add(buildProductsView()); break;
            case "Inventory":  contentArea.add(buildInventoryView()); break;
            case "Orders":     contentArea.add(buildOrdersView()); break;
            case "Customers":  contentArea.add(buildCustomersView()); break;
            case "Sales":      contentArea.add(buildSalesView()); break;
            case "Recipes":    contentArea.add(buildRecipesView()); break;
        }
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ═══════════════════════════════════════
    //           DASHBOARD VIEW
    // ═══════════════════════════════════════

    private JPanel buildDashboardView() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBackground(BG_DARK);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_DARK);
        headerPanel.add(buildPageHeader("Dashboard", "Welcome back! Here's your bakery overview."));

        // Alert Banner
        java.util.List<String> alerts = new java.util.ArrayList<>();
        int expiredCount = 0;
        for (Product p : planner.getBakery().getProducts()) { if (p.isExpired()) expiredCount++; }
        if (expiredCount > 0) alerts.add("⚠️ " + expiredCount + " products are EXPIRED.");
        
        int lowStockCount = 0;
        for (Ingredient i : planner.getBakery().getIngredients()) { if (i.isLowStock()) lowStockCount++; }
        if (lowStockCount > 0) alerts.add("🔴 " + lowStockCount + " ingredients have LOW STOCK.");
        
        int pendingOrders = 0;
        for (Order o : planner.getBakery().getOrders()) { if (o.getStatus().equals("Pending")) pendingOrders++; }
        if (pendingOrders > 0) alerts.add("🟡 " + pendingOrders + " orders are PENDING.");

        if (!alerts.isEmpty()) {
            JPanel banner = new JPanel(new BorderLayout());
            banner.setBackground(new Color(255, 80, 80, 40));
            banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_ROSE, 1, true),
                new EmptyBorder(10, 15, 10, 15)
            ));
            StringBuilder alertText = new StringBuilder("<html><b>ACTION REQUIRED:</b> ");
            for (int i = 0; i < alerts.size(); i++) {
                alertText.append(alerts.get(i));
                if (i < alerts.size() - 1) alertText.append(" &nbsp;&nbsp;|&nbsp;&nbsp; ");
            }
            alertText.append("</html>");
            JLabel bannerLbl = new JLabel(alertText.toString());
            bannerLbl.setForeground(new Color(255, 200, 200));
            bannerLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            banner.add(bannerLbl, BorderLayout.CENTER);
            
            headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            headerPanel.add(banner);
        }

        panel.add(headerPanel, BorderLayout.NORTH);

        // Stats cards row
        int totalProducts = planner.getBakery().getProducts().size();
        int totalOrders = planner.getBakery().getOrders().size();
        double revenue = planner.getSalesManager().getDailySales();
        int totalCustomers = planner.getCustomerManager().getAllCustomers().size();

        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setBackground(BG_DARK);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        cards.add(createStatCard("Total Products", String.valueOf(totalProducts), "🍞", ACCENT_PURPLE));
        cards.add(createStatCard("Active Orders", String.valueOf(totalOrders), "📋", ACCENT_BLUE));
        cards.add(createStatCard("Today's Revenue", "$" + String.format("%.2f", revenue), "💰", ACCENT_GOLD));
        cards.add(createStatCard("Low Stock Alerts", String.valueOf(lowStockCount), "⚠️", lowStockCount > 0 ? ACCENT_ROSE : ACCENT_GREEN));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_DARK);
        centerPanel.add(cards);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // Revenue Chart Section
        JPanel chartCard = buildCard("Top 5 Products by Revenue");
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Aggregate revenue per product
                Map<String, Double> productRev = new java.util.HashMap<>();
                for (SalesRecord r : planner.getSalesManager().getRecords()) {
                    productRev.put(r.getProduct().getName(), productRev.getOrDefault(r.getProduct().getName(), 0.0) + r.getRevenue());
                }
                
                // Sort and get top 5
                List<Map.Entry<String, Double>> sortedRev = new ArrayList<>(productRev.entrySet());
                sortedRev.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                if (sortedRev.size() > 5) sortedRev = sortedRev.subList(0, 5);
                
                if (sortedRev.isEmpty()) {
                    g2.setColor(TEXT_SECONDARY);
                    g2.drawString("No sales data available.", getWidth()/2 - 50, getHeight()/2);
                    return;
                }
                
                double maxRev = sortedRev.get(0).getValue();
                int barWidth = 60;
                int gap = 40;
                int startX = 40;
                int startY = getHeight() - 40;
                int maxBarHeight = getHeight() - 80;
                
                for (int i = 0; i < sortedRev.size(); i++) {
                    Map.Entry<String, Double> entry = sortedRev.get(i);
                    int barHeight = (int) ((entry.getValue() / maxRev) * maxBarHeight);
                    int x = startX + i * (barWidth + gap);
                    int y = startY - barHeight;
                    
                    // Draw Bar
                    g2.setColor(ACCENT_BLUE);
                    g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
                    
                    // Draw Label (Name)
                    g2.setColor(TEXT_PRIMARY);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    String name = entry.getKey();
                    if (name.length() > 8) name = name.substring(0, 6) + "..";
                    g2.drawString(name, x + 5, startY + 20);
                    
                    // Draw Value
                    g2.setColor(ACCENT_GOLD);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.drawString("$" + String.format("%.0f", entry.getValue()), x + 5, y - 10);
                }
            }
        };
        chartPanel.setOpaque(false);
        chartPanel.setPreferredSize(new Dimension(600, 250));
        chartCard.add(chartPanel, BorderLayout.CENTER);
        
        centerPanel.add(chartCard);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // Lower Grid: Best Sellers and Low Stock
        JPanel lowerGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        lowerGrid.setBackground(BG_DARK);
        lowerGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // ── Best Sellers Panel ──
        JPanel bestSellersCard = buildCard("⭐ Best Selling Products");
        JPanel bsList = new JPanel();
        bsList.setLayout(new BoxLayout(bsList, BoxLayout.Y_AXIS));
        bsList.setBackground(BG_CARD);
        
        List<Product> topSelling = planner.getRecommendationEngine().getTopSelling(planner.getSalesManager(), 5);
        if (topSelling.isEmpty()) {
            bsList.add(new JLabel("No sales data yet."));
        } else {
            for (int i = 0; i < topSelling.size(); i++) {
                Product p = topSelling.get(i);
                JPanel item = new JPanel(new BorderLayout(10, 0));
                item.setBackground(BG_CARD);
                item.setBorder(new EmptyBorder(8, 0, 8, 0));
                
                JLabel rank = new JLabel("#" + (i + 1));
                rank.setFont(new Font("Segoe UI", Font.BOLD, 14));
                rank.setForeground(ACCENT_GOLD);
                
                JLabel name = new JLabel(p.getName());
                name.setForeground(TEXT_PRIMARY);
                
                JLabel cat = new JLabel(p.getCategory());
                cat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                cat.setForeground(TEXT_SECONDARY);
                
                JPanel text = new JPanel(new GridLayout(2, 1));
                text.setBackground(BG_CARD);
                text.add(name);
                text.add(cat);
                
                item.add(rank, BorderLayout.WEST);
                item.add(text, BorderLayout.CENTER);
                bsList.add(item);
            }
        }
        bestSellersCard.add(new JScrollPane(bsList), BorderLayout.CENTER);
        lowerGrid.add(bestSellersCard);

        // ── Low Stock Panel ──
        JPanel lowStockCard = buildCard("⚠️ Inventory Alerts");
        JPanel lsList = new JPanel();
        lsList.setLayout(new BoxLayout(lsList, BoxLayout.Y_AXIS));
        lsList.setBackground(BG_CARD);
        
        List<Ingredient> lowStock = new ArrayList<>();
        for (Ingredient i : planner.getBakery().getIngredients()) {
            if (i.isLowStock()) lowStock.add(i);
        }
        
        if (lowStock.isEmpty()) {
            JLabel good = new JLabel("✅ All ingredients are well-stocked.");
            good.setForeground(ACCENT_GREEN);
            lsList.add(good);
        } else {
            for (Ingredient ing : lowStock) {
                JPanel item = new JPanel(new BorderLayout(10, 0));
                item.setBackground(BG_CARD);
                item.setBorder(new EmptyBorder(8, 0, 8, 0));
                
                JLabel icon = new JLabel("🛑");
                JLabel name = new JLabel(ing.getName());
                name.setForeground(TEXT_PRIMARY);
                
                JLabel qty = new JLabel(ing.getStock() + " / " + ing.getMinStockThreshold() + " units");
                qty.setForeground(ACCENT_ROSE);
                qty.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                item.add(icon, BorderLayout.WEST);
                item.add(name, BorderLayout.CENTER);
                item.add(qty, BorderLayout.EAST);
                lsList.add(item);
            }
        }
        lowStockCard.add(new JScrollPane(lsList), BorderLayout.CENTER);
        lowerGrid.add(lowStockCard);

        centerPanel.add(lowerGrid);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════
    //           PRODUCTS VIEW
    // ═══════════════════════════════════════

    private JPanel buildProductsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_DARK);
        panel.add(buildPageHeader("Products", "Manage your bakery's product catalog."), BorderLayout.NORTH);

        JPanel card = buildCard("All Products");
        String[] cols = {"ID", "Name", "Category", "Price", "Qty", "Expires", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        java.util.List<Product> products = planner.getBakery().getProducts();
        for (Product p : products) {
            String status = p.isExpired() ? "⛔ Expired" : (p.getQuantity() < 5 ? "⚠️ Low" : "✅ Good");
            model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(),
                "$" + String.format("%.2f", p.getPrice()), p.getQuantity(), p.getExpirationDate(), status});
        }
        JScrollPane tableScroll = buildStyledTable(model);
        JTable table = (JTable) tableScroll.getViewport().getView();
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Search: "));
        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            private void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);

        // ── Action Buttons ──
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_CARD);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton addBtn = createStyledButton("➕ Add Product", ACCENT_GREEN);
        addBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField qtyField = new JTextField();
            JComboBox<String> catBox = new JComboBox<>(new String[]{"Bread", "Cake", "Pastry"});
            JTextField daysField = new JTextField("7");

            Object[] fields = {
                "Product Name:", nameField,
                "Price ($):", priceField,
                "Quantity:", qtyField,
                "Category:", catBox,
                "Days until expiration:", daysField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Add New Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    String category = (String) catBox.getSelectedItem();
                    int days = Integer.parseInt(daysField.getText().trim());
                    if (name.isEmpty()) { showToast("Name cannot be empty!"); return; }

                    LocalDate expiration = LocalDate.now().plusDays(days);
                    int nextId = 1;
                    for (Product p : planner.getBakery().getProducts()) {
                        if (p.getId() >= nextId) nextId = p.getId() + 1;
                    }
                    Product newProduct = new Product(nextId, name, price, qty, category, expiration);
                    planner.getProductManager().addProduct(planner.getBakery(), newProduct);
                    planner.persistData();
                    navigateTo("Products", activeNavButton);
                    showToast("Product '" + name + "' added ✓");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Operation failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actions.add(addBtn);

        JButton removeBtn = createStyledButton("🗑️ Remove Product", ACCENT_ROSE);
        removeBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a product first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            int productId = (int) model.getValueAt(row, 0);
            String productName = (String) model.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Remove '" + productName + "'?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    planner.getProductManager().removeProduct(planner.getBakery(), productId);
                    planner.persistData();
                    navigateTo("Products", activeNavButton);
                    showToast("Product '" + productName + "' removed ✓");
                } catch (Exception ex) {
                    showToast("Delete failed!");
                }
            }
        });
        actions.add(removeBtn);

        JButton editBtn = createStyledButton("✏️ Edit Product", ACCENT_BLUE);
        editBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a product first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            int productId = (int) model.getValueAt(row, 0);
            Product target = planner.getBakery().getProductById(productId);
            if (target == null) return;

            JTextField priceField = new JTextField(String.valueOf(target.getPrice()));
            JTextField qtyField = new JTextField(String.valueOf(target.getQuantity()));
            JTextField dateField = new JTextField(target.getExpirationDate().toString());

            Object[] fields = {
                "Update Price ($):", priceField,
                "Update Quantity (0 to dispose stock):", qtyField,
                "Update Expiration Date (YYYY-MM-DD):", dateField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Edit Product: " + target.getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double newPrice = Double.parseDouble(priceField.getText().trim());
                    int newQty = Integer.parseInt(qtyField.getText().trim());
                    String newDate = dateField.getText().trim();
                    
                    if (newQty < 0 || newPrice < 0) {
                        showToast("Values must be positive!");
                        return;
                    }
                    
                    target.setPrice(newPrice);
                    target.setQuantity(newQty);
                    target.setExpirationDate(LocalDate.parse(newDate));
                    
                    planner.persistData();
                    navigateTo("Products", activeNavButton);
                    showToast("Product '" + target.getName() + "' updated ✓");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers and date format (YYYY-MM-DD).", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actions.add(editBtn);

        JButton wasteBtn = createStyledButton("🗑️ Dispose Waste", ACCENT_ROSE);
        wasteBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a product first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            int productId = (int) model.getValueAt(row, 0);
            Product target = planner.getBakery().getProductById(productId);
            if (target == null) return;

            String input = JOptionPane.showInputDialog(this, "How many units of " + target.getName() + " to dispose?", "Dispose Waste", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int qty = Integer.parseInt(input.trim());
                    if (qty <= 0) return;
                    if (qty > target.getQuantity()) {
                        JOptionPane.showMessageDialog(this, "Not enough stock!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    target.setQuantity(target.getQuantity() - qty);
                    planner.persistData();
                    navigateTo("Products", activeNavButton);
                    showToast("Disposed " + qty + " units of " + target.getName() + " ✓");
                } catch (NumberFormatException ex) {
                    showToast("Invalid number!");
                }
            }
        });
        actions.add(wasteBtn);

        card.add(actions, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════
    //           INVENTORY VIEW
    // ═══════════════════════════════════════

    private JPanel buildInventoryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_DARK);
        panel.add(buildPageHeader("Inventory", "Track raw ingredients and stock levels."), BorderLayout.NORTH);

        JPanel card = buildCard("Ingredients Stock");
        String[] cols = {"ID", "Name", "Stock", "Min Threshold", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Ingredient> ingredients = planner.getBakery().getIngredients();
        for (Ingredient i : ingredients) {
            String status = i.isLowStock() ? "🔴 LOW STOCK" : "🟢 OK";
            model.addRow(new Object[]{i.getId(), i.getName(), i.getStock(), i.getMinStockThreshold(), status});
        }
        JScrollPane tableScroll = buildStyledTable(model);
        JTable table = (JTable) tableScroll.getViewport().getView();
        card.add(tableScroll, BorderLayout.CENTER);

        // ── Action Buttons ──
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_CARD);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton restockBtn = createStyledButton("📦 Restock", ACCENT_BLUE);
        restockBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select an ingredient first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            int ingId = (int) model.getValueAt(row, 0);
            String ingName = (String) model.getValueAt(row, 1);

            String amountStr = JOptionPane.showInputDialog(this, "How many units to add for '" + ingName + "'?", "Restock Ingredient", JOptionPane.PLAIN_MESSAGE);
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    int amount = Integer.parseInt(amountStr.trim());
                    if (amount <= 0) { showToast("Amount must be positive!"); return; }
                    Ingredient target = planner.getBakery().getIngredientById(ingId);
                    if (target != null) {
                        planner.getInventoryManager().restockIngredient(target, amount);
                        planner.persistData();
                        navigateTo("Inventory", activeNavButton);
                        showToast(ingName + " restocked +" + amount + " units ✓");
                    }
                } catch (Exception ex) {
                    showToast("Restock failed!");
                }
            }
        });
        actions.add(restockBtn);

        JButton addIngBtn = createStyledButton("➕ Add Ingredient", ACCENT_GREEN);
        addIngBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField stockField = new JTextField();
            JTextField thresholdField = new JTextField("10");

            Object[] fields = {
                "Ingredient Name:", nameField,
                "Initial Stock:", stockField,
                "Min Stock Threshold:", thresholdField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Add New Ingredient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    int stock = Integer.parseInt(stockField.getText().trim());
                    int threshold = Integer.parseInt(thresholdField.getText().trim());
                    if (name.isEmpty()) { showToast("Name cannot be empty!"); return; }

                    int nextId = 1;
                    for (Ingredient i : planner.getBakery().getIngredients()) {
                        if (i.getId() >= nextId) nextId = i.getId() + 1;
                    }
                    Ingredient newIng = new Ingredient(nextId, name, stock, threshold);
                    planner.getBakery().addIngredient(newIng);
                    planner.persistData();
                    navigateTo("Inventory", activeNavButton);
                    showToast("Ingredient '" + name + "' added ✓");
                } catch (Exception ex) {
                    showToast("Failed to add ingredient!");
                }
            }
        });
        actions.add(addIngBtn);

        card.add(actions, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════
    //            ORDERS VIEW
    // ═══════════════════════════════════════

    private JPanel buildOrdersView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_DARK);
        panel.add(buildPageHeader("Order Tracking", "Track and manage order status in real time."), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_DARK);

        // ── Status Pipeline Cards ──
        java.util.List<Order> orders = planner.getBakery().getOrders();
        int pending = 0, inProgress = 0, completed = 0, cancelled = 0;
        for (Order o : orders) {
            switch (o.getStatus()) {
                case "Pending":     pending++; break;
                case "In Progress": inProgress++; break;
                case "Completed":   completed++; break;
                case "Cancelled":   cancelled++; break;
            }
        }

        JPanel pipeline = new JPanel(new GridLayout(1, 4, 14, 0));
        pipeline.setBackground(BG_DARK);
        pipeline.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pipeline.add(createStatCard("Pending", String.valueOf(pending), "🟡", new Color(250, 204, 21)));
        pipeline.add(createStatCard("In Progress", String.valueOf(inProgress), "🔵", ACCENT_BLUE));
        pipeline.add(createStatCard("Completed", String.valueOf(completed), "🟢", ACCENT_GREEN));
        pipeline.add(createStatCard("Cancelled", String.valueOf(cancelled), "🔴", ACCENT_ROSE));
        centerPanel.add(pipeline);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Orders Table with Status Badges ──
        JPanel card = buildCard("All Orders");
        String[] cols = {"Order #", "Date", "Status", "Items", "Products", "Total"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Order o : orders) {
            StringBuilder productNames = new StringBuilder();
            for (int i = 0; i < o.getProducts().size(); i++) {
                if (i > 0) productNames.append(", ");
                productNames.append(o.getProducts().get(i).getName());
            }
            String statusBadge;
            switch (o.getStatus()) {
                case "Pending":     statusBadge = "🟡 Pending"; break;
                case "In Progress": statusBadge = "🔵 In Progress"; break;
                case "Completed":   statusBadge = "🟢 Completed"; break;
                case "Cancelled":   statusBadge = "🔴 Cancelled"; break;
                default:            statusBadge = o.getStatus(); break;
            }
            model.addRow(new Object[]{
                "#" + o.getId(), o.getDate(), statusBadge,
                o.getProducts().size(), productNames.toString(),
                "$" + String.format("%.2f", o.getTotalPrice())
            });
        }

        JScrollPane tableScroll = buildStyledTable(model);
        JTable table = (JTable) tableScroll.getViewport().getView();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Search: "));
        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            private void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);

        // ── Action Buttons ──
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_CARD);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton updateBtn = createStyledButton("🔄 Update Status", ACCENT_BLUE);
        updateBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select an order first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            String orderIdStr = model.getValueAt(row, 0).toString().replace("#", "");
            int orderId = Integer.parseInt(orderIdStr);
            Order target = planner.getBakery().getOrderById(orderId);
            if (target == null) return;

            String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};
            String newStatus = (String) JOptionPane.showInputDialog(
                this, "Update status for Order #" + orderId + ":",
                "Update Order Status", JOptionPane.PLAIN_MESSAGE,
                null, statuses, target.getStatus()
            );
            if (newStatus != null && !newStatus.equals(target.getStatus())) {
                try {
                    planner.getOrderTracker().updateStatus(target, newStatus);
                    planner.persistData();
                    navigateTo("Orders", activeNavButton);
                    showToast("Order #" + orderId + " → " + newStatus + " ✓");
                } catch (Exception ex) {
                    showToast("Update failed!");
                }
            }
        });
        actions.add(updateBtn);

        JButton detailsBtn = createStyledButton("📄 View Details", ACCENT_PURPLE);
        detailsBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select an order first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            String orderIdStr = model.getValueAt(row, 0).toString().replace("#", "");
            int orderId = Integer.parseInt(orderIdStr);
            Order target = planner.getBakery().getOrderById(orderId);
            if (target == null) return;

            StringBuilder details = new StringBuilder();
            details.append("Order #").append(target.getId()).append("\n");
            details.append("Date: ").append(target.getDate()).append("\n");
            details.append("Status: ").append(target.getStatus()).append("\n\n");
            details.append("Products:\n");
            for (Product p : target.getProducts()) {
                details.append("  • ").append(p.getName())
                       .append(" — $").append(String.format("%.2f", p.getPrice())).append("\n");
            }
            details.append("\nTotal: $").append(String.format("%.2f", target.getTotalPrice()));

            JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
        });
        actions.add(detailsBtn);

        JButton placeOrderBtn = createStyledButton("🛒 Place Order", ACCENT_GREEN);
        placeOrderBtn.addActionListener(e -> {
            try {
                // Check prerequisites
                java.util.List<Customer> customers = planner.getCustomerManager().getAllCustomers();
                java.util.List<Product> products = planner.getBakery().getProducts();
                if (customers.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No customers registered. Add a customer first.", "No Customers", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (products.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No products available.", "No Products", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Step 1: Select customer
                String[] customerNames = new String[customers.size()];
                for (int i = 0; i < customers.size(); i++) {
                    customerNames[i] = "[" + customers.get(i).getId() + "] " + customers.get(i).getName();
                }
                String selectedCust = (String) JOptionPane.showInputDialog(
                    this, "Select a customer:", "Place New Order — Step 1",
                    JOptionPane.PLAIN_MESSAGE, null, customerNames, customerNames[0]
                );
                if (selectedCust == null) return;
                int custId = Integer.parseInt(selectedCust.substring(1, selectedCust.indexOf("]")));
                Customer customer = null;
                for (Customer c : customers) { if (c.getId() == custId) { customer = c; break; } }
                if (customer == null) return;

                // Step 2: Select products with checkboxes
                JPanel productPanel = new JPanel();
                productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
                productPanel.add(new JLabel("Select products for " + customer.getName() + "'s order:"));
                productPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                JCheckBox[] checkboxes = new JCheckBox[products.size()];
                for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    String label = p.getName() + " — $" + String.format("%.2f", p.getPrice()) + " (qty: " + p.getQuantity() + ")";
                    checkboxes[i] = new JCheckBox(label);
                    checkboxes[i].setEnabled(p.getQuantity() > 0 && !p.isExpired());
                    if (p.getQuantity() <= 0) checkboxes[i].setText(label + " [OUT OF STOCK]");
                    if (p.isExpired()) checkboxes[i].setText(label + " [EXPIRED]");
                    productPanel.add(checkboxes[i]);
                }

                JScrollPane scrollPane = new JScrollPane(productPanel);
                scrollPane.setPreferredSize(new Dimension(400, 250));

                int result = JOptionPane.showConfirmDialog(this, scrollPane, "Place New Order — Step 2", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result != JOptionPane.OK_OPTION) return;

                // Create the order
                Order newOrder = new Order(0, customer, java.time.LocalDate.now(), "Pending");
                int itemCount = 0;
                for (int i = 0; i < checkboxes.length; i++) {
                    if (checkboxes[i].isSelected()) {
                        newOrder.addProduct(products.get(i));
                        itemCount++;
                    }
                }

                if (itemCount == 0) {
                    showToast("No products selected — order cancelled.");
                    return;
                }

                planner.getBakery().addOrder(newOrder);
                customer.getOrderHistory().add(newOrder);
                
                navigateTo("Orders", activeNavButton);
                showToast("Order placed for " + customer.getName() + " ($" + String.format("%.2f", newOrder.getTotalPrice()) + ") ✓");
            } catch (Exception ex) {
                showToast("Failed to place order!");
                ex.printStackTrace();
            }
        });
        actions.add(placeOrderBtn);

        card.add(actions, BorderLayout.SOUTH);
        centerPanel.add(card);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(accent.brighter());
                } else {
                    g2.setColor(accent);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 36));
        return btn;
    }

    // ═══════════════════════════════════════
    //          CUSTOMERS VIEW
    // ═══════════════════════════════════════

    private JPanel buildCustomersView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_DARK);
        panel.add(buildPageHeader("Customers", "Your customer directory."), BorderLayout.NORTH);

        JPanel card = buildCard("All Customers");
        String[] cols = {"ID", "Name", "Phone", "Orders"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        java.util.List<Customer> customers = planner.getCustomerManager().getAllCustomers();
        for (Customer c : customers) {
            model.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), "Active"});
        }
        JScrollPane tableScroll = buildStyledTable(model);
        JTable table = (JTable) tableScroll.getViewport().getView();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 Search: "));
        JTextField searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            private void search() {
                String text = searchField.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);

        // ── Action Buttons ──
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_CARD);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton addBtn = createStyledButton("➕ Add Customer", ACCENT_GREEN);
        addBtn.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();

            Object[] fields = {
                "Customer Name:", nameField,
                "Phone Number:", phoneField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Add New Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                if (name.isEmpty()) { showToast("Name cannot be empty!"); return; }

                int newId = 1;
                for (Customer c : planner.getCustomerManager().getAllCustomers()) {
                    if (c.getId() >= newId) newId = c.getId() + 1;
                }
                planner.getCustomerManager().addCustomer(new Customer(newId, name, phone));
                planner.persistData();
                navigateTo("Customers", activeNavButton);
                showToast("Customer '" + name + "' added ✓");
            }
        });
        actions.add(addBtn);

        JButton removeBtn = createStyledButton("🗑️ Remove Customer", ACCENT_ROSE);
        removeBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a customer first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            int custId = (int) model.getValueAt(row, 0);
            String custName = (String) model.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Remove customer '" + custName + "'?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                planner.getCustomerManager().removeCustomer(custId);
                planner.persistData();
                navigateTo("Customers", activeNavButton);
                showToast("Customer '" + custName + "' removed ✓");
            }
        });
        actions.add(removeBtn);

        JButton viewOrdersBtn = createStyledButton("📋 View Orders", ACCENT_PURPLE);
        viewOrdersBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a customer first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            int custId = (int) model.getValueAt(row, 0);
            Customer c = planner.getCustomerManager().findCustomerById(custId);
            if (c == null) return;
            
            java.util.List<Order> history = c.getOrderHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, c.getName() + " has no order history.", "Order History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Order History for: ").append(c.getName()).append("\n\n");
            double totalSpent = 0;
            for (Order o : history) {
                sb.append("Order #").append(o.getId())
                  .append(" | ").append(o.getDate() != null ? o.getDate().toString() : "N/A")
                  .append(" | Status: ").append(o.getStatus())
                  .append("\n  - Items: ").append(o.getProducts().size())
                  .append(" | Total: $").append(String.format("%.2f", o.getTotalPrice())).append("\n\n");
                totalSpent += o.getTotalPrice();
            }
            sb.append("Total Lifetime Spend: $").append(String.format("%.2f", totalSpent));
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Order History", JOptionPane.PLAIN_MESSAGE);
        });
        actions.add(viewOrdersBtn);

        card.add(actions, BorderLayout.SOUTH);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════
    //            SALES VIEW
    // ═══════════════════════════════════════

    private JPanel buildSalesView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_DARK);
        panel.add(buildPageHeader("Sales Report", "Revenue breakdown and transaction history."), BorderLayout.NORTH);

        // Summary cards
        List<SalesRecord> records = planner.getSalesManager().getRecords();
        double totalRev = 0; int totalQty = 0;
        for (SalesRecord r : records) { totalRev += r.getRevenue(); totalQty += r.getQuantitySold(); }

        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setBackground(BG_DARK);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        stats.add(createStatCard("Total Revenue", "$" + String.format("%.2f", totalRev), "💰", ACCENT_GOLD));
        stats.add(createStatCard("Items Sold", String.valueOf(totalQty), "📦", ACCENT_BLUE));
        stats.add(createStatCard("Transactions", String.valueOf(records.size()), "🧾", ACCENT_PURPLE));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_DARK);
        center.add(stats);
        center.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel card = buildCard("All Transactions");
        String[] cols = {"#", "Product", "Qty", "Revenue", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (SalesRecord r : records) {
            model.addRow(new Object[]{r.getId(), r.getProduct().getName(), r.getQuantitySold(),
                "$" + String.format("%.2f", r.getRevenue()), r.getSaleDate()});
        }
        JScrollPane tableScroll = buildStyledTable(model);
        card.add(tableScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_CARD);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton recordSaleBtn = createStyledButton("💰 Record Sale", ACCENT_GREEN);
        recordSaleBtn.addActionListener(e -> {
            java.util.List<Product> products = planner.getBakery().getProducts();
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products available.", "No Products", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String[] productNames = new String[products.size()];
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                productNames[i] = p.getName() + " ($" + String.format("%.2f", p.getPrice()) + ", " + p.getQuantity() + " in stock)";
            }
            
            JComboBox<String> productBox = new JComboBox<>(productNames);
            JTextField qtyField = new JTextField();
            
            Object[] fields = {
                "Select Product:", productBox,
                "Quantity Sold:", qtyField
            };
            
            int result = JOptionPane.showConfirmDialog(this, fields, "Record New Sale", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int idx = productBox.getSelectedIndex();
                    if (idx == -1) return;
                    Product target = products.get(idx);
                    
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    if (qty <= 0) {
                        showToast("Quantity must be positive!");
                        return;
                    }
                    if (qty > target.getQuantity()) {
                        JOptionPane.showMessageDialog(this, "Not enough stock! Only " + target.getQuantity() + " available.", "Stock Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (target.isExpired()) {
                        JOptionPane.showMessageDialog(this, "Cannot sell expired product!", "Expiration Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    planner.getSalesManager().recordSale(target, qty);
                    planner.persistData();
                    navigateTo("Sales", activeNavButton);
                    showToast("Sale recorded: " + qty + "x " + target.getName() + " ✓");
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actions.add(recordSaleBtn);

        card.add(actions, BorderLayout.SOUTH);
        center.add(card);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════
    //            RECIPES VIEW
    // ═══════════════════════════════════════

    private JPanel buildRecipesView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_DARK);
        panel.add(buildPageHeader("Recipes & Production", "Manage product recipes and calculate production requirements."), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_DARK);

        // ── 1. Recipes Table ──
        JPanel recipesCard = buildCard("All Recipes");
        String[] cols = {"Recipe / Product", "Ingredients Count", "Max Producible Batches"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        java.util.List<Recipe> recipes = planner.getBakery().getRecipes();
        Map<String, Integer> plan = planner.getProductionOptimizer().planProduction(planner.getBakery());
        for (Recipe r : recipes) {
            int maxBatches = plan.getOrDefault(r.getProduct().getName(), 0);
            model.addRow(new Object[]{
                r.getProduct().getName(),
                r.getIngredientsNeeded().size(),
                maxBatches > 0 ? "🟢 " + maxBatches + " batches" : "🔴 Not enough stock"
            });
        }
        
        JScrollPane tableScroll = buildStyledTable(model);
        JTable table = (JTable) tableScroll.getViewport().getView();
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        recipesCard.add(tableScroll, BorderLayout.CENTER);

        // Action Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(BG_CARD);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton viewIngredientsBtn = createStyledButton("📋 View Ingredients", ACCENT_BLUE);
        viewIngredientsBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a recipe first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            Recipe target = recipes.get(row);
            StringBuilder details = new StringBuilder();
            details.append("Recipe for: ").append(target.getProduct().getName()).append("\n\n");
            details.append("Required Ingredients (per batch):\n");
            for (Map.Entry<Ingredient, Integer> entry : target.getIngredientsNeeded().entrySet()) {
                details.append("  • ").append(entry.getKey().getName())
                       .append(" : ").append(entry.getValue()).append(" units\n");
            }
            JOptionPane.showMessageDialog(this, details.toString(), "Recipe Details", JOptionPane.INFORMATION_MESSAGE);
        });
        actions.add(viewIngredientsBtn);

        JButton calcBatchesBtn = createStyledButton("🧮 Calculate & Produce", ACCENT_PURPLE);
        calcBatchesBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a recipe first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            Recipe target = recipes.get(row);
            String input = JOptionPane.showInputDialog(this, "How many batches of " + target.getProduct().getName() + " do you want to produce?", "Produce Batches", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int batches = Integer.parseInt(input.trim());
                    if (batches <= 0) return;
                    
                    Map<Ingredient, Integer> required = target.calculateRequiredIngredients(batches);
                    StringBuilder reqStr = new StringBuilder();
                    reqStr.append("To make ").append(batches).append(" batches of ").append(target.getProduct().getName()).append(":\n\n");
                    boolean possible = true;
                    for (Map.Entry<Ingredient, Integer> entry : required.entrySet()) {
                        Ingredient ing = entry.getKey();
                        int needed = entry.getValue();
                        int available = ing.getStock();
                        reqStr.append("  • ").append(ing.getName()).append(": ").append(needed).append(" needed (have ").append(available).append(")");
                        if (available < needed) {
                            reqStr.append(" ❌ NEED ").append(needed - available).append(" MORE");
                            possible = false;
                        } else {
                            reqStr.append(" ✅");
                        }
                        reqStr.append("\n");
                    }
                    
                    if (!possible) {
                        reqStr.append("\nProduction possible: NO (Missing ingredients)");
                        JOptionPane.showMessageDialog(this, reqStr.toString(), "Cannot Produce", JOptionPane.ERROR_MESSAGE);
                    } else {
                        reqStr.append("\nProduction possible: YES. Do you want to proceed?");
                        int confirm = JOptionPane.showConfirmDialog(this, reqStr.toString(), "Confirm Production", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (confirm == JOptionPane.YES_OPTION) {
                            // Deduct ingredients
                            for (Map.Entry<Ingredient, Integer> entry : required.entrySet()) {
                                Ingredient ing = entry.getKey();
                                ing.setStock(ing.getStock() - entry.getValue());
                            }
                            // Increase product
                            Product p = target.getProduct();
                            p.setQuantity(p.getQuantity() + batches);
                            // Reset expiration date to +7 days from now
                            p.setExpirationDate(LocalDate.now().plusDays(7));
                            
                            planner.persistData();
                            navigateTo("Recipes", activeNavButton);
                            showToast("Produced " + batches + " batches of " + p.getName() + " ✓");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actions.add(calcBatchesBtn);

        JButton addRecipeBtn = createStyledButton("➕ Add New Recipe", ACCENT_GREEN);
        addRecipeBtn.addActionListener(e -> {
            java.util.List<Product> products = planner.getBakery().getProducts();
            java.util.List<Product> noRecipeProducts = new java.util.ArrayList<>();
            for (Product p : products) {
                boolean hasRecipe = false;
                for (Recipe r : planner.getBakery().getRecipes()) {
                    if (r.getProduct().getId() == p.getId()) { hasRecipe = true; break; }
                }
                if (!hasRecipe) noRecipeProducts.add(p);
            }

            if (noRecipeProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All products already have recipes!", "No Products", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] names = new String[noRecipeProducts.size()];
            for (int i = 0; i < noRecipeProducts.size(); i++) names[i] = noRecipeProducts.get(i).getName();
            
            JComboBox<String> pBox = new JComboBox<>(names);
            int res = JOptionPane.showConfirmDialog(this, new Object[]{"Select Product:", pBox}, "Create Recipe - Step 1", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                Product target = noRecipeProducts.get(pBox.getSelectedIndex());
                Recipe newRecipe = new Recipe(target);
                
                // Add ingredients one by one
                while (true) {
                    java.util.List<Ingredient> ingredients = planner.getBakery().getIngredients();
                    String[] ingNames = new String[ingredients.size()];
                    for (int i = 0; i < ingredients.size(); i++) ingNames[i] = ingredients.get(i).getName();
                    
                    JComboBox<String> iBox = new JComboBox<>(ingNames);
                    JTextField qField = new JTextField();
                    Object[] msg = {"Select Ingredient:", iBox, "Quantity needed:", qField};
                    
                    int r = JOptionPane.showConfirmDialog(this, msg, "Add Ingredient to Recipe", JOptionPane.OK_CANCEL_OPTION);
                    if (r == JOptionPane.OK_OPTION) {
                        try {
                            Ingredient ing = ingredients.get(iBox.getSelectedIndex());
                            int qty = Integer.parseInt(qField.getText().trim());
                            newRecipe.addIngredient(ing, qty);
                            
                            int cont = JOptionPane.showConfirmDialog(this, "Ingredient added. Add another?", "Continue?", JOptionPane.YES_NO_OPTION);
                            if (cont == JOptionPane.NO_OPTION) break;
                        } catch (Exception ex) { showToast("Invalid input!"); }
                    } else break;
                }
                
                if (!newRecipe.getIngredientsNeeded().isEmpty()) {
                    planner.getBakery().addRecipe(newRecipe);
                    planner.persistData();
                    navigateTo("Recipes", activeNavButton);
                    showToast("Recipe for " + target.getName() + " created ✓");
                }
            }
        });
        actions.add(addRecipeBtn);

        recipesCard.add(actions, BorderLayout.SOUTH);
        centerPanel.add(recipesCard);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════
    //         REUSABLE COMPONENTS
    // ═══════════════════════════════════════

    private JPanel buildPageHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLbl.setForeground(TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLbl.setForeground(TEXT_SECONDARY);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(titleLbl);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subLbl);
        return header;
    }

    private JPanel buildCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lbl.setForeground(TEXT_PRIMARY);
            card.add(lbl, BorderLayout.NORTH);
        }
        return card;
    }

    private JPanel createStatCard(String title, String value, String icon, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card background
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // Border
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                // Accent top line
                g2.setColor(accent);
                g2.fillRoundRect(20, 0, 40, 3, 4, 4);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 16, 20));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLbl.setForeground(TEXT_PRIMARY);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(TEXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(titleLbl);
        return card;
    }

    private JScrollPane buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(BG_ACTIVE);
                    c.setForeground(ACCENT_GOLD);
                } else {
                    c.setBackground(row % 2 == 0 ? BG_CARD : TABLE_ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new EmptyBorder(0, 12, 0, 12));
                }
                return c;
            }
        };
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(BG_ACTIVE);
        table.setSelectionForeground(ACCENT_GOLD);
        table.setGridColor(BORDER_COLOR);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_SIDEBAR);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                lbl.setBackground(BG_SIDEBAR);
                lbl.setForeground(TEXT_SECONDARY);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);
        return scroll;
    }

    // ── Utility ──

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setForeground(BORDER_COLOR);
        return sep;
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel("  " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(4, 18, 8, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(240, 28));
        return lbl;
    }

    private void showToast(String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Success");
        dialog.setModal(false);
        dialog.setVisible(true);
        new Timer(2000, e -> dialog.dispose()).start();
    }
}
