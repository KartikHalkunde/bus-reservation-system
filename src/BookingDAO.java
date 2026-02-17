import java.sql.*;

public class BookingDAO {
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
            e.printStackTrace();
        }
    }

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
                e.printStackTrace();
            }
    }

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
                e.printStackTrace();
                return false;
            }
        }
}
