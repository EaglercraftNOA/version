package it.unimi.dsi.fastutil.ints;

import java.util.Comparator;

public interface IntComparator extends Comparator<Integer> {

	public int compare(int k1, int k2);

}