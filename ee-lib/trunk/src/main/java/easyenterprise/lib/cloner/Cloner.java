package easyenterprise.lib.cloner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clones object trees. A {@link View} is used to determine what data is
 * to be cloned.
 *
 * @author Ruud Diterwich
 */
public class Cloner {
	@SuppressWarnings("unchecked")
	public static <T> T clone(T object, View view) throws CloneException {
		try {
			return (T) cloneValue(object, view);
		} catch (CloneException e) {
			throw e;
		} catch (Exception e) {
			throw new CloneException("Class " + object.getClass() + " cannot be cloned: " + e.getMessage(), e);
		}
	}
	
	public static <T, U extends T> U copyShallow(T from, Class<U> toClass) throws CloneException {
		try {
			ClassInfo info = ClassInfo.getInfo(from.getClass());
			U to = toClass.newInstance();
			for (PropertyInfo p : info.properties.values()) {
				Object value;
				if (p.getter != null) {
					value = p.getter.invoke(from);
				} else if (p.field != null) {
					value = p.field.get(from);
				} else {
					throw new CloneException("No getter or field found for property '" + p.name + "' in class " + from.getClass());
				}
				if (p.setter != null) {
					p.setter.invoke(to, value);
				} else if (p.field != null) {
					p.field.set(to, value);
				} else {
					throw new CloneException("No setter or field found for property '" + p.name + "' in class " + toClass);
				}
			}
			return to;
		} catch (Exception e) {
			throw new CloneException("Class " + from.getClass() + " cannot be copied to " + toClass + ": " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object cloneValue(Object value, View view) throws Exception {
		if (value == null) {
			return value;
		}
		if (value.getClass().isPrimitive()) {
			return value;
		}
		if (value.getClass().isEnum()) {
			return value;
		}
		if (BasicView.isBasicType(value.getClass())) {
			return value;
		}
		if (Map.class.isAssignableFrom(value.getClass())) {
      Map targetMap = new LinkedHashMap();
			for (Map.Entry mapEntry : ((Map<?,?>) value).entrySet()) {
				MapEntry entryClone = (MapEntry) cloneValue(new MapEntry(mapEntry), view);
				targetMap.put(entryClone.getKey(), entryClone.getValue());
			}
			return targetMap;
		}
		if (List.class.isAssignableFrom(value.getClass())) {
			List targetList = new ArrayList();
			for (Object listObject : (List) value) {
				targetList.add(cloneValue(listObject, view));
			}
			return targetList;
		}
		if (Set.class.isAssignableFrom(value.getClass())) {
			Set targetSet = new LinkedHashSet();
			for (Object listObject : (Set) value) {
				targetSet.add(cloneValue(listObject, view));
			}
			return targetSet;
		}
		return cloneObject(value, view);
	}
	
	private static Object cloneObject(Object object, View view) throws Exception {
		ClassInfo info = ClassInfo.getInfo(object.getClass());
		if (!info.canInstantiate) {
			throw new CloneException("Cannot instantiate class " + info.actualClass.getName() + " (no default constructor?)");
		}
		Object detached = info.actualClass.newInstance();
		List<PropertyInfo> properties;
		try {
			properties = view.getProperties(info);
		} catch (Exception e) {
			throw new CloneException("Couldn't clone " + info.actualClass + ": " + e.getMessage(), e);
		}
		for (PropertyInfo p : properties) {
			View subView = view.getSubView(p.name);
			Object value;
			if (p.getter != null) {
				value = cloneValue(p.getter.invoke(object), subView);
			} else if (p.field != null) {
				value = cloneValue(p.field.get(object), subView);
			} else {
				throw new CloneException("No getter or field found for property '" + p.name + "' in class " + info.actualClass);
			}
			if (p.setter != null) {
				p.setter.invoke(detached, value);
			} else if (p.field != null) {
				p.field.set(detached, value);
			} else {
				throw new CloneException("No setter or field found for property '" + p.name + "' in class " + info.actualClass);
			}
		}
		return detached;
	}
	

	@SuppressWarnings("unused") // We use reflection
	private static final class MapEntry {
		private Object key;
		private Object value;

		public MapEntry() {
		}
		
		public MapEntry(Map.Entry<?, ?> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}
		
		public void setKey(Object key) {
			this.key = key;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
	}
}
