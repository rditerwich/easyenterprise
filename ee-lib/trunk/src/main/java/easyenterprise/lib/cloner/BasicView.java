package easyenterprise.lib.cloner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * View that automatically includes properties with a 'basic' type.
 * Basic types include primitive types, their wrapper types and standard
 * immutable types, like String, Date etc.
 * 
 * @see View
 * @author Ruud Diterwich
 */
@SuppressWarnings("unchecked")
public class BasicView extends View {

	public static final BasicView DEFAULT = new BasicView();
	
	private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<Class<?>>(Arrays.asList(
		    Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));

	private static final Set<Class<?>> BASIC_TYPES = Sets.union(WRAPPER_TYPES, new HashSet<Class<?>>(Arrays.asList(
			String.class, Date.class, java.sql.Date.class, Timestamp.class)));
	
	public BasicView(String... properties) {
		super(properties);
	}
	
	public List<PropertyInfo> getProperties(ClassInfo c) throws Exception {
		List<PropertyInfo> result = new ArrayList<PropertyInfo>();
		for (PropertyInfo p : c.properties.values()) {
			if (isBasicType(p.type)) {
				if (!getExcludedProperties().contains(p.name)) {
					result.add(p);
				}
			}
		}
		for (String property : getIncludedProperties()) {
			PropertyInfo p = c.properties.get(property);
			if (p == null) {
				throw new Exception("Property not found: " + property);
			}
			result.add(p);
		}
		return result;
	}
	
	public static boolean isBasicType(Class<?> type) {
		return type.isPrimitive() || BASIC_TYPES.contains(type) || type.isEnum();
	}
	
	@Override
	protected View getDefaultView() {
		return DEFAULT;
	}
	
	@Override
	protected View createSubView() {
		return new BasicView();
	}
}
