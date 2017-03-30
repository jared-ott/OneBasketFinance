package unl.cse;

public class Stock extends Asset {
	
	private double quarterlyDividend;
	private double baseRateOfReturn;
	private double beta;
	private String stockSymbol;
	private double sharePrice;
	private final String type = "S";
	
	public Stock(String label, String code, double quarterlyDividend, double rateOfReturn, double beta,
			String stockSymbol, double sharePrice) {
		super(label, code);
		this.quarterlyDividend = quarterlyDividend;
		this.baseRateOfReturn = rateOfReturn;
		this.beta = beta;
		this.stockSymbol = stockSymbol;
		this.sharePrice = sharePrice;
	}

	public double getQuarterlyDividend() {
		return quarterlyDividend;
	}

	@Override
	public double getRateOfReturn() {
		return baseRateOfReturn;
	}

	public double getBeta() {
		return beta;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public double getSharePrice() {
		return sharePrice;
	}

	@Override
	public String getType() {
		return type;
	}

}
