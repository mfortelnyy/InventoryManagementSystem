import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class login extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField usernameField;
    private JPasswordField passwordField;
    String username = "";
    
    public login() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setVisible(true); 

        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

       
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                
                JDBC j = new JDBC();
                
                String role = JDBC.authenticate(username, password);
               
                if (role.length() !=0) {
                    
                    JDBC.setUsername(username);
					if (role.equals("admin")) {
					    JOptionPane.showMessageDialog(null, "Welcome Admin!");
					    try {
							showAdminScreen();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					    
					}
					
					else if (role.equals("customer")) {
						
					    JOptionPane.showMessageDialog(null, "Welcome Customer!");
					    showCustomerScreen();
					} 
					
					else if (role.equals("supplier")) {

					    JOptionPane.showMessageDialog(null, "Welcome Supplier!");
					    try {
							showSupplierScreen();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
                } 
                
                else {
                    JOptionPane.showMessageDialog(null, "Invalid Credentials!");
                }
            }
        });
    }

    
    
    private void showAdminScreen() throws SQLException {
        AdminPanel adminPanel = new AdminPanel(username);
        displayPanel(adminPanel);
    }

   
    private void showCustomerScreen() {
        CustomerPanel customerPanel = new CustomerPanel(username);
        displayPanel(customerPanel);
    }


    private void showSupplierScreen() throws SQLException {
        SupplierPanel supplierPanel = new SupplierPanel(username);
        displayPanel(supplierPanel);
    }


    private void displayPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new login().setVisible(true);
            }
        });
    }
}


