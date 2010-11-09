package easyenterprise.lib.util;

public class StringUtil {

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
