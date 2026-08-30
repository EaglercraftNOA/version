package it.unimi.dsi.fastutil.ints;

import java.util.Comparator;

@FunctionalInterface
public interface IntComparator extends Comparator<Integer> {

	public int compare(int k1, int k2);

	@Override
	default int compare(Integer k1, Integer k2) {
		return compare(k1.intValue(), k2.intValue());
	}

}
