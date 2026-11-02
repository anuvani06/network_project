import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerSignup extends JFrame {

    JTextField nameField, phoneField;
    JButton signupBtn;

    Connection con;

    public CustomerSignup() {
        con = DBConnection.getConnection();

        setTitle("Customer Signup");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(25, 25, 25));
        add(panel);

        JLabel title = new JLabel("Create Account");
        title.setBounds(120, 20, 200, 30);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title);

        JLabel l1 = new JLabel("Full Name");
        l1.setBounds(40, 80, 100, 25);
        styleLabel(l1);
        panel.add(l1);

        nameField = new JTextField();
        nameField.setBounds(150, 80, 180, 30);
        styleField(nameField);
        panel.add(nameField);

        JLabel l2 = new JLabel("Phone");
        l2.setBounds(40, 130, 100, 25);
        styleLabel(l2);
        panel.add(l2);

        phoneField = new JTextField();
        phoneField.setBounds(150, 130, 180, 30);
        styleField(phoneField);
        panel.add(phoneField);

        signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(120, 190, 150, 40);
        styleButton(signupBtn);
        panel.add(signupBtn);

        signupBtn.addActionListener(e -> registerCustomer());
    }

    private void styleLabel(JLabel lbl) {
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleField(JTextField tf) {
        tf.setBackground(new Color(45, 45, 45));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 150, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 120, 215));
            }
        });
    }

    private void registerCustomer() {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            // 👉 INSERT WITHOUT PASSWORD
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO customer (name, phone) VALUES (?, ?)"
            );

            pst.setString(1, name);
            pst.setString(2, phone);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Signup Successful");

           
            new DeliveryForm(phone).setVisible(true);
            dispose();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Phone already exists!");

            new DeliveryForm(phoneField.getText()).setVisible(true);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        new CustomerSignup().setVisible(true);
    }
}