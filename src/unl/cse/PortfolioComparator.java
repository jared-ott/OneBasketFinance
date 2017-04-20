package unl.cse;

import java.util.Comparator;

public class PortfolioComparator implements Comparator<Portfolio> {

	// default is to compare by owner
	@Override
	public int compare(Portfolio p1, Portfolio p2) {
		return compareByOwner(p1, p2);
	}
	
	// compare portfolios by owner
	//   last name, then first name.
	public int compareByOwner(Portfolio p1, Portfolio p2) {
		String p1Owner = p1.getOwner().getNameStringLF();
		String p2Owner = p2.getOwner().getNameStringLF();
		
		return p1Owner.compareTo(p2Owner);
	}
	
	// compares portfolios by manager
	//   Expert first then Juniors
	//   Then by last name/first name
	public int compareByManager(Portfolio p1, Portfolio p2) {
		if(((Broker) p1.getManager()).getType() != ((Broker) p2.getManager()).getType()) {
			if(((Broker) p1.getManager()).getType() == BrokerType.EXPERT) {
				return -1;
			} else {
				return 1;
			}
		}
		
		String p1Manager = p1.getManager().getNameStringLF();
		String p2Manager = p2.getManager().getNameStringLF();
		
		return p1Manager.compareTo(p2Manager);
	}

	
	// Compares Portfolios by the total value
	public int compareByValue(Portfolio p1, Portfolio p2) {
		double a = p1.getTotalValue();
		double b = p2.getTotalValue();
		
		if(a<b) {
			return -1;
		} else if(a>b) {
			return 1;
		} else {
			return 0;
		}
	}
	
}