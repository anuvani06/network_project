import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Color;
import java.awt.Desktop;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Image;


public class Purchase extends JFrame {

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    JLabel lblBookImage;

    JTextField txtCustomerName, txtPhone, txtDiscount;
    JTextField txtPrice, txtQty, txtTotal, txtDateTime;

    JComboBox<String> cmbBook;

    JTable table;
    DefaultTableModel model;

    double grandTotal = 0;
    int currentPoints = 0;
    double discountPercent = 0;
    double finalAmount = 0;

    public Purchase() {

    setTitle("Book Store - Purchase");
    setSize(800, 600);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    con = DBConnection.getConnection();

    JLabel title = new JLabel("BOOK STORE PURCHASE SYSTEM", JLabel.CENTER);
    title.setForeground(Color.WHITE);
    title.setOpaque(true);
    title.setBackground(new Color(52, 73, 94));
    title.setPreferredSize(new Dimension(100, 50));
    add(title, BorderLayout.NORTH);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBackground(new Color(245, 247, 250));
    add(mainPanel, BorderLayout.CENTER);

    JPanel customerPanel = new JPanel(new GridLayout(2, 4, 10, 10));
    customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
    customerPanel.setBackground(Color.WHITE);
    lblBookImage = new JLabel();

    txtCustomerName = new JTextField();
    txtPhone = new JTextField();
    txtDiscount = new JTextField();
    txtDiscount.setEditable(false);
    txtDateTime = new JTextField();
    txtDateTime.setEditable(false);

    customerPanel.add(new JLabel("Name"));
    customerPanel.add(txtCustomerName);
    customerPanel.add(new JLabel("Phone"));
    customerPanel.add(txtPhone);

    customerPanel.add(new JLabel("Discount"));
    customerPanel.add(txtDiscount);
    customerPanel.add(new JLabel("Date & Time"));
    customerPanel.add(txtDateTime);

    mainPanel.add(customerPanel);
    JPanel bookPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
    bookPanel.setBorder(BorderFactory.createTitledBorder("Book Selection"));
    bookPanel.setBackground(Color.WHITE);

    cmbBook = new JComboBox<>();
    cmbBook.setPreferredSize(new Dimension(150, 30));

    txtPrice = new JTextField(8);
    txtPrice.setEditable(false);

    txtQty = new JTextField(5);

    JButton btnAdd = new JButton("Add");
    styleButton(btnAdd, new Color(46, 204, 113));

    bookPanel.add(new JLabel("Book"));
    bookPanel.add(cmbBook);
    bookPanel.add(new JLabel("Price"));
    bookPanel.add(txtPrice);
    bookPanel.add(new JLabel("Qty"));
    bookPanel.add(txtQty);
    bookPanel.add(btnAdd);
    lblBookImage.setPreferredSize(new Dimension(100, 120));
lblBookImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));

bookPanel.add(lblBookImage);


    mainPanel.add(bookPanel);
    model = new DefaultTableModel(
            new String[]{"ID", "Book", "Price", "Qty", "Total"}, 0);
    table = new JTable(model);
    table.setRowHeight(25);

    JScrollPane sp = new JScrollPane(table);
    sp.setBorder(BorderFactory.createTitledBorder("Cart Items"));
    mainPanel.add(sp);
    JPanel btnPanel = new JPanel();
    btnPanel.setBackground(new Color(245, 247, 250));

    JButton btnRemove = new JButton("Remove");
    JButton btnBuy = new JButton("Buy");
    JButton btnPrint = new JButton("Print Bill");
    JButton btnBack = new JButton("Back");

