import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Display bus details (all buses)
    public void displayBuses() throws SQLException {
        String query = "SELECT * FROM buses";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Bus No | Source -> Dest | Fare");
            System.out.println("------------------------------");
            while (rs.next()) {
                System.out.println(
                    rs.getString("bus_no") + " | " +
                    rs.getString("source") + " -> " +
                    rs.getString("destination") + " | " +
                    rs.getDouble("fare")
                );
            }
            System.out.println("------------------------------");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Display bus details with filter
    public void displayBuses(String source, String destination) throws SQLException {
        String query = "SELECT * FROM buses WHERE source = ? AND destination = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, source);
            ps.setString(2, destination);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Bus No | Source -> Dest | Fare");
                System.out.println("------------------------------");
                while (rs.next()) {
                    System.out.println(
                        rs.getString("bus_no") + " | " +
                        rs.getString("source") + " -> " +
                        rs.getString("destination") + " | " +
                        rs.getDouble("fare")
                    );
                }
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Display passengers details (optional admin view)
    public void displayPassengers() throws SQLException {
        String query = "SELECT * FROM passengers";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("First Name | Last Name | Email | Phone");
            System.out.println("-------------------------------------------");
            while (rs.next()) {
                System.out.println(
                    rs.getString("first_name") + " | " +
                    rs.getString("last_name") + " | " +
                    rs.getString("email") + " | " +
                    rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Add new passenger (legacy convenience)
    public Boolean addPassenger(Passenger p) {
        String query = "INSERT INTO passengers(first_name, last_name, email, phone) VALUES(?,?,?,?)";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getPhone());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    // Check if seat is available
    public boolean isSeatAvailable(int busId, int seatNo, LocalDate travelDate) {
        String query = "SELECT COUNT(*) FROM bookings WHERE seat_no = ? AND bus_id = ? AND travel_date = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, seatNo);
            ps.setInt(2, busId);
            ps.setDate(3, sqlDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int result = rs.getInt(1);
                    return result == 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
        return false;
    }

    // Get booked tickets count for the day
    public int getBookedCount(int busId, LocalDate travelDate) throws SQLException {
        String query = "SELECT COUNT(*) FROM bookings WHERE bus_id = ? AND travel_date = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, busId);
            ps.setDate(2, sqlDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        }
    }

    // Check bus capacity (no of seats)
    public int getBusCapacity(int busId) throws SQLException {
        String query = "SELECT capacity FROM buses WHERE bus_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, busId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capacity");
                }
                return 0;
            }
        }
    }

    public int getBusIdByBusNo(String busNo) throws SQLException {
        String query = "SELECT bus_id FROM buses WHERE bus_no = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, busNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bus_id");
                }
                return 0;
            }
        }
    }

    public int getBusCapacityByBusNo(String busNo) throws SQLException {
        String query = "SELECT capacity FROM buses WHERE bus_no = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, busNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capacity");
                }
                return 0;
            }
        }
    }

    public double getSeatPriceByBusNo(String busNo) throws SQLException {
        String query = "SELECT fare FROM buses WHERE bus_no = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, busNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("fare");
                }
            }
        }
        return 0.0;
    }

    // Get booked seats
    public List<Integer> getBookedSeats(int busId, LocalDate travelDate) throws SQLException {
        List<Integer> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_no FROM bookings WHERE bus_id = ? AND travel_date = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, busId);
            ps.setDate(2, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookedSeats.add(rs.getInt("seat_no"));
                }
            }
        }
        return bookedSeats;
    }

    public List<Integer> getBookedSeatsByBusNo(String busNo, LocalDate travelDate) throws SQLException {
        List<Integer> bookedSeats = new ArrayList<>();
        String query = "SELECT b.seat_no FROM bookings b JOIN buses bs ON b.bus_id = bs.bus_id WHERE bs.bus_no = ? AND b.travel_date = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, busNo);
            ps.setDate(2, sqlDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookedSeats.add(rs.getInt("seat_no"));
                }
            }
        }
        return bookedSeats;
    }

    public List<Integer> findUnavailableSeats(int busId, LocalDate travelDate, List<Integer> seats) throws SQLException {
        List<Integer> unavailable = new ArrayList<>();
        if (seats == null || seats.isEmpty()) {
            return unavailable;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < seats.size(); i++) {
            placeholders.append("?");
            if (i < seats.size() - 1) {
                placeholders.append(",");
            }
        }

        String query = "SELECT seat_no FROM bookings WHERE bus_id = ? AND travel_date = ? AND seat_no IN (" + placeholders + ")";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, busId);
            ps.setDate(2, sqlDate);
            for (int i = 0; i < seats.size(); i++) {
                ps.setInt(3 + i, seats.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    unavailable.add(rs.getInt("seat_no"));
                }
            }
        }

        return unavailable;
    }

    public List<Integer> findUnavailableSeatsByBusNo(String busNo, LocalDate travelDate, List<Integer> seats) throws SQLException {
        List<Integer> unavailable = new ArrayList<>();
        if (seats == null || seats.isEmpty()) {
            return unavailable;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < seats.size(); i++) {
            placeholders.append("?");
            if (i < seats.size() - 1) {
                placeholders.append(",");
            }
        }

        String query = "SELECT b.seat_no FROM bookings b JOIN buses bs ON b.bus_id = bs.bus_id " +
                       "WHERE bs.bus_no = ? AND b.travel_date = ? AND b.seat_no IN (" + placeholders + ")";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, busNo);
            ps.setDate(2, sqlDate);
            for (int i = 0; i < seats.size(); i++) {
                ps.setInt(3 + i, seats.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    unavailable.add(rs.getInt("seat_no"));
                }
            }
        }

        return unavailable;
    }

    // Book multiple tickets
    public boolean bookMultipleTickets(int passengerId, int busId, LocalDate travelDate, List<Integer> seats, double farePerSeats) throws SQLException {
        String query = "INSERT INTO bookings(bus_id, passenger_id, travel_date, seat_no, amount_paid) VALUES(?,?,?,?,?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        Connection con = null;
        boolean success = false;
        try {
            con = DbConnection.getConnection();
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(query)) {
                for (int seatNo : seats) {
                    ps.setInt(1, busId);
                    ps.setInt(2, passengerId);
                    ps.setDate(3, sqlDate);
                    ps.setInt(4, seatNo);
                    ps.setDouble(5, farePerSeats);

                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
                System.out.println("\nSUCCESS: All " + seats.size() + " tickets booked successfully!");
                success = true;
            } catch (SQLException e) {
                con.rollback();
                System.out.println("\nTRANSACTION FAILED: Rolling back all bookings. Reason: " + e.getMessage());
            }
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Database Error: " + e.getMessage());
                }
            }
        }
        return success;
    }

    // Fetch seat price
    public double getSeatPrice(int busId) throws SQLException {
        String query = "SELECT fare FROM buses WHERE bus_id = ?";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, busId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("fare");
                }
            }
        }
        return 0.0;
    }

    // Display Passenger Ticket
    public void showTicket(int passengerId) {
        String query = "SELECT b.travel_date, GROUP_CONCAT(b.seat_no ORDER BY b.seat_no ASC SEPARATOR ', ') AS booked_seats, " +
                       "SUM(b.amount_paid) AS total_amount, bs.source, bs.destination " +
                       "FROM bookings b " +
                       "JOIN buses bs ON b.bus_id = bs.bus_id " +
                       "WHERE b.passenger_id = ? " +
                       "GROUP BY b.travel_date, b.bus_id, bs.source, bs.destination";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- YOUR TICKETS ---");
                System.out.println("Date       | Route               | Seats    | Total Paid");
                System.out.println("--------------------------------------------------------");

                boolean hasTickets = false;
                while (rs.next()) {
                    hasTickets = true;
                    System.out.printf("%-10s | %s -> %-10s | %-8s | Rs. %.2f\n",
                        rs.getDate("travel_date"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("booked_seats"),
                        rs.getDouble("total_amount")
                    );
                }
                if (!hasTickets) {
                    System.out.println("No tickets found for this passenger.");
                }
                System.out.println("--------------------------------------------------------\n");
            } catch (SQLException e) {
                System.out.println("Error fetching tickets: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to db: " + e.getMessage());
        }
    }

    public List<BookingSummary> getBookingSummaries(int passengerId) {
        List<BookingSummary> summaries = new ArrayList<>();
        String query = "SELECT b.bus_id, bs.bus_no, b.travel_date, GROUP_CONCAT(b.seat_no ORDER BY b.seat_no ASC SEPARATOR ', ') AS booked_seats, " +
                       "SUM(b.amount_paid) AS total_amount, bs.source, bs.destination " +
                       "FROM bookings b " +
                       "JOIN buses bs ON b.bus_id = bs.bus_id " +
                       "WHERE b.passenger_id = ? " +
                       "GROUP BY b.bus_id, b.travel_date, bs.source, bs.destination " +
                       "ORDER BY b.travel_date";
        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summaries.add(new BookingSummary(
                        rs.getInt("bus_id"),
                        rs.getString("bus_no"),
                        rs.getDate("travel_date").toLocalDate(),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("booked_seats"),
                        rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bookings: " + e.getMessage());
        }
        return summaries;
    }

    public double cancelBooking(int passengerId, int busId, LocalDate travelDate) throws SQLException {
        String totalQuery = "SELECT SUM(amount_paid) FROM bookings WHERE passenger_id = ? AND bus_id = ? AND travel_date = ?";
        String deleteQuery = "DELETE FROM bookings WHERE passenger_id = ? AND bus_id = ? AND travel_date = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            double refund = 0.0;
            try (PreparedStatement totalPs = con.prepareStatement(totalQuery)) {
                totalPs.setInt(1, passengerId);
                totalPs.setInt(2, busId);
                totalPs.setDate(3, sqlDate);
                try (ResultSet rs = totalPs.executeQuery()) {
                    if (rs.next()) {
                        refund = rs.getDouble(1);
                    }
                }
            }

            if (refund <= 0.0) {
                con.rollback();
                return 0.0;
            }

            try (PreparedStatement deletePs = con.prepareStatement(deleteQuery)) {
                deletePs.setInt(1, passengerId);
                deletePs.setInt(2, busId);
                deletePs.setDate(3, sqlDate);
                deletePs.executeUpdate();
            }

            con.commit();
            return refund;
        }
    }

    public double cancelBookingByBusNo(int passengerId, String busNo, LocalDate travelDate) throws SQLException {
        String totalQuery = "SELECT SUM(b.amount_paid) FROM bookings b JOIN buses bs ON b.bus_id = bs.bus_id " +
                            "WHERE b.passenger_id = ? AND bs.bus_no = ? AND b.travel_date = ?";
        String deleteQuery = "DELETE b FROM bookings b JOIN buses bs ON b.bus_id = bs.bus_id " +
                             "WHERE b.passenger_id = ? AND bs.bus_no = ? AND b.travel_date = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);

        try (Connection con = DbConnection.getConnection()) {
            con.setAutoCommit(false);
            double refund = 0.0;
            try (PreparedStatement totalPs = con.prepareStatement(totalQuery)) {
                totalPs.setInt(1, passengerId);
                totalPs.setString(2, busNo);
                totalPs.setDate(3, sqlDate);
                try (ResultSet rs = totalPs.executeQuery()) {
                    if (rs.next()) {
                        refund = rs.getDouble(1);
                    }
                }
            }

            if (refund <= 0.0) {
                con.rollback();
                return 0.0;
            }

            try (PreparedStatement deletePs = con.prepareStatement(deleteQuery)) {
                deletePs.setInt(1, passengerId);
                deletePs.setString(2, busNo);
                deletePs.setDate(3, sqlDate);
                deletePs.executeUpdate();
            }

            con.commit();
            return refund;
        }
    }

    // Book a ticket (single)
    public void bookTicket(int passengerId, int busId, LocalDate travelDate, int seatNo, double amountPaid) {
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(travelDate);
            int capacity = getBusCapacity(busId);
            int booked = getBookedCount(busId, travelDate);

            if (capacity > booked) {
                String query = "INSERT INTO bookings(passenger_id, bus_id, travel_date, seat_no, amount_paid) VALUES(?,?,?,?,?)";
                try (Connection con = DbConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(query)) {

                    ps.setInt(1, passengerId);
                    ps.setInt(2, busId);
                    ps.setDate(3, sqlDate);
                    ps.setInt(4, seatNo);
                    ps.setDouble(5, amountPaid);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Booked ticket successfully!");
                    } else {
                        System.out.println("Booking failed.");
                    }
                }
            } else {
                System.out.println("Sorry, no seats available on this bus for the selected date.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
        }
    }
}
