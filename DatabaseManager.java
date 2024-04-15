import java.sql.*;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:C:\\Users\\aladi\\Database\\manager.db";

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            if (conn != null) {
                ResultSet tables = conn.getMetaData().getTables(null, null, "users", null);
                if (!tables.next()) {
                    String sql = "CREATE TABLE users (\n"
                            + " username text PRIMARY KEY,\n"
                            + " password text NOT NULL\n"
                            + ");";
                    conn.createStatement().execute(sql);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean checkUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}