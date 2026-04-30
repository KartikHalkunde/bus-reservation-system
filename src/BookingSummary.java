import java.time.LocalDate;

public class BookingSummary {
    private final int busId;
    private final String busNo;
    private final LocalDate travelDate;
    private final String source;
    private final String destination;
    private final String seats;
    private final double totalAmount;

    public BookingSummary(int busId, String busNo, LocalDate travelDate, String source, String destination, String seats, double totalAmount) {
        this.busId = busId;
        this.busNo = busNo;
        this.travelDate = travelDate;
        this.source = source;
        this.destination = destination;
        this.seats = seats;
        this.totalAmount = totalAmount;
    }

    public int getBusId() {
        return busId;
    }

    public String getBusNo() {
        return busNo;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getSeats() {
        return seats;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
