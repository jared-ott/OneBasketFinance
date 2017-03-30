package unl.cse;

import java.util.Comparator;

public class PortfolioComparator implements Comparator<Portfolio> {

	@Override
	public int compare(Portfolio p1, Portfolio p2) {
		String p1Owner = p1.getOwner().getNameStringLF();
		String p2Owner = p2.getOwner().getNameStringLF();
		
		for(int i = 0 ; i < p1Owner.length() ; i++){
			if (p1Owner.charAt(i) != p2Owner.charAt(i)){
				return -(Character.getNumericValue(p2Owner.charAt(i)) - Character.getNumericValue(p1Owner.charAt(i)));
			}
		}
		return 0;
	}

}
