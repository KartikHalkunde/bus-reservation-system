import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookingDAO dao = new BookingDAO();

        try(Scanner sc = new Scanner(System.in)){
        System.out.println("WHAT U WANT (1: print bus, 2: print passenger info, 3. add passenger");
        int choice = sc.nextInt();
        sc.nextLine();
        switch (choice) {
            case 1 -> dao.displayBuses();
            case 2 -> dao.displayPassengers();
            case 3 -> {
                System.out.print("insert first name: ");
                String firstName = sc.nextLine();
                System.out.print("insert last name: ");
                String lastName = sc.nextLine();
                System.out.print("insert email: ");
                String email = sc.nextLine();
                System.out.print("insert phone: ");
                String phone = sc.nextLine();
                Passenger p = new Passenger(firstName, lastName, email, phone);
                if(dao.addPassenger(p) == true){
                    System.out.println("succesfully added");
                }else{
                    System.out.println("not added :(");
                }
                }
            default -> throw new AssertionError();
                }
            }catch(SQLException e){
        e.printStackTrace();
        }
    }
}

