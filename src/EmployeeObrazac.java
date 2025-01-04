import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EmployeeObrazac {
    private Employee employee;
    private EmployeeService employeeService;
    private JLabel nameLabel;
    private JButton addRequestButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton logoutButton;
    private DefaultTableModel tableModel;
    private JTable requestTable;
    private JPanel employeePanel;

    public EmployeeObrazac(Employee employee) {
        this.employee = employee;
        this.employeeService = new EmployeeService();
        nameLabel = new JLabel("Welcome " + employee.getName() + " " + employee.getSurname());

        addRequestButton = new JButton("Add Request");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");

        String[] columns = {"ID", "Category", "Approved", "Reason", "Start Date", "End Date"};
        tableModel = new DefaultTableModel(columns, 0);
        requestTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(requestTable);
        employeePanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addRequestButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        employeePanel.add(topPanel, BorderLayout.NORTH);
        employeePanel.add(centerPanel, BorderLayout.CENTER);
        employeePanel.add(bottomPanel, BorderLayout.SOUTH);

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
                        Ticket newTicket = new Ticket(category, "Na cekanju", reason, startDate, endDate, employee.getName() + " " + employee.getSurname());
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
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(ticketToEdit.getStartTicketDate());
                JComboBox<Integer> startDayComboBox = createDayComboBox(startCal.get(Calendar.DAY_OF_MONTH));
                JComboBox<Integer> startMonthComboBox = createMonthComboBox(startCal.get(Calendar.MONTH));
                JComboBox<Integer> startYearComboBox = createYearComboBox(startCal.get(Calendar.YEAR));
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(ticketToEdit.getEndTicketDate());
                JComboBox<Integer> endDayComboBox = createDayComboBox(endCal.get(Calendar.DAY_OF_MONTH));
                JComboBox<Integer> endMonthComboBox = createMonthComboBox(endCal.get(Calendar.MONTH));
                JComboBox<Integer> endYearComboBox = createYearComboBox(endCal.get(Calendar.YEAR));

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

                        tableModel.removeRow(selectedRow);

                        employeeService.deleteTicket(employee.getId(), ticketId);

                        loadEmployeeTickets();
                    }
                }
            }
        });

        loadEmployeeTickets();
    }

    private JComboBox<Integer> createDayComboBox() {
        return createDayComboBox(1);
    }

    private JComboBox<Integer> createDayComboBox(int selectedDay) {
        Integer[] days = new Integer[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = i;
        }
        JComboBox<Integer> comboBox = new JComboBox<>(days);
        comboBox.setSelectedItem(selectedDay);
        return comboBox;
    }

    private JComboBox<Integer> createMonthComboBox() {
        return createMonthComboBox(0);
    }

    private JComboBox<Integer> createMonthComboBox(int selectedMonth) {
        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) {
            months[i] = i + 1; // Mjeseci od 1 do 12
        }
        JComboBox<Integer> comboBox = new JComboBox<>(months);
        comboBox.setSelectedItem(selectedMonth + 1); // +1 jer je mjesec 0-indeksiran
        return comboBox;
    }

    private JComboBox<Integer> createYearComboBox() {
        return createYearComboBox(Calendar.getInstance().get(Calendar.YEAR));
    }

    private JComboBox<Integer> createYearComboBox(int selectedYear) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) {
            years[i] = currentYear - i; // Zadnjih 10 godina
        }
        JComboBox<Integer> comboBox = new JComboBox<>(years);
        comboBox.setSelectedItem(selectedYear);
        return comboBox;
    }

    private Date createDateFromComboBoxes(JComboBox<Integer> dayComboBox, JComboBox<Integer> monthComboBox, JComboBox<Integer> yearComboBox) {
        int day = (Integer) dayComboBox.getSelectedItem();
        int month = (Integer) monthComboBox.getSelectedItem() - 1; // Mjesec je 0-indeksiran
        int year = (Integer) yearComboBox.getSelectedItem();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void loadEmployeeTickets() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Ticket> tickets = employeeService.getTicketsForEmployee(employee.getId());
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getCategory(),
                    ticket.getApproved(),
                    ticket.getReason(),
                    ticket.getStartTicketDate(),
                    ticket.getEndTicketDate()
            });
        }
    }

    public JPanel getEmployeePanel() {
        return employeePanel;
    }
}
