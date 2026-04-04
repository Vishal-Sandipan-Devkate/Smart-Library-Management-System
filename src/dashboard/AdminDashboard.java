package dashboard;
import panels.*;
import database.DatabaseConnection;
import auth.LoginScreen;
import javax.swing.*;
import java.awt.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class AdminDashboard extends JFrame {
    private String userId;
    private JPanel contentPanel;
    private JLabel statusLabel;
    private boolean isDarkMode = false;
    private Color darkBg   = new Color(33,33,33),   lightBg   = new Color(242,242,242);
    private Color darkMenu = new Color(30,30,30),    lightMenu = new Color(44,62,80);
    private JPanel menuPanel;
<<<<<<< HEAD:AdminDashboard.java
    private final Color BTN_BLUE   = new Color(52,152,219), BTN_BLUE_H  = new Color(41,128,185);
    private final Color BTN_RED    = new Color(231,76,60),  BTN_RED_H   = new Color(192,57,43);
    private final Color BTN_GREEN  = new Color(39,174,96),  BTN_GREEN_H = new Color(30,140,75);
    private final Color BTN_DARK   = new Color(70,70,70),   BTN_DARK_H  = new Color(100,100,100);
=======

    private final Color BTN_NORMAL = new Color(52, 152, 219);
    private final Color BTN_HOVER = new Color(41, 128, 185);
    private final Color BTN_LOGOUT = new Color(231, 76, 60);
    private final Color BTN_LOGOUT_HOVER = new Color(192, 57, 43);
    private final Color BTN_TOGGLE = new Color(39, 174, 96);
    private final Color BTN_APPROVE = new Color(142, 68, 173);       // Purple for approvals
    private final Color BTN_APPROVE_HOVER = new Color(125, 60, 152); // Purple hover

    // Badge label to show pending count
    private JLabel pendingBadge;
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java

    public AdminDashboard(String userId) {
        this.userId = userId;
        setTitle("Library Management System - Admin Dashboard");
        setSize(1200,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JSplitPane split = new JSplitPane();
        split.setDividerLocation(250);
        menuPanel = createMenuPanel();
        split.setLeftComponent(menuPanel);
        contentPanel = new JPanel(new BorderLayout());
        split.setRightComponent(contentPanel);

        statusLabel = new JLabel("Welcome, Admin!");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(statusLabel, BorderLayout.SOUTH);
        add(split);

        showWelcomeMessage();
        loadPendingCount();
        applyTheme();

        // Update pending badge count on load
        refreshPendingBadge();
    }

    private JPanel createMenuPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
        p.setBackground(lightMenu);

        JLabel lbl = new JLabel("Administrator");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0,15)));

<<<<<<< HEAD:AdminDashboard.java
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(100,120,140));
        sep.setMaximumSize(new Dimension(230,2));
        p.add(sep);
        p.add(Box.createRigidArea(new Dimension(0,10)));

        for (String item : new String[]{"Dashboard Home","📷 ISBN Scanner","Manage Librarians",
                "View Reports","Fine Management","User Approvals","System Settings","Toggle Theme","Logout"}) {
            p.add(menuBtn(item));
            p.add(Box.createRigidArea(new Dimension(0,8)));
=======
        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(adminLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
                "Dashboard Home",
                "Manage Librarians",
                "User Approvals",        // ← NEW
                "View Reports",
                "System Settings",
                "Toggle Theme",
                "Logout"
        };

        for (String item : menuItems) {
            if (item.equals("User Approvals")) {
                panel.add(createApprovalButton());
            } else {
                panel.add(createMenuButton(item));
            }
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java
        }
        return p;
    }

<<<<<<< HEAD:AdminDashboard.java
    private JButton menuBtn(String text) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(230,42)); b.setPreferredSize(new Dimension(230,42));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setForeground(Color.WHITE);

        Color nc = text.equals("Logout")       ? BTN_RED   :
                   text.equals("Toggle Theme") ? BTN_GREEN :
                   isDarkMode ? BTN_DARK : BTN_BLUE;
        Color hc = text.equals("Logout")       ? BTN_RED_H   :
                   text.equals("Toggle Theme") ? BTN_GREEN_H :
                   isDarkMode ? BTN_DARK_H : BTN_BLUE_H;
        b.setBackground(nc);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hc); }
            public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(nc); }
        });
        b.addActionListener(e -> {
            switch(text) {
                case "Dashboard Home":    showWelcomeMessage();      break;
                case "📷 ISBN Scanner":  showPanel(new ISBNScannerPanel()); updateStatus("ISBN Scanner"); break;
                case "Manage Librarians":showPanel(new LibrarianManagementPanel()); updateStatus("Manage Librarians"); break;
                case "View Reports":     showPanel(new ReportsPanel()); updateStatus("Reports"); break;
                case "Fine Management":  showPanel(new FineManagementPanel()); updateStatus("Fine Management"); break;
                case "User Approvals":   showPanel(new UserApprovalPanel()); updateStatus("User Approvals"); break;
                case "System Settings":  showPanel(new SettingsPanel(userId)); updateStatus("Settings"); break;
                case "Toggle Theme":     toggleTheme(); break;
                case "Logout":           logout(); break;
            }
        });
        return b;
