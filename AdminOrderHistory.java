import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminOrderHistory extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;

    Color primaryPurple = new Color(102, 51, 153);
    Color bgWhite = Color.WHITE;

    public AdminOrderHistory() {
        con = DBConnection.getConnection();

        setTitle("Admin - Order History");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(primaryPurple);
        add(panel);

        JLabel title = new JLabel("All Orders");
        title.setBounds(30, 15, 200, 35);
        title.setForeground(bgWhite);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title);

        model = new DefaultTableModel(new String[]{
            "Order ID", "Phone", "Book", "Qty", "Total", "Date"
        }, 0);
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(153, 102, 204));
        table.setSelectionForeground(bgWhite);

        JTableHeader header = table.getTableHeader();
        header.setBackground(bgWhite);
        header.setForeground(primaryPurple);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, primaryPurple));

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 70, 820, 320);
        sp.getViewport().setBackground(bgWhite);
        sp.setBorder(BorderFactory.createEmptyBorder());
        panel.add(sp);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(290, 415, 140, 35);
        styleButton(refreshBtn);
        panel.add(refreshBtn);

        JButton dashboardBtn = new JButton("Dashboard");
        dashboardBtn.setBounds(450, 415, 140, 35);
        styleButton(dashboardBtn);
        panel.add(dashboardBtn);

      
        refreshBtn.addActionListener(e -> loadData());

        dashboardBtn.addActionListener(e -> {
            new Dashboard().setVisible(true);
            this.dispose();
        });

        loadData();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(bgWhite);
        btn.setForeground(primaryPurple);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(primaryPurple, 1));
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM orders ORDER BY order_id DESC");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getString("phone"),
                    rs.getString("book_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("total"),
                    rs.getTimestamp("order_date")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminOrderHistory().setVisible(true));
    }
}
