package easyenterprise.lib.util;

import java.util.Collection;
import java.util.Collections;

public class CollectionUtil {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static <T> Collection<T> notNull(Collection<T> collection) {
		return collection == null ? Collections.<T>emptyList() : collection;
	}
}
