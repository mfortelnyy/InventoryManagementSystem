
import java.sql.*;

public class JDBC {
	static Connection con;
	static String  username;
	
	public static void setUsername(String u) {
	username = u;	
	}
	
	public static String getUsername() {
		return username;
	}
	public JDBC() {
		final String url = "jdbc:postgresql://localhost:5433/postgres";
        final String user = "postgres";
        final String password = "1234";
         	
	 	String mySQL = "SELECT *"+
	 					"FROM ORDR "+
	 					"GROUP BY orderid";
	 	
	 	

	 	
	 	try {
	 		con = DriverManager.getConnection(url, user, password);
 		    Statement stmt = con.createStatement();
 			ResultSet rset = stmt.executeQuery(mySQL);
 			System.out.println( "orderid"  +"\t\t" + "orderdate" +"\t\t" + "total price" +"\t\t" + "customerid" );
 
	 		while(rset.next()) {
	 			System.out.println(rset.getString("orderid") +"\t\t" + rset.getString(2) +"\t\t"+ rset.getString(3) +"\t\t" + rset.getString(4));
	 		}
	 		 	
     
    	 }catch (SQLException e) {
    		 System.out.println(e.getMessage());
    	}
	}
	
	public static String authenticate(String username, String password) {
		String query = "Select * from credentials where username = ? and pswd = ?";
		String role = "";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				role = rs.getString("rol");
			if (role != null)
			{
				username = rs.getString("username");
				System.out.print(username);
				return role;
			}
			else {
				return role;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return role;
		
	}
	
	
	public static Long getIdByUsername(String u) throws SQLException {
		ResultSet rs = null;
		String query = "Select customerid "
				+ "from customer "
				+ "where username = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			System.out.println(username);
			ps.setString(1, u);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		Long orderid = null;
		if (rs.next())
		{
			orderid = rs.getLong("customerid");
		}
		
		return orderid;
		
	}
	
	
	static ResultSet getPurchaseHistory(String u) {
		ResultSet rs = null;
		String query = "Select orderid, order_date, total_price, status "
				+ "from customer "
				+ "join ordr on customer.customerid = ordr.customerid "
				+ "where username = ?";
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, u);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		return rs;
		
	}
	
	
	public static ResultSet getAllProducts() {
		ResultSet rs = null;
		String query = "SELECT productid, product_name, quantity, price, supplier_name, categoryname "
				+ "FROM product "
				+ "JOIN supplier on product.supplierid = supplier.supplierid "
				+ "JOIN category on product.categoryid = category.categoryid "
				+ "Where quantity > 1";
		try {
			PreparedStatement ps = con.prepareStatement(query);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		return rs;	
	}
	
	public static ResultSet getProductsByOrderid(int id) {
		ResultSet rs = null;
		String query = "SELECT product_name, OrderDetail.quantity, product.price "
				+ "FROM ordr "
				+ "JOIN OrderDetail "
				+ "ON ordr.orderid = OrderDetail.orderid "
				+"JOIN product "
				+"ON product.productid = OrderDetail.productid "
				+ "WHERE ordr.orderid = ?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
		    ps.setLong(1, id);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		return rs;	
	}
	
	public static Long createOrder(String u) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		String query = "INSERT INTO ordr (order_date, total_price, customerid, status) "
				+"Values (?, 0, ?, 'processing')";
		System.out.println("username: "+ getIdByUsername(u));
		
		try {
			ps = con.prepareStatement(query);
			
			ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
			ps.setLong(2, getIdByUsername(u));
		    ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		
		Long orderid = null;
		try {
			String queryorderid = "Select orderid from ordr Order by orderid DESC LIMIT 1";
		    ps = con.prepareStatement(queryorderid);
		    rs = ps.executeQuery();
		    rs.next();
		    	orderid = rs.getLong(1);
		        System.out.println(rs.getLong(1));
		    
		}
		catch (Exception e) {
			e.printStackTrace();
			}
		    
		return orderid;	
	}
	
	public static int insertProductOrder(Long orderid, int productid, int quantity) {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		String query = "INSERT INTO OrderDetail (orderID, productID, quantity) "
				+"Values (?, ?, ?)";
		
		String queryUpdateTotalPrice = "Update ordr SET total_price = ? WHERE orderid = ?";
		int res =0;
		
		try {
			ps = con.prepareStatement(query);
			
			ps.setLong(1, orderid );
			ps.setLong(2, productid);
			ps.setInt(3, quantity);
		    res = ps.executeUpdate();
		    
		    rs = getProductsByOrderid(orderid.intValue());
		    double total_price = 0;
		    while(rs.next()){
		    	double q = rs.getInt(2);
		    	double p = rs.getDouble(3);
		    	total_price += q * p;
		    }
		    ps = con.prepareStatement(queryUpdateTotalPrice);
		    ps.setDouble(1, total_price);
		    ps.setLong(2, orderid);
		    ps.executeUpdate();
		    
		    
		}
		catch (Exception e) {
			e.printStackTrace();		
		}
		return res;
	}
	
	public static ResultSet getUser(String u) {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		String query = "SELECT email, firstname, lastname FROM customer where username = ?";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, u);
		    rs = ps.executeQuery();
		   
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
		
	}
	
