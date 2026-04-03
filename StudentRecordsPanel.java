import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

public class StudentRecordsPanel extends JPanel {
    private String userId;
    private boolean isDarkMode;
    private Color darkBackground  = new Color(33, 33, 33);
    private Color lightBackground = new Color(242, 242, 242);
    private DefaultTableModel tableModel;
    private JTable studentsTable;
    private JTextField searchField;

    public StudentRecordsPanel(String userId, boolean isDarkMode) {
        this.userId    = userId;
        this.isDarkMode = isDarkMode;
        setLayout(new BorderLayout(10, 10));
        setBackground(isDarkMode ? darkBackground : lightBackground);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Student Records", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> searchStudents(searchField.getText().trim()));
        searchPanel.add(new JLabel("Search: ")); searchPanel.add(searchField); searchPanel.add(searchBtn);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titleLabel,  BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        String[] columns = {"Student ID", "Full Name", "Email", "Borrowed", "Returned", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentsTable.setRowHeight(25);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
        JButton viewDetailsBtn = new JButton("View Details");
        JButton viewHistoryBtn = new JButton("View History");
        JButton refreshBtn     = new JButton("Refresh");
        for (JButton b : new JButton[]{viewDetailsBtn, viewHistoryBtn, refreshBtn}) {
            b.setBackground(new Color(70, 130, 180)); b.setForeground(Color.WHITE);
            b.setFocusPainted(false); b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnPanel.add(b);
        }
        viewDetailsBtn.addActionListener(e -> viewStudentDetails());
        viewHistoryBtn.addActionListener(e -> viewStudentHistory());
        refreshBtn    .addActionListener(e -> loadStudentData());
        add(btnPanel, BorderLayout.SOUTH);

        loadStudentData();
    }

    private void loadStudentData() {
        tableModel.setRowCount(0);
        try {
            MongoCollection<Document> users      = DatabaseConnection.getCollection("users");
            MongoCollection<Document> borrowings = DatabaseConnection.getCollection("book_borrowings");
            for (Document u : users.find(Filters.eq("role", "STUDENT"))) {
                String uId = u.getObjectId("_id").toString();
                long borrowed = borrowings.countDocuments(Filters.eq("user_id", uId));
                long returned = borrowings.countDocuments(Filters.and(
                    Filters.eq("user_id", uId), Filters.eq("status", "RETURNED")));
                tableModel.addRow(new Object[]{
                    uId, u.getString("full_name"), u.getString("email"),
                    borrowed, returned,
                    u.getBoolean("is_active", false) ? "Active" : "Inactive"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
        }
    }

    private void searchStudents(String term) {
        tableModel.setRowCount(0);
        try {
            MongoCollection<Document> users      = DatabaseConnection.getCollection("users");
            MongoCollection<Document> borrowings = DatabaseConnection.getCollection("book_borrowings");
            Iterable<Document> results = term.isEmpty()
                ? users.find(Filters.eq("role", "STUDENT"))
                : users.find(Filters.and(Filters.eq("role", "STUDENT"),
                    Filters.or(
                        Filters.regex("full_name", term, "i"),
                        Filters.regex("email",     term, "i"),
                        Filters.regex("username",  term, "i"))));
            for (Document u : results) {
                String uId = u.getObjectId("_id").toString();
                long borrowed = borrowings.countDocuments(Filters.eq("user_id", uId));
                long returned = borrowings.countDocuments(Filters.and(
                    Filters.eq("user_id", uId), Filters.eq("status", "RETURNED")));
                tableModel.addRow(new Object[]{
                    uId, u.getString("full_name"), u.getString("email"),
                    borrowed, returned,
                    u.getBoolean("is_active", false) ? "Active" : "Inactive"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching: " + ex.getMessage());
        }
    }

    private void viewStudentDetails() {
        int row = studentsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a student"); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            Document u = DatabaseConnection.getCollection("users")
                .find(Filters.eq("_id", new ObjectId(id))).first();
            if (u == null) return;
            JDialog dlg = new JDialog(); dlg.setTitle("Student Details");
            dlg.setSize(400, 300); dlg.setLocationRelativeTo(this);
            JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            addField(p, "ID:",        id);
            addField(p, "Name:",      u.getString("full_name"));
            addField(p, "Username:",  u.getString("username"));
            addField(p, "Email:",     u.getString("email"));
            addField(p, "Status:",    u.getBoolean("is_active", false) ? "Active" : "Inactive");
            dlg.add(p); dlg.setVisible(true);
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void viewStudentHistory() {
        int row = studentsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a student"); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        JDialog dlg = new JDialog(); dlg.setTitle("Borrowing History");
        dlg.setSize(600, 400); dlg.setLocationRelativeTo(this);
        DefaultTableModel hModel = new DefaultTableModel(
            new String[]{"Book Title", "Borrow Date", "Return Date", "Status"}, 0);
        try {
            for (Document b : DatabaseConnection.getCollection("book_borrowings")
                    .find(Filters.eq("user_id", id))) {
                hModel.addRow(new Object[]{
                    b.getString("title"), b.getDate("borrow_date"),
                    b.getDate("return_date"), b.getString("status")
                });
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        dlg.add(new JScrollPane(new JTable(hModel))); dlg.setVisible(true);
    }

    private void addField(JPanel p, String label, String value) {
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel v = new JLabel(value != null ? value : ""); v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(l); p.add(v);
    }
}