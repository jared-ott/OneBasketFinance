package com.sdb; //DO NOT CHANGE THIS

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
		removeAllPortfolios();
		removeAllPersonEmail();
		removeAllBrokerStatus();
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Person";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.executeUpdate();
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
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
		String query = "SELECT personId FROM Person WHERE personCode = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int personId = -1;
		ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setString(1, personCode);
			rs = ps.executeQuery();
			
			if (rs.next()){
				personId = rs.getInt("personId");
			} else {
				Driver.logger.warning("Update failed: Person does not exist.");
				cm.closeAll(conn, ps, rs);
				return;
			}
			
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		query = "SELECT p.title FROM Portfolio p "
				+ "WHERE ? = ?";
		
		String [] list = {"p.ownerId", "p.managerId", "p.beneficiaryId"};
		ArrayList<ResultSet> resultArr = new ArrayList<ResultSet>();
		
		for (int i = 0 ; i < 3 ; i++){
			ps = cm.prepareStatement(conn, query);
			try{
				ps.setString(1, list[i]);
				ps.setInt(2, personId);
				resultArr.add(ps.executeQuery());
			} catch (Exception e) {
				Driver.logger.warning("Update failed: " + e.getMessage());
				cm.closeAll(conn, ps, rs);
				return;
			}
		}
		
		removePortfolioPerson(resultArr.get(0));
		removePortfolioPerson(resultArr.get(1));
		updateBeneficiary(resultArr.get(2));
		
		query = "DELETE FROM PersonEmail WHERE personId = ?";
		ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setInt(1, personId);
			ps.executeUpdate();
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		query = "DELETE FROM BrokerStatus WHERE personId = ?";
		ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setInt(1, personId);
			ps.executeUpdate();
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		query = "DELETE FROM Person WHERE personId = ?";
		ps = cm.prepareStatement(conn, query);
			
		try {
			ps.setInt(1, personId);
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		} 
		
		cm.closeAll(conn, ps, rs);
	}
	
	public static void removePortfolioPerson(ResultSet rs){
		try {
			while (rs.next()){
				removePortfolio(rs.getString("t.title"));
			}
			if (rs != null && !rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			return;
		}
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

	public static void addPerson(String personCode, String firstName, String lastName, String street, 
			String city, String state, String zip, String country, String brokerType, String secBrokerId) {
	
		if (personCode == null || lastName == null || city == null || street == null){
			Driver.logger.warning("Update failed: non-nullable values entered into parameters.");
			return;
		}
		
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT personId FROM Person WHERE personCode = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		
		int addressId = getAddressId(street, city, state, zip, country);
		int personId = 0;
		
		try{
			ps.setString(1, personCode);
			rs = ps.executeQuery();
			if (!rs.next()){
				query = "INSERT INTO Person (personCode, lastName, firstName, addressId) VALUES (?, ?, ?, ?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, personCode);
				ps.setString(2, lastName);
				ps.setString(3, firstName);
				ps.setInt(4, addressId);
				ps.executeUpdate();
				ps = cm.prepareStatement(conn, "SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				personId = rs.getInt("LAST_INSERT_ID()");
			} else {
				personId = rs.getInt("personId");
			}
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 198");
		}
		
		if(brokerType != null) {
			query = "SELECT brokerId FROM BrokerStatus WHERE personId = ?";
			ps = cm.prepareStatement(conn, query);
			try{
				ps.setInt(1, personId);
				rs = cm.getObjects(ps);
				if (!rs.next()){
					query = "INSERT INTO BrokerStatus (brokerType,secId,personId) VALUES (?, ?, ?)";
					ps = cm.prepareStatement(conn, query);
					ps.setString(1, brokerType);
					ps.setString(2, secBrokerId);
					ps.setInt(3, personId);
					ps.executeUpdate();
				} else {
					Driver.logger.warning("Update failed: Record already exists.");
				}
			} catch (Exception e){
				Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 220");
			}
		}
		cm.closeAll(conn, ps, rs);
		
	}
	
	private static int getAddressId(String street, String city, String state, String zip, String country) {
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT countryId FROM Country WHERE `name` = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		int countryId = 0;
		
		try{
			ps.setString(1, country);
			rs = cm.getObjects(ps);
			if (!rs.next()){
				System.out.println(rs.toString());
				query = "INSERT INTO Country (`name`) VALUES (?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, country);
				ps.executeUpdate();
				ps = cm.prepareStatement(conn, "SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				countryId = rs.getInt("LAST_INSERT_ID()");
			} else {
				countryId = rs.getInt("countryId");
			}
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 249");
		}
		
		query = "SELECT stateId FROM State WHERE `name` = ? AND countryId = ?";
		ps = cm.prepareStatement(conn, query);
		int stateId = 0;
		try{
			ps.setString(1, state);
			ps.setInt(2, countryId);
			rs = cm.getObjects(ps);
			if (!rs.next()){
				query = "INSERT INTO State (`name`, countryId) VALUES (?, ?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, state);
				ps.setInt(2, countryId);
				ps.executeUpdate();
				ps = cm.prepareStatement(conn, "SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				stateId = rs.getInt("LAST_INSERT_ID()");
			} else {
				stateId = rs.getInt("stateId");
			}
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 272");
		}
		
		query = "SELECT addressId FROM Address WHERE zipCode = ? AND streetAddress = ? AND city = ? AND stateId = ?";
		ps = cm.prepareStatement(conn, query);
		int addressId = 0;
		try{
			ps.setString(1, zip);
			ps.setString(2, street);
			ps.setString(3, city);
			ps.setInt(4, stateId);
			rs = cm.getObjects(ps);
			if (!rs.next()){
				query = "INSERT INTO Address (zipCode, streetAddress, city, stateId) VALUES (?, ?, ?, ?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, zip);
				ps.setString(2, street);
				ps.setString(3, city);
				ps.setInt(4, stateId);
				ps.executeUpdate();
				ps = cm.prepareStatement(conn, "SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				addressId = rs.getInt("LAST_INSERT_ID()");
			} else {
				addressId = rs.getInt("addressId");
			}
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 296");
		}
		cm.closeAll(conn, ps, rs);
		return addressId;
	}
	
	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personCode</code>
	 * @param personCode
	 * @param email
	 */
	
	public static void addEmail(String personCode, String email) {
		if(personCode == null || email == null){
			Driver.logger.warning("Update failed: non-nullable values entered as parameters.");
			return;
		}
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT emailId FROM Email WHERE address = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		int emailId = 0;
		
		try{
			ps.setString(1, email);
			rs = cm.getObjects(ps);
			if (!rs.next()){
				query = "INSERT INTO Email (address) VALUES (?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, email);
				ps.executeUpdate();
				ps = cm.prepareStatement(conn, "SELECT LAST_INSERT_ID()");
				rs = ps.executeQuery();
				rs.next();
				emailId = rs.getInt("LAST_INSERT_ID()");
			} else {
				emailId = rs.getInt("emailId");
			}
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		
		query = "SELECT personEmailId FROM PersonEmail WHERE personId = "
				+ "(SELECT personId FROM Person WHERE personCode = ?) AND emailId = ?";
		ps = cm.prepareStatement(conn, query);
		
		try{
			ps.setString(1, personCode);
			ps.setInt(2, emailId);
			rs = cm.getObjects(ps);
			if (!rs.next()){
				query = "INSERT INTO PersonEmail (personId, emailId) VALUES "
						+ "((SELECT personId FROM Person WHERE personCode = ?), ?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, personCode);
				ps.setInt(2, emailId);
				ps.executeUpdate();
			} else {
				Driver.logger.warning("Update failed: PersonEmail record already exists.");
			}
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		cm.closeAll(conn, ps, rs);
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
			cm.closeAll(conn, ps);
			return;
		}
		
		if (update){
			query = "DELETE FROM Asset";
			ps = cm.prepareStatement(conn, query);
			try {
				ps.executeUpdate();
			} catch (Exception e) {
				Driver.logger.warning("Update failed: " + e.getMessage());
				cm.closeAll(conn, ps);
				return;
			}
		} else {
			Driver.logger.warning("Update failed: AssetPortfolio was not emptied.");
			cm.closeAll(conn, ps);
			return;
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
		String query = "SELECT assetId FROM Asset WHERE assetCode = " + assetCode;
		PreparedStatement ps = cm.prepareStatement(conn, query);
		int assetId = -1;
		ResultSet rs = null;
		
		try{
			rs = cm.getObjects(ps);
			if(rs.next()){
				assetId = rs.getInt("assetId");
			} else {
				Driver.logger.warning("Update failed: Asset does not exist");
				cm.closeAll(conn, ps, rs);
				return;
			}
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		try {
			query = "DELETE FROM AssetPortfolio WHERE assetId = ?";
			ps = cm.prepareStatement(conn, query);
			ps.setInt(1, assetId);
			ps.executeUpdate();
			
			query = "DELETE FROM Asset WHERE assetId = ?";
			ps = cm.prepareStatement(conn, query);
			ps.setInt(1, assetId);
			ps.executeUpdate();
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
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
	public static void addDepositAccount(String assetCode, String label, double apr) {
		
		if (assetCode == null || label == null){
			Driver.logger.warning("Update failed: non-nullable values entered as parameters.");
			return;
		}
		
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT assetCode FROM Asset WHERE assetCode = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		
		try{
			ps.setString(1, assetCode);
			rs = cm.getObjects(ps);
			
			if (!rs.next()){
				query = "INSERT INTO Asset (assetType, assetCode, apr, label) VALUES (?, ?, ?, ?)";
				ps = cm.prepareStatement(conn, query);
				ps.setString(1, "D");
				ps.setString(2, assetCode);
				ps.setDouble(3, apr);
				ps.setString(4, label);
				ps.executeUpdate();
			} else {
				Driver.logger.warning("Update failed: Record already exists.");
				cm.closeAll(conn, ps, rs);
				return;
			}
			
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		cm.closeAll(conn, ps, rs);
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
	public static void addPrivateInvestment(String assetCode, String label, Double quarterlyDividend, 
			Double baseRateOfReturn, Double baseOmega, Double totalValue) {
		
		if (assetCode == null || label == null){
			Driver.logger.warning("Update failed: non-nullable values entered as parameters.");
			return;
		}
		
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT assetCode FROM Asset WHERE assetCode = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		try{
			ps.setString(1, assetCode);
			rs = cm.getObjects(ps);
			
			if (!rs.next()){
				query = "INSERT INTO Asset (assetType, assetCode, label, quarterlyDividend, rateOfReturn, risk, `value`) VALUES (?, ?, ?, ?, ?, ?, ?)";
				ps = cm.prepareStatement(conn, query);
				
				ps.setString(1, String.valueOf("P"));
				ps.setString(2, assetCode);
				ps.setString(3, label);
				ps.setDouble(4, quarterlyDividend);
				ps.setDouble(5, baseRateOfReturn);
				ps.setDouble(6, baseOmega);
				ps.setDouble(7, totalValue);
				ps.executeUpdate();
			} else {
				Driver.logger.warning("Update failed: Record already exists.");
				cm.closeAll(conn, ps, rs);
				return;
			}
			
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		cm.closeAll(conn, ps, rs);
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
	public static void addStock(String assetCode, String label, Double quarterlyDividend, 
			Double baseRateOfReturn, Double beta, String stockSymbol, Double sharePrice) {
		
		if (assetCode == null || label == null){
			Driver.logger.warning("Update failed: non-nullable values entered as parameters.");
			return;
		}
		
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT assetCode FROM Asset WHERE assetCode = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		
		try{
			ps.setString(1, assetCode);
			rs = cm.getObjects(ps);
			if (!rs.next()){
				query = "INSERT INTO Asset (assetCode, label, quarterlyDividend, rateOfReturn, risk, symbol, `value`, assetType) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				ps = cm.prepareStatement(conn, query);
				
				ps.setString(1, assetCode);
				ps.setString(2, label);
				ps.setDouble(3, quarterlyDividend);
				ps.setDouble(4, baseRateOfReturn);
				ps.setDouble(5, beta);
				ps.setString(6, stockSymbol);
				ps.setDouble(7, sharePrice);
				ps.setString(8, "S");
				ps.executeUpdate();
				
			} else {
				Driver.logger.warning("Update failed: Record already exists.");
				cm.closeAll(conn, ps, rs);
				return;
			}
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		cm.closeAll(conn, ps, rs);
	}

	/**
	 * Removes all portfolio records from the database
	 */
	public static void removeAllPortfolios() {
		removeAllAssetPortfolios();
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM Portfolio";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
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
		String query = "SELECT a.assetPortfolioId FROM AssetPortfolio a "
				+ "JOIN Portfolio p ON a.portfolioId = p.portfolioId WHERE "
				+ "p.portfolioId = (SELECT portfolioId FROM Portfolio WHERE title = ?)";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		
		try{
			ps.setString(1, portfolioCode);
			rs = cm.getObjects(ps);
			while (rs.next()){
				int id = rs.getInt("a.assetPortfolioId");
				removeAssetPortfolio(id);
			}
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage());
		}
		
		query = "DELETE FROM Portfolio WHERE title = ?";
		ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setString(1, portfolioCode);
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
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
		
		if(portfolioCode == null || ownerCode == null || managerCode == null){
			Driver.logger.warning("Update failed: non-nullable values entered as parameters.");
			return;
		}
		
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		PreparedStatement ps;
		ResultSet rs;
		String query;
		String[] personCodes = {ownerCode, managerCode, beneficiaryCode};
		Integer[] personIds = new Integer[3];
		
		for (int i = 0 ; i < 3 ; i++){
			query = "SELECT personId FROM Person WHERE personCode = ?";
			ps = cm.prepareStatement(conn, query);
			
			try{
				ps.setString(1, personCodes[i]);
				rs = ps.executeQuery();
				if (rs.next()){
					personIds[i] = rs.getInt("personId");
				} else if (i != 2){
					Driver.logger.warning("Update failed: Person " + personCodes[i] + " does not exist.");
					cm.closeAll(conn, ps, rs);
					return;
				} else if (i == 2){
					personIds[i] = null;
				}
			} catch (SQLException e){
				Driver.logger.warning("Update failed: Loop Value: " + i + e.getMessage() + " LINE 710");
				cm.closeAll(conn, ps);
				return;
			}
		}
		
		query = "SELECT portfolioId FROM Portfolio WHERE title = ?";
		ps = cm.prepareStatement(conn, query);
		try {
			ps.setString(1, portfolioCode);
			rs = cm.getObjects(ps);
			
			if(!rs.next()){
				if (personIds[2] != null){
					query = "INSERT INTO Portfolio (title, ownerId, brokerId, beneficiaryId) "
						+ "VALUES (?, ?, ?, ?)";
				} else {
					query = "INSERT INTO Portfolio (title, ownerId, brokerId, beneficiaryId) "
							+ "VALUES (?, ?, ?, null)";
				}
					ps = cm.prepareStatement(conn, query);
				
					ps.setString(1, portfolioCode);
					ps.setInt(2, personIds[0]);
					ps.setInt(3, personIds[1]);
				if (personIds[2] != null){
					ps.setInt(4, personIds[2]);
				} 
				ps.executeUpdate();
			} else {
				Driver.logger.warning("Update failed: Record already exists.");
			}
		} catch (Exception e) {
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 737");
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
		
		if (portfolioCode == null || assetCode == null || value < 0){
			Driver.logger.warning("Update failed: non-nullable values entered as parameters.");
			return;
		}
		
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "SELECT assetId FROM Asset WHERE assetCode = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		ResultSet rs = null;
		int assetId = -1;
		
		try {
			ps.setString(1, assetCode);
			rs = ps.executeQuery();
			if (rs.next()){
				assetId = rs.getInt("assetId");
			} else {
				Driver.logger.warning("Update failed: Asset does not exist");
				cm.closeAll(conn, ps, rs);
				return;
			}
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 779");
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		query = "SELECT portfolioId FROM Portfolio WHERE title = ?";
		ps = cm.prepareStatement(conn, query);
		int portfolioId = -1;
		
		try {
			ps.setString(1, portfolioCode);
			rs = ps.executeQuery();
			if (rs.next()){
				portfolioId = rs.getInt("portfolioId");
			} else {
				Driver.logger.warning("Update failed: Portfolio does not exist");
				cm.closeAll(conn, ps, rs);
				return;
			}
		} catch (SQLException e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 779");
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		query = "INSERT INTO AssetPortfolio (portfolioId, assetId, number) VALUES "
				+ "((SELECT portfolioId FROM Portfolio WHERE title = ?), ?, ?)";
		ps = cm.prepareStatement(conn, query);
		
		try{
			ps.setString(1, portfolioCode);
			ps.setInt(2, assetId);
			ps.setDouble(3, value);
			ps.executeUpdate();	
		} catch (Exception e){
			Driver.logger.warning("Update failed: " + e.getMessage() + " LINE 794");
			cm.closeAll(conn, ps, rs);
			return;
		}
		
		cm.closeAll(conn, ps, rs);
		return;
	}
	
	public static void updateBeneficiary(ResultSet rs){
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "UPDATE Portfolio SET beneficiary = null WHERE title = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			while(rs.next()){
				ps.setString(1, rs.getString("t.title"));
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps, rs);
			return;
		}
		cm.closeAll(conn, ps, rs);
	}
	
	public static void removeAssetPortfolio(int assetPortfolioId){
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM AssetPortfolio WHERE assetPortfolioId = ?";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.setInt(1, assetPortfolioId);
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
		}
		cm.closeAll(conn, ps);
	}
	
	public static void removeAllAssetPortfolios(){
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM AssetPortfolio";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
		}
		cm.closeAll(conn, ps);
	}
	
	public static void removeAllPersonEmail(){
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM PersonEmail";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
		}
		cm.closeAll(conn, ps);
	}
	
	public static void removeAllBrokerStatus(){
		ConnectionManager cm = new ConnectionManager();
		Connection conn = cm.getConnection();
		String query = "DELETE FROM BrokerStatus";
		PreparedStatement ps = cm.prepareStatement(conn, query);
		
		try {
			ps.executeUpdate();
		} catch (SQLException e) {
			Driver.logger.warning("Update failed: " + e.getMessage());
			cm.closeAll(conn, ps);
			return;
		}
		cm.closeAll(conn, ps);
	}
	
}