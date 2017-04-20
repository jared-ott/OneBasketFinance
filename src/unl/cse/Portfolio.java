package unl.cse;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {
	private String portfolioCode;
	private Person owner;
	private Person manager;
	private Person beneficiary;
	private Map<Asset, Double> assetMap;
	private double fees;
	private double commissions;
	private double aggregateRisk;
	private double expectedReturn;
	private double totalValue;
	
	//Constructor that calculates the basic info
	public Portfolio(String portfolioCode, Person owner, Person manager, Person beneficiary, Map<Asset, Double> assetMap) {
		super();
		this.portfolioCode = portfolioCode;
		this.owner = owner;
		this.manager = manager;
		this.beneficiary = beneficiary;
		this.assetMap = assetMap;
		
		if (!(assetMap == null)){
			calculateAssetInfo(assetMap);			
		}
		
		this.commissions = ((Broker)manager).calculateCommission(((Broker)manager).getType(), this.expectedReturn);
		try{
			this.fees = ((Broker)manager).calculateFees(((Broker)manager).getType(), this.assetMap.size());
		} catch (Exception e) {
			this.fees = 0;
		}
	}	
	
	public double getTotalValue() {
		return totalValue;
	}

	public Person getManager() {
		return manager;
	}

	//Calculates basic info for the portfolio
	private void calculateAssetInfo(Map<Asset, Double> assetMap){
		this.aggregateRisk = 0;
		this.expectedReturn = 0;
		this.totalValue = 0;
		
		for (Asset a : assetMap.keySet()){
			this.expectedReturn += a.getExpectedReturn(assetMap.get(a));
			this.totalValue += a.getTotalValue(assetMap.get(a));
		}
		
		this.aggregateRisk = calculateRisk();
	}
	
	//Calculates the aggregate risk of the entire portfolio
	private double calculateRisk() {
		double totalRisk = 0.0;
		double risk = 0.0;
		double totalValues = 0.0;
		Map<Double, Double> values = new HashMap<Double, Double>();
		
		for(Asset a : assetMap.keySet()) {
			double value = a.getTotalValue(assetMap.get(a));
			risk = a.getRiskType(value);
			values.put(value , risk);
			totalValues += value;
		}
		
		for(Double v : values.keySet()) {
			double r = values.get(v);
			totalRisk += r*v/totalValues;
		}		
		
		return totalRisk;
	}
	
	//Returns the basicInfo of each portfolio
	public MyList<Object> getBasicInfo(){
		MyList<Object> list = new MyList<Object>();
		
		list.add(this.portfolioCode);
		list.add(this.owner);
		list.add(this.manager);
		list.add(this.fees);
		list.add(this.commissions);
		list.add(this.aggregateRisk);
		list.add(this.expectedReturn);
		list.add(this.totalValue);
		
		return list;
	}
	
	public Person getOwner() {
		return owner;
	}
	
	//Returns a String with the info from each portfolio
	public String printPortfolio(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("\nPortfolio " + this.portfolioCode + "\n")
		.append("------------------------------------------\n")
		.append("Owner:          " + this.owner.getNameStringLF() + "\n")
		.append("Manager:        " + this.manager.getNameStringLF() + "\n");
		
		if(this.beneficiary != null) {
			sb.append("Beneficiary:    " + this.beneficiary.getNameStringLF() + "\n");
		}
		
		sb.append("Assets\n");
		sb.append("Code       Asset                           Return Rate          Risk  Annual Return          Value\n");

		if (assetMap != null){
			for(Asset a : assetMap.keySet()){
				
				double value = a.getTotalValue(assetMap.get(a));
				double risk = a.getRiskType(value);
				double retRate = a.calculateROR(assetMap.get(a));				
				double annReturn = a.getExpectedReturn(assetMap.get(a));
				
				sb.append((String.format("%-10s %-31s %10.2f%% %13.4f  $%12.2f  $%12.2f\n", 
						a.code, a.label, retRate, risk, annReturn, value)));

			}	
		}
		
		sb.append("                                                        --------------------------------------------\n")
		.append("                                                Totals ")
		.append(String.format("%13.4f  $%12.2f  $%12.2f\n", 
				this.aggregateRisk, this.expectedReturn, this.totalValue));
		
		
		return sb.toString();
	}
}
