package unl.cse;

public class DepositAccount extends Asset {
	
	private double apr;
	private final String type = "D";
	
	public DepositAccount(String label, String code, double apr) {
		super(label, code);
		this.apr = apr;
	}

	public double getApr() {
		return apr;
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public double getRateOfReturn(){
		return 100*(Math.exp(this.apr/100) - 1);
	}
	
}
