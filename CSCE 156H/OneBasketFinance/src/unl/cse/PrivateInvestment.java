package unl.cse;

public class PrivateInvestment extends Asset {

	private double quarterlyDividend;
	private double rateOfReturn;
	private double omega;
	private double totalValue;
	private final String type = "P";
	
	public PrivateInvestment(String label, String code, double quarterlyDividend, double baseRateOfReturn, double omega,
			double totalValue) {
		super(label, code);
		this.quarterlyDividend = quarterlyDividend;
		this.rateOfReturn = baseRateOfReturn;
		this.omega = omega;
		this.totalValue = totalValue;
	}

	public double getQuarterlyDividend() {
		return quarterlyDividend;
	}

	@Override
	public double getRateOfReturn() {
		return rateOfReturn;
	}

	public double getOmega() {
		return omega;
	}

	public double getTotalValue() {
		return totalValue;
	}

	@Override
	public String getType() {
		return type;
	}

}
