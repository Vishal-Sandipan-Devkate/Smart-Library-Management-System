package dashboard;
import panels.*;
import database.DatabaseConnection;
import auth.LoginScreen;
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
    private final Color BTN_APPROVE = new Color(142, 68, 173);
    private final Color BTN_APPROVE_HOVER = new Color(125, 60, 152);
    private JLabel pendingBadge;

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
        refreshPendingBadge();
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panel.setBackground(lightMenuBackground);
        JLabel adminLabel = new JLabel("Administrator");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(adminLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        String[] menuItems = {"Dashboard Home","Manage Librarians","User Approvals","View Reports","System Settings","Toggle Theme","Logout"};
        for (String item : menuItems) {
            panel.add(item.equals("User Approvals") ? createApprovalButton() : createMenuButton(item));
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        return panel;
    }

    private JPanel createApprovalButton() {
        JPanel wrapper = new JPanel(new BorderLayout(6, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(220, 40));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton btn = new JButton("User Approvals");
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(BTN_APPROVE);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(BTN_APPROVE_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(BTN_APPROVE); }
        });
        btn.addActionListener(e -> handleMenu("User Approvals"));
        pendingBadge = new JLabel("");
        pendingBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pendingBadge.setForeground(Color.WHITE);
        pendingBadge.setBackground(new Color(231, 76, 60));
        pendingBadge.setOpaque(true);
        pendingBadge.setHorizontalAlignment(SwingConstants.CENTER);
        pendingBadge.setPreferredSize(new Dimension(28, 40));
        pendingBadge.setVisible(false);
        wrapper.add(btn, BorderLayout.CENTER);
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
        Color normal = text.equals("Logout") ? BTN_LOGOUT : text.equals("Toggle Theme") ? BTN_TOGGLE : BTN_NORMAL;
        Color hover  = text.equals("Logout") ? BTN_LOGOUT_HOVER : BTN_HOVER;
        button.setBackground(normal);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { button.setBackground(normal); }
        });
        button.addActionListener(e -> handleMenu(text));
        return button;
    }

    private void handleMenu(String text) {
        switch (text) {
            case "Dashboard Home":
                showWelcomeMessage();
                return;
            case "Manage Librarians":
                contentPanel.removeAll();
                contentPanel.add(new LibrarianManagementPanel());
                statusLabel.setText("Managing librarians");
                break;
            case "User Approvals":
                contentPanel.removeAll();
                contentPanel.add(new UserApprovalPanel());
                statusLabel.setText("Approving user accounts");
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

    private void refreshPendingBadge() {
        new Thread(() -> {
            int count = getPendingApprovals();
            SwingUtilities.invokeLater(() -> {
                if (pendingBadge != null) {
                    pendingBadge.setText(String.valueOf(count));
                    pendingBadge.setVisible(count > 0);
                }
            });
        }).start();
    }

    private void applyTheme() {
        menuPanel.setBackground(isDarkMode ? darkMenuBackground : lightMenuBackground);
        contentPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
        statusLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void showWelcomeMessage() {
        contentPanel.removeAll();
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(isDarkMode ? darkBackground : lightBackground);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
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
        grid.add(createCardWithLabel("Total Users", u));
        grid.add(createCardWithLabel("Total Books", b));
        grid.add(createCardWithLabel("Active Loans", l));
        grid.add(createCardWithLabel("Pending Approvals", p));
        gbc.gridy = 1;
        wrapper.add(grid, gbc);
        contentPanel.add(wrapper);
        contentPanel.revalidate();
        contentPanel.repaint();
        new Thread(() -> {
            int users = getPendingApprovals() >= 0 ? getTotalUsers() : 0;
            int books = getTotalBooks();
            int loans = getActiveLoans();
            int pending = getPendingApprovals();
            SwingUtilities.invokeLater(() -> {
                u.setText(String.valueOf(users));
                b.setText(String.valueOf(books));
                l.setText(String.valueOf(loans));
                p.setText(String.valueOf(pending));
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
        numberLabel.setForeground(title.equals("Pending Approvals")
            ? new Color(231, 76, 60) : new Color(52, 152, 219));
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(numberLabel, BorderLayout.CENTER);
        return card;
    }

    private int getTotalUsers()       { try { return (int) DatabaseConnection.getCollection("users").countDocuments(Filters.eq("is_active", true)); } catch(Exception e) { return 0; } }
    private int getTotalBooks()       { try { return (int) DatabaseConnection.getCollection("books").countDocuments(); } catch(Exception e) { return 0; } }
    private int getActiveLoans()      { try { return (int) DatabaseConnection.getCollection("book_borrowings").countDocuments(Filters.eq("status", "BORROWED")); } catch(Exception e) { return 0; } }
    private int getPendingApprovals() { try { return (int) DatabaseConnection.getCollection("users").countDocuments(Filters.eq("is_approved", false)); } catch(Exception e) { return 0; } }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { dispose(); new LoginScreen().setVisible(true); }
    }
}
