// OneBasketFinance 

// Ryan Wallace and Jared Ott

// v1.0  2017/2/8
// Creates classes from file input and outputs as classes in XML and JSON formats

package unl.cse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


public class Driver {
	// Main driver
	public static void main(String args[]) {
		ArrayList<String> personsList = processFile("data/Persons.dat");
		ArrayList<String> assetsList = processFile("data/Assets.dat");
		ArrayList<String> portfoliosList = processFile("data/Portfolios.dat");
		
		ArrayList<Person> persons = createPersons(personsList);	
		ArrayList<Asset> assets = createAssets(assetsList);
		ArrayList<Portfolio> portfolios = createPortfolios(portfoliosList, persons, assets);
		
//		createXML(persons, "person");
//		createXML(assets, "asset");
//		createJSON(persons, "persons");
//		createJSON(assets, "assets");
		
		printSummary(portfolios);
		
	}
	
	//Creates all assets from the Assets.dat file
	public static ArrayList<Asset> createAssets(ArrayList<String> list) {
		int lines = Integer.parseInt(list.get(0));
		
		ArrayList<Asset> assets = new ArrayList<Asset>();
		String line = "";
		for(int i=1; i<=lines; i++) {
			line = list.get(i);
			if(!line.trim().isEmpty()) {
				Asset a = null;
				String tokens[] = line.split(";");
				String code = tokens[0];
				String type = tokens[1];
				String label = tokens[2];
				if(type.equals("D")) {
					double apr = Double.parseDouble(tokens[3]);
					a = new DepositAccount(label, code, apr);
				
				} else {
					double qDiv = Double.parseDouble(tokens[3]);
					double rateOfReturn = Double.parseDouble(tokens[4]);
					
					if(type.equals("S")) {
						double beta = Double.parseDouble(tokens[5]);
						String symbol = tokens[6];
						double price = Double.parseDouble(tokens[7]);
						a = new Stock(label, code, qDiv, rateOfReturn, beta, symbol, price);
						
					} else if(type.equals("P")) {
						double omega = Double.parseDouble(tokens[5]);
						double totalVal = Double.parseDouble(tokens[6]);
						
						a = new PrivateInvestment(label, code, qDiv, rateOfReturn, omega, totalVal);
						
					} else {
						throw new IllegalArgumentException("Invalid Asset Type!");
					}
				}
				assets.add(a);
			}
		}
		
		return assets;
	}

