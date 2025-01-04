import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManagerObrazac {
    private JPanel mainPanel;
    private JButton logoutButton;
    private JTable ticketsTable;
    private JLabel nameLabel;
    private JButton searchButton;
    private Employee manager;
    private EmployeeService employeeService;

    private DefaultTableModel tableModel;

    public ManagerObrazac(Employee manager) {
        this.manager = manager;
        this.employeeService = new EmployeeService();
        mainPanel = new JPanel(new BorderLayout());

        nameLabel = new JLabel("Welcome " + manager.getName() + " " + manager.getSurname());
        searchButton = new JButton("Search");
        logoutButton = new JButton("Logout");

        String[] columns = {"Ticket ID", "Employee Name", "Start Date", "Approved"};
        tableModel = new DefaultTableModel(columns, 0);
        ticketsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(ticketsTable);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameLabel, BorderLayout.WEST);
        topPanel.add(searchButton, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close Manager Dashboard
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                topFrame.dispose();

                // Show Login screen again
                JFrame frame = new JFrame("Login");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new Login().getLoginPanel());
                frame.setSize(500, 300); // Increase the size of the frame
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true);
            }
        });

        loadTickets();
    }

    private void showSearchDialog() {
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);

        JCheckBox obicniCheckBox = new JCheckBox("Obicni");
        JCheckBox redovniCheckBox = new JCheckBox("Redovni");
        JCheckBox zdravstveniCheckBox = new JCheckBox("Zdravstveni");

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.add(new JLabel("First Name:"));
        searchPanel.add(firstNameField);
        searchPanel.add(new JLabel("Last Name:"));
        searchPanel.add(lastNameField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(obicniCheckBox);
        searchPanel.add(redovniCheckBox);
        searchPanel.add(zdravstveniCheckBox);

        int option = JOptionPane.showConfirmDialog(null, searchPanel, "Search Tickets", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            boolean obicniSelected = obicniCheckBox.isSelected();
            boolean redovniSelected = redovniCheckBox.isSelected();
            boolean zdravstveniSelected = zdravstveniCheckBox.isSelected();

            searchTickets(firstName, lastName, obicniSelected, redovniSelected, zdravstveniSelected);
        }
    }

    private void loadTickets() {
        List<Ticket> tickets = employeeService.getAllTickets();
        tableModel.setRowCount(0); // Clear existing rows
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getEmployeeName(),
                    ticket.getStartTicketDate(),
                    ticket.getApproved()
            });
        }
    }

    private void searchTickets(String firstName, String lastName, boolean obicni, boolean redovni, boolean zdravstveni) {
        List<Ticket> filteredTickets = employeeService.searchTickets(firstName, lastName, obicni, redovni, zdravstveni);
        tableModel.setRowCount(0); // Clear existing rows
        for (Ticket ticket : filteredTickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getEmployeeName(),
                    ticket.getStartTicketDate(),
                    ticket.getApproved()
            });
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
