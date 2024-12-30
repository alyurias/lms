// EmployeeObrazac.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EmployeeObrazac {
    private JButton logoutButton;
    private JButton addRequestButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton; // New refresh button
    private JTable requestTable;
    private JLabel nameLabel;
    private JPanel employeePanel;

    private DefaultTableModel tableModel;
    private Employee employee;
    private EmployeeService employeeService;

    public EmployeeObrazac(Employee employee) {
        this.employee = employee;
        this.employeeService = new EmployeeService();
        nameLabel = new JLabel("Welcome " + employee.getName() + " " + employee.getSurname());

        addRequestButton = new JButton("Add Request");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        String[] columns = {"ID", "Category", "Approved", "Reason", "Start Date", "End Date"}; // Added Start Date and End Date columns
        tableModel = new DefaultTableModel(columns, 0);
        requestTable = new JTable(tableModel);

        // Set table appearance
        JScrollPane scrollPane = new JScrollPane(requestTable);
        employeePanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameLabel, BorderLayout.NORTH); // Add nameLabel at the top

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRequestButton); // Add buttons for CRUD operations
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton); // Add refresh button

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton); // Add logoutButton below the table

        employeePanel.add(topPanel, BorderLayout.NORTH); // Add top panel at the top
        employeePanel.add(buttonPanel, BorderLayout.NORTH); // Add button panel below the nameLabel
        employeePanel.add(scrollPane, BorderLayout.CENTER); // Add table below the button panel
        employeePanel.add(bottomPanel, BorderLayout.SOUTH); // Add logout button below the table

        // Load initial tickets for the employee
        loadEmployeeTickets();

        addRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] categories = {"Obicni", "Redovni", "Zdravstveni"};
                JComboBox<String> categoryComboBox = new JComboBox<>(categories);
                JTextField reasonField = new JTextField(20);

                // Date pickers for start and end dates
                JComboBox<Integer> startDayComboBox = createDayComboBox();
                JComboBox<Integer> startMonthComboBox = createMonthComboBox();
                JComboBox<Integer> startYearComboBox = createYearComboBox();
                JComboBox<Integer> endDayComboBox = createDayComboBox();
                JComboBox<Integer> endMonthComboBox = createMonthComboBox();
                JComboBox<Integer> endYearComboBox = createYearComboBox();

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(new JLabel("Category:"));
                panel.add(categoryComboBox);
                panel.add(new JLabel("Reason:"));
                panel.add(reasonField);
                panel.add(new JLabel("Start Date:"));
                panel.add(startDayComboBox);
                panel.add(startMonthComboBox);
                panel.add(startYearComboBox);
                panel.add(new JLabel("End Date:"));
                panel.add(endDayComboBox);
                panel.add(endMonthComboBox);
                panel.add(endYearComboBox);

                int option = JOptionPane.showConfirmDialog(null, panel, "Add Ticket", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String category = (String) categoryComboBox.getSelectedItem();
                    String reason = reasonField.getText().trim();
                    Date startDate = createDateFromComboBoxes(startDayComboBox, startMonthComboBox, startYearComboBox);
                    Date endDate = createDateFromComboBoxes(endDayComboBox, endMonthComboBox, endYearComboBox);

                    if (!reason.isEmpty() && startDate != null && endDate != null) {
                        Ticket newTicket = new Ticket(category, false, reason, startDate, endDate);
                        employeeService.addTicketToEmployee(employee.getId(), newTicket);
                        loadEmployeeTickets();
                    } else {
                        JOptionPane.showMessageDialog(null, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = requestTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a ticket to edit.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String ticketId = (String) tableModel.getValueAt(selectedRow, 0);
                Ticket ticketToEdit = null;
                List<Ticket> tickets = employeeService.getTicketsForEmployee(employee.getId());
                for (Ticket ticket : tickets) {
                    if (ticket.getId().equals(ticketId)) {
                        ticketToEdit = ticket;
                        break;
                    }
                }

                if (ticketToEdit == null) {
                    JOptionPane.showMessageDialog(null, "Ticket not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] categories = {"Obicni", "Redovni", "Zdravstveni"};
                JComboBox<String> categoryComboBox = new JComboBox<>(categories);
                categoryComboBox.setSelectedItem(ticketToEdit.getCategory());

                JTextField reasonField = new JTextField(ticketToEdit.getReason(), 20);

                // Date pickers for start and end dates
                JComboBox<Integer> startDayComboBox = createDayComboBox(ticketToEdit.getStartTicketDate().getDate());
                JComboBox<Integer> startMonthComboBox = createMonthComboBox(ticketToEdit.getStartTicketDate().getMonth());
                JComboBox<Integer> startYearComboBox = createYearComboBox(ticketToEdit.getStartTicketDate().getYear() + 1900);
                JComboBox<Integer> endDayComboBox = createDayComboBox(ticketToEdit.getEndTicketDate().getDate());
                JComboBox<Integer> endMonthComboBox = createMonthComboBox(ticketToEdit.getEndTicketDate().getMonth());
                JComboBox<Integer> endYearComboBox = createYearComboBox(ticketToEdit.getEndTicketDate().getYear() + 1900);

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(new JLabel("Category:"));
                panel.add(categoryComboBox);
                panel.add(new JLabel("Reason:"));
                panel.add(reasonField);
                panel.add(new JLabel("Start Date:"));
                panel.add(startDayComboBox);
                panel.add(startMonthComboBox);
                panel.add(startYearComboBox);
                panel.add(new JLabel("End Date:"));
                panel.add(endDayComboBox);
                panel.add(endMonthComboBox);
                panel.add(endYearComboBox);

                int option = JOptionPane.showConfirmDialog(null, panel, "Edit Ticket", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String updatedCategory = (String) categoryComboBox.getSelectedItem();
                    String updatedReason = reasonField.getText().trim();
                    Date updatedStartDate = createDateFromComboBoxes(startDayComboBox, startMonthComboBox, startYearComboBox);
                    Date updatedEndDate = createDateFromComboBoxes(endDayComboBox, endMonthComboBox, endYearComboBox);

                    if (!updatedReason.isEmpty() && updatedStartDate != null && updatedEndDate != null) {
                        ticketToEdit.setCategory(updatedCategory);
                        ticketToEdit.setReason(updatedReason);
                        ticketToEdit.setStartTicketDate(updatedStartDate);
                        ticketToEdit.setEndTicketDate(updatedEndDate);

                        employeeService.updateTicket(ticketToEdit);
                        loadEmployeeTickets();
                    } else {
                        JOptionPane.showMessageDialog(null, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = requestTable.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this ticket?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String ticketId = (String) tableModel.getValueAt(selectedRow, 0);

                        // Remove the row from the table
                        tableModel.removeRow(selectedRow);

                        // Delete the ticket from MongoDB
                        employeeService.deleteTicket(employee.getId(), ticketId);

                        // Optionally reload tickets to ensure consistency
                        loadEmployeeTickets();
                    }
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reload tickets for the employee
                loadEmployeeTickets();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame loginFrame = new JFrame("Login");
                loginFrame.setContentPane(new Login().getLoginPanel());
                loginFrame.pack();
                loginFrame.setVisible(true);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(employeePanel);
                topFrame.dispose(); // Close current EmployeeObrazac window
                loginFrame.setVisible(true); // Show login frame again
            }
        });
    }

    public JPanel getEmployeePanel() {
        return employeePanel;
    }

    private void loadEmployeeTickets() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Ticket> tickets = employeeService.getTicketsForEmployee(employee.getId());
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getCategory(),
                    ticket.isApproved(),
                    ticket.getReason(),
                    ticket.getStartTicketDate(),
                    ticket.getEndTicketDate()
            });
        }
    }

    private JComboBox<Integer> createDayComboBox() {
        Integer[] days = new Integer[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = i;
        }
        return new JComboBox<>(days);
    }

    private JComboBox<Integer> createDayComboBox(int selectedDay) {
        JComboBox<Integer> comboBox = createDayComboBox();
        comboBox.setSelectedItem(selectedDay);
        return comboBox;
    }

    private JComboBox<Integer> createMonthComboBox() {
        Integer[] months = new Integer[12];
        for (int i = 1; i <= 12; i++) {
            months[i - 1] = i;
        }
        return new JComboBox<>(months);
    }

    private JComboBox<Integer> createMonthComboBox(int selectedMonth) {
        JComboBox<Integer> comboBox = createMonthComboBox();
        comboBox.setSelectedItem(selectedMonth + 1); // Month is 0-based
        return comboBox;
    }

    private JComboBox<Integer> createYearComboBox() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = {currentYear, currentYear + 1};
        return new JComboBox<>(years);
    }

    private JComboBox<Integer> createYearComboBox(int selectedYear) {
        JComboBox<Integer> comboBox = createYearComboBox();
        comboBox.setSelectedItem(selectedYear);
        return comboBox;
    }

    private Date createDateFromComboBoxes(JComboBox<Integer> dayComboBox, JComboBox<Integer> monthComboBox, JComboBox<Integer> yearComboBox) {
        int day = (int) dayComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem() - 1; // Month is 0-based in Calendar
        int year = (int) yearComboBox.getSelectedItem();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }
}
