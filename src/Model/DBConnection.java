package Model;
import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/resumescanner";
    private static final String USER = "root";
    private static final String PASSWORD = "Sumukh@123"; // change this

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