	// Takes List of objects (assets or persons) and user-desired name of the class and turns into JSON formatted string
	public static void createJSON(ArrayList<Object> list, String className) {
		ObjectMapper mapper = new ObjectMapper();
		String output = "{\n\"" + className + "\": ";
		try {
			output += mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list) + "}";
			PrintWriter pw = new PrintWriter("data/" + className + ".json");
			pw.println(output);
			pw.close();
		} catch (JsonProcessingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	//Function creates an assetMap for each individual portfolio
	public static Map<Asset, Double> createMap(String assetInfo, ArrayList<Asset> allAssets) {
		Map<Asset, Double> assetMap = new HashMap<Asset, Double>();
		String [] assetTokens = assetInfo.split(",");
		
		for (String s : assetTokens){
			String [] asset = s.split(":");
			String assetCode = (String) asset[0];
			Double number = Double.parseDouble(asset[1]);
			
			Asset thisAsset = null;
			String assetType;
			
			//Find the correct asset
			for (Asset a : allAssets){
				if (assetCode.equals(a.getCode())){
					assetType = a.getType();
					
					if (!assetMap.containsKey(a)){
						if (assetType.equals("S")){
							thisAsset = (Stock)a;
						} else if (assetType.equals("P")){
							thisAsset = (PrivateInvestment)a;
						} else {
							thisAsset = (DepositAccount)a;
						}						
					} else {
						number += assetMap.get(a);
					}
				}
			}
			
			assetMap.put(thisAsset, number);
		}
		
		return assetMap;
	}
	
	//Function creates an ArrayList of Persons from the lines of the Persons.dat file
	public static ArrayList<Person> createPersons(ArrayList<String> list) {
		int lines = Integer.parseInt(list.get(0));
		
		ArrayList<Person> persons = new ArrayList<Person>();
		String line = "";
		for(int i=1; i<=lines; i++) {
			line = list.get(i);
			if(!line.trim().isEmpty()) {
				Person p = null;
				String tokens[] = line.split(";");
				String code = tokens[0];
				String brokerTokens[] = tokens[1].split(",");
				String nameTokens[] = tokens[2].split(",");
				String lastName = nameTokens[0].trim();
				String firstName = nameTokens[1].trim();
				String addressTokens[] = tokens[3].split(",");
				Address address = new Address(addressTokens[0], addressTokens[1], addressTokens[2]
						, addressTokens[3], addressTokens[4]);				
				ArrayList<String> emails = new ArrayList<String>();
				if(tokens.length == 5) {
					String[] temp = tokens[4].split(",");
					for(String email : temp) {
						emails.add(email);
					}
				}
				
				if(brokerTokens.length == 2) {
					BrokerType type = null;
					if(brokerTokens[0].equals("E")) {
						type = BrokerType.EXPERT;
					} else {
						type = BrokerType.JUNIOR;
					}
					String secID = brokerTokens[1];
					
					p = new Broker(code, lastName, firstName, address, emails, secID, type);
					
				} else {
					p = new Person(code, lastName, firstName, address, emails);
				}
				
				persons.add(p);
			}
		}
		
		return persons;
	}
	
	//This function creates all portfolios, and returns an ArrayList of all the portfolios
	public static ArrayList<Portfolio> createPortfolios(ArrayList<String> list, ArrayList<Person> allPersons, ArrayList<Asset> allAssets){
		int lines = Integer.parseInt(list.get(0));
		
		ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();
		String line = "";
		for(int i=1; i<=lines; i++) {
			line = list.get(i);
			if(!line.trim().isEmpty()) {
				Portfolio p = null;
				String [] tokens = line.split(";");
				String code = tokens[0];
				String ownerCode = tokens[1];
				Person owner = findPerson(allPersons, ownerCode);
				String managerCode = tokens[2];
				Person manager = findPerson(allPersons, managerCode);
				String beneficiaryCode;
				String assetInfo;
				
				/*For some reason, some portfolios with repeated empty data entries were giving us issues
				 * so we decided to use a try catch block to fix our null pointer exception
				 */
				try {
					beneficiaryCode = tokens[3];
					assetInfo = tokens[4];
				} catch (Exception e){
					beneficiaryCode = "";
					assetInfo = "";
				}
				
				Map<Asset, Double> assetMap;
				//Some portfolios do not have assets, so we perform a check
				if (!assetInfo.equals("")){
					assetMap = createMap(assetInfo, allAssets);					
				} else {
					assetMap = null;
				}
				
				Person beneficiary;
				//Not all portfolios have beneficiary, so we check what that String is
				if (!beneficiaryCode.equals("")){	
					beneficiary = findPerson(allPersons, beneficiaryCode);
				} else {
					beneficiary = null;
				}
				
				//Create new portfolio and add it to the list
				p = new Portfolio(code, owner, manager, beneficiary, assetMap);
				portfolios.add(p);
			}
		}
		
		return portfolios;
	}
	
	// Takes List of objects (assets or persons) and user-desired name of the class and turns into XML formatted string
	public static void createXML(ArrayList<Object> list, String className) {
		
		ObjectMapper mapper = new XmlMapper();
		String output = "<?xml version=\"1.0\" encoding = \"UTF-8\" ?>\n";
		Map<String, ArrayList<Object>> map = new HashMap<String, ArrayList<Object>>();
		
		map.put(className, list);
		
		try {
			output += mapper.writerWithDefaultPrettyPrinter().withRootName(className + "s").writeValueAsString(map);
			PrintWriter pw = new PrintWriter("data/" + className + "s.xml");
			pw.println(output);
			pw.close();
		} catch (JsonProcessingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
	
	//Function takes a person code and the arrayList of persons and matches the code to the person
	public static Person findPerson(ArrayList<Person> allPersons, String personCode){
		for (Person p : allPersons){
			if(p.getCode().equals(personCode)){
				return p;
			}
		}
		return null;
	}
	
	//Function prints the portfolio summary given an ArrayList of portfolios
	public static void printSummary(ArrayList<Portfolio> portfolioList){
		String title1 = "Portfolio";
		String title2 = "Owner";
		String title3 = "Manager";
		String title4 = "Fees";
		String title5 = "Commissions";
		String title6 = "Weighted Risk";
		String title7 = "Return";
		String title8 = "Total";
		double totalFees = 0;
		double totalCommissions = 0;
		double totalReturn = 0;
		double totalTotal = 0;
		
		
		System.out.println("Portfolio Summary Report");
		
		for (int i = 1 ; i <= 150 ; i++){
			System.out.print("=");
		}
		
		PortfolioComparator c = new PortfolioComparator();
		portfolioList.sort(c);
		
		System.out.printf("\n%-10s %-25s %-25s %15s %15s %15s %15s %15s\n",
				title1, title2, title3, title4, title5, title6, title7, title8);
		
		//Foreach loop that prints out the basic info for each portfolio
		for (Portfolio p : portfolioList){
			ArrayList<Object> info = p.getBasicInfo();
			System.out.printf("%-10s %-25s %-25s $%14.2f $%14.2f %15.4f $%14.2f $%14.2f\n",
				(String)info.get(0), ((Person)info.get(1)).getNameStringLF(), ((Person)info.get(2)).getNameStringLF(), (Double)info.get(3),
				(Double)info.get(4), (Double)info.get(5), (Double)info.get(6), (Double)info.get(7));
			
			//Adds basic info from each portfolio into the totals for the summary
			totalFees += (Double)info.get(3);
			totalCommissions += (Double)info.get(4);
			totalReturn += (Double)info.get(6);
			totalTotal += (Double)info.get(7);
		}
		
		for (int i = 1 ; i <= 150 ; i++){
			System.out.print("-");
		}
		
		System.out.printf("\n%62s $%14.2f $%14.2f %15s $%14.2f $%14.2f\n ", 
				"Totals:", totalFees, totalCommissions, "", totalReturn, totalTotal);
		System.out.println("\n\n\nPortfolio Details");
		
		for (int i = 1 ; i <= 150 ; i++){
			System.out.print("=");
		}
		
		//Foreach loop calls the printPortfolio method for each portfolio
		for (Portfolio p : portfolioList){
			System.out.println(p.printPortfolio());
		}
		
		
	}
	
	// Stores and outputs each line of a file into Strings in an ArrayList
	public static ArrayList<String> processFile(String filename) {
		
		File f = new File(filename);
		
		ArrayList<String> output = new ArrayList<String>();
		
		Scanner s = null;
		
		try {
			s = new Scanner(f);
			
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		while(s.hasNext()) {
			output.add(s.nextLine());
		}
		
		s.close();
		
		return output;
	}
	
	//rs is a left join from Person to BrokerStatus
	public static ArrayList<Person> readPersons(ResultSet rs){
		String code;
		String firstName;
		String lastName;
		Address address;
		ArrayList<String> emails = new ArrayList<String>();
		String secID;
		BrokerType type;
		
		ArrayList<Person> persons = new ArrayList<Person>();
		
		try {
			while (rs.next()){
				code = "PS" + Integer.toString(rs.getInt("personId"));
				firstName = rs.getString("firstName");
				lastName = rs.getString("lastName");
				
				ConnectionManager cm = new ConnectionManager();
				Connection conn = cm.getConnection();
				String query = "";
				PreparedStatement ps = cm.prepareStatement(conn, query);
				ResultSet rs2 = ps.executeQuery();
				
				while (rs2.next()){
					emails.add(rs2.getString("e.address"));
				}
				
				query = "";
				ps = cm.prepareStatement(conn, query);
				rs2 = ps.executeQuery();
				
				rs2.next();
				String streetAddress = rs2.getString("address");
				String zipCode = rs2.getString("a.zipCode");
				String city = rs2.getString("a.city");
				String state = rs2.getString("s.name");
				String country = rs2.getString("c.name");
				
				address = new Address(streetAddress, city, state, zipCode, country);
				
				if (rs.getString("secId") != null){
					secID = rs.getString("secId");
					if ("E" == rs.getString("brokerType")){
						type = BrokerType.EXPERT;
					} else {
						type = BrokerType.JUNIOR;
					}
					persons.add(new Broker(code, lastName, firstName, address, emails, secID, type));
				} else {
					persons.add(new Person(code, lastName, firstName, address, emails));
				}
			}
		} catch (SQLException e) {
			//TODO LOG ERROR
			e.printStackTrace();
		}
		return persons;
	}
	
	public static ArrayList<Asset> readAssets(ResultSet rs){
		String label;
		String code;
		String type;
		double apr;
		double quarterlyDividend;
		double baseRateOfReturn;
		double risk;
		String stockSymbol;
		double sharePrice;
		double value;
		
		ArrayList<Asset> assets = new ArrayList<Asset>();
		try {
			while (rs.next()){
				code = "AST" + Integer.toString(rs.getInt("assetId"));
				label = rs.getString("label");
				type = rs.getString("assetType");
				
				if (type == "D"){
					apr = rs.getDouble("apr");
					assets.add(new DepositAccount(label, code, apr));
				} else {
					quarterlyDividend = rs.getDouble("quarterlyDividend");
					baseRateOfReturn = rs.getDouble("rateOfReturn");
					risk = rs.getDouble("risk");
					value = rs.getDouble("value");
				
					if (type == "S"){
						stockSymbol = rs.getString("symbol");
						assets.add(new Stock(label, code, quarterlyDividend, baseRateOfReturn, risk, stockSymbol, value));
					} else {
						assets.add(new PrivateInvestment(label, code, quarterlyDividend, baseRateOfReturn, risk, value));
					}
				}
			}
		} catch (SQLException e) {
			// TODO LOG ERROR
			e.printStackTrace();
		}
		
		return assets;
	}
	
	public static ArrayList<Portfolio> readPortfolios (ResultSet rs, ArrayList<Person> persons, ArrayList<Asset> assets){
		String code;
		Integer ownerId;
		Integer brokerId;
		Integer beneficiaryId;
		Person owner;
		Person broker;
		Person beneficiary = null;
		Integer keyId;
		Integer assetId;
		Map<Asset, Double> assetMap = new HashMap<Asset, Double>();
		
		Map<Integer, Person> personKeys = new HashMap<Integer, Person>();
		for (Person p : persons){
			keyId = Integer.parseInt(p.code.substring(2));
			personKeys.put(keyId, p);
		}
		
		Map<Integer, Asset> assetKeys = new HashMap<Integer, Asset>();
		for (Asset a : assets){
			keyId = Integer.parseInt(a.code.substring(2));
			assetKeys.put(keyId, a);
		}
		
		ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();
		try {
			while (rs.next()){
				code = "PRT" + Integer.toString(rs.getInt("portfolioId"));
				ownerId = rs.getInt("ownerId");
				brokerId = rs.getInt("brokerId");
				beneficiaryId = rs.getInt("beneficiaryId");
				owner = personKeys.get(ownerId);
				broker = personKeys.get(brokerId);
				
				if (beneficiaryId != null){
					beneficiary = personKeys.get(beneficiaryId);
				}
				//Subquery for assetPortfolio
				ConnectionManager cm = new ConnectionManager();
				Connection conn = cm.getConnection();
				String query = "";
				PreparedStatement ps = cm.prepareStatement(conn, query);
				ResultSet rs2 = ps.executeQuery();
				
				while (rs2.next()){
					assetMap.put(assetKeys.get(rs2.getInt("assetId")), (Double)rs2.getDouble("number"));
				}
				cm.closeAll(conn, ps, rs2);
				
				portfolios.add(new Portfolio(code, owner, broker, beneficiary, assetMap));
			}
		} catch (SQLException e) {
			// TODO LOG ERROR
			e.printStackTrace();
		}
		return portfolios;
	}
	
}
