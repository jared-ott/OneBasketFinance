package unl.cse;

import java.util.ArrayList;
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
	
	public Portfolio(String portfolioCode, Person owner, Person manager, Person beneficiary, Map<Asset, Double> assetMap) {
		super();
		this.portfolioCode = portfolioCode;
		this.owner = owner;
		this.manager = manager;
		this.beneficiary = beneficiary;
		this.assetMap = assetMap;
		if (!(assetMap == null)){
			calculateInfo(assetMap);			
		}
		this.commissions = calculateCommission(manager);
		this.fees = calculateFees(manager);
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Portfolio(String portfolioCode, Person owner, Person manager, Map<Asset, Double> assetMap) {
		new Portfolio(portfolioCode, owner, manager, (Person)null, assetMap);
	}

	private void calculateInfo(Map<Asset, Double> assetMap){
		this.fees = 0;
		this.commissions = 0;
		this.aggregateRisk = 0;
		this.expectedReturn = 0;
		this.totalValue = 0;
		
		for (Asset a : assetMap.keySet()){
			this.expectedReturn += getExpectedReturn(a, assetMap.get(a));
			this.totalValue += getTotalValue(a, assetMap.get(a));
			this.aggregateRisk = calculateRisk();
		}
	}
	
	private double getExpectedReturn(Asset asset, double number){
		double expectedReturn;
		String type = asset.getType();
		
		if (type.equals("D")){
			expectedReturn = number * ((DepositAccount)asset).getRateOfReturn()/100;
		} else if (type.equals("P")) {
			expectedReturn = ((PrivateInvestment)asset).getRateOfReturn()/100 * ((PrivateInvestment)asset).getTotalValue() 
					+ 4 * ((PrivateInvestment)asset).getQuarterlyDividend();
		} else {
			expectedReturn = (((Stock)asset).getRateOfReturn()/100 * ((Stock)asset).getSharePrice() 
					+ 4 * ((Stock)asset).getQuarterlyDividend()) * number;
		}
		
		return expectedReturn;
	}
	
	private double getTotalValue(Asset asset, double number){
		double totalValue;
		String type = asset.getType();
		
		if (type.equals("D")){
			totalValue = number;
		} else if (type.equals("P")) {
			totalValue = number/100*((PrivateInvestment)asset).getTotalValue();
		} else {
			totalValue = ((Stock)asset).getSharePrice() * number;
		}
		return totalValue;
	}
	
	public double calculateRisk() {
		double totalRisk = 0.0;
		double risk = 0.0;
		double riskSum = 0.0;
		double annRet = 0.0;
		double totalValue = 0.0;
		double retRate = 0.0;
		ArrayList<Double> values = new ArrayList<Double>();
		
		for(Asset a : assetMap.keySet()) {
			double value = getTotalValue(a, assetMap.get(a));
			risk = a.getRiskType(value);
			retRate = a.getRateOfReturn();
			
			values.add(value);
			
			riskSum += risk;
			totalValue += value;
		}
		for(Double v : values) {
			totalRisk += v/totalValue;
		}
		totalRisk *= riskSum;		
		
		return totalRisk;
	}
	//TODO:Get fees
	private double calculateFees(Person manager){
		BrokerType type = ((Broker)manager).getType();
		double fees = 0;
		try {
		if (type == BrokerType.EXPERT){
			fees = 10 * this.assetMap.size();
		} else {
			fees = 50 * this.assetMap.size();
		}
		} catch (Exception e) {
			
		}
		return fees;
		
	}
	
	private double calculateCommission(Person manager){
		BrokerType type = ((Broker)manager).getType();
		double commission;
		if (type == BrokerType.EXPERT){
			commission = .05 * this.expectedReturn;
		} else {
			commission = .02 * this.expectedReturn;
		}
		return commission;
	}
	
	public ArrayList<Object> getBasicInfo(){
		ArrayList<Object> list = new ArrayList<Object>();
		
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
				
				double value = getTotalValue(a, assetMap.get(a));
				double risk = a.getRiskType(value);
				double retRate = calculateROR(a, assetMap.get(a));				
				double annReturn = getExpectedReturn(a, assetMap.get(a));
				
				sb.append((String.format("%-10s %-31s %10.2f%% %13.2f  $%12.2f  $%12.2f\n", 
						a.code, a.label, retRate, risk, getExpectedReturn(a, assetMap.get(a)), value)));

			}	
		}
		
		sb.append("                                                        --------------------------------------------\n")
		.append("                                                Totals ")
		.append(String.format("%13.2f  $%12.2f  $%12.2f\n", 
				this.aggregateRisk, this.expectedReturn, this.totalValue));
		
		
		return sb.toString();
	}
	
	private double calculateROR(Asset a, double number){
		return getExpectedReturn(a, number) / getTotalValue(a, number) * 100;
	}
}
