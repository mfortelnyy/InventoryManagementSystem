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

public class SupplierPanel extends JPanel  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String uname = "";
    JTable purchaseTable;
    int supplierid = 0;
   
    
    public SupplierPanel(String username) throws SQLException {
    	uname = username;
    	supplierid = JDBC.getSupplierByUsername(username);
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

        JButton viewProductsButton = new JButton("View My Products");
        JButton addProductButton = new JButton("Add New Product");
        JButton updateProductButton = new JButton("Update Product");
        JButton removeProductButton = new JButton("Remove Product");
        JButton logoutButton = new JButton("Logout");
        
        
        
        buttonPanel.add(viewProductsButton);
        buttonPanel.add(addProductButton);
        buttonPanel.add(updateProductButton);
        buttonPanel.add(removeProductButton);
        buttonPanel.add(logoutButton);
       


        
        add(scrollPane, BorderLayout.CENTER); 
        add(buttonPanel, BorderLayout.SOUTH);
        
        Dimension buttonSize = new Dimension(100, 30); 
        viewProductsButton.setPreferredSize(buttonSize);
        addProductButton.setPreferredSize(buttonSize);
        updateProductButton.setPreferredSize(buttonSize);
        removeProductButton.setPreferredSize(buttonSize);
        
        
        viewProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultSet rs = JDBC.getProductsbyUsername(username);
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
        
        
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
        	        ResultSet rs = JDBC.getAllCategories();
        	        
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
	
	                JTextField productNameField = new JTextField(10);
	                JTextField quantityField = new JTextField(10);
	                JTextField priceField = new JTextField(10);
	                JTextField categoryidField = new JTextField(10);
	
	                JButton addButton = new JButton("Add");
	                addButton.addActionListener(new ActionListener() {
	                    public void actionPerformed(ActionEvent e) {
	                        int quantity = Integer.valueOf(quantityField.getText());
	                        String pName = String.valueOf(productNameField.getText());
	                        double price = Double.valueOf(priceField.getText());
	                        int categoryid = Integer.valueOf(categoryidField.getText());
	                        
	                        int res = JDBC.addProduct(pName, quantity, price, supplierid, categoryid);
	                        if(res > 0) {
	                        	JOptionPane.showMessageDialog(null, "Product with name: " + pName +" successfully added");
	                        	
	                        }
	                        else
	                        	JOptionPane.showMessageDialog(null, "Error when adding!");

	                        productNameField.setText("");
	                        quantityField.setText("");
	                        priceField.setText("");
	                        categoryidField.setText("");
	                    }
	                });

	                entryPanel.add(new JLabel("Product Name: "));
	                entryPanel.add(productNameField);
	                entryPanel.add(new JLabel("Quantity: "));
	                entryPanel.add(quantityField);
	                entryPanel.add(new JLabel("Price per unit: "));
	                entryPanel.add(priceField);
	                entryPanel.add(new JLabel("Category ID: "));
	                entryPanel.add(categoryidField);
	                entryPanel.add(addButton);
	
	                JFrame orderFrame = new JFrame("Add Product");
	                orderFrame.setLayout(new BorderLayout());
	                orderFrame.add(scrollPane, BorderLayout.NORTH);
	                orderFrame.add(entryPanel, BorderLayout.SOUTH);
	
	                orderFrame.setSize(1000, 600);
	                orderFrame.setLocationRelativeTo(null);
	                orderFrame.setVisible(true);
	        		} 
	        		catch (SQLException ex) {
	                ex.printStackTrace();
	        		}
	            
	            }
	        });
	        
        removeProductButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				   ResultSet rs = JDBC.getProductsbyUsername(username);
				   try {
		            JFrame frame = new JFrame("Remove Product");

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
		                        Integer productid = (Integer) data;
		                        int res = JDBC.removeProduct(productid);
		                        if(res > 0)
		                        {
		                        	JOptionPane.showMessageDialog(null, "Product with id: " + productid +" successfully deleted");
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
        
        updateProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
    	        try {
    	            ResultSet rs = JDBC.getProductsbyUsername(username); 
    	            JFrame editFrame = new JFrame("Edit Product");
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
    	                        int productid = (int) table.getValueAt(row, 0);
    	                        String value = JOptionPane.showInputDialog("Enter value for " + columnName + ":");
    	                        int res = JDBC.updateProduct(productid, columnName, value);
    	                        if(res > 0) {
		                        	JOptionPane.showMessageDialog(null, "Product with id: " + productid +" successfully updated");
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
        
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(SupplierPanel.this);
                frame.dispose(); 
                JDBC.setUsername("");
                
                new login().setVisible(true);
            }
        });

	    }
}
