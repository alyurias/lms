import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Employee {
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String role;
    private List<String> tickets;

    public Employee(String id, String name, String surname, String email, String password, String role, List<String> tickets) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tickets = tickets != null ? tickets : new ArrayList<>();
    }

    public Employee(String name, String surname, String email, String password, String role) {
        this(UUID.randomUUID().toString(), name, surname, email, password, role, new ArrayList<>());
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getTickets() {
        return tickets;
    }

    public void setTickets(List<String> tickets) {
        this.tickets = tickets;
    }
}
