import javax.swing.*;
import java.sql.*;

public class DeliveryManagement extends JFrame {

    JTextField txtOrderId;
    JComboBox<String> cmbStatus;
    JButton btnUpdate;

    Connection con;

    public DeliveryManagement() {

        con = DBConnection.getConnection();

        setTitle("Delivery Update");
        setSize(400, 300);
        setLayout(null);

        JLabel l1 = new JLabel("Order ID:");
        l1.setBounds(50, 50, 100, 25);
        add(l1);

        txtOrderId = new JTextField();
        txtOrderId.setBounds(150, 50, 150, 25);
        add(txtOrderId);

        JLabel l2 = new JLabel("Status:");
        l2.setBounds(50, 100, 100, 25);
        add(l2);

        cmbStatus = new JComboBox<>(new String[]{
            "Processing", "Shipped", "Delivered"
        });
        cmbStatus.setBounds(150, 100, 150, 25);
        add(cmbStatus);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(120, 160, 120, 30);
        add(btnUpdate);

        btnUpdate.addActionListener(e -> updateDelivery());
    }

    private void updateDelivery() {
        try {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO delivery (order_id, status) VALUES (?, ?)"
            );

            pst.setInt(1, Integer.parseInt(txtOrderId.getText()));
            pst.setString(2, cmbStatus.getSelectedItem().toString());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Status Updated!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}