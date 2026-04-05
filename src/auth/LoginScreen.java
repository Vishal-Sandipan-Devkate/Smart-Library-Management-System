package auth;
import database.DatabaseConnection;
import dashboard.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class LoginScreen extends JFrame {
    private static final Color PRIMARY       = new Color(13, 71, 161);
    private static final Color PRIMARY_LIGHT = new Color(21, 101, 192);
    private static final Color ACCENT        = new Color(255, 171, 0);
    private static final Color BG_DARK       = new Color(10, 25, 47);
    private static final Color BG_MID        = new Color(15, 40, 70);
    private static final Color TEXT_DARK     = new Color(18, 30, 50);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_COLOR  = new Color(213, 220, 230);
    private static final Color FIELD_FOCUS   = new Color(13, 71, 161);
    private static final Color SUCCESS       = new Color(16, 185, 129);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton        loginBtn;
    private JLabel         statusLabel;

    public LoginScreen() {
        setTitle("Library Management System — Login");
        setSize(980, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), BG_MID));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.setColor(Color.WHITE);
                g2.fillOval(-80, -80, 320, 320);
                g2.fillOval(50, 420, 200, 200);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };
        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(420, 640));
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel sys = new JLabel("Smart Library");
        sys.setFont(new Font("Georgia", Font.BOLD, 24));
        sys.setForeground(ACCENT);
        g.insets = new Insets(0, 54, 4, 20);
        p.add(sys, g);

        JLabel mgmt = new JLabel("Management System");
        mgmt.setFont(new Font("Georgia", Font.PLAIN, 16));
        mgmt.setForeground(new Color(200, 215, 235));
        g.insets = new Insets(0, 54, 28, 20);
        p.add(mgmt, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,30));
        g.insets = new Insets(0, 54, 24, 20);
        p.add(sep, g);

        String[] features = {
            "Manage books & inventory",
            "Multi-role access control",
            "Reports & analytics",
            "Real-time notifications",
            "ISBN & QR scanning"
        };
        for (String f : features) {
            JLabel lbl = new JLabel("•  " + f);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(new Color(180, 200, 225));
            g.insets = new Insets(3, 54, 3, 20);
            p.add(lbl, g);
        }

        JLabel footer = new JLabel("© 2025 Smart Library System");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(100, 130, 165));
        g.insets = new Insets(40, 54, 0, 20);
        p.add(footer, g);
        return p;
    }

    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 236, 245));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(400, 480));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = GridBagConstraints.RELATIVE;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Georgia", Font.BOLD, 26));
        welcome.setForeground(TEXT_DARK);
        gc.insets = new Insets(36, 36, 2, 36);
        card.add(welcome, gc);

        JLabel sub = new JLabel("Sign in to your account to continue");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        gc.insets = new Insets(0, 36, 20, 36);
        card.add(sub, gc);

        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Username"), gc);
        usernameField = new JTextField();
        styleField(usernameField, "Enter your username");
        gc.insets = new Insets(0, 36, 14, 36);
        card.add(usernameField, gc);

        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Password"), gc);
        passwordField = new JPasswordField();
        styleField(passwordField, "");
        gc.insets = new Insets(0, 36, 14, 36);
        card.add(passwordField, gc);

        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Role"), gc);
        roleBox = new JComboBox<>(new String[]{"ADMIN", "LIBRARIAN", "STUDENT"});
        styleComboBox(roleBox);
        gc.insets = new Insets(0, 36, 16, 36);
        card.add(roleBox, gc);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(statusLabel, gc);

        loginBtn = buildButton("Sign In", PRIMARY, PRIMARY_LIGHT, Color.WHITE);
        loginBtn.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        gc.insets = new Insets(0, 36, 10, 36);
        card.add(loginBtn, gc);

        JButton signUpBtn = buildButton("Create New Account", new Color(5, 150, 105), new Color(4, 120, 87), Color.WHITE);
        signUpBtn.addActionListener(e -> { new SignUpScreen().setVisible(true); dispose(); });
        gc.insets = new Insets(0, 36, 36, 36);
        card.add(signUpBtn, gc);

        outer.add(card);
        return outer;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(55, 65, 81));
        return l;
    }

    private void styleField(JTextField f, String placeholder) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(0, 44));
        f.setBackground(new Color(248, 250, 252));
        f.setForeground(placeholder.isEmpty() ? TEXT_DARK : TEXT_MUTED);
        f.setCaretColor(PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, BORDER_COLOR, 1.2f),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        if (!placeholder.isEmpty()) {
            f.setText(placeholder);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT_DARK); }
                    f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(8, FIELD_FOCUS, 2f), BorderFactory.createEmptyBorder(8, 14, 8, 14)));
                    f.setBackground(Color.WHITE);
                }
                public void focusLost(FocusEvent e) {
                    if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(TEXT_MUTED); }
                    f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(8, BORDER_COLOR, 1.2f), BorderFactory.createEmptyBorder(8, 14, 8, 14)));
                    f.setBackground(new Color(248, 250, 252));
                }
            });
        }
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(new Color(248, 250, 252));
        cb.setForeground(TEXT_DARK);
        cb.setPreferredSize(new Dimension(0, 44));
        cb.setBorder(new RoundedBorder(8, BORDER_COLOR, 1.2f));
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                setBackground(isSelected ? PRIMARY : Color.WHITE);
                setForeground(isSelected ? Color.WHITE : TEXT_DARK);
                return this;
            }
        });
    }

    private JButton buildButton(String text, Color bg, Color hover, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? hover.darker() : getModel().isRollover() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(0, 46));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role     = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || username.equals("Enter your username") || password.isEmpty()) {
            statusLabel.setText("Please enter your username and password.");
            return;
        }

        loginBtn.setText("Signing in...");
        loginBtn.setEnabled(false);
        statusLabel.setText(" ");

        new Thread(() -> {
            try {
                MongoCollection<Document> users = DatabaseConnection.getCollection("users");
                if (users == null) {
                    SwingUtilities.invokeLater(() -> { statusLabel.setText("Cannot connect to database."); resetBtn(); });
                    return;
                }
                Document user = users.find(Filters.and(
                    Filters.eq("username", username),
                    Filters.eq("password", password),
                    Filters.eq("role",     role)
                )).first();

                SwingUtilities.invokeLater(() -> {
                    if (user == null) { statusLabel.setText("Invalid username, password, or role."); resetBtn(); return; }
                    Boolean approved = user.getBoolean("is_approved");
                    if (approved == null || !approved) { statusLabel.setText("Account pending admin approval."); resetBtn(); return; }
                    String uid = user.getObjectId("_id").toString();
                    dispose();
                    switch (role) {
                        case "ADMIN":     new AdminDashboard(uid).setVisible(true);     break;
                        case "LIBRARIAN": new LibrarianDashboard(uid).setVisible(true); break;
                        case "STUDENT":   new StudentDashboard(uid).setVisible(true);   break;
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> { statusLabel.setText("Error: " + ex.getMessage()); resetBtn(); });
            }
        }).start();
    }

    private void resetBtn() { loginBtn.setText("Sign In"); loginBtn.setEnabled(true); }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }

    public static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        private final float thickness;
        public RoundedBorder(int r, Color c, float t) { radius = r; color = c; thickness = t; }
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius*2, radius*2);
            g2.dispose();
        }
    }
}
