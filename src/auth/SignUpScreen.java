package auth;
import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.Date;

public class SignUpScreen extends JFrame {

    // ── Color Palette (matches LoginScreen) ───────────────────────
    private static final Color PRIMARY       = new Color(13,  71, 161);
    private static final Color PRIMARY_LIGHT = new Color(21, 101, 192);
    private static final Color ACCENT        = new Color(255, 171,   0);
    private static final Color BG_DARK       = new Color(10,  25,  47);
    private static final Color BG_MID        = new Color(15,  40,  70);
    private static final Color CARD_BG       = new Color(255, 255, 255);
    private static final Color TEXT_DARK     = new Color(18,  30,  50);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_COLOR  = new Color(213, 220, 230);
    private static final Color FIELD_FOCUS   = new Color(13,  71, 161);
    private static final Color SUCCESS       = new Color(5,  150, 105);
    private static final Color DANGER        = new Color(220,  53,  69);

    private JTextField     fullNameField;
    private JTextField     usernameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton        signUpBtn;
    private JLabel         statusLabel;

    // Placeholder texts
    private static final String PH_FULLNAME = "Enter your full name";
    private static final String PH_USERNAME = "Choose a username";
    private static final String PH_EMAIL    = "Enter your email address";

    public SignUpScreen() {
        setTitle("Library Management System — Sign Up");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Root panel
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), BG_MID));
                g2.fillRect(0, 0, getWidth(), getHeight());
                drawDecorations(g2);
            }
        };

        root.add(buildLeftPanel(),  BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private void drawDecorations(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
        g2.setColor(Color.WHITE);
        g2.fillOval(-60, -60, 280, 280);
        g2.fillOval(60, 500, 160, 160);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
        g2.setColor(ACCENT);
        g2.fillOval(320, 520, 150, 150);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    // ── Left branding panel ───────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillRoundRect(36, 60, 4, 80, 4, 4);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(getWidth()-1, 40, getWidth()-1, getHeight()-40);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(380, 680));
        p.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Book icon
        JLabel icon = new JLabel(buildBookIcon());
        g.insets = new Insets(0, 54, 16, 20);
        p.add(icon, g);

        // Title
        JLabel sys = new JLabel("Smart Library");
        sys.setFont(new Font("Georgia", Font.BOLD, 22));
        sys.setForeground(ACCENT);
        g.insets = new Insets(0, 54, 4, 20);
        p.add(sys, g);

        JLabel mgmt = new JLabel("Management System");
        mgmt.setFont(new Font("Georgia", Font.PLAIN, 15));
        mgmt.setForeground(new Color(200, 215, 235));
        g.insets = new Insets(0, 54, 24, 20);
        p.add(mgmt, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,25));
        g.insets = new Insets(0, 54, 20, 20);
        p.add(sep, g);

        // Steps for joining
        JLabel stepsTitle = new JLabel("How to get started:");
        stepsTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stepsTitle.setForeground(new Color(255, 200, 80));
        g.insets = new Insets(0, 54, 12, 20);
        p.add(stepsTitle, g);

        String[] steps = {
            "①  Fill in your details",
            "②  Choose your role",
            "③  Submit registration",
            "④  Wait for admin approval",
            "⑤  Login and get started!"
        };
        for (String s : steps) {
            JLabel lbl = new JLabel(s);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(new Color(180, 200, 225));
            g.insets = new Insets(3, 54, 3, 20);
            p.add(lbl, g);
        }

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255,255,255,25));
        g.insets = new Insets(20, 54, 16, 20);
        p.add(sep2, g);

        JLabel note = new JLabel("<html><i>Admin approval required<br>before first login.</i></html>");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        note.setForeground(new Color(120, 150, 185));
        g.insets = new Insets(0, 54, 0, 20);
        p.add(note, g);

        JLabel footer = new JLabel("© 2025 Smart Library System");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(80, 110, 145));
        g.insets = new Insets(30, 54, 0, 20);
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
                g2.setColor(new Color(225, 232, 242));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(440, 580));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = GridBagConstraints.RELATIVE;
        gc.fill  = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        // Header
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Georgia", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        gc.insets = new Insets(30, 36, 2, 36);
        card.add(title, gc);

        JLabel sub = new JLabel("Join the Smart Library community");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        gc.insets = new Insets(0, 36, 10, 36);
        card.add(sub, gc);

        // Gold accent bar
        JPanel accent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                ((Graphics2D)g).setColor(ACCENT);
                ((Graphics2D)g).fillRoundRect(0, 0, 48, 3, 3, 3);
            }
        };
        accent.setOpaque(false);
        accent.setPreferredSize(new Dimension(48, 3));
        gc.insets = new Insets(0, 36, 16, 36);
        card.add(accent, gc);

        // ── Two-column row: Full Name + Username ──────────────────
        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setOpaque(false);

        JPanel fnPanel = new JPanel(new BorderLayout(0, 4));
        fnPanel.setOpaque(false);
        fnPanel.add(fieldLabel("Full Name"), BorderLayout.NORTH);
        fullNameField = new JTextField();
        styleField(fullNameField, PH_FULLNAME);
        fnPanel.add(fullNameField, BorderLayout.CENTER);

        JPanel unPanel = new JPanel(new BorderLayout(0, 4));
        unPanel.setOpaque(false);
        unPanel.add(fieldLabel("Username"), BorderLayout.NORTH);
        usernameField = new JTextField();
        styleField(usernameField, PH_USERNAME);
        unPanel.add(usernameField, BorderLayout.CENTER);

        row1.add(fnPanel);
        row1.add(unPanel);
        gc.insets = new Insets(0, 36, 12, 36);
        card.add(row1, gc);

        // Email
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Email Address"), gc);
        emailField = new JTextField();
        styleField(emailField, PH_EMAIL);
        gc.insets = new Insets(0, 36, 12, 36);
        card.add(emailField, gc);

        // ── Two-column row: Password + Confirm Password ───────────
        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setOpaque(false);

        JPanel pwPanel = new JPanel(new BorderLayout(0, 4));
        pwPanel.setOpaque(false);
        pwPanel.add(fieldLabel("Password"), BorderLayout.NORTH);
        passwordField = new JPasswordField();
        styleField(passwordField, "");
        pwPanel.add(passwordField, BorderLayout.CENTER);

        JPanel cpPanel = new JPanel(new BorderLayout(0, 4));
        cpPanel.setOpaque(false);
        cpPanel.add(fieldLabel("Confirm Password"), BorderLayout.NORTH);
        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField, "");
        cpPanel.add(confirmPasswordField, BorderLayout.CENTER);

        row2.add(pwPanel);
        row2.add(cpPanel);
        gc.insets = new Insets(0, 36, 12, 36);
        card.add(row2, gc);

        // Role selector
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(fieldLabel("Register As"), gc);
        roleComboBox = new JComboBox<>(new String[]{"Student", "Librarian"});
        styleComboBox(roleComboBox);
        gc.insets = new Insets(0, 36, 14, 36);
        card.add(roleComboBox, gc);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gc.insets = new Insets(0, 36, 4, 36);
        card.add(statusLabel, gc);

        // Sign Up button
        signUpBtn = buildButton("Create Account", SUCCESS, new Color(4, 120, 87), Color.WHITE);
        signUpBtn.addActionListener(e -> handleSignUp());
        gc.insets = new Insets(0, 36, 10, 36);
        card.add(signUpBtn, gc);

        // Back to login button
        JButton backBtn = buildButton("Back to Login", PRIMARY, PRIMARY_LIGHT, Color.WHITE);
        backBtn.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });
        gc.insets = new Insets(0, 36, 30, 36);
        card.add(backBtn, gc);

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
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(0, 40));
        f.setBackground(new Color(248, 250, 252));
        f.setForeground(placeholder.isEmpty() ? TEXT_DARK : TEXT_MUTED);
        f.setCaretColor(PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LoginScreen.RoundedBorder(8, BORDER_COLOR, 1.2f),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        if (!placeholder.isEmpty()) {
            f.setText(placeholder);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (f.getText().equals(placeholder)) {
                        f.setText(""); f.setForeground(TEXT_DARK);
                    }
                    f.setBorder(BorderFactory.createCompoundBorder(
                        new LoginScreen.RoundedBorder(8, FIELD_FOCUS, 2f),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                    f.setBackground(Color.WHITE);
                }
                public void focusLost(FocusEvent e) {
                    if (f.getText().isEmpty()) {
                        f.setText(placeholder); f.setForeground(TEXT_MUTED);
                    }
                    f.setBorder(BorderFactory.createCompoundBorder(
                        new LoginScreen.RoundedBorder(8, BORDER_COLOR, 1.2f),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                    f.setBackground(new Color(248, 250, 252));
                }
            });
        } else {
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    f.setBorder(BorderFactory.createCompoundBorder(
                        new LoginScreen.RoundedBorder(8, FIELD_FOCUS, 2f),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                    f.setBackground(Color.WHITE);
                }
                public void focusLost(FocusEvent e) {
                    f.setBorder(BorderFactory.createCompoundBorder(
                        new LoginScreen.RoundedBorder(8, BORDER_COLOR, 1.2f),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                    f.setBackground(new Color(248, 250, 252));
                }
            });
        }
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(new Color(248, 250, 252));
        cb.setForeground(TEXT_DARK);
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setBorder(new LoginScreen.RoundedBorder(8, BORDER_COLOR, 1.2f));
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
        btn.setPreferredSize(new Dimension(0, 44));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private ImageIcon buildBookIcon() {
        int w = 52, h = 52;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ACCENT);
        g2.fillRoundRect(8, 6, 34, 42, 6, 6);
        g2.setColor(new Color(180, 120, 0));
        g2.fillRoundRect(8, 6, 8, 42, 4, 4);
        g2.setColor(new Color(255, 255, 255, 160));
        g2.setStroke(new BasicStroke(1.5f));
        for (int y = 15; y <= 36; y += 6) g2.drawLine(20, y, 36, y);
        g2.setColor(PRIMARY);
        g2.fillPolygon(new int[]{36,42,42}, new int[]{6,6,20}, 3);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ── Signup logic ──────────────────────────────────────────────
    private void handleSignUp() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm  = new String(confirmPasswordField.getPassword()).trim();
        String role     = (String) roleComboBox.getSelectedItem();

        // Strip placeholders
        if (fullName.equals(PH_FULLNAME)) fullName = "";
        if (username.equals(PH_USERNAME)) username = "";
        if (email.equals(PH_EMAIL))       email    = "";

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            setStatus("⚠  All fields are required.", DANGER); return;
        }
        if (!password.equals(confirm)) {
            setStatus("⚠  Passwords do not match.", DANGER); return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            setStatus("⚠  Enter a valid email address.", DANGER); return;
        }
        if (username.length() < 3) {
            setStatus("⚠  Username must be at least 3 characters.", DANGER); return;
        }
        if (password.length() < 6) {
            setStatus("⚠  Password must be at least 6 characters.", DANGER); return;
        }

        signUpBtn.setText("Creating account...");
        signUpBtn.setEnabled(false);
        setStatus(" ", DANGER);

        final String fn = fullName, un = username, em = email, pw = password, r = role;

        new Thread(() -> {
            try {
                MongoCollection<Document> users = DatabaseConnection.getCollection("users");
                if (users == null) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("⚠  Cannot connect to database.", DANGER);
                        resetBtn();
                    });
                    return;
                }
                if (users.find(Filters.eq("username", un)).first() != null) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("⚠  Username already exists.", DANGER);
                        resetBtn();
                    });
                    return;
                }
                if (users.find(Filters.eq("email", em)).first() != null) {
                    SwingUtilities.invokeLater(() -> {
                        setStatus("⚠  Email already registered.", DANGER);
                        resetBtn();
                    });
                    return;
                }

                users.insertOne(new Document()
                    .append("full_name",  fn)
                    .append("username",   un)
                    .append("password",   pw)
                    .append("role",       r.toUpperCase())
                    .append("type",       r.toUpperCase())
                    .append("email",      em)
                    .append("is_approved", false)
                    .append("is_active",   true)
                    .append("created_at",  new Date()));

                DatabaseConnection.getCollection("notifications").insertOne(new Document()
                    .append("message",    "New " + r + " registration: " + un)
                    .append("type",       "APPROVAL")
                    .append("is_read",    false)
                    .append("created_at", new Date()));

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "✓ Account created successfully!\n\n" +
                        "Please wait for admin approval before logging in.",
                        "Registration Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                    new LoginScreen().setVisible(true);
                    dispose();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    setStatus("⚠  Error: " + ex.getMessage(), DANGER);
                    resetBtn();
                });
            }
        }).start();
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void resetBtn() {
        signUpBtn.setText("Create Account");
        signUpBtn.setEnabled(true);
    }
}