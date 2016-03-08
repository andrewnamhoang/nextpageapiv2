//class for creating and maniuplating the database


import java.sql.*;
import java.util.Properties;
import java.net.URI;
import java.net.URISyntaxException;
	
public class DBDemo {
		/** The name of the MySQL account to use (or empty for anonymous) */
		private final String userName = "awsmaster";
		/** The password for the MySQL account (or empty for anonymous) */
		private final String password = "randomkey123";
		/** The name of the computer running MySQL */
		private final String serverName = "nextpagedb2.cy9nrdlkzrrv.us-east-1.rds.amazonaws.com";
		/** The port of the MySQL server (default is 3306) */
		private final int portNumber = 3306;
		/** The name of the database we are testing with (this default is installed with MySQL) */
		private final String dbName = "mydb2";

		//Code from git to JSONINZE a Result
		public  String rStoJason(ResultSet rs) throws SQLException 
		{
		  if(rs.first() == false) {return "[]";} else {rs.beforeFirst();} // empty rs
		  StringBuilder sb=new StringBuilder();
		  Object item; String value;
		  java.sql.ResultSetMetaData rsmd = rs.getMetaData();
		  int numColumns = rsmd.getColumnCount();

		  sb.append("[{");
		  while (rs.next()) {

		    for (int i = 1; i < numColumns + 1; i++) {
		        String column_name = rsmd.getColumnName(i);
		        item=rs.getObject(i);
		        if (item !=null )
		           {value = item.toString(); value=value.replace('"', '\'');}
		        else 
		           {value = "null";}
		        sb.append("\"" + column_name+ "\":\"" + value +"\",");

		    }                                   //end For = end record

		    sb.setCharAt(sb.length()-1, '}');   //replace last comma with curly bracket
		    sb.append(",{");
		 }                                      // end While = end resultset

		 sb.delete(sb.length()-3, sb.length()); //delete last two chars
		 sb.append("}]");

		 return sb.toString();
		}
		
		public Connection getConnection() throws SQLException {
			Connection conn = null;
			Properties connectionProps = new Properties();
			connectionProps.put("user", this.userName);
			connectionProps.put("password", this.password);
			String connectionString = "jdbc:mysql://"+ this.serverName + ":" + this.portNumber + "/" + this.dbName;
			
			conn = DriverManager.getConnection(connectionString,
					connectionProps);
			return conn;
		}
		
		public boolean executeUpdate(Connection conn, String command) throws SQLException {
		    Statement stmt = null;
		    try {
		        stmt = conn.createStatement();
		        stmt.executeUpdate(command); // This will throw a SQLException if it fails
		        return true;
		    } finally {

		    	// This will run whether we throw an exception or not
		        if (stmt != null) { stmt.close(); }
		    }
		}
		
		//feed a book id and the connection and it returns the information for a particular book
		public String getBookInfo(Connection conn, String bookid) throws SQLException {
		    Statement stmt = null;
		    try {
		        stmt = conn.createStatement();
		        String sql = "SELECT * FROM mydb2.books WHERE bookid ='"  + bookid + "'";
		        ResultSet  rs = stmt.executeQuery(sql);
		        String response = rStoJason(rs);
		        rs.close();
		        return response;
		    } finally {

		    	// This will run whether we throw an exception or not
		        if (stmt != null) { stmt.close(); }
		    }
		}
		
		//Give it the connection and it returns a JSON string of the entire book table (all info)
		public String getAllBooks(Connection conn) throws SQLException {
		    Statement stmt = null;
		    try {
		        stmt = conn.createStatement();
		        String sql = "SELECT * FROM mydb2.books";
		        ResultSet  rs = stmt.executeQuery(sql);
		        //replace with gson
		        String response = rStoJason(rs);
		        //end replace with gson
		        rs.close();
		        return response;
		    } finally {
		    	// This will run whether we throw an exception or not
		        if (stmt != null) { stmt.close(); }
		    }
		}
		
		public String getPath(Connection conn, String string) throws SQLException {
		    Statement stmt = null;
		    try {
		    	String path = null;
		        stmt = conn.createStatement();
		        String sql = "SELECT * FROM mydb2.books WHERE bookid ='"  + string + "'";
		        ResultSet  rs = stmt.executeQuery(sql);
		        while(rs.next()){
		            //Retrieve by column name
		            path = rs.getString("path");
		         }
		        rs.close();
		        return path;
		    } finally {

		    	// This will run whether we throw an exception or not
		        if (stmt != null) { stmt.close(); }
		    }
		}
		
		//Used in development... doesn't really do anything useful
		public boolean getResult(Connection conn, String command) throws SQLException {
		    Statement stmt = null;
		    try {
		        stmt = conn.createStatement();
		        ResultSet  rs = stmt.executeQuery(command);
		        while(rs.next()){
		            //Retrieve by column name
		            int id  = rs.getInt("bookid");
		            String title = rs.getString("title");
		            String path = rs.getString("path");

		            //Display values
		            System.out.print("Book ID: " + id);
		            System.out.print(", Title: " + title);
		            System.out.println(", path: " + path);
		         }
		        rs.close();
		        return true;
		    } finally {

		    	// This will run whether we throw an exception or not
		        if (stmt != null) { stmt.close(); }
		    }
		}
		/**
		 * Connect to MySQL and do some stuff.
		 * @return 
		 */
		public String run() {

			// Connect to MySQL
			Connection conn = null;
			try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (SQLException e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
				return "Error Could Not Connect to the DB";
			}

			// Try two queries
			try { 
			    String getMeThisBook= "1";
				String newString = this.getBookInfo(conn, getMeThisBook);
				System.out.println("This is a book path:  " + newString);
				//Statement stmt = null;			
				String booklist = getAllBooks(conn);
				return(booklist);			
		    } catch (SQLException e) {
				System.out.println("ERROR: Could not get result");
				e.printStackTrace();
				return "Error Could Not Get Resutl";
			}
			
		}
		
		public String getListOfBooksAPI(){
			//Connect to DB
			Connection conn = null;
			try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (SQLException e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
				return "Error Could Not Connect to the DB";
			}
			//Get list of books
			try { 			
				String booklist = getAllBooks(conn);
				return(booklist);	
		    } catch (SQLException e) {
				System.out.println("System Out - ERROR: Could not get List of Books");
				e.printStackTrace();
				return "Returned - Error Could Not Get List Of Books";
			}
		}
	
		public String getABookAPI(String bookrequest){
			//Connect to DB
			Connection conn = null;
			try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (SQLException e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
				return "Error Could Not Connect to the DB";
			}
			//Get list of books
			try { 			
				String bookinfo = getBookInfo(conn, bookrequest);
				return(bookinfo);	
		    } catch (SQLException e) {
				System.out.println("System Out - ERROR: Could not get Info of a book");
				e.printStackTrace();
				return "Returned - ERROR: Could not get Info of a book";
			}
		}
		
		public String getABookPathAPI(String string){
			//Connect to DB
			Connection conn = null;
			try {
				conn = this.getConnection();
				System.out.println("Connected to database");
			} catch (SQLException e) {
				System.out.println("ERROR: Could not connect to the database");
				e.printStackTrace();
				return "Error Could Not Connect to the DB";
			}
			//Get list of books
			try { 			
				String bookinfo = getPath(conn, string);
				return(bookinfo);	
		    } catch (SQLException e) {
				System.out.println("System Out - ERROR: Could not get a path");
				e.printStackTrace();
				return "Returned - Error Could Not Get a path";
			}
		}
		
		
}
