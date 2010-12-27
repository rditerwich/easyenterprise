package easyenterprise.lib.util;

public class ObjectUtil {

	public static <T> T orElse(T value, T alternative) {
		if (value != null) return value;
		else return alternative;
	}
}
