package unl.cse;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer o1, Integer o2) {
		int diff = o1 - o2;
		return diff;
	}
}