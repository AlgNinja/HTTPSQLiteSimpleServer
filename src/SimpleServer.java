import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;	
/**
 * <p>
 * Title: Simple HTTP Server
 * </p>
 * 
 * <p>
 * Description: HTTP server that uses built in java classes and SQLite to create a table from a dataset and output it to the client
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * 
 * @author Christopher Koshy
 * 
 */


public class SimpleServer {
	/**
	 * Main Method
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		//Connection with a SQLite database
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:ssa-babynames-for-2015.sqlite");
			System.out.println("Database connected!");
			//very simple SQL statement
			String select = "SELECT * from babynames";
			Statement stmt = conn.createStatement();
			//The ResultSet class allows you process data easily
			ResultSet rs = stmt.executeQuery(select);
			//The DatabaseMeta class allows you to get a ton of information that adds clarity for both yourself without staring at the db file and allows you to output the data better
			DatabaseMetaData md = conn.getMetaData();
			//you can also get tables, use % if you want to get all
			ResultSet r = md.getColumns(null, null, "babynames", null);
			//Server side socket
			final ServerSocket server = new ServerSocket(8080);
			System.out.println("Listening for connection on port 8080...");
			while(true) {
				//Client side socket
				try(Socket client = server.accept()) {
					StringBuilder httpResponse = new StringBuilder();
					httpResponse.append("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
					httpResponse.append("<html><body><h1>Social Security Administration Baby Names 2015</h1><table border='1'>");
					while(r.next()) {
						httpResponse.append("<th>"+r.getString(4).trim()+"</th>");
					}
					while(rs.next()) {
						//adds columns 1 through 7 for each row
						httpResponse.append("<tr>");
						for(int i = 1; i <= 7; i++)
							httpResponse.append("<td>" + rs.getString(i).trim() + "</td>");
						httpResponse.append("</tr>");
						
					}
					httpResponse.append("</table></body></html>");
					//writes to client side output stream our string as bytes
					client.getOutputStream().write(httpResponse.toString().getBytes());
				}
					
				
			}
		//exception for if there is no database
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
}

