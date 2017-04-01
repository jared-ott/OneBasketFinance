package unl.cse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.media.jfxmedia.logging.Logger;

public class ConnectionManager {
	public Connection getConnection(){
		String url = "jdbc:mysql://cse.unl.edu/rwallace";
		String u = "rwallace";
		String p = "L7f:rz";
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			System.out.println("InstantiationException: ");
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			System.out.println("IllegalAccessException: ");
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException: ");
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		Connection conn = null;
		
		try { 
			conn = DriverManager.getConnection(url, u, p);
		} catch (SQLException e){
			//TODO: LOG ERROR
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
		}
		
		return conn;
	}
	
	public PreparedStatement prepareStatement(Connection conn, String query){
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(query);
		} catch (SQLException e) {
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
		}
		return ps;
	}
	
	public ResultSet getObjects(PreparedStatement ps){
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			// TODO LOG ERROR
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
		}
		return rs;
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
			Driver.logger.warning("YA DONE GOOFED " + e.getMessage());
		}
	}

}
