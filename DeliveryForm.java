import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;



public class DeliveryForm extends JFrame {

    Connection con;

    JComboBox<String> cmbBook;
    JTextField txtCustomer, txtPhone, txtAddress, txtQty;
    JLabel lblPrice, lblStock, lblDeliveryDate;

    public DeliveryForm() {

        con = DBConnection.getConnection();

        setTitle("Delivery Management");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(650, 60));

        JLabel title = new JLabel("Delivery Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(title);
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(8, 2, 12, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30),
                BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
        card.add(createLabel("Customer Name"));
        txtCustomer = createField();
        card.add(txtCustomer);

        card.add(createLabel("Phone Number"));
        txtPhone = createField();
        card.add(txtPhone);

        card.add(createLabel("Address"));
        txtAddress = createField();
        card.add(txtAddress);

        card.add(createLabel("Select Book"));
        cmbBook = new JComboBox<>();
        cmbBook.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(cmbBook);

        card.add(createLabel("Quantity"));
        txtQty = createField();
        card.add(txtQty);

        lblPrice = createInfoLabel("Price: ₹ 0");
        card.add(lblPrice);

        lblStock = createInfoLabel("Stock: 0");
        card.add(lblStock);

        lblDeliveryDate = createInfoLabel("Delivery Date: -");
        card.add(lblDeliveryDate);
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 247, 250));

        JButton btnOrder = createButton("Place Order", new Color(46, 204, 113));
        JButton btnHistory = createButton("My Orders", new Color(52, 152, 219));
        JButton btnDashboard = createButton("Dashboard", new Color(231, 76, 60));

        btnPanel.add(btnOrder);
        btnPanel.add(btnHistory);
        btnPanel.add(btnDashboard);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(card, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadBooks();

        cmbBook.addActionListener(e -> loadBookDetails());

        btnOrder.addActionListener(e -> placeOrder());

        btnHistory.addActionListener(e -> {
            String phone = txtPhone.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter phone number!");
                return;
            }
            new CustomerOrderHistory(phone);
        });

        btnDashboard.addActionListener(e -> {
            new Dashboard().setVisible(true);
            dispose();
        });

        txtPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                loadCustomerDetails();
            }
        });
    }
public DeliveryForm(String phone) {
    this(); 
    txtPhone.setText(phone);   
    loadCustomerDetails();    
}

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return txt;
    }

    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(44, 62, 80));
        return lbl;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    private void sendOrderEmail(String toEmail, String name, String book,
                            int qty, String address, String date) {

    final String from = "malathimalathi02750@gmail.com";
    final String password = "vwsk dfpp pmqg jqwu";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props,
        new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

    try {
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(toEmail));

        msg.setSubject(" Order Confirmation - Book Store");

        msg.setText(
            "Hello " + name + ",\n\n" +
            " Your order has been placed successfully!\n\n" +
            "Book: " + book + "\n" +
            "Quantity: " + qty + "\n" +
            " Address: " + address + "\n" +
            "Delivery Date: " + date + "\n\n" +
            "Thank you for shopping with us!\n"
        );

        Transport.send(msg);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void loadBooks() {
        try {
            PreparedStatement pst = con.prepareStatement("SELECT book_name FROM books");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                cmbBook.addItem(rs.getString("book_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBookDetails() {
        try {
            String book = cmbBook.getSelectedItem().toString();

            PreparedStatement pst = con.prepareStatement(
                    "SELECT price, quantity FROM books WHERE book_name=?"
            );
            pst.setString(1, book);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblPrice.setText("Price: ₹ " + rs.getDouble("price"));
                lblStock.setText("Stock: " + rs.getInt("quantity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomerDetails() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT name, address FROM customer WHERE phone=?"
            );
            pst.setString(1, txtPhone.getText().trim());

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtCustomer.setText(rs.getString("name"));
                txtAddress.setText(rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
    try {
        String name = txtCustomer.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String book = cmbBook.getSelectedItem().toString();
        int qty = Integer.parseInt(txtQty.getText().trim());

          String email = null;
        PreparedStatement pstEmail = con.prepareStatement(
            "SELECT email FROM customer WHERE phone=?"
        );
        pstEmail.setString(1, phone);
        ResultSet rsEmail = pstEmail.executeQuery();

        if (rsEmail.next()) {
            email = rsEmail.getString("email");
        }
         if (email == null || email.trim().isEmpty()) {
                email = JOptionPane.showInputDialog(this, "Enter Email:");
                
            }

        PreparedStatement pst = con.prepareStatement(
            "INSERT INTO delivery (customer_name, phone, address, book_name, quantity, delivery_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)"
        );

        LocalDate date = LocalDate.now().plusDays(3);

        pst.setString(1, name);
        pst.setString(2, phone);
        pst.setString(3, address);
        pst.setString(4, book);
        pst.setInt(5, qty);
        pst.setDate(6, Date.valueOf(date));
        pst.setString(7, "Placed");

        pst.executeUpdate();

        lblDeliveryDate.setText("Delivery Date: " + date);

        if (!email.isEmpty()) {
            sendOrderEmail(email, name, book, qty, address, date.toString());
        }

        JOptionPane.showMessageDialog(this, " Ordered Successfully");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    public static void main(String[] args) {
        new DeliveryForm().setVisible(true);
    }
}