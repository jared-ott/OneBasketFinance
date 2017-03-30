package unl.cse;

abstract class Asset {
	
	protected String label;
	protected String code;
	
	public Asset(String label, String code) {
		super();
		this.label = label;
		this.code = code;
	}
	
	//Calculates the Rate of Return for any asset
	public double calculateROR(double number){
		return this.getExpectedReturn(number) / this.getTotalValue(number) * 100;
	}

	public String getCode() {
		return code;
	}
	
	//Calculates the expected return of any Asset
	public double getExpectedReturn(double number){
		double expectedReturn;
		String type = this.getType();
		
		if (type.equals("D")){
			expectedReturn = number * ((DepositAccount)this).getRateOfReturn()/100;
		} else if (type.equals("P")) {
			expectedReturn = number/100 * (((PrivateInvestment)this).getRateOfReturn()/100 * ((PrivateInvestment)this).getTotalValue() 
					+ 4 * ((PrivateInvestment)this).getQuarterlyDividend());
		} else {
			expectedReturn = number * (((Stock)this).getRateOfReturn()/100 * ((Stock)this).getSharePrice()
					+ 4 * ((Stock)this).getQuarterlyDividend());
		}
		
		return expectedReturn;
	}
	
	public String getLabel() {
		return label;
	}
	abstract public double getRateOfReturn();
	
	//Calculates the risk for any Asset
	public double getRiskType(double value) {
		double risk = 0.0;
		
		if(this.getType().equals("S")) {
			risk = ((Stock) this).getBeta();
			
		} else if(this.getType().equals("P")) {
			risk = ((PrivateInvestment)this).getOmega();
			risk += Math.exp(-100000/((PrivateInvestment)this).getTotalValue());
		} // NOTE: risk is 0.0 anyway for Deposit Accounts.
		
		return risk;
	}
	
	//Calculates the totalValue of any asset
	public double getTotalValue(double number){
		double totalValue;
		String type = this.getType();
		
		if (type.equals("D")){
			totalValue = number;
		} else if (type.equals("P")) {
			totalValue = number/100*((PrivateInvestment)this).getTotalValue();
		} else {
			totalValue = ((Stock)this).getSharePrice() * number;
		}
		return totalValue;
	}
	
	abstract public String getType();	
	
}
