import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AvailableBooks extends JFrame {

    JTable table;
    DefaultTableModel model;
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    JButton btnRefresh, btnBack;
    JTextField txtSearch;

    Color primaryPurple = new Color(102, 51, 153); 
    Color bgWhite = Color.WHITE;

    public AvailableBooks() {
        setTitle("Available Books");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(primaryPurple);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryPurple);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel(" Available Books");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(bgWhite);
        header.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(primaryPurple);
        JLabel lblSearch = new JLabel("Search: ");
        lblSearch.setForeground(bgWhite);
        
        txtSearch = new JTextField(20);
        txtSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        header.add(searchPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "Book ID", "Book Name", "Price", "Available Qty"
        }, 0);

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(153, 102, 204)); 
        table.setSelectionForeground(bgWhite);

        JTableHeader headerColumns = table.getTableHeader();
        headerColumns.setBackground(bgWhite); 
        headerColumns.setForeground(primaryPurple); 
        headerColumns.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerColumns.setPreferredSize(new Dimension(100, 35));
        headerColumns.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, primaryPurple));

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(bgWhite); 
        sp.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(primaryPurple);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Dashboard"); // Renamed for clarity

        styleButton(btnRefresh);
        styleButton(btnBack);

        bottom.add(btnRefresh);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        con = DBConnection.getConnection();
        loadBooks();

        btnRefresh.addActionListener(e -> loadBooks());
        
     
        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true); 
            this.dispose();
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchBooks(txtSearch.getText());
            }
        });
    }

    private void styleButton(JButton btn) {
        btn.setBackground(bgWhite);
        btn.setForeground(primaryPurple);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryPurple, 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadBooks() {
        try {
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT * FROM books");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void searchBooks(String keyword) {
        try {
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT * FROM books WHERE book_name LIKE ?");
            pst.setString(1, "%" + keyword + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new AvailableBooks().setVisible(true));
    }
}
