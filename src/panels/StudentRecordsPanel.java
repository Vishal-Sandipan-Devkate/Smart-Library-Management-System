package panels;

import database.DatabaseConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRecordsPanel extends JPanel {

    private final String  userId;
    private final boolean isDarkMode;

    private static final Color DARK_BG    = new Color(33,  33,  33);
    private static final Color LIGHT_BG   = new Color(242, 242, 242);
    private static final Color HEADER_CLR = new Color(44,  62,  80);
    private static final Color ACCENT     = new Color(52, 152, 219);
    private static final Color ROW_ODD    = new Color(245, 248, 252);
    private static final Color ROW_EVEN   = Color.WHITE;
    private static final Color DARK_ROW1  = new Color(45,  45,  45);
    private static final Color DARK_ROW2  = new Color(55,  55,  55);

    private JTable     table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JLabel     totalLabel;

    public StudentRecordsPanel(String userId, boolean isDarkMode) {
        this.userId     = userId;
        this.isDarkMode = isDarkMode;
        setLayout(new BorderLayout(0, 0));
        setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        loadStudents("");
    }

    // ── Top header with title + search ───────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(HEADER_CLR);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Student Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        searchField.setBackground(new Color(255, 255, 255, 30));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name or username...");

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(ACCENT);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setPreferredSize(new Dimension(90, 34));
        searchBtn.addActionListener(e -> loadStudents(searchField.getText().trim()));
        searchField.addActionListener(e -> loadStudents(searchField.getText().trim()));

        JButton refreshBtn = new JButton("↻ Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshBtn.setBackground(new Color(39, 174, 96));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudents("");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(searchField);
        btnPanel.add(searchBtn);
        btnPanel.add(refreshBtn);

        header.add(title,    BorderLayout.WEST);
        header.add(btnPanel, BorderLayout.EAST);
        return header;
    }

    // ── Table ─────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] columns = {
            "#", "Full Name", "Username", "Email",
            "Books Borrowed", "Active Loans", "Fines (₹)", "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(52, 152, 219, 60));
        table.setSelectionForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        table.setBackground(isDarkMode ? DARK_ROW1 : Color.WHITE);
        table.setForeground(isDarkMode ? Color.WHITE : new Color(30, 30, 30));

        // Column widths
        int[] widths = {40, 160, 130, 190, 110, 100, 90, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Header style
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(44, 62, 80));
        th.setForeground(Color.WHITE);
        th.setPreferredSize(new Dimension(0, 42));
        th.setReorderingAllowed(false);

        // Alternating row colors + centered status badge
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(col == 0 || col >= 4 ? CENTER : LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (!sel) {
                    setBackground(isDarkMode
                        ? (row % 2 == 0 ? DARK_ROW1 : DARK_ROW2)
                        : (row % 2 == 0 ? ROW_ODD   : ROW_EVEN));
                    setForeground(isDarkMode ? new Color(220, 220, 220) : new Color(30, 30, 30));
                }

                // Color the Status column
                if (col == 7 && val != null) {
                    String s = val.toString();
                    if (s.equals("Active")) {
                        setForeground(new Color(16, 185, 129));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(new Color(180, 180, 180));
                    }
                }

                // Color fines column if > 0
                if (col == 6 && val != null) {
                    try {
                        double fine = Double.parseDouble(val.toString().replace("₹", "").trim());
                        if (fine > 0) setForeground(new Color(231, 76, 60));
                    } catch (NumberFormatException ignored) {}
                }

                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        return scroll;
    }

    // ── Footer with count ─────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(isDarkMode ? new Color(40, 40, 40) : new Color(236, 240, 245));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        totalLabel = new JLabel("Loading...");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalLabel.setForeground(isDarkMode ? new Color(180, 180, 180) : new Color(100, 116, 139));

        JLabel hint = new JLabel("Click a row to view details");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(isDarkMode ? new Color(120, 120, 120) : new Color(150, 160, 175));

        footer.add(totalLabel, BorderLayout.WEST);
        footer.add(hint,       BorderLayout.EAST);
        return footer;
    }

    // ── Load data from MongoDB ────────────────────────────────────
    private void loadStudents(String keyword) {
        model.setRowCount(0);
        totalLabel.setText("Loading...");

        new Thread(() -> {
            try {
                MongoCollection<Document> users  = DatabaseConnection.getCollection("users");
                MongoCollection<Document> borrow = DatabaseConnection.getCollection("book_borrowings");
                MongoCollection<Document> fines  = DatabaseConnection.getCollection("fines");

                List<Object[]> rows = new ArrayList<>();

                // Fetch all students
                for (Document doc : users.find(Filters.eq("role", "STUDENT"))) {
                    String id       = doc.getObjectId("_id").toString();
                    String name     = doc.getString("full_name");
                    String username = doc.getString("username");
                    String email    = doc.getString("email");
                    boolean active  = Boolean.TRUE.equals(doc.getBoolean("is_approved"));

                    if (name     == null) name     = "";
                    if (username == null) username = "";
                    if (email    == null) email    = "";

                    // Filter by keyword
                    if (!keyword.isEmpty()) {
                        String kl = keyword.toLowerCase();
                        if (!name.toLowerCase().contains(kl) &&
                            !username.toLowerCase().contains(kl)) continue;
                    }

                    // Count total borrowings
                    long totalBorrowed = borrow.countDocuments(
                        Filters.eq("student_id", id));

                    // Count active loans
                    long activeLoans = borrow.countDocuments(Filters.and(
                        Filters.eq("student_id", id),
                        Filters.eq("status", "BORROWED")));

                    // Sum unpaid fines
                    double totalFine = 0;
                    for (Document f : fines.find(Filters.and(
                            Filters.eq("student_id", id),
                            Filters.eq("paid", false)))) {
                        Object amt = f.get("amount");
                        if (amt instanceof Number)
                            totalFine += ((Number) amt).doubleValue();
                    }

                    rows.add(new Object[]{
                        0,                          // row number — filled below
                        name,
                        username,
                        email,
                        totalBorrowed,
                        activeLoans,
                        String.format("%.0f", totalFine),
                        active ? "Active" : "Inactive"
                    });
                }

                // Fill row numbers
                for (int i = 0; i < rows.size(); i++) rows.get(i)[0] = i + 1;

                final List<Object[]> finalRows = rows;
                SwingUtilities.invokeLater(() -> {
                    for (Object[] r : finalRows) model.addRow(r);
                    totalLabel.setText("Total students: " + finalRows.size()
                        + (keyword.isEmpty() ? "" : "  (filtered)"));
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                    totalLabel.setText("Error loading records: " + ex.getMessage()));
            }
        }).start();
    }
}