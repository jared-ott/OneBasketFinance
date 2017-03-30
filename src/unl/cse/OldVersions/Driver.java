// OneBasketFinance 

// Ryan Wallace and Jared Ott

// v1.0  2017/2/8
// Creates classes from file input and outputs as classes in XML and JSON formats

package unl.cse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
	
	// Takes List of objects (assets or persons) and user-desired name of the class and turns into XML formatted string
	public static void createXML(ArrayList<? extends XMLable> list, String className) {
		
		ObjectMapper mapper = new XmlMapper();
		String output = "<?xml version=\"1.0\" encoding = \"UTF-8\" ?>\n";
		Map<String, ArrayList<? extends XMLable>> map = new HashMap<String, ArrayList<? extends XMLable>>();
		
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
	
	// Takes List of objects (assets or persons) and user-desired name of the class and turns into JSON formatted string
	public static void createJSON(ArrayList<? extends JSONable> list, String className) {
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
				
				try {
					beneficiaryCode = tokens[3];
					assetInfo = tokens[4];
				} catch (Exception e){
					beneficiaryCode = "";
					assetInfo = "";
				}
				
				Map<Asset, Double> assetMap;
				
				if (!assetInfo.equals("")){
					assetMap = createMap(assetInfo, allAssets);					
				} else {
					assetMap = null;
				}
				
				Person beneficiary;
				if (!beneficiaryCode.equals("")){
					beneficiary = findPerson(allPersons, beneficiaryCode);
				} else {
					beneficiary = null;
				}
				
				p = new Portfolio(code, owner, manager, beneficiary, assetMap);
				portfolios.add(p);
			}
		}
		
		return portfolios;
	}
	
	public static Map<Asset, Double> createMap(String assetInfo, ArrayList<Asset> allAssets) {
		Map<Asset, Double> assetMap = new HashMap<Asset, Double>();
		String [] assetTokens = assetInfo.split(",");
		
		for (String s : assetTokens){
			String [] asset = s.split(":");
			String assetCode = asset[0];
			Double number = Double.parseDouble(asset[1]);
			
			Asset thisAsset = null;
			String assetType;
			for (Asset a : allAssets){
				if (assetCode.equals(a.getCode())){
					assetType = a.getType();
					
					if (assetType.equals("S")){
						thisAsset = a;
					} else if (assetType.equals("P")){
						thisAsset = a;
					} else {
						thisAsset = a;
					}
				}
			}
			
			assetMap.put(thisAsset, number);
		}
		
		return assetMap;
	}
	
	public static Person findPerson(ArrayList<Person> allPersons, String personCode){
		for (Person p : allPersons){
			if(p.getCode().equals(personCode)){
				return p;
			}
		}
		return null;
	}
	
	public static void printSummary(ArrayList<Portfolio> portfolioList){
		String title1 = "Portfolio";
		String title2 = "Owner";
		String title3 = "Manager";
		String title4 = "Fees";
		String title5 = "Commissions";
		String title6 = "Weighted Risk";
		String title7 = "Return";
		String title8 = "Total";
		
		System.out.println("Portfolio Summary Report");
		
		for (int i = 1 ; i <= 150 ; i++){
			System.out.print("=");
		}
		PortfolioComparator c = new PortfolioComparator();
		portfolioList.sort(c);
		
		System.out.printf("\n%-10s %-25s %-25s %-15s %-15s %15s %-15s %-15s\n",
				title1, title2, title3, title4, title5, title6, title7, title8);
		for (Portfolio p : portfolioList){
			ArrayList<Object> info = p.getBasicInfo();
			System.out.printf("%-10s %-25s %-25s $%12.2f $%12.2f %13.2f $%12.2f $%12.2f\n",
				info.get(0), ((Person)info.get(1)).getNameStringLF(), ((Person)info.get(2)).getNameStringLF(), info.get(3),
				info.get(4), info.get(5), info.get(6), info.get(7));
		}
		
		System.out.println("\n\n\nPortfolio Details");
		
		for (int i = 1 ; i <= 150 ; i++){
			System.out.print("=");
		}
		
		for (Portfolio p : portfolioList){
			System.out.println(p.printPortfolio());
		}
		
		
	}
	
}
