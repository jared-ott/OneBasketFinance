package com.sdb; //DO NOT CHANGE THIS

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import unl.cse.ConnectionManager;
import unl.cse.Driver;

/**
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */
public class PortfolioData {

	/**
	 * Method that removes every person record from the database
	 */
	public static void removeAllPersons() {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Person";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.executeUpdate();
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}

	/**
	 * Removes the person record from the database corresponding to the
	 * provided <code>personCode</code>
	 * @param personCode
	 */
	
	public static void removePerson(String personCode) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Person WHERE code = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setString(1, personCode);
			ps.executeUpdate();
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Method to add a person record to the database with the provided data. The
	 * <code>brokerType</code> will either be "E" or "J" (Expert or Junior) or 
	 * <code>null</code> if the person is not a broker.
	 * @param personCode
	 * @param firstName
	 * @param lastName
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @param brokerType
	 */
	
	//TODO: Double insert into address and person. Look up how to retrieve key for previous insert
	public static void addPerson(String personCode, String firstName, String lastName, String street, 
			String city, String state, String zip, String country, String brokerType, String secBrokerId) {
		
	}
	
	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personCode</code>
	 * @param personCode
	 * @param email
	 */
	//TODO: Is there a join table? Finish this method
	public static void addEmail(String personCode, String email) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "INSERT INTO Email (address) VALUES ";
		PreparedStatement ps = cm.prepareStatement(conn, query);
	}

	/**
	 * Removes all asset records from the database
	 */
	public static void removeAllAssets() {
		removeAllPortfolios();
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM AssetPortfolio";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		boolean update = false;
		
		try {
			ps.executeUpdate();
			update = true;
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		
		if (update){
			query = "DELETE FROM Asset";
			ps = cm.prepareStatement(conn, query);
			try {
				ps.executeUpdate();
			} catch (Exception e) {
				Driver.logger.warning("Update failed: " + e.getMessage());
			}
		} else {
			Driver.logger.warning("Update failed: AssetPortfolio was not emptied.");
		}
		cm.closeAll(conn, ps);
		return;
	}

	/**
	 * Removes the asset record from the database corresponding to the
	 * provided <code>assetCode</code>
	 * @param assetCode
	 */
	public static void removeAsset(String assetCode) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Asset WHERE code = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setString(1, assetCode);
			ps.executeUpdate();
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Adds a deposit account asset record to the database with the
	 * provided data. 
	 * @param assetCode
	 * @param label
	 * @param apr
	 */
	//TODO: Check for good parameters
	public static void addDepositAccount(String assetCode, String label, double apr) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "INSERT INTO Asset (code, label, apr) VALUES ?, ?, ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		//TODO: Check all is right
		try {
			ps.setString(1, assetCode);
			ps.setString(2, label);
			ps.setDouble(3, apr);
			ps.executeUpdate();
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Adds a private investment asset record to the database with the
	 * provided data.  The <code>baseRateOfReturn</code> is assumed to be on the
	 * scale [0, 1].
	 * @param assetCode
	 * @param label
	 * @param quarterlyDividend
	 * @param baseRateOfReturn
	 * @param baseOmega
	 * @param totalValue
	 */
	//TODO: Check for good parameters
	public static void addPrivateInvestment(String assetCode, String label, Double quarterlyDividend, 
			Double baseRateOfReturn, Double baseOmega, Double totalValue) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "INSERT INTO Asset (code, label, quarterlyDividend, baseRateOfReturn, risk, value) VALUES ?, ?, ?, ?, ?, ?, ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		//TODO: Check all is right
		try {
			ps.setString(1, assetCode);
			ps.setString(2, label);
			ps.setDouble(3, quarterlyDividend);
			ps.setDouble(4, baseRateOfReturn);
			ps.setDouble(5, baseOmega);
			ps.setDouble(6, totalValue);
			ps.executeUpdate();
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Adds a stock asset record to the database with the
	 * provided data.  The <code>baseRateOfReturn</code> is assumed to be on the 
	 * scale [0, 1].
	 * @param assetCode
	 * @param label
	 * @param quarterlyDividend
	 * @param baseRateOfReturn
	 * @param beta
	 * @param stockSymbol
	 * @param sharePrice
	 */
	//TODO: Parameters good
	public static void addStock(String assetCode, String label, Double quarterlyDividend, 
			Double baseRateOfReturn, Double beta, String stockSymbol, Double sharePrice) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "INSERT INTO Asset (code, label, quarterlyDividend, baseRateOfReturn, risk, symbol, price) VALUES ?, ?, ?, ?, ?, ?, ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		//TODO: Check all is right
		try {
			ps.setString(1, assetCode);
			ps.setString(2, label);
			ps.setDouble(3, quarterlyDividend);
			ps.setDouble(4, baseRateOfReturn);
			ps.setDouble(5, beta);
			ps.setString(6, stockSymbol);
			ps.setDouble(7, sharePrice);
			ps.executeUpdate();
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}

	/**
	 * Removes all portfolio records from the database
	 */
	public static void removeAllPortfolios() {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Portfolio";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Removes the portfolio record from the database corresponding to the
	 * provided <code>portfolioCode</code>
	 * @param portfolioCode
	 */
	public static void removePortfolio(String portfolioCode) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Portfolio WHERE portfolioCode = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setString(1, portfolioCode);
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Adds a portfolio records to the database with the given data.  If the portfolio has no
	 * beneficiary, the <code>beneficiaryCode</code> will be <code>null</code>
	 * @param portfolioCode
	 * @param ownerCode
	 * @param managerCode
	 * @param beneficiaryCode
	 */
	public static void addPortfolio(String portfolioCode, String ownerCode, String managerCode, String beneficiaryCode) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "INSERT INTO Portfolio (code, ownerCode, managerCode, beneficiaryCode) "
				+ "VALUES ?, ?, ?, ?";
		
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setString(1, portfolioCode);
			ps.setString(2, ownerCode);
			ps.setString(3, managerCode);
			ps.setString(4, beneficiaryCode);	
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps);
	}
	
	/**
	 * Associates the asset record corresponding to <code>assetCode</code> with the 
	 * portfolio corresponding to the provided <code>portfolioCode</code>.  The third 
	 * parameter, <code>value</code> is interpreted as a <i>balance</i>, <i>number of shares</i>
	 * or <i>stake percentage</i> (on the scale [0, 1]) depending on the type of asset the <code>assetCode</code> is
	 * associated with.  
	 * @param portfolioCode
	 * @param assetCode
	 * @param value
	 */
	public static void addAsset(String portfolioCode, String assetCode, double value) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "INSERT INTO AssetPortfolio (portfolioCode, assetCode, number) VALUES ?, ?, ?";
		
		PreparedStatement ps = cm.prepareStatement(conn, query);
		try{
			ps.setString(1, portfolioCode);
			ps.setString(2, assetCode);
			ps.setDouble(3, value);
			ps.executeUpdate();	
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		
		cm.closeAll(conn, ps);
		return;
	}
	
	
}