	public static int updateProfile(String columnName, String value, String u) {
		PreparedStatement ps = null ;
		int res = 0;
		String query = "UPDATE customer SET " + columnName
				+" = ? WHERE username = ?";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, value);
		    ps.setString(2, u);
		    res = ps.executeUpdate();
		   
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	public static ResultSet getProductsbyUsername(String u) {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		String query = "SELECT productid, product_name, quantity, price "
				+ "FROM product "
				+ "JOIN supplier "
				+ "ON product.supplierid = supplier.supplierid "
				+ "WHERE username = ?";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, u);
		   
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;

	}
	
	
	public static int getSupplierByUsername(String u) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		String query = "SELECT supplierid "
				+ "FROM supplier "
				+ "WHERE username = ?";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, u);
		   
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		rs.next();
		return rs.getInt(1);
		
	}
	
	
	public static int addProduct(String pName, int quantity, double price, int supplierid, int categoryid) {
		PreparedStatement ps = null ;
		int res = 0;
		String query = "Insert Into product (product_name, quantity, price, supplierID , categoryID) "
				+ "VALUES (?, ?, ?, ?, ?) ";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, pName);
		    ps.setInt(2, quantity);
		    ps.setDouble(3, price);
		    ps.setInt(4, supplierid);
		    ps.setInt(5, categoryid);
		   
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
		
	}
	
	public static ResultSet getAllCategories() {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		String query = "Select * from category";
		try {
		    ps = con.prepareStatement(query);
		   
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static int removeProduct(Integer id) {
		PreparedStatement ps = null ;
		int res = 0;
		String query = "Delete From product where productid = ?";
		try {
		    ps = con.prepareStatement(query);
		    ps.setLong(1, id);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static int updateProduct(int productid, String columnName, String value) {
		PreparedStatement ps = null ;
		int res = 0;
		String query = "Update product SET " + columnName + " = '" + value + "' where productid = ?";
		try {
		    ps = con.prepareStatement(query);
		    ps.setLong(1, productid);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	public static ResultSet getAllUsersAdmin() {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		
		String query = "select username, rol  from credentials";
		try {
		    ps = con.prepareStatement(query);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
		
	}
	
	
	public static int updateUsersAdmin(String uname, String columnName, String value) {
		System.out.println(uname +"          "+ columnName + "       "+value);
		PreparedStatement ps = null ;
		int res = 0;
		String query = "Update credentials SET " + columnName + " = '" + value + "' where username = ?";
		System.out.println(query);
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, uname);
		    System.out.println(query);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static int addCredentials(String uname, String role, String pswd) {
		int res =0;
		PreparedStatement ps = null ;
		
		String query = "Insert Into credentials(username, rol, pswd) Values(?, ?, ?)";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, uname);
		    ps.setString(2, role);
		    ps.setString(3, pswd);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static int addSupplier(String sname, int stnumber, String stname, String city, String state, String zip, String phone,String username) {
		int res =0;
		PreparedStatement ps = null ;
		
		String query = "INSERT INTO supplier(supplier_name , street_number , street_name, city, state, zip, phone_number, username) "
				+ "Values(?, ?, ?, ?, ?, ?, ?, ?)";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, sname);
		    ps.setInt(2, stnumber);
		    ps.setString(3, stname);
		    ps.setString(4, city);
		    ps.setString(5, state);
		    ps.setString(6, zip);
		    ps.setString(7, phone);
		    ps.setString(8, username);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static int addCustomer(String email, String firstName, String lastName, String username) {
		int res =0;
		PreparedStatement ps = null ;
		
		String query = "INSERT INTO customer(email, firstname, lastname, username) "
				+ "Values(?, ?, ?, ?)";
		try {
		    ps = con.prepareStatement(query);
		    ps.setString(1, email);
		    ps.setString(2, firstName);
		    ps.setString(3, lastName);
		    ps.setString(4, username);
		    
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static int removeUser(String username, String role) {
		int res =0;
		PreparedStatement ps = null ;
		
		String queryCredentials = "DELETE FROM credentials WHERE username = ?";
		String queryUser = "";
		
		try {
		   		    
		    ps = con.prepareStatement(queryCredentials);
		    ps.setString(1, username);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	public static ResultSet getAllSuppliers() {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		
		String query = "select * from supplier";
		try {
		    ps = con.prepareStatement(query);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
		
	}
	
	
	public static int updateSupplier(int id, String collumnName, String value, String uname) {
		int res =0;
		PreparedStatement ps = null ;
		if(collumnName.contains("username")) {
			String query = "Update credentials SET " + collumnName +" = '"+ value+ "' WHERE username = ?";
			System.out.println(query);
			try {
			    ps = con.prepareStatement(query);
			    ps.setString(1, uname);
			    res = ps.executeUpdate();
			    
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else {			
			String query = "Update supplier SET " + collumnName +" = '"+ value+ "' WHERE supplierid = ?";
			System.out.println(query);
			try {
			    ps = con.prepareStatement(query);
			    ps.setLong(1, id);
			    res = ps.executeUpdate();
			    
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			}
		return res;
	}
	
	
	public static int updateCustomer(int id, String collumnName, String value, String uname) {
		int res =0;
		PreparedStatement ps = null ;
		//System.out.println("uname: "+ collumnName + collumnName == "username");
		if(collumnName.contains("username")) {
			String query = "Update credentials SET " + collumnName +" = '"+ value+ "' WHERE username = ?";
			System.out.println(query);
			try {
			    ps = con.prepareStatement(query);
			    ps.setString(1, uname);
			    res = ps.executeUpdate();
			    
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		else {
			String query = "Update customer SET " + collumnName +" = '"+ value+ "' WHERE customerid = ?";
			System.out.println(query);
			try {
			    ps = con.prepareStatement(query);
			    ps.setLong(1, id);
			    res = ps.executeUpdate();
			    
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
		
	
	
	public static int deleteSupplierCredentials(int supplierid, String supplierusername) {
		int res =0;
		PreparedStatement ps = null ;
		
		String querySupplier = "DELETE FROM supplier WHERE supplierid = ?";
		String queryCredentials = "DELETE FROM credentials WHERE username = ?";
		try {
		    ps = con.prepareStatement(querySupplier);
		    ps.setLong(1, supplierid);
		    res = ps.executeUpdate();
		    
		    ps = con.prepareStatement(queryCredentials);
		    ps.setString(1, supplierusername);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
		
	}
	
	public static int deleteCustomerCredentials(int customerid, String customerusername){
		int res =0;
		PreparedStatement ps = null ;
		
		String querySupplier = "DELETE FROM customer WHERE customerid = ?";
		String queryCredentials = "DELETE FROM credentials WHERE username = ?";
		try {
		    ps = con.prepareStatement(querySupplier);
		    ps.setLong(1, customerid);
		    res = ps.executeUpdate();
		    
		    ps = con.prepareStatement(queryCredentials);
		    ps.setString(1, customerusername);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
	public static ResultSet getAllCustomers() {
		ResultSet rs = null;
		PreparedStatement ps = null ;
		
		String query = "select * from customer";
		try {
		    ps = con.prepareStatement(query);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
		
	}
	
	
	public static ResultSet getAllOrders() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		String query = "Select * from orderdetail "
				+ "JOIN ordr ON ordr.orderid = orderdetail.orderid";		
		try {
		    ps = con.prepareStatement(query);
		    rs = ps.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	
	
	public static int removerOrder(int orderid) {
		int res = 0;
		PreparedStatement ps = null;
		
		String query = "DELETE FROM ordr WHERE orderid = ?";		
		try {
		    ps = con.prepareStatement(query);
		    ps.setLong(1, orderid);
		    res = ps.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
		
	}
	
	
	
	
	
	public static void main(String[] args) throws ClassNotFoundException {
		login l = new login();
		l.setVisible(true);
	}

	



}
