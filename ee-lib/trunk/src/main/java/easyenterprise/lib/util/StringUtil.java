package easyenterprise.lib.util;

public class StringUtil {

	public static String beforeLast(String s, char c, String defaultString) {
		int index = s.lastIndexOf(c);
		if (index < 0) return defaultString;
		else return s.substring(0, index);
	}
	
	public static String afterLast(String s, char c, String defaultString) {
		int index = s.lastIndexOf(c);
		if (index < 0) return defaultString;
		else return s.substring(index + 1);
	}
	
	public static String beforeFirst(String s, char c, String defaultString) {
		int index = s.indexOf(c);
		if (index < 0) return defaultString;
		else return s.substring(0, index);
	}
	
	public static String afterFirst(String s, char c, String defaultString) {
		int index = s.indexOf(c);
		if (index < 0) return defaultString;
		else return s.substring(index + 1);
	}
	
	public static String mkString(String sep, Object... parts) {
		StringBuilder builder = new StringBuilder();
		String cursep = "";
		for (Object part : parts) {
			if (part != null) {
				String s = part.toString().trim();
				if (!s.equals("")) {
					builder.append(cursep);
					builder.append(s);
					cursep = sep;
				}
			}
		}
		return builder.toString();
	}
}
