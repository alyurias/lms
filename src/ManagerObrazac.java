import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManagerObrazac {
    private JPanel mainPanel;
    private JButton logoutButton;
    private JTable ticketsTable;
    private JLabel nameLabel;
    private JButton searchButton;
    private JButton refreshButton;
    private Employee manager;
    private EmployeeService employeeService;

    private DefaultTableModel tableModel;

    public ManagerObrazac(Employee manager) {
        this.manager = manager;
        this.employeeService = new EmployeeService();
        mainPanel = new JPanel(new BorderLayout());

        nameLabel = new JLabel("          Welcome " + manager.getName() + " " + manager.getSurname());
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        String[] columns = {"Ticket ID", "Employee Name", "Start Date", "Category", "Approved"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        ticketsTable = new JTable(tableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(ticketsTable);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameLabel, BorderLayout.WEST);
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.add(searchButton);
        topRightPanel.add(refreshButton);
        topPanel.add(topRightPanel, BorderLayout.EAST);

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

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTickets();
                JOptionPane.showMessageDialog(mainPanel, "Unosi su uspješno osvježeni.", "Osvježenje", JOptionPane.INFORMATION_MESSAGE);
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

        ticketsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = ticketsTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String ticketId = (String) tableModel.getValueAt(selectedRow, 0);
                        showTicketDetails(ticketId);
                        ticketsTable.setSelectionBackground(Color.LIGHT_GRAY); // Change background color of the selected row
                    }
                }
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
            JOptionPane.showMessageDialog(mainPanel, "Pretraga je uspješno obavljena.", "Pretraga", JOptionPane.INFORMATION_MESSAGE);
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
                    ticket.getCategory(), // Adding the Category column
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
                    ticket.getCategory(), // Adding the Category column
                    ticket.getApproved()
            });
        }
    }

    private void showTicketDetails(String ticketId) {
        Ticket ticket = employeeService.getTicketById(ticketId);
        Employee employee = employeeService.getEmployeeByTicketId(ticketId);

        if (ticket != null && employee != null) {
            JTextArea textArea = new JTextArea(15, 30);
            textArea.setText(
                    "Ticket ID: " + ticket.getId() + "\n" +
                            "Category: " + ticket.getCategory() + "\n" +
                            "Approved: " + ticket.getApproved() + "\n" +
                            "Reason: " + ticket.getReason() + "\n" +
                            "Start Date: " + ticket.getStartTicketDate() + "\n" +
                            "End Date: " + ticket.getEndTicketDate() + "\n\n" +
                            "Employee Name: " + employee.getName() + " " + employee.getSurname() + "\n" +
                            "Employee Email: " + employee.getEmail() + "\n" +
                            "Employee Role: " + employee.getRole()
            );
            textArea.setEditable(false);

            String[] statusOptions = {"Na cekanju", "Odobreno", "Odbijeno"};
            JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
            statusComboBox.setSelectedItem(ticket.getApproved());

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
            panel.add(statusComboBox, BorderLayout.SOUTH);

            int option = JOptionPane.showConfirmDialog(null, panel, "Ticket Details", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String updatedStatus = (String) statusComboBox.getSelectedItem();
                ticket.setApproved(updatedStatus);
                employeeService.updateTicket(ticket);
                loadTickets();
                JOptionPane.showMessageDialog(mainPanel, "Status tiketa je uspješno ažuriran.", "Ažuriranje", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
