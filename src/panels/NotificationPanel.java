package panels;
import database.DatabaseConnection;
import models.*;
import utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class NotificationPanel extends JPanel {
    private final String userId;
    private final boolean isDarkMode;
    private final Color darkBackground = new Color(33, 33, 33);
    private final Color lightBackground = new Color(242, 242, 242);
    private DefaultTableModel tableModel;

    public NotificationPanel(String userId, boolean isDarkMode) {
        this.userId = userId;
        this.isDarkMode = isDarkMode;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(isDarkMode ? darkBackground : lightBackground);

        JLabel title = new JLabel("Notifications", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Message", "Type", "Date"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(isDarkMode ? darkBackground : lightBackground);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(70, 130, 180));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadNotifications());
        actions.add(refreshBtn);
        add(actions, BorderLayout.SOUTH);

        loadNotifications();
    }

    private void loadNotifications() {
        tableModel.setRowCount(0);
        try {
            MongoCollection<Document> notifications = DatabaseConnection.getCollection("notifications");
            if (notifications == null) return;

            for (Document doc : notifications.find(Filters.eq("user_id", userId))) {
                tableModel.addRow(new Object[]{
                    doc.getString("message") != null ? doc.getString("message") : "",
                    doc.getString("type") != null ? doc.getString("type") : "INFO",
                    doc.getDate("created_at") != null ? doc.getDate("created_at") : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage());
        }
    }
}
