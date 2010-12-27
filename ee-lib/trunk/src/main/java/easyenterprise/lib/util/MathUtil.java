package easyenterprise.lib.util;

public class MathUtil {

	public static Long orZero(Long value) {
		if (value == null) return Long.valueOf(0);
		else return value;
	}
	
	public static Integer orZero(Integer value) {
		if (value == null) return Integer.valueOf(0);
		else return value;
	}
}
