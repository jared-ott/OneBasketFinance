package unl.cse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionManager {

	public Connection getConnection(){
		String url = "jdbc:mysql://cse.unl.edu/rwallace";
		String u = "rwallace";
		String p = "L7f:rz";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			System.out.println("InstantiationException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException: ");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Connection conn = null;
		
		try { 
			conn = DriverManager.getConnection(url, u, p);
		} catch (SQLException e){
			//TODO: LOG ERROR
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public void closeAll(Connection conn, PreparedStatement ps){
		this.closeAll(conn, ps, null);
	}
	
	public void closeAll(Connection conn, PreparedStatement ps, ResultSet rs){
		try {
			if (conn != null && !conn.isClosed()){
				conn.close();				
			}
			if (ps != null && !ps.isClosed()){
				ps.close();
			}
			if (rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e){
			//TODO: LOG ERROR
			e.printStackTrace();
		}
	}

}
