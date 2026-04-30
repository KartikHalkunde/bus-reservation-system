import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PassengerDAO {
    public Passenger getPassengerById(int passengerId) throws SQLException {
        String query = "SELECT passenger_id, first_name, last_name, email, phone FROM passengers WHERE passenger_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }
    public int addPassenger(Connection con, Passenger passenger) throws SQLException {
        String query = "INSERT INTO passengers(first_name, last_name, email, phone) VALUES(?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, passenger.getFirstName());
            ps.setString(2, passenger.getLastName());
            ps.setString(3, passenger.getEmail());
            ps.setString(4, passenger.getPhone());
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

    public boolean updatePassenger(int passengerId, String firstName, String lastName, String email, String phone) throws SQLException {
        String query = "UPDATE passengers SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE passenger_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setInt(5, passengerId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassenger(Connection con, int passengerId, String firstName, String lastName, String email, String phone) throws SQLException {
        String query = "UPDATE passengers SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE passenger_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setInt(5, passengerId);
            return ps.executeUpdate() > 0;
        }
    }
}
