
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentUnitTest extends JFrame {

    JTextField roll, name;
    JButton add;
        add(new JLabel("Roll Number"));
        roll = new JTextField(15);
        add(roll);

        add(new JLabel("Student Name"));
        name = new JTextField(15);
        add(name);

    StudentUnitTest() {

        setTitle("Student Unit Testing");
        setSize(350, 250);
        setLayout(new FlowLayout());


        add = new JButton("Add Student");
        add(add);

        add.addActionListener(e -> {

            if (!testStudentName(name.getText())) {
                JOptionPane.showMessageDialog(
                    this, "Invalid Student Name");
                return;
            }

            try {
                Connection con = DBConnection.getConnection();

                if (con == null) {
                    throw new SQLException("Database connection failed");
                }

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO students VALUES (?, ?)");

                ps.setInt(1, Integer.parseInt(roll.getText()));
                ps.setString(2, name.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                    this, "Student Added Successfully");

                con.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this, ex.getMessage());
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Unit Testing Method
    static boolean testStudentName(String n) {
        return n != null && !n.trim().isEmpty();
    }

    public static void main(String[] args) {

        System.out.println(
            "Test 1: " + testStudentName("Ravi"));

        System.out.println(
            "Test 2: " + testStudentName(""));

        new StudentUnitTest();
    }
}
