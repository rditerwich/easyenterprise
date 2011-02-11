package easyenterprise.lib.cloner;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import easyenterprise.lib.util.SMap;

/**
 * Clones object trees. A {@link View} is used to determine what data is
 * to be cloned.
 *
 * @author Ruud Diterwich
 */
public class Printer {
	public static <T> String print(T object, View view) {
		try {
			return printValue(object, view, "\n", new StringBuilder()).toString();
		} catch (Exception e) {
			throw new CloneException("Class " + object.getClass() + " cannot be printed: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static StringBuilder printValue(Object value, View view, String prefix, StringBuilder out) throws Exception {
		if (value == null) {
			return out.append("(null)");
		}
		if (value.getClass().isPrimitive()) {
			return out.append(value.toString());
		}
		if (value.getClass().isEnum()) {
			return out.append(value.toString());
		}
		if (BasicView.isBasicType(value.getClass())) {
			return out.append(value.toString());
		}
		if (SMap.class.isAssignableFrom(value.getClass())) {
			return printMap((SMap<Object, Object>) value, view, prefix, out);
		}
		if (Map.class.isAssignableFrom(value.getClass())) {
			return printMap(((Map<Object, Object>) value).entrySet(), view, prefix, out);
		}
		if (Collection.class.isAssignableFrom(value.getClass())) {
			return printCollection((Collection<?>) value, view, prefix, out);
		}
		return printObject(value, view, prefix, out);
	}
	
	private static StringBuilder printCollection(Collection<?> collection, View view, String prefix, StringBuilder out) throws Exception {
		out.append("{");
		for (Object value : collection) {
			printValue(value, view, prefix + "  ", out);
		}
		out.append(prefix).append("}");
		return out;
	}
	
	private static StringBuilder printMap(Iterable<Map.Entry<Object,Object>> entries, View view, String prefix, StringBuilder out) throws Exception {
		out.append("{");
		for (Entry<?, ?> entry : entries) {
			out.append(prefix).append("  ").append(entry.getKey() == null ? "(null)" : entry.getKey().toString()).append(":");
			printValue(entry.getValue(), view, prefix + "    ", out);
		}
		out.append(prefix).append("}");
		return out;
	}
	
	private static StringBuilder printObject(Object object, View view, String prefix, StringBuilder out) throws Exception {
		ClassInfo info = ClassInfo.getInfo(object.getClass());
		out.append(info.c.getName());
		List<PropertyInfo> properties;
		properties = view.getProperties(info);
		for (PropertyInfo p : properties) {
			out.append(prefix).append("  ").append(p.name).append(" = ");
			View subView = view.getSubView(p.name);
			Object value;
			if (p.getter != null) {
				value = p.getter.invoke(object);
			} else if (p.field != null) {
				value = p.field.get(object);
			} else {
				throw new Exception("No getter or field found for property '" + p.name + "' in class " + info.actualClass);
			}
			printValue(value, subView, prefix + "    ", out);
		}
		return out;
	}
}
