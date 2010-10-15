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
			Object value = cloneValue(p.getter.invoke(object), subView);
			if (p.setter == null) {
				throw new CloneException("No setter found for property '" + p.name + "' in class " + info.actualClass);
			}
			p.setter.invoke(detached, value);
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
