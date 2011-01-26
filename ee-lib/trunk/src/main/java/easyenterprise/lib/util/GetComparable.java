package easyenterprise.lib.util;

import java.util.Comparator;

public abstract class GetComparable<T> implements Comparator<T> {

	@Override
	@SuppressWarnings({ "unchecked" })
	public final int compare(T o1, T o2) {
		if (o1 == o2) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;
		Comparable<Object> c1 = (Comparable<Object>) getComparable(o1);
		Comparable<Object> c2 = (Comparable<Object>) getComparable(o2);
		if (c1 == c2) return 0;
		if (c1 == null) return -1;
		if (c2 == null) return 1;
		return c1.compareTo(c2);
	}
	
	public abstract Comparable<?> getComparable(T object);

}
