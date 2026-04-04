package panels;

import database.DatabaseConnection;
import models.*;
import utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import com.mongodb.client.*;
import com.mongodb.client.model.*;

import org.bson.Document;
import org.bson.types.ObjectId;

public class UserApprovalPanel extends JPanel {
    private JTable pendingTable;
    private DefaultTableModel tableModel;

    public UserApprovalPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents();
        loadPendingApprovals();
    }

    private void initializeComponents() {
        String[] columns = { "ID", "Username", "Full Name", "Email", "Role" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        pendingTable = new JTable(tableModel);
        pendingTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pendingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        pendingTable.setRowHeight(25);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton approveBtn = createBtn("Approve");
        JButton rejectBtn = createBtn("Reject");
        JButton refreshBtn = createBtn("Refresh");
        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);

        approveBtn.addActionListener(e -> handleApproval(true));
        rejectBtn.addActionListener(e -> handleApproval(false));
        refreshBtn.addActionListener(e -> loadPendingApprovals());

        add(new JScrollPane(pendingTable), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JButton createBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(70, 130, 180));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        return b;
    }

    private void loadPendingApprovals() {
        tableModel.setRowCount(0);
        try {
            for (Document d : DatabaseConnection.getCollection("users")
                    .find(Filters.eq("is_approved", false))) {
                tableModel.addRow(new Object[] {
                        d.getObjectId("_id").toString(),
                        d.getString("username"),
                        d.getString("full_name"),
                        d.getString("email"),
                        d.getString("role")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading pending approvals: " + ex.getMessage());
        }
    }

    private void handleApproval(boolean approved) {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to " + (approved ? "approve" : "reject"));
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String role = (String) tableModel.getValueAt(row, 4); // ← get role

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + (approved ? "APPROVE" : "REJECT")
                        + " user: " + username + " (" + role + ")?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        new Thread(() -> { // ← run off EDT
            try {
                DatabaseConnection.getCollection("users").updateOne(
                        Filters.eq("_id", new ObjectId(id)),
                        Updates.combine(
                                Updates.set("is_approved", approved),
                                Updates.set("is_active", approved)));

                DatabaseConnection.getCollection("notifications").insertOne(
                        new Document()
                                .append("user_id", id)
                                .append("message", "Your account has been "
                                        + (approved ? "approved ✓" : "rejected ✗")
                                        + " by the administrator.")
                                .append("is_read", false)
                                .append("created_at", new Date()));

                SwingUtilities.invokeLater(() -> {
                    tableModel.removeRow(row);
                    JOptionPane.showMessageDialog(this,
                            "User " + username + " (" + role + ") has been "
                                    + (approved ? "approved ✓" : "rejected ✗") + " successfully!");
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()));
            }
        }).start();
    }
}