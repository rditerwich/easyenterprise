package easyenterprise.lib.cloner;

import com.google.common.base.Objects;

/**
 * Print objects, object changes, and particular properties of objects.
 * 
 * Sample output:
 * <p>
 * <code>PostcodeRoutering[id=120, postcodeVan=1200AA (was 1300XX), postcodeTotEnMet=1300ZZ]</code>
 * 
 * @author Ruud Diterwich
 */
public final class Print<T> {

	private final T old;
	private final T object;
	private final View view;
	private final String properties;

	public static <T> Print<T> print(T object) {
		return new Print<T>(null, object, BasicView.DEFAULT, "");
	}
	
	public static <T> Print<T> print(T old, T object) {
		return new Print<T>(old, object, BasicView.DEFAULT, "");
	}
	
	public static <T> Print<T> print(T object, String properties) {
		return new Print<T>(null, object, null, properties);
	}
	
	public static <T> Print<T> print(T old, T object, String properties) {
		return new Print<T>(old, object, null, properties);
	}
	
	public static <T> Print<T> print(T object, View view) {
		return new Print<T>(null, object, view, null);
	}
	
	public static <T> Print<T> print(T old, T object, View view) {
		return new Print<T>(old, object, view, null);
	}
	
	private Print(T old, T object, View view, String properties) {
		this.old = old;
		this.object = object;
		this.view = view;
		this.properties = properties;
	}
	
	public String toString() {
		if (object == null) {
			return "null";
		}
		StringBuilder out = new StringBuilder();
		View view = this.view != null ? this.view : new BasicView(properties);
		ClassInfo info = ClassInfo.getInfo(object.getClass());
		String sep = "[";
		String post = "";
		out.append(object.getClass().getSimpleName());
		try {
			for (PropertyInfo property : view.getProperties(info)) {
				out.append(sep);
				out.append(property.name).append("=");
				Object value = property.getter.invoke(object);
				out.append(toString(value));
				if (old != null) {
					Object oldValue = property.getter.invoke(old);
					if (!Objects.equal(value, oldValue)) {
						out.append(" (was ").append(toString(oldValue)).append(")");
					}
				}
				sep = ", ";
				post = "]";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		out.append(post);
		return out.toString();
	}

	private String toString(Object value) {
		if (value == null) {
			return "(null)";
		}
		return value.toString();
	}
}
