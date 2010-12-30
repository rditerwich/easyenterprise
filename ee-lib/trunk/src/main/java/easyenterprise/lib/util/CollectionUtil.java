package easyenterprise.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

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
	
	public static <T> LinkedHashSet<T> unique(Collection<T> collection) {
		return new LinkedHashSet<T>(collection);
	}
	
	public static <T> T first(Iterable<T> iterable) {
		return iterable.iterator().next();
	}
	
	public static <T> T firstOrNull(Iterable<T> iterable) {
		Iterator<T> it = iterable.iterator();
		if (it.hasNext()) return it.next();
		else return null;
	}
	
	public static void setSize(List<?> list, int size) {
		while (list.size() > size) {
			list.remove(list.size() - 1);
		}
		while (list.size() < size) {
			list.add(null);
		}
	}
}
