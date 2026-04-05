import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class LoginScreen extends JFrame {

    // ── Color Palette ──────────────────────────────────────────────
    private static final Color PRIMARY        = new Color(13,  71, 161);  // Deep Academic Blue
    private static final Color PRIMARY_LIGHT  = new Color(21, 101, 192);  // Hover Blue
    private static final Color ACCENT         = new Color(255, 171,  0);  // Gold Accent
    private static final Color BG_DARK        = new Color(10,  25,  47);  // Deep Navy
    private static final Color BG_MID         = new Color(15,  40,  70);  // Navy Mid
    private static final Color CARD_BG        = new Color(255, 255, 255); // White Card
    private static final Color TEXT_DARK      = new Color(18,  30,  50);  // Near Black
    private static final Color TEXT_MUTED     = new Color(100, 116, 139); // Muted Gray
    private static final Color BORDER_COLOR   = new Color(213, 220, 230); // Light Border
    private static final Color FIELD_FOCUS    = new Color(13,  71, 161);  // Focus Ring
    private static final Color SUCCESS        = new Color(16, 185, 129);  // Green
    private static final Color SIGNUP_COLOR   = new Color(5,  150, 105);  // Signup btn

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
        setUndecorated(false);

        // Root panel — deep navy background
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background gradient
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), BG_MID);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                drawDecorations(g2);
            }
        };

        // ── Left panel (branding) ─────────────────────────────────
        JPanel left = buildLeftPanel();
        left.setPreferredSize(new Dimension(420, 640));
        left.setOpaque(false);

        // ── Right panel (form card) ───────────────────────────────
        JPanel right = buildRightPanel();
        right.setOpaque(false);

        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Decorative circles on background ─────────────────────────
    private void drawDecorations(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
        g2.setColor(Color.WHITE);
        g2.fillOval(-80, -80, 320, 320);
        g2.fillOval(50, 420, 200, 200);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
        g2.setColor(ACCENT);
        g2.fillOval(300, 500, 180, 180);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    // ── Left branding panel ───────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gold vertical accent bar
                g2.setColor(ACCENT);
                g2.fillRoundRect(36, 60, 4, 80, 4, 4);

                // Subtle separator line on right
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(getWidth() - 1, 40, getWidth() - 1, getHeight() - 40);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(6, 54, 6, 20);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Book icon (drawn)
        JLabel icon = new JLabel(buildBookIcon());
        g.insets = new Insets(0, 54, 18, 20);
        p.add(icon, g);

        // System name
        JLabel sys = new JLabel("<html><span style='font-size:22px;color:#FFAB00'><b>Smart Library</b></span></html>");
        sys.setFont(new Font("Georgia", Font.BOLD, 22));
        sys.setForeground(ACCENT);
        g.insets = new Insets(0, 54, 4, 20);
        p.add(sys, g);

        JLabel mgmt = new JLabel("Management System");
        mgmt.setFont(new Font("Georgia", Font.PLAIN, 16));
        mgmt.setForeground(new Color(200, 215, 235));
        g.insets = new Insets(0, 54, 28, 20);
        p.add(mgmt, g);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setBackground(new Color(255, 255, 255, 30));
        g.insets = new Insets(0, 54, 24, 20);
        p.add(sep, g);

        // Feature bullets
        String[] features = {
            "📚  Manage books & inventory",
            "👥  Multi-role access control",
            "📊  Reports & analytics",
            "🔔  Real-time notifications",
            "🔍  ISBN & QR scanning"
        };
        for (String f : features) {
            JLabel lbl = new JLabel(f);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(new Color(180, 200, 225));
            g.insets = new Insets(3, 54, 3, 20);
            p.add(lbl, g);
        }

        // Footer
        JLabel footer = new JLabel("© 2025 Smart Library System");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(100, 130, 165));
        g.insets = new Insets(40, 54, 0, 20);
        p.add(footer, g);

        return p;
    }

    // ── Right form panel ─────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        // White card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
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
        card.setPreferredSize(new Dimension(400, 520));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = GridBagConstraints.RELATIVE;
        gc.fill  = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets  = new Insets(6, 36, 6, 36);

        // Header
        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Georgia", Font.BOLD, 26));
        welcome.setForeground(TEXT_DARK);
        gc.insets = new Insets(36, 36, 2, 36);
        card.add(welcome, gc);

        JLabel sub = new JLabel("Sign in to your account to continue");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        gc.insets = new Insets(0, 36, 24, 36);
        card.add(sub, gc);

        // Gold top border accent
        JPanel accent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(ACCENT);
                g2.fillRoundRect(0, 0, 48, 3, 3, 3);
            }
        };
        accent.setOpaque(false);
        accent.setPreferredSize(new Dimension(48, 3));
        gc.insets = new Insets(0, 36, 20, 36);
        card.add(accent, gc);

        // Username field
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Username"), gc);
        usernameField = new JTextField();
        styleField(usernameField, "Enter your username");
        gc.insets = new Insets(0, 36, 14, 36);
        card.add(usernameField, gc);

        // Password field
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Password"), gc);
        passwordField = new JPasswordField();
        styleField(passwordField, "Enter your password");
        gc.insets = new Insets(0, 36, 14, 36);
        card.add(passwordField, gc);

        // Role selector
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Role"), gc);
        roleBox = new JComboBox<>(new String[]{"ADMIN", "LIBRARIAN", "STUDENT"});
        styleComboBox(roleBox);
        gc.insets = new Insets(0, 36, 20, 36);
        card.add(roleBox, gc);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(statusLabel, gc);

        // Login button
        loginBtn = buildButton("Sign In", PRIMARY, PRIMARY_LIGHT, Color.WHITE);
        loginBtn.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        gc.insets = new Insets(0, 36, 10, 36);
        card.add(loginBtn, gc);

        // Sign up button
        JButton signUpBtn = buildButton("Create New Account", SIGNUP_COLOR,
                                        new Color(4, 120, 87), Color.WHITE);
        signUpBtn.addActionListener(e -> { new SignUpScreen().setVisible(true); dispose(); });
        gc.insets = new Insets(0, 36, 36, 36);
        card.add(signUpBtn, gc);

        outer.add(card);
        return outer;
    }

    // ── Helpers ───────────────────────────────────────────────────
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
        f.setForeground(TEXT_DARK);
        f.setCaretColor(PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, BORDER_COLOR, 1.2f),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        // Placeholder simulation
        f.setForeground(TEXT_MUTED);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT_DARK);
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, FIELD_FOCUS, 2f),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
                f.setBackground(Color.WHITE);
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setForeground(TEXT_MUTED);
                    f.setText(placeholder);
                }
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, BORDER_COLOR, 1.2f),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
                f.setBackground(new Color(248, 250, 252));
            }
        });
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(new Color(248, 250, 252));
        cb.setForeground(TEXT_DARK);
        cb.setPreferredSize(new Dimension(0, 44));
        cb.setBorder(new RoundedBorder(8, BORDER_COLOR, 1.2f));
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
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
                g2.setColor(getModel().isPressed() ? hover.darker()
                          : getModel().isRollover() ? hover : bg);
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

    // ── Book SVG-style icon using Java2D ─────────────────────────
    private ImageIcon buildBookIcon() {
        int w = 56, h = 56;
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Book cover
        g2.setColor(ACCENT);
        g2.fillRoundRect(8, 6, 36, 44, 6, 6);
        // Spine
        g2.setColor(new Color(180, 120, 0));
        g2.fillRoundRect(8, 6, 8, 44, 4, 4);
        // Lines (pages)
        g2.setColor(new Color(255, 255, 255, 160));
        g2.setStroke(new BasicStroke(1.5f));
        for (int y = 16; y <= 38; y += 6) g2.drawLine(20, y, 38, y);
        // Bookmark ribbon
        g2.setColor(PRIMARY);
        int[] bx = {38, 44, 44}, by = {6, 6, 22};
        g2.fillPolygon(bx, by, 3);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ── Login logic ───────────────────────────────────────────────
    private void login() {
        String placeholder1 = "Enter your username";
        String placeholder2 = "Enter your password";

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role     = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || username.equals(placeholder1) ||
            password.isEmpty() || password.equals(placeholder2)) {
            statusLabel.setText("⚠  Please enter your username and password.");
            return;
        }

        loginBtn.setText("Signing in...");
        loginBtn.setEnabled(false);
        statusLabel.setText(" ");

        new Thread(() -> {
            try {
                MongoCollection<Document> users = DatabaseConnection.getCollection("users");
                if (users == null) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("⚠  Cannot connect to database.");
                        resetBtn();
                    });
                    return;
                }

                Document user = users.find(Filters.and(
                    Filters.eq("username", username),
                    Filters.eq("password", password),
                    Filters.eq("role",     role)
                )).first();

                SwingUtilities.invokeLater(() -> {
                    if (user == null) {
                        statusLabel.setText("⚠  Invalid username, password, or role.");
                        resetBtn();
                        return;
                    }
                    Boolean approved = user.getBoolean("is_approved");
                    if (approved == null || !approved) {
                        statusLabel.setForeground(new Color(217, 119, 6));
                        statusLabel.setText("⏳  Account pending admin approval.");
                        resetBtn();
                        return;
                    }

                    String userId = user.getObjectId("_id").toString();
                    dispose();
                    switch (role) {
                        case "ADMIN":     new AdminDashboard(userId).setVisible(true);     break;
                        case "LIBRARIAN": new LibrarianDashboard(userId).setVisible(true); break;
                        case "STUDENT":   new StudentDashboard(userId).setVisible(true);   break;
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("⚠  Database error: " + ex.getMessage());
                    resetBtn();
                });
            }
        }).start();
    }

    private void resetBtn() {
        loginBtn.setText("Sign In");
        loginBtn.setEnabled(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }

    // ── Custom rounded border ─────────────────────────────────────
    static class RoundedBorder implements Border {
        private final int   radius;
        private final Color color;
        private final float thickness;
        RoundedBorder(int r, Color c, float t) { radius = r; color = c; thickness = t; }
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