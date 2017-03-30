package unl.cse;

abstract class Asset implements XMLable, JSONable {
	
	protected String label;
	protected String code;
	
	public Asset(String label, String code) {
		super();
		this.label = label;
		this.code = code;
	}
	
	public String getLabel() {
		return label;
	}

	public String getCode() {
		return code;
	}
	
	public double getRiskType(double value) {
		double risk = 0.0;
		
		if(this.getType().equals("S")) {
			risk = ((Stock) this).getBeta();
			
		} else if(this.getType().equals("P")) {
			risk = ((PrivateInvestment) this).getOmega();
			risk += Math.exp(-100000/value);
		} // NOTE: risk is 0.0 anyway for Deposit Accounts.
		
		return risk;
	}
	
	abstract public String getType();
	abstract public double getRateOfReturn();
	
}
