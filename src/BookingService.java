import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDAO;

    public BookingService(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    public void displayBuses(String source, String destination) throws SQLException {
        if (source == null || destination == null || source.trim().isEmpty() || destination.trim().isEmpty()) {
            bookingDAO.displayBuses();
        } else {
            bookingDAO.displayBuses(source, destination);
        }
    }

    public int getBusCapacity(int busId) throws SQLException {
        return bookingDAO.getBusCapacity(busId);
    }

    public int getBusCapacityByBusNo(String busNo) throws SQLException {
        return bookingDAO.getBusCapacityByBusNo(busNo);
    }

    public double getSeatPrice(int busId) throws SQLException {
        return bookingDAO.getSeatPrice(busId);
    }

    public double getSeatPriceByBusNo(String busNo) throws SQLException {
        return bookingDAO.getSeatPriceByBusNo(busNo);
    }

    public List<Integer> getBookedSeats(int busId, LocalDate date) throws SQLException {
        return bookingDAO.getBookedSeats(busId, date);
    }

    public List<Integer> getBookedSeatsByBusNo(String busNo, LocalDate date) throws SQLException {
        return bookingDAO.getBookedSeatsByBusNo(busNo, date);
    }

    public void printBusLayout(int capacity, List<Integer> bookedSeats) {
        System.out.println("\n=======================================");
        System.out.println("        FRONT OF BUS (Driver)          ");
        System.out.println("=======================================");
        System.out.println("[XX] = Booked | [NN] = Available");

        for (int i = 1; i <= capacity; i++) {
            if (bookedSeats.contains(i)) {
                System.out.print("[XX]");
            } else {
                System.out.printf("[%2d]", i);
            }
            if (i % 2 == 0 && i % 4 != 0) {
                System.out.print("   ");
            }
            if (i % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("=======================================\n");
    }

    public void showTicketsForPassenger(int passengerId) {
        bookingDAO.showTicket(passengerId);
    }

    public List<BookingSummary> getBookingSummaries(int passengerId) {
        return bookingDAO.getBookingSummaries(passengerId);
    }

    public double cancelBooking(int passengerId, int busId, LocalDate travelDate) throws SQLException {
        return bookingDAO.cancelBooking(passengerId, busId, travelDate);
    }

    public double cancelBookingByBusNo(int passengerId, String busNo, LocalDate travelDate) throws SQLException {
        return bookingDAO.cancelBookingByBusNo(passengerId, busNo, travelDate);
    }

    public String bookSeats(int passengerId, int busId, LocalDate travelDate, List<Integer> seats, double farePerSeat) throws SQLException {
        if (travelDate == null) {
            return "Invalid travel date.";
        }
        if (seats == null || seats.isEmpty()) {
            return "No seat numbers provided.";
        }

        int capacity = bookingDAO.getBusCapacity(busId);
        if (capacity == 0) {
            return "Invalid bus id.";
        }

        for (int seat : seats) {
            if (seat <= 0 || seat > capacity) {
                return "Seat " + seat + " does not exist on this bus.";
            }
        }

        List<Integer> unavailable = bookingDAO.findUnavailableSeats(busId, travelDate, seats);
        if (!unavailable.isEmpty()) {
            return "Seats already booked: " + unavailable;
        }

        boolean success = bookingDAO.bookMultipleTickets(passengerId, busId, travelDate, seats, farePerSeat);
        return success ? "SUCCESS" : "Booking failed. Please try again.";
    }

    public String bookSeatsByBusNo(int passengerId, String busNo, LocalDate travelDate, List<Integer> seats, double farePerSeat) throws SQLException {
        if (travelDate == null) {
            return "Invalid travel date.";
        }
        if (seats == null || seats.isEmpty()) {
            return "No seat numbers provided.";
        }

        int capacity = bookingDAO.getBusCapacityByBusNo(busNo);
        if (capacity == 0) {
            return "Invalid bus number.";
        }

        for (int seat : seats) {
            if (seat <= 0 || seat > capacity) {
                return "Seat " + seat + " does not exist on this bus.";
            }
        }

        List<Integer> unavailable = bookingDAO.findUnavailableSeatsByBusNo(busNo, travelDate, seats);
        if (!unavailable.isEmpty()) {
            return "Seats already booked: " + unavailable;
        }

        int busId = bookingDAO.getBusIdByBusNo(busNo);
        if (busId == 0) {
            return "Invalid bus number.";
        }

        boolean success = bookingDAO.bookMultipleTickets(passengerId, busId, travelDate, seats, farePerSeat);
        return success ? "SUCCESS" : "Booking failed. Please try again.";
    }
}
