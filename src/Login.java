import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private JPanel loginPanel;
    private EmployeeService employeeService;

    public Login() {
        // Set up FlatLaf look and feel
        FlatLightLaf.setup();

        // Initialize components
        employeeService = new EmployeeService();
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        submitButton = new JButton("Potvrdi");

        // Increase font size for components
        emailField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));

        // Set up background image
        loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("C:\\Users\\lejla\\IdeaProjects\\lms\\src\\pozadina2.gif");
                Image backgroundImage = backgroundIcon.getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Lozinka:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(submitButton, gbc);

        // Set up action listener for the button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String emailText = emailField.getText().trim();
                String passwordText = new String(passwordField.getPassword()).trim();

                Employee employee = employeeService.authenticate(emailText, passwordText);
                if (employee != null) {
                    String role = employee.getRole();
                    switch (role) {
                        case "Employee":
                            showEmployeePanel(employee);
                            break;
                        case "Manager":
                            showManagerPanel(employee);
                            break;
                        case "Admin":
                            showAdminPanel(employee);
                            break;
                        default:
                            JOptionPane.showMessageDialog(loginPanel, "Neispravna uloga", "Greška", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(loginPanel, "Nije tačna lozinka ili email", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create JFrame and set the application icon
        JFrame frame = new JFrame("Prijava");
        IconUtils.setAppIcon(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(loginPanel);
        frame.setSize(500, 300); // Increase the size of the frame
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    private void showEmployeePanel(Employee employee) {
        JFrame frame = new JFrame("Zaposlenik");
        IconUtils.setAppIcon(frame);
        frame.setContentPane(new EmployeeObrazac(employee).getEmployeePanel());
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
        // Hide login frame
        SwingUtilities.getWindowAncestor(loginPanel).dispose();
    }

    private void showManagerPanel(Employee employee) {
        JFrame frame = new JFrame("Menadžer");
        IconUtils.setAppIcon(frame);
        frame.setContentPane(new ManagerObrazac(employee).getMainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
        // Hide login frame
        SwingUtilities.getWindowAncestor(loginPanel).dispose();
    }

    private void showAdminPanel(Employee employee) {
        JFrame frame = new JFrame("Admin");
        IconUtils.setAppIcon(frame);
        frame.setContentPane(new AdminObrazac(employee).getMainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
        // Hide login frame
        SwingUtilities.getWindowAncestor(loginPanel).dispose();
    }

    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public static class IconUtils {
        public static void setAppIcon(JFrame frame) {
            ImageIcon icon = new ImageIcon("C:\\Users\\lejla\\IdeaProjects\\lms\\src\\korisnik.jpg");
            frame.setIconImage(icon.getImage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
