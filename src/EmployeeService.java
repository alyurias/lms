import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeService {
    private final MongoCollection<Document> employeeCollection;
    private final MongoCollection<Document> ticketCollection;
    private final SimpleDateFormat dateFormat;

    public EmployeeService() {
        MongoDatabase database = MongoDBController.getInstance().getDatabase();
        this.employeeCollection = database.getCollection("kolekcija"); // Kolekcija za employees
        this.ticketCollection = database.getCollection("tickets"); // Nova kolekcija za tickets
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // Definiramo format datuma
    }

    // Metoda za autentifikaciju korisnika
    public Employee authenticate(String email, String password) {
        Document doc = employeeCollection.find(Filters.eq("email", email)).first();

        if (doc != null) {
            String storedPassword = doc.getString("password");

            if (storedPassword.equals(password)) {
                String id = doc.getString("_id");
                String name = doc.getString("name");
                String surname = doc.getString("surname");
                String role = doc.getString("role");
                List<String> tickets = (List<String>) doc.get("tickets");

                return new Employee(id, name, surname, email, storedPassword, new ArrayList<>(tickets), role);
            } else {
                System.out.println("Passwords do not match.");
            }
        } else {
            System.out.println("User with this email doesn't exist.");
        }
        return null;
    }

    // Metoda za dodavanje tiketa zaposleniku
    public void addTicketToEmployee(String employeeId, Ticket ticket) {
        Document ticketDoc = new Document("_id", ticket.getId())
                .append("type", "ticket")
                .append("id", ticket.getId())
                .append("category", ticket.getCategory())
                .append("approved", ticket.getApproved())
                .append("reason", ticket.getReason())
                .append("startTicketDate", ticket.getStartTicketDate())
                .append("endTicketDate", ticket.getEndTicketDate());

        ticketCollection.insertOne(ticketDoc);

        Document query = new Document("_id", employeeId);
        employeeCollection.updateOne(query, Updates.push("tickets", ticket.getId()));
    }

    // Metoda za dohvatanje tiketa za zaposlenika
    public List<Ticket> getTicketsForEmployee(String employeeId) {
        Document employeeDoc = employeeCollection.find(Filters.eq("_id", employeeId)).first();
        List<String> ticketIds = (List<String>) employeeDoc.get("tickets");

        List<Ticket> tickets = new ArrayList<>();
        for (String ticketId : ticketIds) {
            Document ticketDoc = ticketCollection.find(Filters.eq("id", ticketId)).first();
            if (ticketDoc != null) {
                String id = ticketDoc.getString("id");
                String category = ticketDoc.getString("category");
                String approved = ticketDoc.getString("approved");
                String reason = ticketDoc.getString("reason");

                Date startTicketDate = getDateFromDocument(ticketDoc, "startTicketDate");
                Date endTicketDate = getDateFromDocument(ticketDoc, "endTicketDate");

                tickets.add(new Ticket(id, category, approved, reason, startTicketDate, endTicketDate, employeeDoc.getString("name") + " " + employeeDoc.getString("surname")));
            }
        }
        return tickets;
    }

    // Metoda za ažuriranje tiketa
    public void updateTicket(Ticket ticket) {
        Document query = new Document("_id", ticket.getId());
        Document update = new Document("$set", new Document("category", ticket.getCategory())
                .append("approved", ticket.getApproved())
                .append("reason", ticket.getReason())
                .append("startTicketDate", ticket.getStartTicketDate())
                .append("endTicketDate", ticket.getEndTicketDate()));

        ticketCollection.updateOne(query, update);
    }

    // Metoda za brisanje tiketa
    public void deleteTicket(String employeeId, String ticketId) {
        Document query = new Document("_id", ticketId);
        ticketCollection.deleteOne(query);

        Document employeeQuery = new Document("_id", employeeId);
        employeeCollection.updateOne(employeeQuery, Updates.pull("tickets", ticketId));
    }

    // Metoda za dohvatanje svih tiketa (za ManagerObrazac)
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        for (Document ticketDoc : ticketCollection.find()) {
            String id = ticketDoc.getString("id");
            String category = ticketDoc.getString("category");
            String approved = ticketDoc.getString("approved");
            String reason = ticketDoc.getString("reason");
            Date startTicketDate = getDateFromDocument(ticketDoc, "startTicketDate");
            Date endTicketDate = getDateFromDocument(ticketDoc, "endTicketDate");

            Document employeeDoc = employeeCollection.find(Filters.eq("tickets", id)).first();
            if (employeeDoc != null) { // Provjera da li je employeeDoc null
                String employeeName = employeeDoc.getString("name") + " " + employeeDoc.getString("surname");
                tickets.add(new Ticket(id, category, approved, reason, startTicketDate, endTicketDate, employeeName));
            } else {
                System.out.println("Employee document not found for ticket ID: " + id);
            }
        }
        return tickets;
    }

    // Metoda za pretraživanje tiketa (za ManagerObrazac)
    public List<Ticket> searchTickets(String firstName, String lastName, boolean obicni, boolean redovni, boolean zdravstveni) {
        List<Ticket> filteredTickets = new ArrayList<>();
        List<Bson> filters = new ArrayList<>();

        if (!firstName.isEmpty()) {
            filters.add(Filters.regex("name", firstName, "i")); // Case-insensitive pretraga
        }
        if (!lastName.isEmpty()) {
            filters.add(Filters.regex("surname", lastName, "i")); // Case-insensitive pretraga
        }

        List<String> categories = new ArrayList<>();
        if (obicni) categories.add("Obicni");
        if (redovni) categories.add("Redovni");
        if (zdravstveni) categories.add("Zdravstveni");

        Bson combinedFilters;
        if (filters.isEmpty()) {
            combinedFilters = new Document(); // Koristi prazan Document ako nema filtera
        } else {
            combinedFilters = Filters.and(filters);
        }

        for (Document employeeDoc : employeeCollection.find(combinedFilters)) {
            List<String> ticketIds = (List<String>) employeeDoc.get("tickets");

            if (ticketIds != null) { // Provjera da li je ticketIds null
                for (String ticketId : ticketIds) {
                    Document ticketDoc = ticketCollection.find(Filters.eq("id", ticketId)).first();
                    if (ticketDoc != null && (categories.isEmpty() || categories.contains(ticketDoc.getString("category")))) {
                        String id = ticketDoc.getString("id");
                        String category = ticketDoc.getString("category");
                        String approved = ticketDoc.getString("approved");
                        String reason = ticketDoc.getString("reason");
                        Date startTicketDate = getDateFromDocument(ticketDoc, "startTicketDate");
                        Date endTicketDate = getDateFromDocument(ticketDoc, "endTicketDate");

                        String employeeName = employeeDoc.getString("name") + " " + employeeDoc.getString("surname");

                        filteredTickets.add(new Ticket(id, category, approved, reason, startTicketDate, endTicketDate, employeeName));
                    }
                }
            }
        }

        return filteredTickets;
    }

    // Metoda za dobavljanje datuma iz dokumenta
    private Date getDateFromDocument(Document doc, String key) {
        Object dateObj = doc.get(key);
        if (dateObj instanceof Date) {
            return (Date) dateObj;
        } else if (dateObj instanceof String) {
            try {
                return dateFormat.parse((String) dateObj);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Metoda za dohvatanje tiketa po ID-u
    public Ticket getTicketById(String ticketId) {
        Document ticketDoc = ticketCollection.find(Filters.eq("id", ticketId)).first();
        if (ticketDoc != null) {
            String id = ticketDoc.getString("id");
            String category = ticketDoc.getString("category");
            String approved = ticketDoc.getString("approved");
            String reason = ticketDoc.getString("reason");
            Date startTicketDate = getDateFromDocument(ticketDoc, "startTicketDate");
            Date endTicketDate = getDateFromDocument(ticketDoc, "endTicketDate");

            String employeeName = "Unknown"; // Placeholder, to be updated

            return new Ticket(id, category, approved, reason, startTicketDate, endTicketDate, employeeName);
        }
        return null;
    }

    // Metoda za dohvatanje zaposlenika po tiketu ID
    public Employee getEmployeeByTicketId(String ticketId) {
        Document employeeDoc = employeeCollection.find(Filters.eq("tickets", ticketId)).first();
        if (employeeDoc != null) {
            String id = employeeDoc.getString("_id");
            String name = employeeDoc.getString("name");
            String surname = employeeDoc.getString("surname");
            String email = employeeDoc.getString("email");
            String role = employeeDoc.getString("role");
            List<String> tickets = (List<String>) employeeDoc.get("tickets");

            return new Employee(id, name, surname, email, "", new ArrayList<>(tickets), role);
        }
        return null;
    }
}
