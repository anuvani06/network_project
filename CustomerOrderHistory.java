import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class CustomerOrderHistory extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;

    Color primaryPurple = new Color(102, 51, 153);
    Color bgWhite = Color.WHITE;

    public CustomerOrderHistory(String phone) {
        con = DBConnection.getConnection();

        setTitle("My Orders");
        setSize(700, 450);
        setLocationRelativeTo(null);
        getContentPane().setBackground(primaryPurple);

        model = new DefaultTableModel(
                new String[]{"Customer", "Book", "Qty", "Delivery Date", "Status"}, 0
        );

        table = new JTable(model);
        styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(bgWhite);
        sp.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        add(sp, BorderLayout.CENTER);

        loadData(phone);
        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(153, 102, 204));
        table.setSelectionForeground(bgWhite);

        JTableHeader header = table.getTableHeader();
        header.setBackground(bgWhite);
        header.setForeground(primaryPurple);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, primaryPurple));
    }

    private void loadData(String phone) {
        try {
            model.setRowCount(0);
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM delivery WHERE phone=?"
            );
            pst.setString(1, phone.trim());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("customer_name"),
                        rs.getString("book_name"),
                        rs.getInt("quantity"),
                        rs.getDate("delivery_date"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
