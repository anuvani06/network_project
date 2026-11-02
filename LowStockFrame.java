import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class LowStockFrame extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public LowStockFrame() {

        setTitle("Low Stock Monitor");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        con = DBConnection.getConnection();
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 43, 54));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel(" Low Stock Monitor");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        header.add(title, BorderLayout.WEST);
        JPanel topRight = new JPanel();
        topRight.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnBack = new JButton("Dashboard");

        styleButton(btnRefresh);
        styleButton(btnBack);

        topRight.add(btnRefresh);
        topRight.add(btnBack);

        header.add(topRight, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        model = new DefaultTableModel(new String[]{
                "Book ID", "Book Name", "Price", "Stock", "Status"
        }, 0);

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                int stock = Integer.parseInt(table.getValueAt(row, 3).toString());
                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 247, 250));
                } else {
                    c.setBackground(Color.WHITE);
                }

                c.setForeground(Color.BLACK);
                if (stock < 5) {
                    c.setBackground(new Color(255, 102, 102));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(sp, BorderLayout.CENTER);
        loadBooks();
        btnRefresh.addActionListener(e -> loadBooks());

        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });
    }
    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private void loadBooks() {
        try {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM books");
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {

                int stock = rs.getInt("quantity");
                String status = (stock < 5) ? "LOW STOCK" : "OK";

                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getDouble("price"),
                        stock,
                        status
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LowStockFrame().setVisible(true);
    }
}