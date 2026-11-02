
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class CustomerModule extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    JTextField txtName, txtPhone, txtAddress;
    JTable table;
    DefaultTableModel model;
    JButton btnAdd, btnUpdate, btnDelete, btnClear, btnBack;

    Color primaryPurple = new Color(102, 51, 153);
    Color bgWhite = Color.WHITE;

    public CustomerModule() {
        con = DBConnection.getConnection();

        setTitle("Customer Management System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        getContentPane().setBackground(primaryPurple);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryPurple);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Customer Management");
        title.setForeground(bgWhite);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        btnBack = new JButton("Dashboard");
        styleButton(btnBack);
        header.add(btnBack, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(primaryPurple);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBackground(bgWhite);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryPurple), "Customer Details", 0, 0, 
            new Font("Segoe UI", Font.BOLD, 14), primaryPurple));

        txtName = createField();
        txtPhone = createField();
        txtAddress = createField();

        addField(formPanel, "Customer Name:", txtName);
        addField(formPanel, "Phone Number:", txtPhone);
        addField(formPanel, "Address:", txtAddress);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(primaryPurple);

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        styleButton(btnAdd);
        styleButton(btnUpdate);
        styleButton(btnDelete);
        styleButton(btnClear);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        model = new DefaultTableModel(new String[]{"Name", "Phone", "Address"}, 0);
        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(bgWhite);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBackground(primaryPurple);
        leftContainer.add(formPanel, BorderLayout.NORTH);
        leftContainer.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(leftContainer, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearFields());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(model.getValueAt(row, 0).toString());
                txtPhone.setText(model.getValueAt(row, 1).toString());
                txtAddress.setText(model.getValueAt(row, 2).toString());
            }
        });

        loadCustomers();
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, primaryPurple));
        return field;
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(primaryPurple);
        panel.add(l);
        panel.add(field);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(bgWhite);
        btn.setForeground(primaryPurple);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));
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
    }

    private void loadCustomers() {
        try {
            model.setRowCount(0);
            pst = con.prepareStatement("SELECT * FROM customer");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("name"), rs.getString("phone"), rs.getString("address")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addCustomer() {
        try {
            pst = con.prepareStatement("INSERT INTO add_customer(name, phone, address) VALUES (?, ?, ?)");
            pst.setString(1, txtName.getText());
            pst.setString(2, txtPhone.getText());
            pst.setString(3, txtAddress.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer Added");
            loadCustomers();
            clearFields();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: Phone must be unique."); }
    }

    private void updateCustomer() {
        try {
            pst = con.prepareStatement("UPDATE customer SET name=?, address=? WHERE phone=?");
            pst.setString(1, txtName.getText());
            pst.setString(2, txtAddress.getText());
            pst.setString(3, txtPhone.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer Updated");
            loadCustomers();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void deleteCustomer() {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            try {
                pst = con.prepareStatement("DELETE FROM customer WHERE phone=?");
                pst.setString(1, txtPhone.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted");
                loadCustomers();
                clearFields();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
    }

    public static void main(String[] args) {
        new CustomerModule().setVisible(true);
    }
}
