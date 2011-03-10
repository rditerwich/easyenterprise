package easyenterprise.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class CollectionUtil {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static boolean isEmpty(SMap<?, ?> m) {
		return m == null || m.isEmpty();
	}
	
	public static <K,V> SMap<K,V> notNull(SMap<K, V> map) {
		return map == null? SMap.<K,V>empty() : map;
	}
	
	public static <T> Iterable<T> notNull(Iterable<T> iterable) {
		return iterable == null ? Collections.<T>emptyList() : iterable;
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
	
	public static <T> List<T> asList(Iterable<T> values) {
		List<T> result = new ArrayList<T>();
		
		if (values != null) {
			for (T value : values) {
				result.add(value);
			}
		}
		
		return result;
	}

	public static <T> List<T> asList(T... values) {
		List<T> result = new ArrayList<T>(values != null? values.length : 0);

		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				result.add(values[i]);
			}
		}
		
		return result;
	}
	
	public static <T> List<T> subList(List<T> list, int from, int to) {
		if (from == 0 && to == list.size()) return list;
		ArrayList<T> result = new ArrayList<T>();
		for (int i = from; i < list.size() && i < to; i++) {
			result.add(list.get(i));
		}
		return result;
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
	
	public static <T> T removeFirst(Iterable<T> iterable) {
		Iterator<T> it = iterable.iterator();
		T result = it.next();
		it.remove();
		return result;
	}
	
	public static <T> T removeFirstOrNull(Iterable<T> iterable) {
		Iterator<T> it = iterable.iterator();
		if (it.hasNext()) {
			T result = it.next();
			it.remove();
			return result;
		}
		else return null;
	}
	
	public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator) {
		List<T> list = new ArrayList<T>(collection);
		Collections.sort(list, comparator);
		return list;
	}
	
	public static <T> int indexOfRef(Collection<T> collection, T ref) {
		int index = 0;
		for (T obj : collection) {
			if (obj == ref) return index;
			index++;
		}
		return -1;
	}
	
	public static <T> boolean containsRef(Collection<T> collection, T ref) {
		return indexOfRef(collection, ref) != -1;
	}
}
