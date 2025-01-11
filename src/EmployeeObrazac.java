import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
        nameLabel = new JLabel("          Dobrodošli, " + employee.getName() + " " + employee.getSurname());

        addRequestButton = new JButton("Dodaj zahtjev");
        editButton = new JButton("Uredi");
        deleteButton = new JButton("Izbriši");
        refreshButton = new JButton("Osvježi");
        logoutButton = new JButton("Odjava");

        String[] columns = {"ID", "Kategorija", "Status", "Razlog", "Početni datum", "Završni datum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        requestTable = new JTable(tableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set custom renderer for the "Status" column
        requestTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

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
                String[] categories = {"Obični", "Redovni", "Zdravstveni"};
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
                panel.add(new JLabel("Kategorija:"));
                panel.add(categoryComboBox);
                panel.add(new JLabel("Razlog:"));
                panel.add(reasonField);
                panel.add(new JLabel("Početni datum:"));
                panel.add(startDayComboBox);
                panel.add(startMonthComboBox);
                panel.add(startYearComboBox);
                panel.add(new JLabel("Završni datum:"));
                panel.add(endDayComboBox);
                panel.add(endMonthComboBox);
                panel.add(endYearComboBox);

                int option = JOptionPane.showConfirmDialog(null, panel, "Dodaj tiket", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String category = (String) categoryComboBox.getSelectedItem();
                    String reason = reasonField.getText().trim();
                    Date startDate = createDateFromComboBoxes(startDayComboBox, startMonthComboBox, startYearComboBox);
                    Date endDate = createDateFromComboBoxes(endDayComboBox, endMonthComboBox, endYearComboBox);

                    if (!reason.isEmpty() && startDate != null && endDate != null) {
                        Ticket newTicket = new Ticket(category, "Na čekanju", reason, startDate, endDate, employee.getName() + " " + employee.getSurname());
                        employeeService.addTicketToEmployee(employee.getId(), newTicket);
                        loadEmployeeTickets();
                        JOptionPane.showMessageDialog(employeePanel, "Novi zahtjev je uspješno dodan.", "Dodavanje", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Sve mora biti popunjeno.", "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Other buttons' action listeners are similar, no need to modify those

        loadEmployeeTickets();
    }

    // Custom renderer for the "Status" column to change background color based on the status
    class StatusCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            if (value != null) {
                String status = value.toString();
                if (status.equals("Na čekanju")) {
                    setBackground(new Color(255, 255, 2)); // Pastel Yellow
                } else if (status.equals("Odobreno")) {
                    setBackground(new Color(115, 255, 0)); // Pastel Green
                } else if (status.equals("Odbijeno")) {
                    setBackground(new Color(255, 47, 0)); // Pastel Red
                } else {
                    setBackground(Color.WHITE); // Default color
                }

            }
            setOpaque(true); // Ensure the background color is visible
            return this;
        }
    }

    // Helper methods for creating combo boxes and dates
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
            months[i] = i + 1; // Months from 1 to 12
        }
        JComboBox<Integer> comboBox = new JComboBox<>(months);
        comboBox.setSelectedItem(selectedMonth + 1); // +1 because month is 0-indexed
        return comboBox;
    }

    private JComboBox<Integer> createYearComboBox() {
        return createYearComboBox(Calendar.getInstance().get(Calendar.YEAR));
    }

    private JComboBox<Integer> createYearComboBox(int selectedYear) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[10];
        for (int i = 0; i < 10; i++) {
            years[i] = currentYear - i; // Last 10 years
        }
        JComboBox<Integer> comboBox = new JComboBox<>(years);
        comboBox.setSelectedItem(selectedYear);
        return comboBox;
    }

    private Date createDateFromComboBoxes(JComboBox<Integer> dayComboBox, JComboBox<Integer> monthComboBox, JComboBox<Integer> yearComboBox) {
        int day = (Integer) dayComboBox.getSelectedItem();
        int month = (Integer) monthComboBox.getSelectedItem() - 1; // Month is 0-indexed
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
