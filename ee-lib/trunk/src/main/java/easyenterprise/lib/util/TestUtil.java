package easyenterprise.lib.util;

public class TestUtil {
	/**
	 * Be aware that, contrary to standard junit assertions, the first argument is the actual output, not the expected. This is
	 * to be able to use varargs.   
	 * @param output
	 * @param expectedOutput
	 */
	public static void assertOutput(LineWriter output, String... expectedOutput) {
		try {
			try {
				output.flush();
			} catch (Throwable t) {
				throw new AssertionError(getDescription(t.getMessage(), t.getCause(), false));
			}
			String[] actualOutput = output.getLines();
			for (int line = 0; line < Math.max(actualOutput.length, expectedOutput.length); line++) {
				if (line >= actualOutput.length) {
					throw new AssertionError("Missing output at line " + (line + 1) + ": " + expectedOutput[line]);
				}
				if (line >= expectedOutput.length) {
					throw new AssertionError("Unexpected output at line " + (line + 1) + ": " + actualOutput[line]);
				}
				if (!expectedOutput[line].equals(actualOutput[line])) {
					throw new AssertionError("Invalid output at line " + (line + 1) + ": " + actualOutput[line] + " expected: " + expectedOutput[line]);
				}
			}
		}		
		catch (Error t) {
			System.out.println(t.getMessage());
			System.out.println("Expected output:");
			for (int i = 0; i < expectedOutput.length; i++) {
				System.out.print("line ");
				System.out.print(i + 1);
				System.out.print(": ");
				System.out.println(expectedOutput[i].trim());
			}
			throw t;
		}
	}
	
	public static String getDescription(String message, Throwable cause, boolean fullStackTrace) {
		if (cause == null) {
			return message;
		}
		return getDescription(new StringBuilder(message), cause, fullStackTrace).toString();
	}
	


	public static StringBuilder getDescription(StringBuilder result, Throwable cause, boolean fullStackTrace) {
		if (cause == null) {
			return result;
		}
		if (result.length() > 0) {
			result.append(": ");
		}
		Throwable rootCause = cause;
		while (rootCause.getCause() != null) {
			rootCause = rootCause.getCause();
		}
		if (cause.getMessage() != null) {
			result.append(rootCause.getClass().getSimpleName() + ":" + cause.getMessage());
		} else { 
			result.append(rootCause.getClass().getSimpleName());
		}
		StackTraceElement[] stackTrace = rootCause.getStackTrace();
		if (stackTrace != null) {
			for (int i = 0; i < stackTrace.length; i++) {
				StackTraceElement elt = stackTrace[i];
				String className = elt.getClassName();
				if (className.endsWith(".Assert")) {
					continue;
				}
				result.append(" at ");
				int index = className.lastIndexOf('.');
				if (index >= 0) {
					className = className.substring(index + 1);
				}
				result.append(className);
				result.append(".");
				result.append(elt.getMethodName());
				result.append("(");
				result.append(elt.getFileName());
				result.append(":");
				result.append(elt.getLineNumber());
				result.append(")");
				if (fullStackTrace) {
					result.append("\n");
				} 
				else {
					break;
				}
			}
		}
		return result;
	}


}
