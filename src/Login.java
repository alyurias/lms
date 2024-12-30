// Login.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JTextField email;
    private JPasswordField password;
    private JButton submitButton;
    private JPanel loginPanel;
    private EmployeeService employeeService;

    public Login() {
        employeeService = new EmployeeService();

        email = new JTextField(20);
        password = new JPasswordField(20);
        submitButton = new JButton("Submit");

        // Increase font size for components
        email.setFont(new Font("Arial", Font.PLAIN, 18));
        password.setFont(new Font("Arial", Font.PLAIN, 18));
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));

        // Layout for the login panel
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(email, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailText = email.getText();
                String passwordText = new String(password.getPassword());

                Employee employee = employeeService.authenticate(emailText, passwordText);
                if (employee != null) {
                    String role = employee.getRole();
                    switch (role) {
                        case "Employee":
                            showEmployeePanel(employee);
                            break;
                        case "Manager":
                            showManagerPanel();
                            break;
                        case "Admin":
                            showAdminPanel();
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid role");
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password");
                }
            }
        });
    }

    private void showEmployeePanel(Employee employee) {
        JFrame frame = new JFrame("Employee Dashboard");
        frame.setContentPane(new EmployeeObrazac(employee).getEmployeePanel());
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
        // Hide login frame
        SwingUtilities.getWindowAncestor(loginPanel).setVisible(false);
    }

    private void showManagerPanel() {
        JFrame frame = new JFrame("Manager Dashboard");
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    private void showAdminPanel() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Login");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new Login().getLoginPanel());
                frame.setSize(500, 300); // Increase the size of the frame
                frame.setLocationRelativeTo(null); // Center the frame
                frame.setVisible(true);
            }
        });
    }
}
