package unl.cse;

import java.util.Comparator;

public class PortfolioCompareByManager implements Comparator<Portfolio> {
	
	// compares portfolios by manager
	//   Expert first then Juniors
	//   Then by last name/first name
	@Override
	public int compare(Portfolio p1, Portfolio p2) {
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
}
