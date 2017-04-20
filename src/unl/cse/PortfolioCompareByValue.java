package unl.cse;

import java.util.Comparator;

public class PortfolioCompareByValue implements Comparator<Portfolio> {
	
	// Compares Portfolios by the total value
	@Override
	public int compare(Portfolio p1, Portfolio p2) {
		double a = p1.getTotalValue();
		double b = p2.getTotalValue();
		
		if(a<b) {
			return 1;
		} else if(a>b) {
			return -1;
		} else {
			return 0;
		}
	}
}