styleButton(btnBack, new Color(52, 152, 219));
    styleButton(btnRemove, new Color(231, 76, 60));
    styleButton(btnBuy, new Color(52, 152, 219));
    styleButton(btnPrint, new Color(155, 89, 182));

    btnPanel.add(btnRemove);
    btnPanel.add(btnBuy);
    btnPanel.add(btnPrint);
    btnPanel.add(btnBack);

    mainPanel.add(btnPanel);
    JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    totalPanel.setBackground(Color.WHITE);

    txtTotal = new JTextField(10);
    txtTotal.setEditable(false);

    totalPanel.add(new JLabel("Grand Total: "));
    totalPanel.add(txtTotal);

    add(totalPanel, BorderLayout.SOUTH);
    loadBooks();
    loadDateTime();
    cmbBook.addActionListener(e -> loadPrice());
    btnAdd.addActionListener(e -> addToCart());
    btnRemove.addActionListener(e -> removeItem());
    btnBuy.addActionListener(e -> savePurchase());
    btnPrint.addActionListener(e -> generatePDF());
    btnBack.addActionListener(e -> {
    new Dashboard().setVisible(true); 
    this.dispose();
});
}
    private void styleButton(JButton btn, Color color) {
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
}
    
    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 100, 30);
        add(lbl);
    }

    private JTextField addTextField(int x, int y) {
        JTextField txt = new JTextField();
        txt.setBounds(x, y, 150, 30);
        add(txt);
        return txt;
    }

  
    private void loadBooks() {
        try {
            pst = con.prepareStatement("SELECT book_name FROM books");
            rs = pst.executeQuery();
            while (rs.next()) {
                cmbBook.addItem(rs.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

      private void loadPrice() {
        try {
            pst = con.prepareStatement("SELECT price, image FROM books WHERE book_name=?");
            pst.setString(1, cmbBook.getSelectedItem().toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                txtPrice.setText(rs.getString("price"));

                byte[] img = rs.getBytes("image");
                if (img != null) {
                    ImageIcon icon = new ImageIcon(img);
                    java.awt.Image image = icon.getImage().getScaledInstance(120, 150, java.awt.Image.SCALE_SMOOTH);
                    lblBookImage.setIcon(new ImageIcon(image));
                } else {
                    lblBookImage.setIcon(null);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

  
    private void addToCart() {
        try {
            int qty = Integer.parseInt(txtQty.getText());
            double price = Double.parseDouble(txtPrice.getText());

            pst = con.prepareStatement("SELECT * FROM books WHERE book_name=?");
            pst.setString(1, cmbBook.getSelectedItem().toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                int stockss = rs.getInt("quantity");

                if (qty > stockss) {
                    JOptionPane.showMessageDialog(this, "Stock not available");
                    return;
                }

                double total = qty * price;

                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        cmbBook.getSelectedItem(),
                        price,
                        qty,
                        total
                });

                grandTotal += total;
                txtTotal.setText(String.valueOf(grandTotal));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }
private void sendLowStockEmail(String message) {

    final String from = "malathimalathi02750@gmail.com";
    final String password = "vwsk dfpp pmqg jqwu"; 
    final String to = "anusya116@gmail.com";

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
                InternetAddress.parse(to));

        msg.setSubject(" LOW STOCK ALERT - BOOK STORE");

        
        msg.setText(
            "Hello Admin,\n\n" +
            message + "\n\n" +
            "Please restock the item immediately.\n\n" +
            "Regards,\nBook Store System"
        );

        Transport.send(msg);

        JOptionPane.showMessageDialog(this, "Email Sent Successfully!");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Email Failed!");
        e.printStackTrace();
    }
}
    private void removeItem() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            double val = Double.parseDouble(model.getValueAt(row, 4).toString());
            grandTotal -= val;
            txtTotal.setText(String.valueOf(grandTotal));
            model.removeRow(row);
        }
    }

private void checkCustomer() {
    try {
        pst = con.prepareStatement(
            "SELECT loyalty_points, get_discount(loyalty_points) FROM customer WHERE phone=?"
        );

        pst.setString(1, txtPhone.getText());
        rs = pst.executeQuery();

        if (rs.next()) {
            currentPoints = rs.getInt(1);
            discountPercent = rs.getDouble(2);
            txtDiscount.setText(discountPercent + "%");
        } else {
            currentPoints = 0;
            discountPercent = 0;
            txtDiscount.setText("0%");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e);
    }
}
    private void saveCustomer() {
        try {
            pst = con.prepareStatement("SELECT * FROM customer WHERE phone=?");
            pst.setString(1, txtPhone.getText());
            rs = pst.executeQuery();

            if (!rs.next()) {
                pst = con.prepareStatement(
                        "INSERT INTO customer(name, phone, loyalty_points) VALUES (?, ?, 0)");
                pst.setString(1, txtCustomerName.getText());
                pst.setString(2, txtPhone.getText());
                pst.executeUpdate();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

  
 private void savePurchase() {
    try {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        checkCustomer();
        saveCustomer();

        double discountAmt = (grandTotal * discountPercent) / 100;
        finalAmount = grandTotal - discountAmt;

        for (int i = 0; i < model.getRowCount(); i++) {

            String book = model.getValueAt(i, 1).toString();
            int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
            double total = Double.parseDouble(model.getValueAt(i, 4).toString());

            pst = con.prepareStatement(
                "INSERT INTO bill(customer_name, phone, book_name, quantity, total, date_time) VALUES (?, ?, ?, ?, ?, NOW())"
            );

            pst.setString(1, txtCustomerName.getText());
            pst.setString(2, txtPhone.getText());
            pst.setString(3, book);
            pst.setInt(4, qty);
            pst.setDouble(5, total);

            pst.executeUpdate(); 
        }

        PreparedStatement pstPoints = con.prepareStatement(
            "SELECT calculate_points(?)"
        );
        pstPoints.setDouble(1, finalAmount);

        ResultSet rsPoints = pstPoints.executeQuery();

        int earnedPoints = 0;
        if (rsPoints.next()) {
            earnedPoints = rsPoints.getInt(1);
        }

        int newPoints = currentPoints + earnedPoints;

        pst = con.prepareStatement(
            "UPDATE customer SET loyalty_points=? WHERE phone=?"
        );
        pst.setInt(1, newPoints);
        pst.setString(2, txtPhone.getText());
        pst.executeUpdate();

        JOptionPane.showMessageDialog(this, "Purchase Saved!");

        checkLowStockAlert(); 
        generatePDF();

        model.setRowCount(0);
        grandTotal = 0;
        txtTotal.setText("0");

    } catch (SQLException e) {

        if (e.getMessage().contains("Not enough stock")) {
            JOptionPane.showMessageDialog(this, "❌ Not enough stock!");
        } else {
            JOptionPane.showMessageDialog(this, e);
        }
    }
}

   private void checkLowStockAlert() {
    try {
        PreparedStatement pst = con.prepareStatement(
            "SELECT message FROM admin_alerts ORDER BY Book_id DESC LIMIT 1"
        );

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String msg = rs.getString("message");

            JOptionPane.showMessageDialog(this, msg,
                    "Low Stock Alert", JOptionPane.WARNING_MESSAGE);

            sendLowStockEmail(msg);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private void generatePDF() {
    try {
        Document doc = new Document();
        String path = "Bill_" + System.currentTimeMillis() + ".pdf";

        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();

        com.itextpdf.text.Font titleFont =
            new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA,
                18,
                com.itextpdf.text.Font.BOLD,
                BaseColor.WHITE
            );

        com.itextpdf.text.Font headerFont =
            new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA,
                12,
                com.itextpdf.text.Font.BOLD
            );

        com.itextpdf.text.Font normalFont =
            new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA,
                11,
                com.itextpdf.text.Font.NORMAL
            );

        try {
            Image logo = Image.getInstance(getClass().getResource("/image.png"));
            logo.scaleToFit(70, 70);
            logo.setAlignment(Element.ALIGN_LEFT);
            doc.add(logo);
        } catch (Exception e) {
        }

        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell(
            new Phrase("PONNA BOOK STORE", titleFont)
        );
        headerCell.setBackgroundColor(new BaseColor(44, 62, 80));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(10);
        headerCell.setBorder(Rectangle.NO_BORDER);

        headerTable.addCell(headerCell);
        doc.add(headerTable);

        PdfPTable details = new PdfPTable(2);
        details.setWidthPercentage(100);
        details.setSpacingBefore(10);

        details.addCell(getCell("Name:", headerFont));
        details.addCell(getCell(txtCustomerName.getText(), normalFont));

        details.addCell(getCell("Phone:", headerFont));
        details.addCell(getCell(txtPhone.getText(), normalFont));

        details.addCell(getCell("Date:", headerFont));
        details.addCell(getCell(txtDateTime.getText(), normalFont));

        doc.add(details);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);
        table.setWidths(new float[]{1, 3, 2, 1, 2, 2});

        String[] headers = {"ID", "Book", "Price", "Qty", "Total", "Image"};

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h));
            cell.setBackgroundColor(new BaseColor(52, 152, 219));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);

            cell.setPhrase(new Phrase(h, new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA,
                12,
                com.itextpdf.text.Font.BOLD,
                BaseColor.WHITE
            )));

            table.addCell(cell);
        }

        
        for (int i = 0; i < model.getRowCount(); i++) {

            int bookId = Integer.parseInt(model.getValueAt(i, 0).toString());

       
            for (int j = 0; j < 5; j++) {
                PdfPCell cell = new PdfPCell(
                    new Phrase(model.getValueAt(i, j).toString(), normalFont)
                );
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

           
            PreparedStatement pstImg = con.prepareStatement(
                "SELECT image FROM books WHERE book_id=?"
            );
            pstImg.setInt(1, bookId);
            ResultSet rsImg = pstImg.executeQuery();

            if (rsImg.next()) {
                byte[] img = rsImg.getBytes("image");

                if (img != null) {
                    Image im = Image.getInstance(img);
                    im.scaleToFit(60, 60);

                    PdfPCell imgCell = new PdfPCell(im);
                    imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(imgCell);
                } else {
                    table.addCell("No Image");
                }
            } else {
                table.addCell("No Image");
            }
        }

        doc.add(table);

        
        PdfPTable total = new PdfPTable(2);
        total.setWidthPercentage(40);
        total.setHorizontalAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(15);

        total.addCell(getCell("Total:", headerFont));
        total.addCell(getCell("₹ " + grandTotal, normalFont));

        total.addCell(getCell("Discount:", headerFont));
        total.addCell(getCell(discountPercent + " %", normalFont));

        total.addCell(getCell("Final Amount:", headerFont));
        total.addCell(getCell("₹ " + finalAmount, headerFont));

        doc.add(total);

        Paragraph footer = new Paragraph(
            "\nThank you for shopping with us!",
            normalFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();

        Desktop.getDesktop().open(new File(path));

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e);
    }
}
    private PdfPCell getCell(String text, com.itextpdf.text.Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setPadding(6);
    cell.setBorderWidth(0.5f);
    return cell;
}private void loadDateTime() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        txtDateTime.setText(sdf.format(new java.util.Date()));
    }

    public static void main(String[] args) {
        new Purchase().setVisible(true);
    }
}