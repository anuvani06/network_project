import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bookdb";
    private static final String USER = "root"; 
    private static final String PASS = "Anusuya";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
            return null; 
        }
    }

    public static void main(String args[]) {
        if (getConnection() != null) {
            System.out.println("Database connected successfully!");
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
