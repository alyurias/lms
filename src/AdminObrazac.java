import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.regex.Pattern;

public class AdminObrazac {
    private JTable table1;
    private JButton logoutButton;
    private JButton refreshButton;
    private JButton deleteButton;
    private JButton addButton;
    private JButton editButton;
    private JButton searchButton;
    private JPanel mainPanel;
    private JLabel nameLabel;

    private DefaultTableModel tableModel;
    private EmployeeService employeeService;
    private Employee admin;

    public AdminObrazac(Employee admin) {
        this.admin = admin;
        employeeService = new EmployeeService();

        nameLabel = new JLabel("          Dobrodošli, " + admin.getName() + " " + admin.getSurname());

        String[] columns = {"ID", "Ime", "Prezime", "Email", "Uloga" +
                ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        table1 = new JTable(tableModel);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table1);

        mainPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton = new JButton("Dodaj"));
        buttonPanel.add(editButton = new JButton("Uredi"));
        buttonPanel.add(deleteButton = new JButton("Obriši"));
        buttonPanel.add(searchButton = new JButton("Pretraži"));
        buttonPanel.add(refreshButton = new JButton("Osvježi"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton = new JButton("Odjava"));

        mainPanel.add(nameLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadEmployees();
                table1.clearSelection(); // Deselect selected row
                JOptionPane.showMessageDialog(mainPanel, "Unosi su uspješno osvježeni.", "Osvježenje", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddDialog();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Molim Vas odaberite radnika za uređivanje", "Greška", JOptionPane.ERROR_MESSAGE);
                } else {
                    String employeeId = (String) tableModel.getValueAt(selectedRow, 0);
                    showEditDialog(employeeId);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Molim Vas odaberite zaposlenika", "Greška", JOptionPane.ERROR_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(null, "Da li sigurno želite obrisati ovog zaposlenika?", "Potvrdi brisanje", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String employeeId = (String) tableModel.getValueAt(selectedRow, 0);
                        employeeService.deleteEmployee(employeeId);
                        loadEmployees();
                        JOptionPane.showMessageDialog(mainPanel, "Korisnik je uspješno obrisan.", "Brisanje", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table1.getSelectedRow();
                    if (selectedRow != -1) {
                        String employeeId = (String) tableModel.getValueAt(selectedRow, 0);
                        showEmployeeTicketsDialog(employeeId);
                    }
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close Admin Dashboard
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                topFrame.dispose();

                // Show Login screen again
                JFrame frame = new JFrame("Prijava");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new Login().getLoginPanel());
                frame.setSize(500, 300); // Increase the size of the frame
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true);
            }
        });

        loadEmployees();
    }

    private void showSearchDialog() {
        JTextField nameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.add(new JLabel("Ime:"));
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("Prezime:"));
        searchPanel.add(surnameField);
        searchPanel.add(new JLabel("Email:"));
        searchPanel.add(emailField);

        int option = JOptionPane.showConfirmDialog(null, searchPanel, "Pretraži zapslenike", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String email = emailField.getText().trim();

            searchEmployees(name, surname, email);
            JOptionPane.showMessageDialog(mainPanel, "Pretraga je uspješno obavljena.", "Pretraga", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchEmployees(String name, String surname, String email) {
        List<Employee> filteredEmployees = employeeService.searchEmployees(name, surname, email);
        tableModel.setRowCount(0); // Clear existing rows
        for (Employee employee : filteredEmployees) {
            tableModel.addRow(new Object[]{
                    employee.getId(),
                    employee.getName(),
                    employee.getSurname(),
                    employee.getEmail(),
                    employee.getRole()
            });
        }
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField(20);
        JTextField surnameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JRadioButton employeeRadio = new JRadioButton("Zaposlenik");
        JRadioButton managerRadio = new JRadioButton("Menadžer");
        JRadioButton adminRadio = new JRadioButton("Admin");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(employeeRadio);
        roleGroup.add(managerRadio);
        roleGroup.add(adminRadio);

        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
        addPanel.add(new JLabel("Ime:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Prezime:"));
        addPanel.add(surnameField);
        addPanel.add(new JLabel("Email:"));
        addPanel.add(emailField);
        addPanel.add(new JLabel("Lozinka:"));
        addPanel.add(passwordField);
        addPanel.add(new JLabel("Uloga:"));
        addPanel.add(employeeRadio);
        addPanel.add(managerRadio);
        addPanel.add(adminRadio);

        while (true) {
            int option = JOptionPane.showConfirmDialog(null, addPanel, "Dodaj zaposlenika", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                break;
            }

            String name = formatName(nameField.getText().trim());
            String surname = formatName(surnameField.getText().trim());
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = employeeRadio.isSelected() ? "Zaposlenik" : managerRadio.isSelected() ? "Menadžer" : "Admin";

            if (!isValidName(name)) {
                JOptionPane.showMessageDialog(null, "Neispravan format imena.", "Greška", JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                continue;
            }
            if (!isValidName(surname)) {
                JOptionPane.showMessageDialog(null, "Neispravan format prezimena.", "Greška", JOptionPane.ERROR_MESSAGE);
                surnameField.requestFocus();
                continue;
            }
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(null, "Neispravan format email-a.", "Greška", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                continue;
            }
            if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(null, "Neispravan format lozinke.", "Greška", JOptionPane.ERROR_MESSAGE);
                passwordField.requestFocus();
                continue;
            }

            Employee newEmployee = new Employee(name, surname, email, password, role);
            employeeService.addEmployee(newEmployee);
            loadEmployees();
            JOptionPane.showMessageDialog(mainPanel, "Novi korisnik je uspješno dodan.", "Dodavanje", JOptionPane.INFORMATION_MESSAGE);
            break;
        }
    }

    private void showEditDialog(String employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        if (employee != null) {
            JTextField nameField = new JTextField(employee.getName(), 20);
            JTextField surnameField = new JTextField(employee.getSurname(), 20);
            JTextField emailField = new JTextField(employee.getEmail(), 20);
            JPasswordField passwordField = new JPasswordField(20);
            JRadioButton employeeRadio = new JRadioButton("Zaposlenik");
            JRadioButton managerRadio = new JRadioButton("Menadžer");
            JRadioButton adminRadio = new JRadioButton("Admin");
            ButtonGroup roleGroup = new ButtonGroup();
            roleGroup.add(employeeRadio);
            roleGroup.add(managerRadio);
            roleGroup.add(adminRadio);

            if (employee.getRole().equals("Zaposlenik")) {
                employeeRadio.setSelected(true);
            } else if (employee.getRole().equals("Menadžer")) {
                managerRadio.setSelected(true);
            } else {
                adminRadio.setSelected(true);
            }

            JPanel editPanel = new JPanel();
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            editPanel.add(new JLabel("Ime:"));
            editPanel.add(nameField);
            editPanel.add(new JLabel("Prezime:"));
            editPanel.add(surnameField);
            editPanel.add(new JLabel("Email:"));
            editPanel.add(emailField);
            editPanel.add(new JLabel("Lozinka: (leave blank to keep current)"));
            editPanel.add(passwordField);
            editPanel.add(new JLabel("Uloga:"));
            editPanel.add(employeeRadio);
            editPanel.add(managerRadio);
            editPanel.add(adminRadio);

            int option = JOptionPane.showConfirmDialog(null, editPanel, "Uredi zaposlenika", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String updatedName = formatName(nameField.getText().trim());
                String updatedSurname = formatName(surnameField.getText().trim());
                String updatedEmail = emailField.getText().trim();
                String updatedPassword = new String(passwordField.getPassword()).trim();
                String updatedRole = employeeRadio.isSelected() ? "Zaposlenik" : managerRadio.isSelected() ? "Menadžer" : "Admin";

                if (!isValidName(updatedName)) {
                    JOptionPane.showMessageDialog(null, "Neispravan format imena.", "Greška", JOptionPane.ERROR_MESSAGE);
                    nameField.requestFocus();
                    return;
                }
                if (!isValidName(updatedSurname)) {
                    JOptionPane.showMessageDialog(null, "Neispravan format prezimena.", "Greška", JOptionPane.ERROR_MESSAGE);
                    surnameField.requestFocus();
                    return;
                }
                if (!isValidEmail(updatedEmail)) {
                    JOptionPane.showMessageDialog(null, "Neispravan format email-a.", "Greška", JOptionPane.ERROR_MESSAGE);
                    emailField.requestFocus();
                    return;
                }

                employee.setName(updatedName);
                employee.setSurname(updatedSurname);
                employee.setEmail(updatedEmail);
                employee.setRole(updatedRole);

                if (!updatedPassword.isEmpty()) {
                    if (isValidPassword(updatedPassword)) {
                        employee.setPassword(updatedPassword);
                    } else {
                        JOptionPane.showMessageDialog(null, "Neispravan format lozinke", "Greška", JOptionPane.ERROR_MESSAGE);
                        passwordField.requestFocus();
                        return;
                    }
                }

                employeeService.updateEmployee(employee);
                loadEmployees();
                JOptionPane.showMessageDialog(mainPanel, "Korisnik je uspješno ažuriran.", "Ažuriranje", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showEmployeeTicketsDialog(String employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            List<String> tickets = employee.getTickets();
            if (tickets.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Ovaj zaposlenik nema tikete.", "Tiketi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JPanel ticketPanel = new JPanel();
            ticketPanel.setLayout(new BoxLayout(ticketPanel, BoxLayout.Y_AXIS));

            for (String ticketId : tickets) {
                Ticket ticket = employeeService.getTicketById(ticketId);
                if (ticket != null) {
                    JPanel ticketInfoPanel = new JPanel(new GridLayout(1, 3));
                    ticketInfoPanel.add(new JLabel("ID: " + ticket.getId()));
                    ticketInfoPanel.add(new JLabel("Kategorija: " + ticket.getCategory()));
                    ticketInfoPanel.add(new JLabel("Razlog: " + ticket.getReason()));
                    ticketPanel.add(ticketInfoPanel);
                }
            }

            JButton clearButton = new JButton("Izbrisi sve tikete");
            clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Da li sigurno zelis obrisati sve tikete?", "Potvrdi brisanje", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        employeeService.clearAllTicketsForEmployee(employeeId);
                        loadEmployees();
                        JOptionPane.showMessageDialog(mainPanel, "Svi tiekti su obrisani", "Obriši tikete", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });

            ticketPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space before the button
            ticketPanel.add(clearButton);

            JOptionPane.showMessageDialog(mainPanel, ticketPanel, "Tiketi zaposlenika", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void loadEmployees() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                    employee.getId(),
                    employee.getName(),
                    employee.getSurname(),
                    employee.getEmail(),
                    employee.getRole()
            });
        }
    }

    private String formatName(String name) {
        if (name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    private boolean isValidName(String name) {
        return name.matches("^[A-Z][a-z]*$");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        return hasUpperCase && hasLowerCase && hasSpecialChar;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}