import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;
    private final PassengerDAO passengerDAO;

    public AuthService(UserDAO userDAO, PassengerDAO passengerDAO) {
        this.userDAO = userDAO;
        this.passengerDAO = passengerDAO;
    }

    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!PasswordUtils.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            return null;
        }
        return user;
    }

    public User register(Passenger passenger, String username, String password) throws SQLException {
        User existingByUsername = userDAO.findByUsername(username);
        if (existingByUsername != null) {
            return null;
        }
        User existingByEmail = userDAO.findByEmail(passenger.getEmail());
        if (existingByEmail != null) {
            return null;
        }

        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(password, salt);

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int passengerId = passengerDAO.addPassenger(con, passenger);
                if (passengerId == 0) {
                    con.rollback();
                    return null;
                }

                int userId = userDAO.createUser(con, passengerId, username, passenger.getEmail(), hash, salt);
                if (userId == 0) {
                    con.rollback();
                    return null;
                }

                con.commit();
                return new User(userId, passengerId, username, passenger.getEmail(), hash, salt);
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public Passenger getPassengerById(int passengerId) throws SQLException {
        return passengerDAO.getPassengerById(passengerId);
    }

    public boolean updateProfile(int passengerId, int userId, String firstName, String lastName, String email, String phone) throws SQLException {
        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                boolean passengerUpdated = passengerDAO.updatePassenger(con, passengerId, firstName, lastName, email, phone);
                boolean userUpdated = userDAO.updateEmail(con, userId, email);
                if (!passengerUpdated || !userUpdated) {
                    con.rollback();
                    return false;
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean changePassword(int userId, String newPassword) throws SQLException {
        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hashPassword(newPassword, salt);
        return userDAO.updatePassword(userId, hash, salt);
    }
}
