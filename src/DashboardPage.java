import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DashboardPage extends JFrame {
    private JPanel dashboardPanel;
    private JLabel lbName;
    private JLabel lbUsername;
    private JButton btnLogout;
    private JTextPane fnList;

    public DashboardPage(User user) {
        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(300, 400));
        setSize(500, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        if (user != null) {
            lbName.setText(user.name);
            setLocationRelativeTo(null);
            lbUsername.setText(user.username);
            setVisible(true);
        } else {
            dispose();
        }

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LoginForm(null);
                    }
                });
            }
        });
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