=======
    // ── Special approval button with pending count badge ──────────
    private JPanel createApprovalButton() {
        JPanel wrapper = new JPanel(new BorderLayout(6, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(220, 40));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btn = new JButton("User Approvals");
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(BTN_APPROVE);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(BTN_APPROVE_HOVER); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(BTN_APPROVE); }
        });
        btn.addActionListener(e -> handleMenu("User Approvals"));

        // Red badge showing pending count
        pendingBadge = new JLabel("...");
        pendingBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pendingBadge.setForeground(Color.WHITE);
        pendingBadge.setBackground(new Color(231, 76, 60));
        pendingBadge.setOpaque(true);
        pendingBadge.setHorizontalAlignment(SwingConstants.CENTER);
        pendingBadge.setPreferredSize(new Dimension(28, 40));

        wrapper.add(btn,          BorderLayout.CENTER);
        wrapper.add(pendingBadge, BorderLayout.EAST);
        return wrapper;
    }

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normal = text.equals("Logout") ? BTN_LOGOUT
                     : text.equals("Toggle Theme") ? BTN_TOGGLE
                     : BTN_NORMAL;
        Color hover  = text.equals("Logout") ? BTN_LOGOUT_HOVER : BTN_HOVER;

        button.setBackground(normal);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hover); }
            public void mouseExited (java.awt.event.MouseEvent e) { button.setBackground(normal); }
        });
        button.addActionListener(e -> handleMenu(text));
        return button;
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java
    }

    private void showPanel(JPanel p) { contentPanel.removeAll(); contentPanel.add(p); contentPanel.revalidate(); contentPanel.repaint(); }

    private void toggleTheme() { isDarkMode = !isDarkMode; applyTheme(); }

<<<<<<< HEAD:AdminDashboard.java
    private void applyTheme() {
        menuPanel.setBackground(isDarkMode ? darkMenu : lightMenu);
        contentPanel.setBackground(isDarkMode ? darkBg : lightBg);
        for (Component c : menuPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton)c;
                b.setBackground(b.getText().equals("Logout") ? BTN_RED :
                    b.getText().equals("Toggle Theme") ? BTN_GREEN :
                    isDarkMode ? BTN_DARK : BTN_BLUE);
                b.setForeground(Color.WHITE);
            }
        }
=======
            case "Dashboard Home":
                showWelcomeMessage();
                return;

            case "Manage Librarians":
                contentPanel.removeAll();
                contentPanel.add(new LibrarianManagementPanel());
                statusLabel.setText("Managing librarians");
                break;

            case "User Approvals":                              // ← NEW
                contentPanel.removeAll();
                contentPanel.add(new UserApprovalPanel());
                statusLabel.setText("Approving user accounts");
                // Refresh badge after visiting approvals
                refreshPendingBadge();
                break;

            case "View Reports":
                contentPanel.removeAll();
                contentPanel.add(new ReportsPanel());
                statusLabel.setText("Viewing reports");
                break;

            case "System Settings":
                contentPanel.removeAll();
                contentPanel.add(new SettingsPanel(userId));
                statusLabel.setText("System settings");
                break;

            case "Toggle Theme":
                isDarkMode = !isDarkMode;
                applyTheme();
                return;

            case "Logout":
                logout();
                return;
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= BADGE =================
    private void refreshPendingBadge() {
        new Thread(() -> {
            int count = getPendingApprovals();
            SwingUtilities.invokeLater(() -> {
                if (pendingBadge != null) {
                    pendingBadge.setText(count > 0 ? String.valueOf(count) : "");
                    pendingBadge.setVisible(count > 0);
                }
            });
        }).start();
    }

    // ================= THEME =================
    private void applyTheme() {

        menuPanel.setBackground(isDarkMode ? darkMenuBackground : lightMenuBackground);
        contentPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java
        statusLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        SwingUtilities.updateComponentTreeUI(this);
    }

