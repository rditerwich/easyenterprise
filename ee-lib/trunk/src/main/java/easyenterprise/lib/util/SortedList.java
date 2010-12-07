package easyenterprise.lib.util;

import java.util.TreeSet;

public class SortedList<T extends Comparable<T>> extends TreeSet<T> {

	private static final long serialVersionUID = 1L;

	public int getIndexOf(T value) {
		int index = 0;
		for (T t : this) {
			if (t.compareTo(value) == 0) {
				return index;
			}
			index++;
		}
		return -1;
 	}
	
	public T get(int index) {
		for (T t : this) {
			if (index-- == 0) {
				return t;
			}
		}
		return null;
	}
}
