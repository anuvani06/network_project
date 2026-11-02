import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignup;
    Color primaryPurple = new Color(102, 51, 153);
    Color bgWhite = Color.WHITE;
    Color hoverPurple = new Color(120, 70, 170);
    public LoginForm() {
        setTitle("Login - Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(primaryPurple);
        setContentPane(background);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 500));
        card.setBackground(bgWhite);
        card.setLayout(new BorderLayout());
        
        card.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,20), 1));
        JPanel cardHeader = new JPanel();
        cardHeader.setBackground(primaryPurple);
        cardHeader.setPreferredSize(new Dimension(400, 100));
        cardHeader.setLayout(new GridBagLayout());
        JLabel lblLogin = new JLabel("WELCOME BACK");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogin.setForeground(bgWhite);
        cardHeader.add(lblLogin);
        
        JPanel cardBody = new JPanel(null); 
        cardBody.setBackground(bgWhite);

        JLabel l1 = new JLabel("Username / Phone");
        l1.setBounds(50, 40, 300, 20);
        l1.setForeground(primaryPurple);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 65, 300, 35);
        txtUsername.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, primaryPurple));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel l2 = new JLabel("Password");
        l2.setBounds(50, 120, 300, 20);
        l2.setForeground(primaryPurple);
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 145, 300, 35);
        txtPassword.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, primaryPurple));

        btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(50, 220, 300, 45);
        styleMainButton(btnLogin);

        JLabel lblOr = new JLabel("OR", JLabel.CENTER);
        lblOr.setBounds(50, 275, 300, 20);
        lblOr.setForeground(Color.GRAY);

        btnSignup = new JButton("Create New Account");
        btnSignup.setBounds(50, 310, 300, 35);
        styleSecondaryButton(btnSignup);

        cardBody.add(l1);
        cardBody.add(txtUsername);
        cardBody.add(l2);
        cardBody.add(txtPassword);
        cardBody.add(btnLogin);
        cardBody.add(lblOr);
        cardBody.add(btnSignup);

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(cardBody, BorderLayout.CENTER);

        background.add(card);

        btnLogin.addActionListener(e -> loginAction());
        btnSignup.addActionListener(e -> {
            new CustomerSignup().setVisible(true);
            this.dispose();
        });

        setVisible(true);
    }

    private void styleMainButton(JButton btn) {
        btn.setBackground(primaryPurple);
        btn.setForeground(bgWhite);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(bgWhite);
        btn.setForeground(primaryPurple);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(primaryPurple, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loginAction() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all details");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            
            PreparedStatement pst1 = con.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?");
            pst1.setString(1, username);
            pst1.setString(2, password);
            if (pst1.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Admin Login Successful");
                new Dashboard().setVisible(true);
                this.dispose();
                return;
            }

            PreparedStatement pst2 = con.prepareStatement("SELECT * FROM customer WHERE phone=? AND password=?");
            pst2.setString(1, username);
            pst2.setString(2, password);
            if (pst2.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Customer Login Successful");
                new CustomerHome(username).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
