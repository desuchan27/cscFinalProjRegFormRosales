import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOk;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel loginPanel;

    private User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(525, 250));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthenticator(email, password);

                if (user != null) {
                    dispose();
                    System.out.println("Successful Authentication of: " + user.name);
                    System.out.println("Email: " + user.email);
                    System.out.println("Phone: " + user.phone);
                    System.out.println("Address: " + user.address);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new DashboardPage(user);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Invalid Email or Password",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.out.println("Authentication Canceled");
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new RegistrationForm(null);
                    }
                });
            }
        });

        setVisible(true);
    }

    private User getAuthenticator(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost:3306/userdb?useSSL=false&serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection c = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.name = resultSet.getString("name");
                user.username = resultSet.getString("username");
                user.email = resultSet.getString("email");
                user.phone = resultSet.getString("phone");
                user.address = resultSet.getString("address");
                user.password = resultSet.getString("password");
                user.gender = resultSet.getString("gender");
            }

            resultSet.close();
            preparedStatement.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public User getUser() {
        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm(null);
                User user = loginForm.getUser();
                if (user != null) {
                    System.out.println("Successful Authentication of: " + user.name);
                    System.out.println("Email: " + user.email);
                    System.out.println("Phone: " + user.phone);
                    System.out.println("Address: " + user.address);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new DashboardPage(user);
                        }
                    });
                } else {
                    System.out.println("Authentication Canceled");
                }
            }
        });
    }
}
