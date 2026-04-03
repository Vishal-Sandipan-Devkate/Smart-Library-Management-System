import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.types.ObjectId;

public class ReturnBooksPanel extends JPanel {
    private String userId;
    private boolean isDarkMode;
    private Color darkBackground  = new Color(33, 33, 33);
    private Color lightBackground = new Color(242, 242, 242);
    private DefaultTableModel model;

    // Constructor with isDarkMode
    public ReturnBooksPanel(String userId, boolean isDarkMode) {
        this.userId    = userId;
        this.isDarkMode = isDarkMode;
        init();
    }

    // Constructor without isDarkMode (for StudentDashboard)
    public ReturnBooksPanel(String userId) {
        this.userId    = userId;
        this.isDarkMode = false;
        init();
    }

    private void init() {
        setLayout(new BorderLayout(10, 10));
        setBackground(isDarkMode ? darkBackground : lightBackground);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Return Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Borrowing ID", "Book ID", "Title", "Borrow Date", "Due Date"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBorrowedBooks();

        JButton returnBtn = new JButton("Return Selected Book");
        returnBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        returnBtn.setBackground(new Color(70, 130, 180));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setFocusPainted(false);
        returnBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) returnBook(
                (String) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                model, row);
            else JOptionPane.showMessageDialog(this, "Please select a book to return");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(isDarkMode ? darkBackground : lightBackground);
        btnPanel.add(returnBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadBorrowedBooks() {
        model.setRowCount(0);
        try {
            MongoCollection<Document> borrowings = DatabaseConnection.getCollection("book_borrowings");
            for (Document b : borrowings.find(Filters.and(
                    Filters.eq("user_id", userId),
                    Filters.eq("status", "BORROWED")))) {
                model.addRow(new Object[]{
                    b.getObjectId("_id").toString(),
                    b.getString("book_id"),
                    b.getString("title"),
                    b.getDate("borrow_date"),
                    b.getDate("due_date")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading borrowed books: " + ex.getMessage());
        }
    }

    private void returnBook(String borrowingId, String bookId, DefaultTableModel model, int row) {
        try {
            MongoCollection<Document> borrowings = DatabaseConnection.getCollection("book_borrowings");
            MongoCollection<Document> books      = DatabaseConnection.getCollection("books");

            borrowings.updateOne(
                Filters.eq("_id", new ObjectId(borrowingId)),
                Updates.combine(
                    Updates.set("status",      "RETURNED"),
                    Updates.set("return_date", new Date())));

            try {
                books.updateOne(
                    Filters.eq("_id", new ObjectId(bookId)),
                    Updates.inc("available_quantity", 1));
            } catch (Exception ignored) {}

            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + ex.getMessage());
        }
    }
}