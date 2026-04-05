package panels;
import database.DatabaseConnection;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class ReturnBooksPanel extends JPanel {
    private String  userId;
    private boolean isDarkMode;
    private Color darkBackground  = new Color(33, 33, 33);
    private Color lightBackground = new Color(242, 242, 242);

    private DefaultTableModel model;
    private JTable            table;

    public ReturnBooksPanel(String userId, boolean isDarkMode) {
        this.userId    = userId;
        this.isDarkMode = isDarkMode;

        setLayout(new BorderLayout(10, 10));
        setBackground(isDarkMode ? darkBackground : lightBackground);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Title ─────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("Return Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────
        String[] columns = {"Borrowing ID", "Book Title", "Author", "Borrow Date", "Due Date"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(isDarkMode ? new Color(45, 45, 45) : Color.WHITE);
        table.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);

        // Hide borrowing ID column (used internally)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────────
        JButton returnBtn  = makeBtn("Return Selected Book", new Color(70, 130, 180));
        JButton refreshBtn = makeBtn("Refresh",              new Color(39, 174, 96));

        returnBtn.addActionListener(e -> returnSelected());
        refreshBtn.addActionListener(e -> loadBorrowedBooks());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
        btnPanel.add(returnBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Load data
        loadBorrowedBooks();
    }

    // ── Load borrowed books for this user from MongoDB ────────────
    private void loadBorrowedBooks() {
        model.setRowCount(0);
        new Thread(() -> {
            try {
                MongoCollection<Document> borrowings =
                    DatabaseConnection.getCollection("book_borrowings");
                MongoCollection<Document> books =
                    DatabaseConnection.getCollection("books");

                for (Document b : borrowings.find(Filters.and(
                        Filters.eq("user_id", userId),
                        Filters.eq("status",  "BORROWED")))) {

                    String borrowingId = b.getObjectId("_id").toString();
                    String bookId      = b.getString("book_id");
                    Date   borrowDate  = b.getDate("borrow_date");
                    Date   dueDate     = b.getDate("due_date");

                    // Fetch book details
                    String title  = "Unknown";
                    String author = "Unknown";
                    if (bookId != null) {
                        Document book = null;
                        try {
                            book = books.find(
                                Filters.eq("_id", new ObjectId(bookId))).first();
                        } catch (Exception ignored) {
                            book = books.find(
                                Filters.eq("book_id", bookId)).first();
                        }
                        if (book != null) {
                            title  = book.getString("title")  != null ? book.getString("title")  : "Unknown";
                            author = book.getString("author") != null ? book.getString("author") : "Unknown";
                        }
                    }

                    final Object[] row = {
                        borrowingId,
                        title,
                        author,
                        borrowDate != null ? borrowDate.toString() : "N/A",
                        dueDate    != null ? dueDate.toString()    : "N/A"
                    };
                    SwingUtilities.invokeLater(() -> model.addRow(row));
                }

                if (model.getRowCount() == 0) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "You have no books currently borrowed.",
                            "No Borrowed Books",
                            JOptionPane.INFORMATION_MESSAGE));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                        "Error loading borrowed books: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // ── Return selected book ──────────────────────────────────────
    private void returnSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to return.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String borrowingId = (String) model.getValueAt(selectedRow, 0);
        String bookTitle   = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Return \"" + bookTitle + "\"?",
            "Confirm Return", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        final int rowToRemove = selectedRow;
        new Thread(() -> {
            try {
                MongoCollection<Document> borrowings =
                    DatabaseConnection.getCollection("book_borrowings");
                MongoCollection<Document> books =
                    DatabaseConnection.getCollection("books");

                // Find the borrowing record
                Document borrowing = borrowings.find(
                    Filters.eq("_id", new ObjectId(borrowingId))).first();
                if (borrowing == null) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "Borrowing record not found.", "Error",
                            JOptionPane.ERROR_MESSAGE));
                    return;
                }

                // Update borrowing status to RETURNED
                borrowings.updateOne(
                    Filters.eq("_id", new ObjectId(borrowingId)),
                    Updates.combine(
                        Updates.set("status",      "RETURNED"),
                        Updates.set("return_date", new Date())
                    ));

                // Increment available quantity on the book
                String bookId = borrowing.getString("book_id");
                if (bookId != null) {
                    try {
                        books.updateOne(
                            Filters.eq("_id", new ObjectId(bookId)),
                            Updates.inc("available_quantity", 1));
                    } catch (Exception ignored) {
                        books.updateOne(
                            Filters.eq("book_id", bookId),
                            Updates.inc("available_quantity", 1));
                    }
                }

                // Add notification
                DatabaseConnection.getCollection("notifications").insertOne(
                    new Document()
                        .append("user_id",    userId)
                        .append("message",    "You have successfully returned: " + bookTitle)
                        .append("is_read",    false)
                        .append("created_at", new Date()));

                SwingUtilities.invokeLater(() -> {
                    model.removeRow(rowToRemove);
                    JOptionPane.showMessageDialog(this,
                        "\"" + bookTitle + "\" returned successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                        "Error returning book: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
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
        btn.setPreferredSize(new Dimension(200, 38));
        return btn;
    }
}