import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class EmployeeForm extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField txtId, txtName, txtSalary, txtAge, txtPhone, txtDesignation, txtExp, txtSearch;
    JButton btnAdd, btnUpdate, btnDisplay, btnBack;

    Color primaryPurple = new Color(102, 51, 153);
    Color bgWhite = Color.WHITE;

    public EmployeeForm() {
        setTitle("Employee Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(primaryPurple);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryPurple);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Employee Management System");
        title.setForeground(bgWhite);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        btnBack = new JButton("Dashboard");
        styleButton(btnBack);

        header.add(title, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(primaryPurple);

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 15, 15));
        formPanel.setBackground(bgWhite);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryPurple), "Employee Details", 0, 0, new Font("Segoe UI", Font.BOLD, 14), primaryPurple));

        txtId = createField();
        txtName = createField();
        txtSalary = createField();
        txtAge = createField();
        txtPhone = createField();
        txtDesignation = createField();
        txtExp = createField();
        txtSearch = createField();

        addField(formPanel, "ID", txtId);
        addField(formPanel, "Name", txtName);
        addField(formPanel, "Salary", txtSalary);
        addField(formPanel, "Age", txtAge);
        addField(formPanel, "Phone", txtPhone);
        addField(formPanel, "Designation", txtDesignation);
        addField(formPanel, "Experience", txtExp);
        addField(formPanel, "Search", txtSearch);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(primaryPurple);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDisplay = new JButton("Refresh");

        styleButton(btnAdd);
        styleButton(btnUpdate);
        styleButton(btnDisplay);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDisplay);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Salary", "Age", "Phone", "Designation", "Experience"}, 0);
        table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(bgWhite);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDisplay.addActionListener(e -> loadTable());
        btnBack.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String key = txtSearch.getText();
                if (key.isEmpty()) loadTable();
                else searchEmployee(key);
            }
        });

        loadTable();
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
        btn.setPreferredSize(new Dimension(120, 35));
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

    private void addEmployee() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO employee VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.setString(2, txtName.getText());
            ps.setDouble(3, Double.parseDouble(txtSalary.getText()));
            ps.setInt(4, Integer.parseInt(txtAge.getText()));
            ps.setString(5, txtPhone.getText());
            ps.setString(6, txtDesignation.getText());
            ps.setInt(7, Integer.parseInt(txtExp.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee Added");
            loadTable();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void loadTable() {
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM employee");
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{ rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getInt(7) });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateEmployee() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE employee SET name=?, salary=?, age=?, phone=?, designation=?, experience=? WHERE id=?");
            ps.setString(1, txtName.getText());
            ps.setDouble(2, Double.parseDouble(txtSalary.getText()));
            ps.setInt(3, Integer.parseInt(txtAge.getText()));
            ps.setString(4, txtPhone.getText());
            ps.setString(5, txtDesignation.getText());
            ps.setInt(6, Integer.parseInt(txtExp.getText()));
            ps.setInt(7, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Updated");
            loadTable();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void searchEmployee(String key) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM employee WHERE id LIKE ? OR name LIKE ?");
            ps.setString(1, "%" + key + "%");
            ps.setString(2, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{ rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getInt(7) });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeForm().setVisible(true));
    }
}
