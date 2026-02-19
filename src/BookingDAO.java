import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingDAO {

    //Display bus details 
    public void displayBuses() throws SQLException{
        String query = "SELECT * FROM buses";

        try (Connection con = DbConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery())
        {
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
