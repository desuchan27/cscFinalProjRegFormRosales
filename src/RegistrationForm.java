import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfUsername;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JComboBox<String> jcbGender;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;
    private JTextField tfSection;

    private User user;

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(600, 390));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }

        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LoginForm(null); // Open the registration form
                    }
                });
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = tfName.getText();
        String username = tfUsername.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = new String(pfConfirmPassword.getPassword());
        String gender = jcbGender.getSelectedItem().toString();
        String section = tfSection.getText();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = addUserToDatabase(name, username, email, phone, address, gender, password, section);
        if (user != null) {
            dispose();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginForm(null);
                }
            });

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private User addUserToDatabase(String name, String username, String email, String phone, String address, String gender, String password, String section) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost:3306/userdb?useSSL=false&serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection c = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String mysql = "INSERT INTO users (name, username, email, phone, address, password, gender, section) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(mysql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, password);
            ps.setString(7, gender);
            ps.setString(8, section);

            int addedRows = ps.executeUpdate();
            if (addedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user = new User();
                    user.name = name;
                    user.username = username;
                    user.email = email;
                    user.phone = phone;
                    user.address = address;
                    user.password = password;
                    user.gender = gender;
                    user.section = section;
                }
                generatedKeys.close();
            }

            ps.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            user = null; // Set user to null in case of failure
        }

        return user;
    }

    static class User {
        String name;
        String username;
        String email;
        String phone;
        String address;
        String password;
        String gender;
        String section;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
    }
}
