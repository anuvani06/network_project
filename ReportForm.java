import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ReportForm extends JFrame {

    private Connection con;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cmbDate, cmbMonth;
    private JLabel lblTotal;

    Color primaryPurple = new Color(102, 51, 153);
    Color bgWhite = Color.WHITE;

    public ReportForm() {
        con = DBConnection.getConnection();

        setTitle("Report System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(primaryPurple);

        JLabel title = new JLabel("Sales Report Dashboard");
        title.setBounds(300, 10, 400, 35);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(bgWhite);
        add(title);

        JPanel panel = new JPanel();
        panel.setBounds(20, 60, 840, 70);
        panel.setBackground(bgWhite);
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(bgWhite));
        add(panel);

        JLabel lblDate = new JLabel("Select Date:");
        lblDate.setBounds(20, 20, 100, 25);
        lblDate.setForeground(primaryPurple);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblDate);

        cmbDate = new JComboBox<>();
        cmbDate.setBounds(120, 20, 150, 25);
        panel.add(cmbDate);

        JLabel lblMonth = new JLabel("Select Month:");
        lblMonth.setBounds(320, 20, 100, 25);
        lblMonth.setForeground(primaryPurple);
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblMonth);

        cmbMonth = new JComboBox<>(new String[]{
            "Select Month","1","2","3","4","5","6",
            "7","8","9","10","11","12"
        });
        cmbMonth.setBounds(420, 20, 120, 25);
        panel.add(cmbMonth);

        model = new DefaultTableModel(new String[]{
            "Bill No", "Customer", "Phone", "Book", "Qty", "Total", "Date"
        }, 0);
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(153, 102, 204));
        table.setSelectionForeground(bgWhite);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(bgWhite);
        header.setForeground(primaryPurple);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, primaryPurple));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 150, 840, 280);
        scroll.getViewport().setBackground(bgWhite);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll);

        lblTotal = new JLabel("Total Sales: ₹ 0");
        lblTotal.setBounds(20, 450, 300, 30);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(bgWhite);
        add(lblTotal);

        JButton btnBack = new JButton("Dashboard");
        btnBack.setBounds(720, 450, 140, 35); // Adjusted width
        btnBack.setBackground(bgWhite);
        btnBack.setForeground(primaryPurple);
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnBack);

        loadDates();

        cmbDate.addActionListener(e -> {
            if (cmbDate.getSelectedItem() != null && !cmbDate.getSelectedItem().equals("Select Date"))
                loadDayReport(cmbDate.getSelectedItem().toString());
        });

        cmbMonth.addActionListener(e -> {
            if (!cmbMonth.getSelectedItem().toString().equals("Select Month"))
                loadMonthReport(Integer.parseInt(cmbMonth.getSelectedItem().toString()));
        });

       
        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true); 
            this.dispose();
        });
    }

    private void loadDates() {
        try {
            cmbDate.removeAllItems();
            cmbDate.addItem("Select Date");
            PreparedStatement pst = con.prepareStatement("SELECT DISTINCT DATE(date_time) AS d FROM bill ORDER BY d DESC");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cmbDate.addItem(rs.getDate("d").toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dates");
        }
    }

    private void loadDayReport(String date) {
        try {
            model.setRowCount(0);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM bill WHERE DATE(date_time)=?");
            pst.setString(1, date);
            ResultSet rs = pst.executeQuery();
            double total = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("bill_no"), rs.getString("customer_name"),
                    rs.getString("phone"), rs.getString("book_name"),
                    rs.getInt("quantity"), rs.getDouble("total"),
                    rs.getTimestamp("date_time")
                });
                total += rs.getDouble("total");
            }
            lblTotal.setText("Total Sales: ₹ " + total);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMonthReport(int month) {
        try {
            model.setRowCount(0);
            PreparedStatement pst = con.prepareStatement("SELECT * FROM bill WHERE MONTH(date_time)=?");
            pst.setInt(1, month);
            ResultSet rs = pst.executeQuery();
            double total = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("bill_no"), rs.getString("customer_name"),
                    rs.getString("phone"), rs.getString("book_name"),
                    rs.getInt("quantity"), rs.getDouble("total"),
                    rs.getTimestamp("date_time")
                });
                total += rs.getDouble("total");
            }
            lblTotal.setText("Total Sales: ₹ " + total);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportForm().setVisible(true));
    }
}
