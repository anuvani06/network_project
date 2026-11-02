import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

public class OptionForm extends JFrame {

    private Connection con;
    private PreparedStatement pst;
    private DefaultTableModel model;

    private JTextField txtId, txtName, txtPrice, txtGenre, txtQty, txtSearch;
    private JTable table;

    private JLabel lblImage;
    private JButton btnChooseImage;
    private byte[] imageData;

    Color primary = new Color(102, 51, 153);
    Color secondary = new Color(153, 102, 204);
    Color white = Color.WHITE;

    public OptionForm() {
        initUI();
        connectDB();
        loadTable();
    }

    private void initUI() {

        setTitle("Book Management Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(white);

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(white);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primary),
                "Book Details",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                primary
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtGenre = new JTextField();
        txtQty = new JTextField();

        styleField(txtId);
        styleField(txtName);
        styleField(txtPrice);
        styleField(txtGenre);
        styleField(txtQty);

        addField(leftPanel, gbc, "Book ID", txtId, 0);
        addField(leftPanel, gbc, "Book Name", txtName, 1);
        addField(leftPanel, gbc, "Price", txtPrice, 2);
        addField(leftPanel, gbc, "Genre", txtGenre, 3);
        addField(leftPanel, gbc, "Quantity", txtQty, 4);

        lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(120, 150));
        lblImage.setOpaque(true);
        lblImage.setBackground(white);
        lblImage.setBorder(BorderFactory.createLineBorder(primary));

        btnChooseImage = new JButton("Choose Image");
        styleButton(btnChooseImage);

        gbc.gridx = 0; gbc.gridy = 5;
        leftPanel.add(new JLabel("Image"), gbc);

        gbc.gridx = 1;
        leftPanel.add(lblImage, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        leftPanel.add(btnChooseImage, gbc);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnDashboard = new JButton("Dashboard");

        styleButton(btnAdd);
        styleButton(btnUpdate);
        styleButton(btnDelete);
        styleButton(btnDashboard);

        gbc.gridx = 0; gbc.gridy = 7;
        leftPanel.add(btnAdd, gbc);

        gbc.gridx = 1;
        leftPanel.add(btnUpdate, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        leftPanel.add(btnDelete, gbc);

        gbc.gridx = 1;
        leftPanel.add(btnDashboard, gbc);

        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(white);

        txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primary),
                "Search",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 13),
                primary
        ));
        rightPanel.add(txtSearch, BorderLayout.NORTH);

        table = new JTable();
        model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Price", "Genre", "Qty"}, 0);
        table.setModel(model);

        styleTable(table);

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

       
        btnChooseImage.addActionListener(e -> chooseImage());
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnDashboard.addActionListener(e -> goDashboard());

        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchBook(txtSearch.getText());
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> fillFields());
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          String label, JTextField field, int y) {

        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(primary);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createLineBorder(primary));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(primary);
        btn.setForeground(white);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(secondary);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(primary);
            }
        });
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(secondary);
        table.setSelectionForeground(white);

        JTableHeader header = table.getTableHeader();
        header.setBackground(primary);
        header.setForeground(white);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void connectDB() {
        con = DBConnection.getConnection();
    }

    private void loadTable() {
        try {
            model.setRowCount(0);

            pst = con.prepareStatement("SELECT * FROM books");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getDouble("price"),
                        rs.getString("genre"),
                        rs.getInt("quantity")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                FileInputStream fis = new FileInputStream(file);
                imageData = fis.readAllBytes();

                ImageIcon icon = new ImageIcon(imageData);
                Image img = icon.getImage().getScaledInstance(120, 150, Image.SCALE_SMOOTH);
                BufferedImage tinted = new BufferedImage(
                        img.getWidth(null),
                        img.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB
                );

                Graphics2D g2 = tinted.createGraphics();
                g2.drawImage(img, 0, 0, null);
                g2.setComposite(AlphaComposite.SrcAtop);
                g2.setColor(new Color(102, 51, 153, 60));
                g2.fillRect(0, 0, img.getWidth(null), img.getHeight(null));
                g2.dispose();

                lblImage.setIcon(new ImageIcon(tinted));

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void addBook() {
        try {
            pst = con.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?, ?)");

            pst.setInt(1, Integer.parseInt(txtId.getText()));
            pst.setString(2, txtName.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setString(4, txtGenre.getText());
            pst.setInt(5, Integer.parseInt(txtQty.getText()));
            pst.setBytes(6, imageData);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Added");
            loadTable();
            clear();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateBook() {
        try {
            pst = con.prepareStatement(
                "UPDATE books SET book_name=?, price=?, genre=?, quantity=?, image=? WHERE book_id=?");

            pst.setString(1, txtName.getText());
            pst.setDouble(2, Double.parseDouble(txtPrice.getText()));
            pst.setString(3, txtGenre.getText());
            pst.setInt(4, Integer.parseInt(txtQty.getText()));
            pst.setBytes(5, imageData);
            pst.setInt(6, Integer.parseInt(txtId.getText()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Updated");
            loadTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteBook() {
        try {
            pst = con.prepareStatement("DELETE FROM books WHERE book_id=?");
            pst.setInt(1, Integer.parseInt(txtId.getText()));

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Deleted");
            loadTable();
            clear();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void searchBook(String key) {
        try {
            pst = con.prepareStatement(
                "SELECT * FROM books WHERE book_name LIKE ? OR genre LIKE ?");
            pst.setString(1, "%" + key + "%");
            pst.setString(2, "%" + key + "%");

            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getDouble("price"),
                        rs.getString("genre"),
                        rs.getInt("quantity")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void fillFields() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                txtId.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtPrice.setText(model.getValueAt(row, 2).toString());
                txtGenre.setText(model.getValueAt(row, 3).toString());
                txtQty.setText(model.getValueAt(row, 4).toString());

                pst = con.prepareStatement("SELECT image FROM books WHERE book_id=?");
                pst.setInt(1, Integer.parseInt(txtId.getText()));
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    byte[] img = rs.getBytes("image");
                    if (img != null) {
                        ImageIcon icon = new ImageIcon(img);
                        Image image = icon.getImage().getScaledInstance(120, 150, Image.SCALE_SMOOTH);
                        lblImage.setIcon(new ImageIcon(image));
                        imageData = img;
                    }
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void clear() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtGenre.setText("");
        txtQty.setText("");
        lblImage.setIcon(null);
        imageData = null;
    }

    private void goDashboard() {
        new Dashboard().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        new OptionForm().setVisible(true);
    }
}