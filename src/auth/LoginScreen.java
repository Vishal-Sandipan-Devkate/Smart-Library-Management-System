package auth;
import dashboard.*;
import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private RoundedPanel loginPanel;
    private GradientPanel mainPanel;

    public LoginScreen() {
        setTitle("Library Management System");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

<<<<<<< HEAD:LoginScreen.java
        mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        createLoginPanel();
        mainPanel.add(loginPanel);
=======
        // Main gradient panel
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(41, 128, 185),
                    0, getHeight(), new Color(109, 213, 250)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Login card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 230));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(10, 10, 10, 10);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Title
        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(titleLabel, gbc);

        // Username
        gbc.gridy = 1;
        JPanel usernamePanel = new JPanel(new BorderLayout(5, 0));
        usernamePanel.setOpaque(false);
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLbl.setForeground(new Color(44, 62, 80));
        usernameField = new JTextField(20);
        styleField(usernameField);
        usernamePanel.add(userLbl, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        card.add(usernamePanel, gbc);

        // Password
        gbc.gridy = 2;
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.setOpaque(false);
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLbl.setForeground(new Color(44, 62, 80));
        passwordField = new JPasswordField(20);
        styleField(passwordField);
        passwordPanel.add(passLbl, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        card.add(passwordPanel, gbc);

        // Role
        gbc.gridy = 3;
        JPanel rolePanel = new JPanel(new BorderLayout(5, 0));
        rolePanel.setOpaque(false);
        JLabel roleLbl = new JLabel("Role:");
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLbl.setForeground(new Color(44, 62, 80));
        roleBox = new JComboBox<>(new String[]{"ADMIN", "LIBRARIAN", "STUDENT"});
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleBox.setPreferredSize(new Dimension(250, 38));
        rolePanel.add(roleLbl, BorderLayout.NORTH);
        rolePanel.add(roleBox, BorderLayout.CENTER);
        card.add(rolePanel, gbc);

        // Login button
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 5, 10);
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setBackground(Color.orange);
        loginBtn.setForeground(Color.BLUE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(250, 42));
        loginBtn.addActionListener(e -> login());
        // Allow pressing Enter to login
        passwordField.addActionListener(e -> login());
        card.add(loginBtn, gbc);

        // Sign up button
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 10, 10);
        JButton signUpBtn = new JButton("Create New Account");
        signUpBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signUpBtn.setBackground(Color.red);
        signUpBtn.setForeground(Color.green);
        signUpBtn.setFocusPainted(false);
        signUpBtn.setBorderPainted(false);
        signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpBtn.setPreferredSize(new Dimension(250, 42));
        signUpBtn.addActionListener(e -> { new SignUpScreen().setVisible(true); dispose(); });
        card.add(signUpBtn, gbc);

        mainPanel.add(card);
>>>>>>> vishal-work-branch:src/auth/LoginScreen.java
        add(mainPanel);
    }

    private void createLoginPanel() {
        loginPanel = new RoundedPanel(20, new Color(255,255,255,220));
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44,62,80));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(titleLabel, gbc);

        // Username
        gbc.gridy++;
        JPanel usernamePanel = createInputPanel("Username:");
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        usernamePanel.add(usernameField);
        loginPanel.add(usernamePanel, gbc);

        // Password
        gbc.gridy++;
        JPanel passwordPanel = createInputPanel("Password:");
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        passwordPanel.add(passwordField);
        loginPanel.add(passwordPanel, gbc);

        // Role
        gbc.gridy++;
        JPanel rolePanel = createInputPanel("Role:");
        String[] roles = {"Admin","Librarian","Student"};
        roleComboBox = new JComboBox<>(roles);
        styleComboBox(roleComboBox);
        rolePanel.add(roleComboBox);
        loginPanel.add(rolePanel, gbc);

        // Login Button
        gbc.gridy++;
        gbc.insets = new Insets(20,0,10,0);
        CustomButton loginButton = new CustomButton("LOGIN", new Color(41,128,185), false);
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton, gbc);

        // Sign Up Button
        gbc.gridy++;
        gbc.insets = new Insets(0,0,10,0);
        CustomButton signUpButton = new CustomButton("Create New Account", new Color(46,204,113), false);
        signUpButton.addActionListener(e -> { new SignUpScreen().setVisible(true); dispose(); });
        loginPanel.add(signUpButton, gbc);
    }

    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(44,62,80));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label);
        return panel;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(250,35));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189,195,199),1,true),
            BorderFactory.createEmptyBorder(5,10,5,10)));
        textField.setBackground(Color.WHITE);
    }

    private void styleComboBox(JComboBox<String> c) {
        c.setPreferredSize(new Dimension(250,35));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setBorder(new LineBorder(new Color(189,195,199),1,true));
        c.setBackground(Color.WHITE);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role     = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password!"); return;
        }

        try {
            MongoCollection<Document> users = DatabaseConnection.getCollection("users");
            if (users == null) { showError("Cannot connect to MongoDB.\nMake sure MongoDB is running!"); return; }

            Document user = users.find(Filters.and(
                Filters.eq("username", username),
                Filters.eq("password", password),
                Filters.eq("role", role.toUpperCase())
            )).first();

            if (user == null) { showError("Invalid credentials!"); return; }

            Boolean approved = user.getBoolean("is_approved");
            if (approved == null || !approved) {
                showError("Your account is pending admin approval.\nPlease wait for approval."); return;
            }

            String userId = user.getObjectId("_id").toString();
            dispose();
            switch (role.toUpperCase()) {
                case "ADMIN":     new AdminDashboard(userId).setVisible(true);     break;
                case "LIBRARIAN": new LibrarianDashboard(userId).setVisible(true); break;
                case "STUDENT":   new StudentDashboard(userId).setVisible(true);   break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Database error: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}