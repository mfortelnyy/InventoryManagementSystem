import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;



public class AdminPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String uname = "";
    JTable purchaseTable;
    
   
    
    public AdminPanel(String username) throws SQLException {
    	uname = username;
    	setLayout(new BorderLayout());
        setBackground(Color.WHITE); 

        JTextArea messageArea = new JTextArea("\t\tWelcome " + uname + "!");
        messageArea.setEditable(false); 
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true); 
        messageArea.setBackground(Color.WHITE); 
        messageArea.setFont(messageArea.getFont().deriveFont(Font.BOLD, 42)); 
        
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        purchaseTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(purchaseTable);
        tableScrollPane.setPreferredSize(new Dimension(300, 150));
        
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 10)); 
        buttonPanel.setBackground(new Color(245, 245, 220)); 

        JButton viewAllUsers = new JButton("View All Users");
        JButton updateUsers = new JButton("Update Users");
        JButton addUserButton = new JButton("Add User");
        JButton removeUserButton = new JButton("Remove User");
        JButton viewUpdDelSupplier = new JButton("View/Update/Delete Suppliers");
        JButton viewUpdDelCustomer = new JButton("View/Update/Delete Customers");
        JButton viewDelOrder = new JButton("View/Delete Orders");
        JButton logoutButton = new JButton("Logout");
        
        
        
        buttonPanel.add(viewAllUsers);
        buttonPanel.add(updateUsers);
        buttonPanel.add(addUserButton);
        buttonPanel.add(removeUserButton);
        buttonPanel.add(viewUpdDelSupplier);
        buttonPanel.add(viewUpdDelCustomer);
        buttonPanel.add(viewDelOrder);
        buttonPanel.add(logoutButton);
       


        
        add(scrollPane, BorderLayout.CENTER); 
        add(buttonPanel, BorderLayout.SOUTH);
        
        Dimension buttonSize = new Dimension(100, 30); 
        viewAllUsers.setPreferredSize(buttonSize);
        updateUsers.setPreferredSize(buttonSize);
        addUserButton.setPreferredSize(buttonSize);
        removeUserButton.setPreferredSize(buttonSize);
        viewUpdDelSupplier.setPreferredSize(buttonSize);
        viewUpdDelCustomer.setPreferredSize(buttonSize);
        viewDelOrder.setPreferredSize(buttonSize);
        
        viewAllUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	ResultSet rs = JDBC.getAllUsersAdmin();
                try {
                    JFrame frame = new JFrame("All Products by Supplier " + username);

                    DefaultTableModel tableModel = new DefaultTableModel();
                    JTable table = new JTable(tableModel);
                    int columnCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(rs.getMetaData().getColumnName(i));
                    }

                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            row[i - 1] = rs.getObject(i);
                        }
                        tableModel.addRow(row);
                    }

                    JScrollPane scrollPane = new JScrollPane(table);
                    frame.add(scrollPane);

                    frame.setSize(600, 400);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        
        updateUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 try {
     	            ResultSet rs = JDBC.getAllUsersAdmin(); 
     	            JFrame editFrame = new JFrame("Update Users");
     	            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     	            
     	            DefaultTableModel tableModel =  new DefaultTableModel();
     	            JTable table = new JTable(tableModel);
     	            JScrollPane scrollPane = new JScrollPane(table);
     	            editFrame.getContentPane().add(scrollPane);
     	            int columnCount = rs.getMetaData().getColumnCount();
     	            for (int i = 1; i <= columnCount; i++) {
     	                tableModel.addColumn(rs.getMetaData().getColumnName(i));
     	            }

     	            while (rs.next()) {
     	                Object[] row = new Object[columnCount];
     	                for (int i = 1; i <= columnCount; i++) {
     	                    row[i - 1] = rs.getObject(i);
     	                }
     	                tableModel.addRow(row);
     	            }
     	            
     	            table.addMouseListener(new MouseAdapter() {
     	                @Override
     	                public void mouseClicked(MouseEvent e) {
     	                    int row = table.rowAtPoint(e.getPoint());
     	                    int column = table.columnAtPoint(e.getPoint());
     	                    if (row >= 0 && column >= 0) {
     	                        String columnName = table.getColumnName(column);
     	                        String uname = (String) table.getValueAt(row, 0);
     	                        System.out.println(uname);
     	                        String value = JOptionPane.showInputDialog("Enter value for " + columnName + ":");
     	                        int res = JDBC.updateUsersAdmin(uname, columnName, value);
     	                        if(res > 0) {
 		                        	JOptionPane.showMessageDialog(null, "User with username: " + uname +" successfully updated");
     	                        	editFrame.dispose();
     	                        }
     	                        else
 		                        	JOptionPane.showMessageDialog(null, "Error when updating!");

     	                    }
     	                }
     	            });
     	            
     	            editFrame.pack();
     	            editFrame.setLocationRelativeTo(null);
     	            editFrame.setVisible(true);
     	        } catch (SQLException ex) {
     	            ex.printStackTrace();
     	        }
     	    }
        });
        
        
        
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 try {
            		ResultSet rs = JDBC.getAllUsersAdmin();
            		
         	        DefaultTableModel tableModel = new DefaultTableModel();
         	        JTable table = new JTable(tableModel);
         	
         	        int columnCount = rs.getMetaData().getColumnCount();
         	        for (int i = 1; i <= columnCount; i++) {
         	            tableModel.addColumn(rs.getMetaData().getColumnName(i));
         	        }

         	        while (rs.next()) {
         	        	Object[] row = new Object[columnCount];
         	        	for (int i = 1; i <= columnCount; i++) {
         	        		row[i - 1] = rs.getObject(i);
         	        	}
         	        	tableModel.addRow(row);
         	        }

 	                JScrollPane scrollPane = new JScrollPane(table);
 	                scrollPane.setPreferredSize(new Dimension(600, 250));
 	
 	                JPanel entryPanel = new JPanel(new FlowLayout());
 	
 	                JTextField usernameField = new JTextField(10);
 	                JTextField roleField = new JTextField(10);
 	                JTextField pswdField = new JTextField(10);
 	                
 	                JFrame userFrame = new JFrame("Add User");
	                userFrame.setLayout(new BorderLayout());
	                userFrame.add(scrollPane, BorderLayout.NORTH);
	                userFrame.add(entryPanel, BorderLayout.SOUTH);
	
	                userFrame.setSize(1000, 600);
	                userFrame.setLocationRelativeTo(null);
	                userFrame.setVisible(true);
 	                
 	
 	                JButton addButton = new JButton("Add User");
 	                addButton.addActionListener(new ActionListener() {
 	                    public void actionPerformed(ActionEvent e) {
 	                        String uname = String.valueOf(usernameField.getText());
 	                        String role = String.valueOf(roleField.getText());
 	                        String pswd = String.valueOf(pswdField.getText());
 	                        
 	                        JDBC.addCredentials(uname, role, pswd);
 	                        userFrame.dispose();
 	                        System.out.println(role);
 	                        if(role.equalsIgnoreCase("supplier")) {
 	        	
 	        	                JPanel entryPanel = new JPanel(new FlowLayout());
 	        	
 	        	                JTextField suppliernameField = new JTextField(10);
 	        	                JTextField streetnumField = new JTextField(10);
 	        	                JTextField streetnameField = new JTextField(10);
 	        	                JTextField cityField = new JTextField(10);
 	        	                JTextField stateField = new JTextField(2);
 	        	                JTextField zipField = new JTextField(6);
 	        	                JTextField phoneField = new JTextField(10);
 	        	                
 	        	                JFrame supplierFrame = new JFrame("Add Supplier");
 	        	                supplierFrame.setLayout(new BorderLayout());
 	        	                supplierFrame.add(scrollPane, BorderLayout.NORTH);
 	        	                supplierFrame.add(entryPanel, BorderLayout.CENTER);
 	        	
 	        	                supplierFrame.setSize(1000, 600);
 	        	                supplierFrame.setLocationRelativeTo(null);
 	        	                supplierFrame.setVisible(true);
 	        	
 	        	                JButton addButton = new JButton("Add Supplier");
 	        	                addButton.addActionListener(new ActionListener() {
 	        	                    public void actionPerformed(ActionEvent e) {
 	        	                        String suppliername = String.valueOf(suppliernameField.getText());
 	        	                        int streetnum = Integer.valueOf(streetnumField.getText());
 	        	                        String streetname = String.valueOf(streetnameField.getText());
 	        	                        String city = String.valueOf(cityField.getText());
 	        	                        String state = String.valueOf(stateField.getText());
 	        	                        String zip = String.valueOf(zipField.getText());
 	        	                        String phone = String.valueOf(phoneField.getText());
 	        	                        JDBC.addSupplier(suppliername, streetnum, streetname, city, state, zip, phone, uname);
 	        	                        supplierFrame.dispose();
 	        	                        
 	        	                        suppliernameField.setText("");
 	        	                        streetnumField.setText("");
 	        	                        streetnameField.setText("");
 	        	                        cityField.setText("");
 	        	                        stateField.setText("");
 	        	                        zipField.setText("");
 	        	                        phoneField.setText("");
 	        	                        phoneField.setText("");
 	        	                    }
 	        	                });

 	        	                entryPanel.add(new JLabel("Supplier Name: "));
 	        	                entryPanel.add(suppliernameField);
 	        	                entryPanel.add(new JLabel("Street Number "));
 	        	                entryPanel.add(streetnumField);
 	        	                entryPanel.add(new JLabel("Street Name : "));
 	        	                entryPanel.add(streetnameField);
 	        	                entryPanel.add(new JLabel("City: "));
 	        	                entryPanel.add(cityField);
 	        	                entryPanel.add(new JLabel("State (2 characters): "));
	        	                entryPanel.add(stateField);
	        	                entryPanel.add(new JLabel("Zip (5 characters): "));
	        	                entryPanel.add(zipField);
	        	                entryPanel.add(new JLabel("Phone (10 digits): "));
	        	                entryPanel.add(phoneField);
 	        	                entryPanel.add(addButton);
 	        	
 	        	               
 	                        }
 	                        else if(role.equalsIgnoreCase("customer")) {
 	            	        	
 	        	                JPanel ep = new JPanel(new FlowLayout());
 	        	
 	        	                JTextField emailFeild = new JTextField(10);
 	        	                JTextField firstNameField = new JTextField(10);
 	        	                JTextField lastNameField = new JTextField(10);
 	        	                
 	        	                JFrame customerFrame = new JFrame("Add Customer");
 	        	                customerFrame.setLayout(new BorderLayout());
 	        	                customerFrame.add(scrollPane, BorderLayout.NORTH);
 	        	                customerFrame.add(ep, BorderLayout.CENTER);
 	        	
 	               	            customerFrame.setSize(1000, 600);
 	        	                customerFrame.setLocationRelativeTo(null);
 	        	                customerFrame.setVisible(true);
	        	                
 	        	
 	        	                JButton addButton = new JButton("Add Customer");
 	        	                addButton.addActionListener(new ActionListener() {
 	        	                    public void actionPerformed(ActionEvent e) {
 	        	                        String email = String.valueOf(emailFeild.getText());
 	        	                        String firstName = String.valueOf(firstNameField.getText());
 	        	                        String lastName = String.valueOf(lastNameField.getText());
 	        	                        JDBC.addCustomer(email, firstName, lastName, uname);
 	        	                        customerFrame.dispose();
 	        	                        
 	        	                        emailFeild.setText("");
 	        	                        firstNameField.setText("");
 	        	                        lastNameField.setText("");
 	        	                       
 	        	                     
 	        	                    }
 	        	                });

 	        	                ep.add(new JLabel("Customer email: "));
 	        	                ep.add(emailFeild);
 	        	                ep.add(new JLabel("Customer First Name: "));
 	        	               	ep.add(firstNameField);
 	        	               	ep.add(new JLabel("Customer Last Name: "));
 	        	             	ep.add(lastNameField);
 	        	            	ep.add(addButton);
 	                        }
 	                        
 	                        usernameField.setText("");
 	                        roleField.setText("");
 	                        pswdField.setText("");
 	                    }
 	                });

 	                entryPanel.add(new JLabel("username: "));
 	                entryPanel.add(usernameField);
 	                entryPanel.add(new JLabel("Role(customer, supplier, admin): "));
 	                entryPanel.add(roleField);
 	                entryPanel.add(new JLabel("password : "));
 	                entryPanel.add(pswdField);
 	                entryPanel.add(addButton);
 	
 	                
 	        		} 
 	        		catch (SQLException ex) {
 	                ex.printStackTrace();
 	        		}
 	            
 	            }
 	        });
        
        removeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 JOptionPane.showMessageDialog(null, "Tap to delete");
            	 ResultSet rs = JDBC.getAllUsersAdmin();
				   try {
		            JFrame frame = new JFrame("Remove User");

		            DefaultTableModel tableModel = new DefaultTableModel();
		            JTable table = new JTable(tableModel);
		            int columnCount = rs.getMetaData().getColumnCount();
		            for (int i = 1; i <= columnCount; i++) {
		                tableModel.addColumn(rs.getMetaData().getColumnName(i));
		            }

		            while (rs.next()) {
		                Object[] row = new Object[columnCount];
		                for (int i = 1; i <= columnCount; i++) {
		                    row[i - 1] = rs.getObject(i);
		                }
		                tableModel.addRow(row);
		            }

		            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		            public void valueChanged(ListSelectionEvent e) {
		                if (!e.getValueIsAdjusting()) {
		                    int selectedRow = table.getSelectedRow();
		                    if (selectedRow != -1) {
		                        Object data = table.getValueAt(selectedRow, 0);
		                        Object data2 = table.getValueAt(selectedRow, 1);
		                        String username = (String) data;
		                        String role = (String) data2;
		                        int res = JDBC.removeUser(username, role);
		                        if(res > 0)
		                        {
		                        	JOptionPane.showMessageDialog(null, "User with id: " + username +" successfully deleted");
		                        	frame.dispose();
		                        }
		                        else
		                        	JOptionPane.showMessageDialog(null, "Deletion error!");
		                        
		                    }
		                }
		            }
		            });

			        JScrollPane scrollPane = new JScrollPane(table);
			        frame.add(scrollPane);
			
			        frame.setSize(600, 400);
			        frame.setLocationRelativeTo(null);
			        frame.setVisible(true);
			    } catch (SQLException ex) {
			        ex.printStackTrace();
			    }
            	
            }
        });
        
        viewUpdDelSupplier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
    	            ResultSet rs = JDBC.getAllSuppliers(); 
    	            JFrame editFrame = new JFrame("Edit Suppliers");
    	            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	            
    	            DefaultTableModel tableModel =  new DefaultTableModel();
    	            JTable table = new JTable(tableModel);
    	            JScrollPane scrollPane = new JScrollPane(table);
    	            editFrame.getContentPane().add(scrollPane);
    	            int columnCount = rs.getMetaData().getColumnCount();
    	            for (int i = 1; i <= columnCount; i++) {
    	                tableModel.addColumn(rs.getMetaData().getColumnName(i));
    	            }

    	            while (rs.next()) {
    	                Object[] row = new Object[columnCount];
    	                for (int i = 1; i <= columnCount; i++) {
    	                    row[i - 1] = rs.getObject(i);
    	                }
    	                tableModel.addRow(row);
    	            }
    	            
    	            table.addMouseListener(new MouseAdapter() {
    	                @Override
    	                public void mouseClicked(MouseEvent e) {
    	                    int row = table.rowAtPoint(e.getPoint());
    	                    int column = table.columnAtPoint(e.getPoint());
    	                    if (row >= 0 && column >= 0) {
    	                        String columnName = table.getColumnName(column);
    	                        int supplierid = (int) table.getValueAt(row, 0);
    	                        //String uname = (String) table.getValueAt(row, 8);
    	                        String value = JOptionPane.showInputDialog("Enter value for " + columnName + ":");
    	                        int res = JDBC.updateSupplier(supplierid, columnName, value);
    	                        if(res > 0) {
		                        	JOptionPane.showMessageDialog(null, "Supplier with id: " + supplierid +" successfully updated");
    	                        	editFrame.dispose();
    	                        }
    	                        else
		                        	JOptionPane.showMessageDialog(null, "Error when updating!");

    	                    }
    	                }
    	            });
    	            
    	            JPanel entryPanel = new JPanel(new FlowLayout());
    	        	
	                JTextField supplierid = new JTextField(10);
	                JTextField supplierusername = new JTextField(10);
	                
	
	                JButton deleteButton = new JButton("Delete Supplier");
	                deleteButton.addActionListener(new ActionListener() {
	                    public void actionPerformed(ActionEvent e) {
	                        int id = Integer.valueOf(supplierid.getText());
	                        String uname = String.valueOf(supplierusername.getText());
	                        int res = JDBC.deleteSupplierCredentials(id, uname);
	                        if(res > 0) {
	                        	JOptionPane.showMessageDialog(null, "Supplier with id: " + id +" successfully Deleted");
	                        	editFrame.dispose();
	                        }
	                        else
	                        	JOptionPane.showMessageDialog(null, "Error when updating!");
	                        supplierid.setText("");
	                        supplierusername.setText("");
	                       
	                    }
	                });

	                entryPanel.add(new JLabel("Supplier ID: "));
	                entryPanel.add(supplierid);
	                entryPanel.add(new JLabel("Supplier username: "));
	                entryPanel.add(supplierusername);
	                entryPanel.add(deleteButton);
    	            
	                editFrame.add(scrollPane, BorderLayout.NORTH);
	                editFrame.add(entryPanel, BorderLayout.CENTER);
    	            editFrame.pack();
    	            editFrame.setLocationRelativeTo(null);
    	            editFrame.setVisible(true);
    	        } catch (SQLException ex) {
    	            ex.printStackTrace();
    	        }
            	
            }
        });
        
        
        
        viewUpdDelCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
    	            ResultSet rs = JDBC.getAllCustomers(); 
    	            JFrame editFrame = new JFrame("Edit Customers");
    	            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	            
    	            DefaultTableModel tableModel =  new DefaultTableModel();
    	            JTable table = new JTable(tableModel);
    	            JScrollPane scrollPane = new JScrollPane(table);
    	            editFrame.getContentPane().add(scrollPane);
    	            int columnCount = rs.getMetaData().getColumnCount();
    	            for (int i = 1; i <= columnCount; i++) {
    	                tableModel.addColumn(rs.getMetaData().getColumnName(i));
    	            }

    	            while (rs.next()) {
    	                Object[] row = new Object[columnCount];
    	                for (int i = 1; i <= columnCount; i++) {
    	                    row[i - 1] = rs.getObject(i);
    	                }
    	                tableModel.addRow(row);
    	            }
    	            
    	            table.addMouseListener(new MouseAdapter() {
    	                @Override
    	                public void mouseClicked(MouseEvent e) {
    	                    int row = table.rowAtPoint(e.getPoint());
    	                    int column = table.columnAtPoint(e.getPoint());
    	                    if (row >= 0 && column >= 0) {
    	                        String columnName = table.getColumnName(column);
    	                        int customerid = (int) table.getValueAt(row, 0);
    	                        //String uname = (String) table.getValueAt(row, 4);
    	                        String value = JOptionPane.showInputDialog("Enter value for " + columnName + ":");
    	                        int res = JDBC.updateCustomer(customerid, columnName, value);
    	                        if(res > 0) {
		                        	JOptionPane.showMessageDialog(null, "Customer with id: " + customerid +" successfully updated");
    	                        	editFrame.dispose();
    	                        }
    	                        else
		                        	JOptionPane.showMessageDialog(null, "Error when updating!");

    	                    }
    	                }
    	            });
    	            
    	            JPanel entryPanel = new JPanel(new FlowLayout());
    	        	
	                JTextField customerId = new JTextField(10);
	                JTextField customerUsername = new JTextField(10);
	                
	
	                JButton deleteButton = new JButton("Delete Customer");
	                deleteButton.addActionListener(new ActionListener() {
	                    public void actionPerformed(ActionEvent e) {
	                        int id = Integer.valueOf(customerId.getText());
	                        String uname = String.valueOf(customerUsername.getText());
	                        int res = JDBC.deleteCustomerCredentials(id, uname);
	                        if(res > 0) {
	                        	JOptionPane.showMessageDialog(null, "Customer with id: " + id +" successfully Deleted");
	                        	editFrame.dispose();
	                        }
	                        else
	                        	JOptionPane.showMessageDialog(null, "Error when updating!");
	                        customerId.setText("");
	                        customerUsername.setText("");
	                       
	                    }
	                });

	                entryPanel.add(new JLabel("Customer ID: "));
	                entryPanel.add(customerId);
	                entryPanel.add(new JLabel("Customer username: "));
	                entryPanel.add(customerUsername);
	                entryPanel.add(deleteButton);
    	            
	                editFrame.add(scrollPane, BorderLayout.NORTH);
	                editFrame.add(entryPanel, BorderLayout.CENTER);
    	            editFrame.pack();
    	            editFrame.setLocationRelativeTo(null);
    	            editFrame.setVisible(true);
    	        } catch (SQLException ex) {
    	            ex.printStackTrace();
    	        }
            	
            }
        });
        
        viewDelOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		JOptionPane.showMessageDialog(null, "Tap to delete whole order");
    	            ResultSet rs = JDBC.getAllOrders(); 
    	            JFrame editFrame = new JFrame("Edit Orders");
    	            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	            
    	            DefaultTableModel tableModel =  new DefaultTableModel();
    	            JTable table = new JTable(tableModel);
    	            JScrollPane scrollPane = new JScrollPane(table);
    	            editFrame.getContentPane().add(scrollPane);
    	            int columnCount = rs.getMetaData().getColumnCount();
    	            for (int i = 1; i <= columnCount; i++) {
    	                tableModel.addColumn(rs.getMetaData().getColumnName(i));
    	            }

    	            while (rs.next()) {
    	                Object[] row = new Object[columnCount];
    	                for (int i = 1; i <= columnCount; i++) {
    	                    row[i - 1] = rs.getObject(i);
    	                }
    	                tableModel.addRow(row);
    	            }
    	            
    	            table.addMouseListener(new MouseAdapter() {
    	                @Override
    	                public void mouseClicked(MouseEvent e) {
    	                    int row = table.rowAtPoint(e.getPoint());
    	                    int column = table.columnAtPoint(e.getPoint());
    	                    if (row >= 0 && column >= 0) {
    	                        int orderid = (int) table.getValueAt(row, 1);
    	                        int res = JDBC.removerOrder(orderid);
    	                        if(res > 0) {
		                        	JOptionPane.showMessageDialog(null, "Order with id: " + orderid +" successfully deleted");
    	                        	editFrame.dispose();
    	                        }
    	                        else
		                        	JOptionPane.showMessageDialog(null, "Error when deleting order!");

    	                    }
    	                }
    	            });
    	            
    	            editFrame.add(scrollPane, BorderLayout.NORTH);
    	            editFrame.pack();
    	            editFrame.setLocationRelativeTo(null);
    	            editFrame.setVisible(true);
    	            
    	        } catch (SQLException ex) {
    	            ex.printStackTrace();
    	        }
                
            }
        });
        
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(AdminPanel.this);
                frame.dispose(); 
                JDBC.setUsername("");
                
                new login().setVisible(true);
            }
        });
        
        
        
        
    }

}
