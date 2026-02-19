import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookingDAO dao = new BookingDAO();

        try(Scanner sc = new Scanner(System.in)){
        System.out.println("WHAT U WANT (1: print bus, 2: print passenger info, 3. add passenger, 4. book ticket:");
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
            case 4 -> {
                System.out.print("Enter passenger id: ");
                int passengerId = sc.nextInt();
                System.out.print("Enter bus id: ");
                int busId = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter date (dd-MM-yyyy): ");
                String date = sc.nextLine().trim();
                System.out.print("Enter seat num: ");
                int seat_no = sc.nextInt();
                System.out.print("Enter amount to pay: ");
                double amount_paid = sc.nextDouble();
                dao.bookTicket(passengerId, busId, date, seat_no, amount_paid);
                
            }
            default -> throw new AssertionError();
                }
            }catch(SQLException e){
                System.out.println(e);
        }
    }
}