<<<<<<< HEAD:AdminDashboard.java
    private void showWelcomeMessage() {
        contentPanel.removeAll();
        JPanel wp = new JPanel(new GridBagLayout());
        wp.setBackground(isDarkMode ? darkBg : lightBg);
=======
    // ================= DASHBOARD HOME =================
    private void showWelcomeMessage() {
        contentPanel.removeAll();

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(isDarkMode ? darkBackground : lightBackground);
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

<<<<<<< HEAD:AdminDashboard.java
        JLabel wl = new JLabel("Welcome to Admin Dashboard");
        wl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        wl.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; wp.add(wl, gbc);

        gbc.gridy = 1; wp.add(createStatsPanel(), gbc);
        contentPanel.add(wp); contentPanel.revalidate(); contentPanel.repaint();
        updateStatus("Welcome to Dashboard");
    }

    private JPanel createStatsPanel() {
        JPanel p = new JPanel(new GridLayout(2,2,20,20));
        p.setBackground(isDarkMode ? darkBg : lightBg);
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.add(statCard("Total Users",       count("users", null)));
        p.add(statCard("Total Books",       count("books", null)));
        p.add(statCard("Active Loans",      count("book_borrowings", Filters.eq("status","BORROWED"))));
        p.add(statCard("Pending Approvals", count("users", Filters.eq("is_approved", false))));
        return p;
    }

    private long count(String col, org.bson.conversions.Bson filter) {
        try {
            var c = DatabaseConnection.getCollection(col);
            return filter == null ? c.countDocuments() : c.countDocuments(filter);
        } catch(Exception e) { return 0; }
    }

    private JPanel statCard(String title, long value) {
        JPanel card = new JPanel(new BorderLayout(5,5));
        card.setBackground(isDarkMode ? new Color(45,45,45) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isDarkMode ? new Color(60,60,60) : new Color(200,200,200)),
            BorderFactory.createEmptyBorder(15,15,15,15)));
        JLabel tl = new JLabel(title); tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        JLabel vl = new JLabel(String.valueOf(value)); vl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        vl.setForeground(new Color(52,152,219));
        card.add(tl, BorderLayout.NORTH); card.add(vl, BorderLayout.CENTER);
=======
        JLabel title = new JLabel("Welcome to Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        wrapper.add(title, gbc);

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(isDarkMode ? darkBackground : lightBackground);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel u = new JLabel("..."), b = new JLabel("..."),
               l = new JLabel("..."), p = new JLabel("...");

        grid.add(createCardWithLabel("Total Users",        u));
        grid.add(createCardWithLabel("Total Books",        b));
        grid.add(createCardWithLabel("Active Loans",       l));
        grid.add(createCardWithLabel("Pending Approvals",  p));

        gbc.gridy = 1;
        wrapper.add(grid, gbc);
        contentPanel.add(wrapper);
        contentPanel.revalidate();
        contentPanel.repaint();

        new Thread(() -> {
            int users   = getTotalUsers();
            int books   = getTotalBooks();
            int loans   = getActiveLoans();
            int pending = getPendingApprovals();
            SwingUtilities.invokeLater(() -> {
                u.setText(String.valueOf(users));
                b.setText(String.valueOf(books));
                l.setText(String.valueOf(loans));
                p.setText(String.valueOf(pending));
                // Also refresh the sidebar badge
                refreshPendingBadge();
            });
        }).start();
    }

    private JPanel createCardWithLabel(String title, JLabel numberLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(isDarkMode ? new Color(45, 45, 45) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);

        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        // Highlight pending approvals card in orange if > 0
        numberLabel.setForeground(title.equals("Pending Approvals")
                ? new Color(231, 76, 60)
                : new Color(52, 152, 219));

        card.add(titleLbl,    BorderLayout.NORTH);
        card.add(numberLabel, BorderLayout.CENTER);
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java
        return card;
    }

    private void loadPendingCount() {
        long c = count("users", Filters.eq("is_approved", false));
        if (c > 0) updateStatus("You have " + c + " pending user approvals");
    }

    private void logout() {
<<<<<<< HEAD:AdminDashboard.java
        int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { dispose(); new LoginScreen().setVisible(true); }
=======
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginScreen().setVisible(true);
        }
>>>>>>> vishal-work-branch:src/dashboard/AdminDashboard.java
    }

    private void updateStatus(String msg) { statusLabel.setText("Status: " + msg); }
}