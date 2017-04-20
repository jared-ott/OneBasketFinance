package unl.cse;

import java.util.Comparator;

public class PortfolioCompareByOwner implements Comparator<Portfolio> {
	// compare portfolios by owner
	//   last name, then first name.
	@Override 
	public int compare(Portfolio p1, Portfolio p2) {
		String p1Owner = p1.getOwner().getNameStringLF();
		String p2Owner = p2.getOwner().getNameStringLF();
		
		return p1Owner.compareTo(p2Owner);
	}
	
}
