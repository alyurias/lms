import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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

        nameLabel = new JLabel("          Dobrodošli, " + manager.getName() + " " + manager.getSurname());
        searchButton = new JButton("Pretraži");
        refreshButton = new JButton("Osvježi");
        logoutButton = new JButton("Odjava");

        String[] columns = {"Ticket ID", "Ime zaposlenika", "Početni datum", "Kategorija", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketsTable = new JTable(tableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.setDefaultRenderer(Object.class, new StatusCellRenderer());

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
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                topFrame.dispose();
                JFrame frame = new JFrame("Prijava");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new Login().getLoginPanel());
                frame.setSize(500, 300);
                frame.setLocationRelativeTo(null);
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
                        ticketsTable.setSelectionBackground(Color.LIGHT_GRAY);
                    }
                }
            }
        });

        loadTickets();
    }

    private void showSearchDialog() {
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);

        JCheckBox obicniCheckBox = new JCheckBox("Obični");
        JCheckBox redovniCheckBox = new JCheckBox("Redovni");
        JCheckBox zdravstveniCheckBox = new JCheckBox("Zdravstveni");

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.add(new JLabel("Ime:"));
        searchPanel.add(firstNameField);
        searchPanel.add(new JLabel("Prezime:"));
        searchPanel.add(lastNameField);
        searchPanel.add(new JLabel("Kategorija:"));
        searchPanel.add(obicniCheckBox);
        searchPanel.add(redovniCheckBox);
        searchPanel.add(zdravstveniCheckBox);

        int option = JOptionPane.showConfirmDialog(null, searchPanel, "Pretraži tikete", JOptionPane.OK_CANCEL_OPTION);
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
        tableModel.setRowCount(0);
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getEmployeeName(),
                    ticket.getStartTicketDate(),
                    ticket.getCategory(),
                    ticket.getApproved()
            });
        }
    }

    private void searchTickets(String firstName, String lastName, boolean obicni, boolean redovni, boolean zdravstveni) {
        List<Ticket> filteredTickets = employeeService.searchTickets(firstName, lastName, obicni, redovni, zdravstveni);
        tableModel.setRowCount(0);
        for (Ticket ticket : filteredTickets) {
            tableModel.addRow(new Object[]{
                    ticket.getId(),
                    ticket.getEmployeeName(),
                    ticket.getStartTicketDate(),
                    ticket.getCategory(),
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
                            "Kategorija: " + ticket.getCategory() + "\n" +
                            "Status: " + ticket.getApproved() + "\n" +
                            "Razlog: " + ticket.getReason() + "\n" +
                            "Početni datum: " + ticket.getStartTicketDate() + "\n" +
                            "Završni datum: " + ticket.getEndTicketDate() + "\n\n" +
                            "Ime zaposlenika: " + employee.getName() + " " + employee.getSurname() + "\n" +
                            "Email zaposlenika: " + employee.getEmail() + "\n" +
                            "Uloga zaposlenika: " + employee.getRole()
            );
            textArea.setEditable(false);

            String[] statusOptions = {"Na čekanju", "Odobreno", "Odbijeno"};
            JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
            statusComboBox.setSelectedItem(ticket.getApproved());

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
            panel.add(statusComboBox, BorderLayout.SOUTH);

            int option = JOptionPane.showConfirmDialog(null, panel, "Detalji o tiketu", JOptionPane.OK_CANCEL_OPTION);
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

    private class StatusCellRenderer extends JLabel implements TableCellRenderer {
        public StatusCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (value != null) {
                switch (value.toString()) {
                    case "Na čekanju":
                        setForeground(new Color(255, 203, 31));
                        break;
                    case "Odobreno":
                        setForeground(new Color(144, 238, 144));
                        break;
                    case "Odbijeno":
                        setForeground(new Color(255, 102, 102));
                        break;
                }
            }

            return this;
        }
    }
}
