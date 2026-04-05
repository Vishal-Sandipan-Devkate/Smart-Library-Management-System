package dashboard;
import panels.StudentRecordsPanel;
import panels.*;
import database.DatabaseConnection;
import auth.LoginScreen;
import javax.swing.*;
import java.awt.*;

public class LibrarianDashboard extends JFrame {
	private String userId;
	private JPanel contentPanel;
	private JPanel menuPanel;
	private JLabel statusLabel;
	private boolean isDarkMode = false;

	private final Color darkBackground = new Color(33, 33, 33);
	private final Color lightBackground = new Color(242, 242, 242);
	private final Color darkMenuBackground = new Color(30, 30, 30);
	private final Color lightMenuBackground = new Color(44, 62, 80);

	private final Color BTN_NORMAL = new Color(52, 152, 219);
	private final Color BTN_HOVER = new Color(41, 128, 185);
	private final Color BTN_LOGOUT = new Color(231, 76, 60);
	private final Color BTN_LOGOUT_HOVER = new Color(192, 57, 43);
	private final Color BTN_TOGGLE = new Color(39, 174, 96);

	public LibrarianDashboard(String userId) {
		this.userId = userId;

		setTitle("Library Management System - Librarian Dashboard");
		setSize(1200, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(260);

		menuPanel = createMenuPanel();
		splitPane.setLeftComponent(menuPanel);

		contentPanel = new JPanel(new BorderLayout());
		splitPane.setRightComponent(contentPanel);

		statusLabel = new JLabel("Welcome, Librarian!");
		statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		add(splitPane, BorderLayout.CENTER);
		add(statusLabel, BorderLayout.SOUTH);

		showWelcomeMessage();
		applyTheme();
	}

	private JPanel createMenuPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
		panel.setBackground(lightMenuBackground);

		JLabel label = new JLabel("Librarian");
		label.setFont(new Font("Segoe UI", Font.BOLD, 18));
		label.setForeground(Color.WHITE);
		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(0, 20)));

		String[] items = {
			"Dashboard Home",
			"Book Management",
			"Issue Books",
			"Return Books",
			"Issued Books",
			"Student Records",
			"Fine Management",
			"Reports",
			"Toggle Theme",
			"Logout"
		};

		for (String item : items) {
			panel.add(createMenuButton(item));
			panel.add(Box.createRigidArea(new Dimension(0, 10)));
		}
		return panel;
	}

	private JButton createMenuButton(String text) {
		JButton button = new JButton(text);
		button.setMaximumSize(new Dimension(230, 40));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setForeground(Color.WHITE);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		Color normal = text.equals("Logout") ? BTN_LOGOUT :
				text.equals("Toggle Theme") ? BTN_TOGGLE : BTN_NORMAL;
		Color hover = text.equals("Logout") ? BTN_LOGOUT_HOVER : BTN_HOVER;
		button.setBackground(normal);

		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hover); }
			public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(normal); }
		});

		button.addActionListener(e -> handleMenu(text));
		return button;
	}

	private void handleMenu(String item) {
		contentPanel.removeAll();

		switch (item) {
			case "Dashboard Home":
				showWelcomeMessage();
				return;
			case "Book Management":
				contentPanel.add(new BookManagementPanel());
				statusLabel.setText("Managing books");
				break;
			case "Issue Books":
				contentPanel.add(new IssueBooksPanel());
				statusLabel.setText("Issuing books");
				break;
			case "Return Books":
				contentPanel.add(new ReturnBooksPanel(userId, isDarkMode));
				statusLabel.setText("Returning books");
				break;
			case "Issued Books":
				contentPanel.add(new IssuedBooksPanel(userId, isDarkMode));
				statusLabel.setText("Viewing issued books");
				break;
			case "Student Records":
				contentPanel.add(new StudentRecordsPanel(userId, isDarkMode));
				statusLabel.setText("Viewing student records");
				break;
			case "Fine Management":
				contentPanel.add(new FineManagementPanel());
				statusLabel.setText("Managing fines");
				break;
			case "Reports":
				contentPanel.add(new ReportsPanel());
				statusLabel.setText("Viewing reports");
				break;
			case "Toggle Theme":
				isDarkMode = !isDarkMode;
				applyTheme();
				return;
			case "Logout":
				logout();
				return;
			default:
				showWelcomeMessage();
				return;
		}

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void showWelcomeMessage() {
		contentPanel.removeAll();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(isDarkMode ? darkBackground : lightBackground);

		JLabel title = new JLabel("Welcome to Librarian Dashboard");
		title.setFont(new Font("Segoe UI", Font.BOLD, 24));
		title.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
		panel.add(title);

		contentPanel.add(panel, BorderLayout.CENTER);
		statusLabel.setText("Welcome, Librarian!");
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void applyTheme() {
		menuPanel.setBackground(isDarkMode ? darkMenuBackground : lightMenuBackground);
		contentPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
		statusLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);

		for (Component c : menuPanel.getComponents()) {
			if (c instanceof JLabel) {
				((JLabel) c).setForeground(Color.WHITE);
			}
		}

		SwingUtilities.updateComponentTreeUI(this);
	}

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
