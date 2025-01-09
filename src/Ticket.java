import java.util.UUID;
import java.util.Date;

public class Ticket {
    private String id;
    private String category;
    private String approved;
    private String reason;
    private Date startTicketDate; // Novi atribut
    private Date endTicketDate; // Novi atribut
    private String employeeName; // Dodajemo employeeName

    // Konstruktor sa reason, start i end dates i employeeName
    public Ticket(String category, String approved, String reason, Date startTicketDate, Date endTicketDate, String employeeName) {
        this.id = UUID.randomUUID().toString();
        this.category = category;
        this.approved = (approved == null) ? "Na čekanju" : approved;
        this.reason = reason;
        this.startTicketDate = startTicketDate;
        this.endTicketDate = endTicketDate;
        this.employeeName = employeeName; // Inicijalizacija employeeName
    }

    // Konstruktor sa ID, reason, start i end dates i employeeName
    public Ticket(String id, String category, String approved, String reason, Date startTicketDate, Date endTicketDate, String employeeName) {
        this.id = id;
        this.category = category;
        this.approved = (approved == null) ? "Na čekanju" : approved;
        this.reason = reason;
        this.startTicketDate = startTicketDate;
        this.endTicketDate = endTicketDate;
        this.employeeName = employeeName; // Inicijalizacija employeeName
    }

    // Getteri i setteri za sve atribute
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

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", Kategorija='" + category + '\'' +
                ", Status='" + approved + '\'' +
                ", Razlog='" + reason + '\'' +
                ", Početni datum=" + startTicketDate +
                ", Završni datum=" + endTicketDate +
                ", Ime='" + employeeName + '\'' +
                '}';
    }
}
