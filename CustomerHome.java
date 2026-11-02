import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerHome extends JFrame {

    Connection con;
    JTable table;
    DefaultTableModel model;

    JTextField txtQty;
    JButton btnOrder, historyBtn, refreshBtn;

    String phone;

    public CustomerHome(String phone) {
        this.phone = phone;

        con = DBConnection.getConnection();

        setTitle("Customer Dashboard");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(30, 30, 30));
        add(panel);

        JLabel title = new JLabel("Book Store Dashboard");
        title.setBounds(300, 10, 300, 30);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.setColumnIdentifiers(new String[]{
            "Book ID", "Book Name", "Price"
        });

        table.setBackground(new Color(45, 45, 45));
        table.setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(0, 120, 215));

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(50, 60, 780, 260);
        panel.add(sp);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setBounds(250, 340, 100, 25);
        lblQty.setForeground(Color.WHITE);
        panel.add(lblQty);

        txtQty = new JTextField();
        txtQty.setBounds(330, 340, 120, 30);
        txtQty.setBackground(new Color(60, 60, 60));
        txtQty.setForeground(Color.WHITE);
        panel.add(txtQty);

        btnOrder = new JButton("Place Order");
        btnOrder.setBounds(250, 400, 150, 35);
        styleButton(btnOrder);
        panel.add(btnOrder);

        historyBtn = new JButton("My Orders");
        historyBtn.setBounds(420, 400, 150, 35);
        styleButton(historyBtn);
        panel.add(historyBtn);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(590, 400, 120, 35);
        styleButton(refreshBtn);
        panel.add(refreshBtn);

        btnOrder.addActionListener(e -> placeOrder());
        historyBtn.addActionListener(e -> new CustomerOrderHistory(phone).setVisible(true));
        refreshBtn.addActionListener(e -> loadBooks());

        loadBooks();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void loadBooks() {
        try {
            model.setRowCount(0); 

            PreparedStatement pst = con.prepareStatement("SELECT * FROM books");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getDouble("price")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void placeOrder() {
        try {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a book!");
                return;
            }

            int bookId = (int) model.getValueAt(row, 0);
            String bookName = (String) model.getValueAt(row, 1);
            double price = (double) model.getValueAt(row, 2);

            int qty = Integer.parseInt(txtQty.getText());
            double total = price * qty;

            CallableStatement cs = con.prepareCall("{call place_order_proc(?,?,?,?,?)}");

            cs.setString(1, phone);
            cs.setInt(2, bookId);
            cs.setString(3, bookName);
            cs.setInt(4, qty);
            cs.setDouble(5, total);

            cs.execute();

            JOptionPane.showMessageDialog(this, " Order Placed Successfully!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}