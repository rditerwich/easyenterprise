package easyenterprise.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CollectionUtil {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static <T> Collection<T> notNull(Collection<T> collection) {
		return collection == null ? Collections.<T>emptyList() : collection;
	}
	
	public static <T> Collection<T> concat(Collection<T> c1, Collection<T> c2) {
		if (c1 == null && c2 == null) return Collections.emptyList();
		if (c1 == null) return c2;
		if (c2 == null) return c1;
		ArrayList<T> list = new ArrayList<T>(c1.size() + c2.size());
		list.addAll(c1);
		list.addAll(c2);
		return list;
	}
	
	public static <T> T first(Iterable<T> iterable) {
		return iterable.iterator().next();
	}
}
