import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String uname = "";
    JTable purchaseTable;
    Long orderid = null;
    
    public CustomerPanel(String username) {
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

        JButton viewProductsButton = new JButton("View Products");
        JButton placeOrdersButton = new JButton("Place Order");
        JButton viewOrderHistoryButton = new JButton("View Order History");
        JButton editProfileButton = new JButton("Edit Profile");
        JButton logoutButton = new JButton("Logout");
        
        
        
        buttonPanel.add(viewProductsButton);
        buttonPanel.add(placeOrdersButton);
        buttonPanel.add(viewOrderHistoryButton);
        buttonPanel.add(editProfileButton);
        buttonPanel.add(logoutButton);


        
        add(scrollPane, BorderLayout.CENTER); 
        add(buttonPanel, BorderLayout.SOUTH);
        
        Dimension buttonSize = new Dimension(100, 30); 
        viewProductsButton.setPreferredSize(buttonSize);
        placeOrdersButton.setPreferredSize(buttonSize);
        viewOrderHistoryButton.setPreferredSize(buttonSize);
        editProfileButton.setPreferredSize(buttonSize);
        
    
    viewProductsButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ResultSet rs = JDBC.getAllProducts();
            try {
                JFrame frame = new JFrame("All Products");

                DefaultTableModel tableModel = new DefaultTableModel();
                JTable historyTable = new JTable(tableModel);
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

                JScrollPane scrollPane = new JScrollPane(historyTable);
                frame.add(scrollPane);

                frame.setSize(600, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
     });

        
        
    placeOrdersButton.addActionListener(new ActionListener() {
    	@Override
		public void actionPerformed(ActionEvent e) {
		try {
			JOptionPane.showMessageDialog(null, "Enter product id and quantity to add an item to the cart. "
					+ "Once 'Place order' window is closed you won't be able to add more items to the current order.");
			
			//orderid for the current order
			//used to add products 
			orderid = JDBC.createOrder(username);
			
		    ResultSet rs = JDBC.getAllProducts();
	        
	        DefaultTableModel tableModel = new DefaultTableModel();
	        JTable historyTable = new JTable(tableModel);
	
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

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(600, 250));

        JPanel entryPanel = new JPanel(new FlowLayout());

        JTextField productIdField = new JTextField(8);
        JTextField quantityField = new JTextField(8);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.valueOf(quantityField.getText());
                int productId = Integer.valueOf(productIdField.getText());
                JDBC.insertProductOrder(orderid, productId, quantity);
                productIdField.setText("");
                quantityField.setText("");
            }
       });

            entryPanel.add(new JLabel("Product ID: "));
            entryPanel.add(productIdField);
            entryPanel.add(new JLabel("Quantity: "));
            entryPanel.add(quantityField);
            entryPanel.add(addButton);

            JFrame orderFrame = new JFrame("Place Order");
            orderFrame.setLayout(new BorderLayout());
            orderFrame.add(scrollPane, BorderLayout.NORTH);
            orderFrame.add(entryPanel, BorderLayout.SOUTH);

            orderFrame.setSize(600, 400);
            orderFrame.setLocationRelativeTo(null);
            orderFrame.setVisible(true);
    		} catch (SQLException ex) {
            ex.printStackTrace();
    		}
          }
    });

        
    viewOrderHistoryButton.addActionListener(new ActionListener() {
	   @Override
	   public void actionPerformed(ActionEvent e) {
		   ResultSet rs = JDBC.getPurchaseHistory(username);
		   try {
            JFrame historyFrame = new JFrame("Purchase History");

            DefaultTableModel tableModel = new DefaultTableModel();
            JTable historyTable = new JTable(tableModel);
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

            historyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = historyTable.getSelectedRow();
                    if (selectedRow != -1) {
                        Object data = historyTable.getValueAt(selectedRow, 0);
                        try {
                        Integer id = (Integer) data;
                        ResultSet rs = JDBC.getProductsByOrderid(id);
                        JFrame productsOrder = new JFrame("Order details: " + id);

                        DefaultTableModel tableModel = new DefaultTableModel();
                        JTable productsOrderTable = new JTable(tableModel);
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
                        JScrollPane scrollPane = new JScrollPane(productsOrderTable);
                        productsOrder.add(scrollPane);

                        productsOrder.setSize(600, 400);
                        productsOrder.setLocationRelativeTo(null);
                        productsOrder.setVisible(true);
                        
                        } catch (SQLException e1) {
                        	e1.printStackTrace();
						}
                    }
                }
            }
            });

	        JScrollPane scrollPane = new JScrollPane(historyTable);
	        historyFrame.add(scrollPane);
	
	        historyFrame.setSize(600, 400);
	        historyFrame.setLocationRelativeTo(null);
	        historyFrame.setVisible(true);
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
 }
 });

       
    editProfileButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        try {
	            ResultSet rs = JDBC.getUser(username); 
	            JFrame editProfileFrame = new JFrame("Edit Profile");
	            editProfileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	            
	            DefaultTableModel tableModel =  new DefaultTableModel();
	            JTable table = new JTable(tableModel);
	            JScrollPane scrollPane = new JScrollPane(table);
	            editProfileFrame.getContentPane().add(scrollPane);
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
	                        String value = JOptionPane.showInputDialog("Enter value for " + columnName + ":");
	                        int res = JDBC.updateProfile(columnName, value, username);
	                        if(res > 0) {
	                        	JOptionPane.showMessageDialog(null, columnName +" updated with values: "+ value);
	                        	editProfileFrame.dispose();
	                        }
	                        else {
	                        	JOptionPane.showMessageDialog(null," Error when updating "+columnName);
	                        	
	                        }
	                       
	                        
	                    }
	                }
	            });
	            
	            editProfileFrame.pack();
	            editProfileFrame.setLocationRelativeTo(null);
	            editProfileFrame.setVisible(true);
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
     });


       
    logoutButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(CustomerPanel.this);
            frame.dispose(); 
            JDBC.setUsername("");
            
            new login().setVisible(true);
        }
    });
  
  }
}
