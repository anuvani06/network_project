import java.sql.*;

public class DBBConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/db",
                "root",
                "Anusuya"
            );

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}