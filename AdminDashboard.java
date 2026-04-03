import javax.swing.*;
import java.awt.*;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class AdminDashboard extends JFrame {

    private String userId;
    private JPanel contentPanel;
    private JLabel statusLabel;
    private boolean isDarkMode = false;

    private Color darkBackground = new Color(33, 33, 33);
    private Color lightBackground = new Color(242, 242, 242);
    private Color darkMenuBackground = new Color(30, 30, 30);
    private Color lightMenuBackground = new Color(44, 62, 80);

    private JPanel menuPanel;

    private final Color BTN_NORMAL = new Color(52, 152, 219);
    private final Color BTN_HOVER = new Color(41, 128, 185);
    private final Color BTN_LOGOUT = new Color(231, 76, 60);
    private final Color BTN_LOGOUT_HOVER = new Color(192, 57, 43);
    private final Color BTN_TOGGLE = new Color(39, 174, 96);

    public AdminDashboard(String userId) {

        this.userId = userId;

        setTitle("Library Management System - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(250);

        menuPanel = createMenuPanel();
        splitPane.setLeftComponent(menuPanel);

        contentPanel = new JPanel(new BorderLayout());
        splitPane.setRightComponent(contentPanel);

        statusLabel = new JLabel("Welcome, Admin!");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);

        add(splitPane);

        showWelcomeMessage();
        applyTheme();
    }

    // ================= MENU =================
    private JPanel createMenuPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panel.setBackground(lightMenuBackground);

        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        adminLabel.setForeground(Color.WHITE);

        panel.add(adminLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
                "Dashboard Home",
                "Manage Librarians",
                "View Reports",
                "System Settings",
                "Toggle Theme",
                "Logout"
        };

        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(220, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normal = text.equals("Logout") ? BTN_LOGOUT :
                       text.equals("Toggle Theme") ? BTN_TOGGLE : BTN_NORMAL;

        Color hover = text.equals("Logout") ? BTN_LOGOUT_HOVER :
                      BTN_HOVER;

        button.setBackground(normal);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normal);
            }
        });

        button.addActionListener(e -> handleMenu(text));

        return button;
    }

    private void handleMenu(String text) {

        switch (text) {

            case "Dashboard Home":
                showWelcomeMessage();
                break;

            case "Manage Librarians":
                contentPanel.removeAll();
                contentPanel.add(new LibrarianManagementPanel());
                break;

            case "View Reports":
                contentPanel.removeAll();
                contentPanel.add(new ReportsPanel());
                break;

            case "System Settings":
                contentPanel.removeAll();
                contentPanel.add(new SettingsPanel(userId));
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

    // ================= THEME =================
    private void applyTheme() {

        menuPanel.setBackground(isDarkMode ? darkMenuBackground : lightMenuBackground);
        contentPanel.setBackground(isDarkMode ? darkBackground : lightBackground);

        statusLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);

        SwingUtilities.updateComponentTreeUI(this);
    }

    // ================= DASHBOARD UI =================
    private void showWelcomeMessage() {

        contentPanel.removeAll();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(isDarkMode ? darkBackground : lightBackground);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Welcome to Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(title, gbc);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cardsPanel.add(createCard("Total Users", getTotalUsers()));
        cardsPanel.add(createCard("Total Books", getTotalBooks()));
        cardsPanel.add(createCard("Active Loans", getActiveLoans()));
        cardsPanel.add(createCard("Pending Approvals", getPendingApprovals()));

        gbc.gridy = 1;
        mainPanel.add(cardsPanel, gbc);

        contentPanel.add(mainPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCard(String title, int value) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(isDarkMode ? new Color(45, 45, 45) : Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(new Color(52, 152, 219));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ================= DATABASE =================
    private int getTotalUsers() {
        return (int) DatabaseConnection.getCollection("users")
                .countDocuments(Filters.eq("is_active", true));
    }

    private int getTotalBooks() {
        return (int) DatabaseConnection.getCollection("books").countDocuments();
    }

    private int getActiveLoans() {
        return (int) DatabaseConnection.getCollection("book_borrowings")
                .countDocuments(Filters.eq("status", "BORROWED"));
    }

    private int getPendingApprovals() {
        return (int) DatabaseConnection.getCollection("users")
                .countDocuments(Filters.eq("is_approved", false));
    }

    // ================= LOGOUT =================
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginScreen().setVisible(true);
        }
    }
}