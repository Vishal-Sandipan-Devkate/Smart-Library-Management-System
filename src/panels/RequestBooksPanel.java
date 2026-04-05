package panels;
import database.DatabaseConnection;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class RequestBooksPanel extends JPanel {
    private String userId;
    private DefaultTableModel model;
    private JTextField searchField;

    public RequestBooksPanel(String userId) {
        this.userId = userId;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(242, 242, 242));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("Request Books", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        add(title, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(242, 242, 242));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 34));
        JButton searchBtn = makeBtn("Search", new Color(52, 152, 219));
        JButton refreshBtn = makeBtn("Show All", new Color(39, 174, 96));
        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadBooks(""); });
        searchField.addActionListener(e -> loadBooks(searchField.getText().trim()));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        // Table
        String[] columns = {"Book ID", "Title", "Author", "ISBN", "Available"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide Book ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setBackground(new Color(242, 242, 242));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Request button
        JButton requestBtn = makeBtn("Request Selected Book", new Color(142, 68, 173));
        requestBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to request.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bookId    = (String) model.getValueAt(row, 0);
            String bookTitle = (String) model.getValueAt(row, 1);
            requestBook(bookId, bookTitle);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(242, 242, 242));
        btnPanel.add(requestBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadBooks("");
    }

    private void loadBooks(String keyword) {
        model.setRowCount(0);
        new Thread(() -> {
            try {
                MongoCollection<Document> books = DatabaseConnection.getCollection("books");
                for (Document d : books.find()) {
                    String title  = d.getString("title")  != null ? d.getString("title")  : "";
                    String author = d.getString("author") != null ? d.getString("author") : "";
                    if (!keyword.isEmpty()) {
                        String kl = keyword.toLowerCase();
                        if (!title.toLowerCase().contains(kl) && !author.toLowerCase().contains(kl)) continue;
                    }
                    int available = d.getInteger("available_quantity", 0);
                    Object[] row = {
                        d.getObjectId("_id").toString(),
                        title,
                        author,
                        d.getString("isbn") != null ? d.getString("isbn") : "",
                        available > 0 ? "Yes (" + available + ")" : "Not Available"
                    };
                    SwingUtilities.invokeLater(() -> model.addRow(row));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void requestBook(String bookId, String bookTitle) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Request \"" + bookTitle + "\"?",
            "Confirm Request", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try {
                // Check if already requested
                Document existing = DatabaseConnection.getCollection("book_requests")
                    .find(Filters.and(
                        Filters.eq("user_id",  userId),
                        Filters.eq("book_id",  bookId),
                        Filters.eq("status",   "PENDING")
                    )).first();

                if (existing != null) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "You already have a pending request for this book.",
                            "Already Requested", JOptionPane.WARNING_MESSAGE));
                    return;
                }

                DatabaseConnection.getCollection("book_requests").insertOne(new Document()
                    .append("user_id",    userId)
                    .append("book_id",    bookId)
                    .append("book_title", bookTitle)
                    .append("status",     "PENDING")
                    .append("created_at", new Date()));

                DatabaseConnection.getCollection("notifications").insertOne(new Document()
                    .append("user_id",    userId)
                    .append("message",    "Your request for \"" + bookTitle + "\" has been submitted.")
                    .append("is_read",    false)
                    .append("created_at", new Date()));

                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                        "Request for \"" + bookTitle + "\" submitted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE));

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
