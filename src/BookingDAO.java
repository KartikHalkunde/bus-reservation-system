import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    //Display bus details 
    public void displayBuses(String source, String destination) throws SQLException{
        String query = "SELECT * FROM buses WHERE source = ? AND destination = ?";

        try (Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query))
        {
            ps.setString(1, source);
            ps.setString(2, destination);
            ResultSet rs = ps.executeQuery();
            System.out.println("Bus No | Source -> Dest | Fare");
            System.out.println("------------------------------");
            while(rs.next()){
                System.out.println(
                    rs.getString("bus_no") + " | " +
                    rs.getString("source") + " -> " +
                    rs.getString("destination") + " | " +
                    rs.getDouble("fare")
                );
            }
            System.out.println("------------------------------");
        }catch(SQLException e){
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    //Display passengers details
    public void displayPassengers() throws SQLException{
        String query = "SELECT * FROM passengers";
        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()){
                System.out.println("First Name | Last Name | email id | Phone ");
                System.out.println("-------------------------------------------");
                while(rs.next()){
                System.out.println(
                    rs.getString("first_name") + " | " +
                    rs.getString("last_name") + " | " + 
                    rs.getString("email") + " | " +
                    rs.getString("phone")
                );
            }
            }
            catch(SQLException e){
            }
    }

    //Add new passenger
    public Boolean addPassenger(Passenger p){
        String query = "INSERT INTO passengers(first_name, last_name, email, phone) VALUES(?,?,?,?);";

        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
                ps.setString(1, p.getFirstName());
                ps.setString(2, p.getLastName());
                ps.setString(3, p.getEmail()); 
                ps.setString(4, p.getPhone());
                int rows = ps.executeUpdate();
                return rows > 0;
            }
            catch(SQLException e){
                return false;
            }
        }


    //check if seat avaliable
    public boolean isSeatAvaliable(int busId, int seatNo, String dateString){
        String query = "SELECT COUNT(*) FROM bookings WHERE seat_no = ? AND bus_id = ? AND travel_date = ?";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        
        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){

                ps.setInt(1, seatNo);
                ps.setInt(2, busId);
                ps.setDate(3, sqlDate);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    int result = rs.getInt(1);
                    return result == 0;
                }else{
                    return false;
                }
            }
            }catch(SQLException e){
                return false;
            }
    }


    //Get booked tickets count for the day
     public int getBookedCount(int busId, String dateString) throws SQLException {
        String query = "SELECT COUNT(*) FROM bookings WHERE bus_id = ? AND travel_date = ?";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, busId);
            ps.setDate(2, sqlDate);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            return 0;
        }
    }
    //Check bus capacity (no of seats)
    public int getBusCapacity(int busId) throws SQLException{

        String query = "SELECT * FROM buses WHERE bus_id = ?";
        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, busId);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getInt("capacity");
                }
                return 0;
            }    
    }

    //Get Booked seats
    public List<Integer> getBookedSeats(int busId, String dateString)throws SQLException {
        List<Integer> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_no FROM bookings WHERE bus_id = ? AND travel_date = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, busId);
                ps.setDate(2, sqlDate);
                
                try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookedSeats.add(rs.getInt("seat_no"));
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
        return bookedSeats;
    }

    //Book multiple tickets
    public void bookMultipleTickets(int passengerId, int busId, String date, String[] seats, double farePerSeats){
        String query = "INSERT INTO bookings(bus_id, passenger_id, travel_date, seat_no, amount_paid) VALUES(?,?,?,?,?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        Connection con = null;
        try {
            con = DbConnection.getConnection();
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement(query)){
                for(String seatStr : seats){
                    int seatNo = Integer.parseInt(seatStr.trim());

                    ps.setInt(1, busId);
                    ps.setInt(2, passengerId);
                    ps.setDate(3, sqlDate);
                    ps.setInt(4, seatNo);
                    ps.setDouble(5, farePerSeats);

                    ps.addBatch();
                }
            ps.executeBatch();
            con.commit();
            System.out.println("\nSUCCESS: All " + seats.length + " tickets booked successfully!");
            }catch(SQLException e){
                con.rollback();
                System.out.println("\nTRANSACTION FAILED: Rolling back all bookings. Reason: " + e.getMessage());
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: "+ e);
        }finally{
            if(con!=null){
                try{ con.setAutoCommit(true); con.close(); } catch(SQLException e){ System.out.println(e);}
            }
        }
    }

    //fetch seat price
    public double getSeatPrice(int busId)throws SQLException{
        String query = "SELECT fare FROM buses WHERE bus_id = ?";
        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, busId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return rs.getDouble("fare");
                }
            }
            }
            return 0.0;
    }


    //Display Passenger Ticket

    public void showTicket(int passengerId){
        String query = "SELECT b.travel_date, GROUP_CONCAT(b.seat_no ORDER BY b.seat_no ASC SEPARATOR ', ') AS booked_seats, " +
                       "SUM(b.amount_paid) AS total_amount, bs.source, bs.destination " +
                       "FROM bookings b " +
                       "JOIN buses bs ON b.bus_id = bs.bus_id " +
                       "WHERE b.passenger_id = ? " +
                       "GROUP BY b.travel_date, b.bus_id, bs.source, bs.destination";
        try(Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
                ps.setInt(1, passengerId);
                try(ResultSet rs = ps.executeQuery()){
                    System.out.println("\n--- YOUR TICKETS ---");
                System.out.println("Date       | Route               | Seats    | Total Paid");
                System.out.println("--------------------------------------------------------");
                
                boolean hasTickets = false;
                while(rs.next()){
                    hasTickets = true;
                    System.out.printf("%-10s | %s -> %-10s | %-8s | Rs. %.2f\n",
                        rs.getDate("travel_date"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getString("booked_seats"),  // This will print "4, 5, 6"
                        rs.getDouble("total_amount")
                    );
                }
                if (!hasTickets) {
                    System.out.println("No tickets found for this passenger.");
                }
                System.out.println("--------------------------------------------------------\n");
                }
            catch(SQLException e){
                System.out.println("Error fetching tickets: "+e.getMessage());
            }
            }
            catch(SQLException e){
                System.out.println("error connnecting to db: "+e.getMessage());
            }
    }

    //Book a ticket 
    public void bookTicket(int passenger_id, int bus_id, String date, int seat_no, double amount_paid) {
    try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        int capacity = getBusCapacity(bus_id);
        int booked = getBookedCount(bus_id, date);
        
        if (capacity > booked) {
            String query = "INSERT INTO bookings(passenger_id, bus_id, travel_date, seat_no, amount_paid) VALUES(?,?,?,?,?)";
            try (Connection con = DbConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                
                ps.setInt(1, passenger_id);
                ps.setInt(2, bus_id);
                ps.setDate(3, sqlDate);
                ps.setInt(4,seat_no);
                ps.setDouble(5, amount_paid);
                
                int rowsAffected = ps.executeUpdate();
                if(rowsAffected>0){
                    System.out.println("Booked ticket Succesfully!");
                }else{
                    System.out.println("booking failed:(");
                }
            }
        } else {
            System.out.println("Sorry, no seats available on this bus for the selected date.");
        }
    } catch (SQLException e) {
        System.out.println("SQL Exception: "+ e);
    }
}
}
