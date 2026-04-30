import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookingDAO dao = new BookingDAO();

        try(Scanner sc = new Scanner(System.in)){
        System.out.println("WHAT U WANT (1: print bus, 2: print passenger info, 3. add passenger, 4. book ticket:");
        int choice = sc.nextInt();
        sc.nextLine();
        switch (choice) {
            case 1 -> {
                System.out.print("Enter bus source: ");
                String source = sc.nextLine();
                System.out.print("Enter bus destination: ");
                String destination = sc.nextLine();
                dao.displayBuses(source, destination);
            }
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
                    int capacity = dao.getBusCapacity(busId);
                    if(capacity == 0){ 
                        System.out.println("Invalid bus id"); 
                        break;
                    }
                    List<Integer> bookedSeats = dao.getBookedSeats(busId, date);
                    System.out.println("\n=======================================");
                    System.out.println("        FRONT OF BUS (Driver)          ");
                    System.out.println("=======================================");
                    
                    for(int i = 1; i <= capacity; i++){
                        if(bookedSeats.contains(i)){
                            System.out.print("[XX]");
                        }else{
                            System.out.printf("[%2d]", i);
                        }
                        if(i % 2 == 0 && i % 4 != 0){
                            System.out.print("   ");
                        }
                        if(i % 4 == 0){
                            System.out.println();
                        }
                    }

                    System.out.println("Price of each seat: "+ dao.getSeatPrice(busId));

                    System.out.println("\n=======================================");

                    if(bookedSeats.size() >= capacity){
                        System.out.println("The bus is completely full for this date :(");
                        break;
                    }
                    
                    System.out.print("Enter seat numbers separated by comma (e.g. 1, 2, 5): ");
                    String seatInput = sc.nextLine();

                    String[] selectedSeats = seatInput.split(",");
                    boolean allValid = true;
                    for(String s : selectedSeats){
                        int seat = Integer.parseInt(s.trim());
                        if(seat>capacity || seat <= 0){
                            System.out.println("Error: Seat " + seat + " doesn't exist on this bus.");
                            allValid = false;
                            break;
                        }
                        if(!dao.isSeatAvaliable(busId, seat, date)){
                            System.out.println("Error: Seat " + seat + " is already taken!");
                            allValid = false;
                            break;
                            }
                        }

                        if(allValid){
                            double fare = dao.getSeatPrice(busId);
                            System.out.println("Total amount to be paid: "+ selectedSeats.length * fare);
                            System.out.println("Processing booking for "+ selectedSeats.length+" seats..");
                            dao.bookMultipleTickets(passengerId, busId, date, selectedSeats, fare);
                        }else {
                            System.out.println("Booking cancelled due to invalid seat selection. Please try again.");
                        }                     
                }
            default -> throw new AssertionError();
                }
            }catch(SQLException e){
                System.out.println(e);
        }
    }
}

