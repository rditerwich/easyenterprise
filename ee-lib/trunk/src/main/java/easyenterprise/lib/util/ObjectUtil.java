package easyenterprise.lib.util;


public class ObjectUtil {

	public static <T> T orElse(T value, T alternative) {
		if (value != null) return value;
		else return alternative;
	}

	public static boolean orFalse(Boolean value) {
		if (value != null) return value;
		else return false;
	}
	
	public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
		if (o1 == o2) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;
		return o1.compareTo(o2);
	}
}
