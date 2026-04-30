import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    public User findByUsername(String username) throws SQLException {
        String query = "SELECT user_id, passenger_id, username, email, password_hash, salt FROM users WHERE username = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getInt("passenger_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("salt")
                    );
                }
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String query = "SELECT user_id, passenger_id, username, email, password_hash, salt FROM users WHERE email = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getInt("passenger_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("salt")
                    );
                }
            }
        }
        return null;
    }

    public int createUser(Connection con, int passengerId, String username, String email, String passwordHash, String salt) throws SQLException {
        String query = "INSERT INTO users(passenger_id, username, email, password_hash, salt) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, passengerId);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, passwordHash);
            ps.setString(5, salt);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                return 0;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean updatePassword(int userId, String passwordHash, String salt) throws SQLException {
        String query = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, passwordHash);
            ps.setString(2, salt);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEmail(Connection con, int userId, String email) throws SQLException {
        String query = "UPDATE users SET email = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
