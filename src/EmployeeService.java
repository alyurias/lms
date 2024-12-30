// EmployeeService.java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeService {
    private final MongoCollection<Document> collection;

    public EmployeeService() {
        MongoDatabase database = MongoDBController.getInstance().getDatabase();
        this.collection = database.getCollection("kolekcija");
    }

    // Method for authenticating users
    public Employee authenticate(String email, String password) {
        Document doc = collection.find(Filters.eq("email", email)).first();

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

    public void addTicketToEmployee(String employeeId, Ticket ticket) {
        Document ticketDoc = new Document("_id", ticket.getId())
                .append("type", "ticket")
                .append("id", ticket.getId())
                .append("category", ticket.getCategory())
                .append("approved", ticket.isApproved())
                .append("reason", ticket.getReason()) // Dodavanje razloga
                .append("startTicketDate", ticket.getStartTicketDate())
                .append("endTicketDate", ticket.getEndTicketDate());

        collection.insertOne(ticketDoc);

        Document query = new Document("_id", employeeId);
        collection.updateOne(query, Updates.push("tickets", ticket.getId()));
    }

    // EmployeeService.java
// Method for updating a ticket
    public void updateTicket(Ticket ticket) {
        // Update the ticket document in the collection
        Document query = new Document("_id", ticket.getId());
        Document update = new Document("$set", new Document("category", ticket.getCategory())
                .append("approved", ticket.isApproved())
                .append("reason", ticket.getReason()) // Add reason field
                .append("startTicketDate", ticket.getStartTicketDate()) // Add start date field
                .append("endTicketDate", ticket.getEndTicketDate())); // Add end date field

        collection.updateOne(query, update);
    }

    // Method for deleting a ticket
    public void deleteTicket(String employeeId, String ticketId) {
        // Delete the ticket document from the collection
        Document query = new Document("_id", ticketId);
        collection.deleteOne(query);

        // Remove the ticket ID from the employee's tickets array
        Document employeeQuery = new Document("_id", employeeId);
        collection.updateOne(employeeQuery, Updates.pull("tickets", ticketId));
    }

    public List<Ticket> getTicketsForEmployee(String employeeId) {
        Document employeeDoc = collection.find(Filters.eq("_id", employeeId)).first();
        List<String> ticketIds = (List<String>) employeeDoc.get("tickets");

        List<Ticket> tickets = new ArrayList<>();
        for (String ticketId : ticketIds) {
            Document ticketDoc = collection.find(Filters.eq("id", ticketId)).first();
            if (ticketDoc != null) {
                String id = ticketDoc.getString("id");
                String category = ticketDoc.getString("category");
                boolean approved = ticketDoc.getBoolean("approved");
                String reason = ticketDoc.getString("reason"); // Dohvatanje razloga

                // Fetching start and end dates
                Date startTicketDate = ticketDoc.getDate("startTicketDate");
                Date endTicketDate = ticketDoc.getDate("endTicketDate");

                tickets.add(new Ticket(id, category, approved, reason, startTicketDate, endTicketDate));
            }
        }
        return tickets;
    }
}
