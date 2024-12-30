import java.util.UUID;
import java.util.Date;

public class Ticket {
    private String id;
    private String category;
    private boolean approved;
    private String reason;
    private Date startTicketDate; // New attribute
    private Date endTicketDate; // New attribute

    // Constructor with reason, start and end dates
    public Ticket(String category, boolean approved, String reason, Date startTicketDate, Date endTicketDate) {
        this.id = UUID.randomUUID().toString();
        this.category = category;
        this.approved = approved;
        this.reason = reason;
        this.startTicketDate = startTicketDate;
        this.endTicketDate = endTicketDate;
    }

    // Constructor with ID, reason, start and end dates
    public Ticket(String id, String category, boolean approved, String reason, Date startTicketDate, Date endTicketDate) {
        this.id = id;
        this.category = category;
        this.approved = approved;
        this.reason = reason;
        this.startTicketDate = startTicketDate;
        this.endTicketDate = endTicketDate;
    }

    // Getters and setters for all attributes
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartTicketDate() {
        return startTicketDate;
    }

    public void setStartTicketDate(Date startTicketDate) {
        this.startTicketDate = startTicketDate;
    }

    public Date getEndTicketDate() {
        return endTicketDate;
    }

    public void setEndTicketDate(Date endTicketDate) {
        this.endTicketDate = endTicketDate;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", approved=" + approved +
                ", reason='" + reason + '\'' +
                ", startTicketDate=" + startTicketDate +
                ", endTicketDate=" + endTicketDate +
                '}';
    }
}
