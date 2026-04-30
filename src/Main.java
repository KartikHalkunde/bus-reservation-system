import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final BookingService bookingService = new BookingService(new BookingDAO());
    private static final AuthService authService = new AuthService(new UserDAO(), new PassengerDAO());

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Welcome to Bus Reservation System");
            while (true) {
                User user = authenticate(sc);
                if (user == null) {
                    System.out.println("Goodbye!");
                    return;
                }
                runMainMenu(sc, user);
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private static User authenticate(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n1. Login\n2. Register\n3. View buses\n0. Exit");
            int choice = readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> {
                    String username = readLine(sc, "Username: ");
                    String password = readLine(sc, "Password: ");
                    User user = authService.login(username, password);
                    if (user != null) {
                        System.out.println("Login successful. Welcome!");
                        return user;
                    }
                    System.out.println("Invalid credentials. Try again.");
                }
                case 2 -> {
                    User user = handleRegistration(sc);
                    if (user != null) {
                        System.out.println("Account created. You are now logged in.");
                        return user;
                    }
                }
                case 3 -> handleBusSearch(sc);
                case 0 -> {
                    return null;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static User handleRegistration(Scanner sc) throws SQLException {
        String firstName = readLine(sc, "First name: ");
        String lastName = readLine(sc, "Last name: ");
        String username = readLine(sc, "Username (4-20 letters/numbers/_): ");
        String email = readLine(sc, "Email: ");
        String phone = readLine(sc, "Phone (10-15 digits): ");
        String password = readLine(sc, "Password (min 6 chars): ");

        if (!InputValidator.isValidName(firstName) || !InputValidator.isValidName(lastName)) {
            System.out.println("Invalid name. Please try again.");
            return null;
        }
        if (!InputValidator.isValidUsername(username)) {
            System.out.println("Invalid username. Use 4-20 letters/numbers/_ only.");
            return null;
        }
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return null;
        }
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("Invalid phone number.");
            return null;
        }
        if (!InputValidator.isValidPassword(password)) {
            System.out.println("Password too short.");
            return null;
        }

        Passenger passenger = new Passenger(firstName.trim(), lastName.trim(), email.trim(), phone.trim());
        User user = authService.register(passenger, username.trim(), password);
        if (user == null) {
            System.out.println("Username or email already registered. Please login.");
        }
        return user;
    }

    private static void runMainMenu(Scanner sc, User user) throws SQLException {
        while (true) {
            System.out.println("\n1. List buses\n2. Book seats\n3. View bookings\n4. View account\n0. Exit");
            int choice = readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> handleBusSearch(sc);
                case 2 -> handleBooking(sc, user);
                case 3 -> handleBookingsMenu(sc, user);
                case 4 -> {
                    boolean logout = handleAccountMenu(sc, user);
                    if (logout) {
                        return;
                    }
                }
                case 0 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleBusSearch(Scanner sc) throws SQLException {
        String source = readLine(sc, "Enter source (leave blank for all): ");
        String destination = readLine(sc, "Enter destination (leave blank for all): ");
        bookingService.displayBuses(source, destination);
    }

    private static void handleBooking(Scanner sc, User user) throws SQLException {
        String busNo = readLine(sc, "Enter bus number: ");
        String dateInput = readLine(sc, "Enter travel date (" + InputValidator.DATE_PATTERN + "): ");
        LocalDate travelDate = InputValidator.parseDate(dateInput);
        if (travelDate == null) {
            System.out.println("Invalid date format.");
            return;
        }

        LocalDate today = LocalDate.now();
        if (travelDate.isBefore(today)) {
            System.out.println("You cannot book tickets for past dates.");
            return;
        }

        int capacity = bookingService.getBusCapacityByBusNo(busNo);
        if (capacity == 0) {
            System.out.println("Invalid bus number.");
            return;
        }

        List<Integer> bookedSeats = bookingService.getBookedSeatsByBusNo(busNo, travelDate);
        if (bookedSeats.size() >= capacity) {
            System.out.println("The bus is full for this date.");
            return;
        }

        bookingService.printBusLayout(capacity, bookedSeats);
        double fare = bookingService.getSeatPriceByBusNo(busNo);
        System.out.println("Price per seat: Rs. " + fare);

        String seatInput = readLine(sc, "Enter seat numbers separated by comma (e.g. 1, 2, 5): ");
        List<Integer> selectedSeats = InputValidator.parseSeatNumbers(seatInput, capacity);
        if (selectedSeats == null) {
            System.out.println("Invalid seat selection.");
            return;
        }

        double total = selectedSeats.size() * fare;
        System.out.println("Total amount to be paid: Rs. " + total);

        String result = bookingService.bookSeatsByBusNo(user.getPassengerId(), busNo, travelDate, selectedSeats, fare);
        if (!"SUCCESS".equals(result)) {
            System.out.println(result);
        }
    }

    private static void handleBookingsMenu(Scanner sc, User user) throws SQLException {
        while (true) {
            System.out.println("\n1. View booked tickets\n2. Cancel booking\n0. Back");
            int choice = readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> handleViewBookings(sc, user);
                case 2 -> handleCancelBooking(sc, user);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleViewBookings(Scanner sc, User user) {
        List<BookingSummary> summaries = bookingService.getBookingSummaries(user.getPassengerId());
        if (summaries.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("\n--- YOUR BOOKINGS ---");
        System.out.println("BusNo | Date       | Route               | Seats    | Total Paid");
        System.out.println("-------------------------------------------------------------");
        for (BookingSummary summary : summaries) {
            System.out.printf("%-5s | %-10s | %s -> %-10s | %-8s | Rs. %.2f\n",
                summary.getBusNo(),
                summary.getTravelDate(),
                summary.getSource(),
                summary.getDestination(),
                summary.getSeats(),
                summary.getTotalAmount()
            );
        }
        System.out.println("-------------------------------------------------------------\n");
    }

    private static void handleCancelBooking(Scanner sc, User user) throws SQLException {
        handleViewBookings(sc, user);
        String dateInput = readLine(sc, "Enter travel date to cancel (" + InputValidator.DATE_PATTERN + "): ");
        LocalDate travelDate = InputValidator.parseDate(dateInput);
        if (travelDate == null) {
            System.out.println("Invalid date format.");
            return;
        }
        String busNo = readLine(sc, "Enter bus number to cancel: ");

        double refund = bookingService.cancelBookingByBusNo(user.getPassengerId(), busNo, travelDate);
        if (refund <= 0.0) {
            System.out.println("No matching booking found.");
            return;
        }
        System.out.println("Booking cancelled. Refund amount: Rs. " + refund);
    }

    private static boolean handleAccountMenu(Scanner sc, User user) throws SQLException {
        while (true) {
            System.out.println("\n1. Edit profile\n2. Change password\n3. Logout\n0. Back");
            int choice = readInt(sc, "Choose an option: ");
            switch (choice) {
                case 1 -> handleProfileEdit(sc, user);
                case 2 -> handlePasswordChange(sc, user);
                case 3 -> {
                    System.out.println("Logged out.");
                    return true;
                }
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleProfileEdit(Scanner sc, User user) throws SQLException {
        Passenger current = authService.getPassengerById(user.getPassengerId());
        if (current == null) {
            System.out.println("Unable to load profile.");
            return;
        }

        System.out.println("Choose fields to edit (comma-separated): 1.Name 2.Email 3.Phone");
        String selection = readLine(sc, "Your choice: ");
        String[] parts = selection.split(",");

        String firstName = current.getFirstName();
        String lastName = current.getLastName();
        String email = current.getEmail();
        String phone = current.getPhone();

        for (String part : parts) {
            String choice = part.trim();
            if ("1".equals(choice)) {
                firstName = readLine(sc, "New first name: ");
                lastName = readLine(sc, "New last name: ");
            } else if ("2".equals(choice)) {
                email = readLine(sc, "New email: ");
            } else if ("3".equals(choice)) {
                phone = readLine(sc, "New phone (10-15 digits): ");
            }
        }

        if (selection.trim().isEmpty()) {
            System.out.println("No changes selected.");
            return;
        }

        if (!InputValidator.isValidName(firstName) || !InputValidator.isValidName(lastName)) {
            System.out.println("Invalid name.");
            return;
        }
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("Invalid phone number.");
            return;
        }

        boolean updated = authService.updateProfile(user.getPassengerId(), user.getId(), firstName.trim(), lastName.trim(), email.trim(), phone.trim());
        if (updated) {
            System.out.println("Profile updated successfully.");
        } else {
            System.out.println("Profile update failed.");
        }
    }

    private static void handlePasswordChange(Scanner sc, User user) throws SQLException {
        String newPassword = readLine(sc, "New password (min 6 chars): ");
        if (!InputValidator.isValidPassword(newPassword)) {
            System.out.println("Password too short.");
            return;
        }

        boolean updated = authService.changePassword(user.getId(), newPassword);
        if (updated) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Password update failed.");
        }
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
