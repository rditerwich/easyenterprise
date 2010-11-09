package easyenterprise.lib.cloner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

final class ClassInfo {
	
	private static final ConcurrentMap<Class<?>, ClassInfo> map = new MapMaker().weakKeys().makeComputingMap(new Function<Class<?>, ClassInfo>() {
		public ClassInfo apply(Class<?> c) {
			return new ClassInfo(c);
		}
	});
	
	public final Class<?> c;
	public final Class<?> actualClass;
	public final Map<String, PropertyInfo> properties;
	public final boolean canInstantiate;
	
	public static ClassInfo getInfo(Class<?> c) {
		return map.get(c);
	}
	
	private ClassInfo(Class<?> c) {
		this.c = c;
		this.actualClass = findActualActualClass(c);
		this.properties = findProperties(actualClass);
		this.canInstantiate = tryInstantiate(actualClass);
	}

	private Class<?> findActualActualClass(Class<?> c) {
		if (c.getSimpleName().contains("$$")) {
			c = c.getSuperclass();
		}
		return c;
	}
	
	private static boolean tryInstantiate(Class<?> c) {
		try {
			c.newInstance();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private Map<String, PropertyInfo> findProperties(Class<?> c) {
		Map<String, PropertyInfo> result = new LinkedHashMap<String, PropertyInfo>();
		findProperties(c, result);
		return Collections.unmodifiableMap(result);
	}

	private void findProperties(Class<?> c, Map<String, PropertyInfo> result) {
		// Recurse ourselves to get a 'nicer' property order in the map.
		Class<?> superclass = c.getSuperclass();
		if (superclass != null && !superclass.equals(Object.class)) {
			findProperties(superclass, result);
		}
		
		for (Field field : c.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (!Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers)) {
				field.setAccessible(true);
				result.put(field.getName(), new PropertyInfo(field.getName(), field.getType(), field, null, null));
			}
		}
		
		for (Method method : c.getDeclaredMethods()) {
			String name = getPropertyName(method, "get");
			if (name != null && method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.class)) {
				PropertyInfo info = result.get(name);
				result.put(name, new PropertyInfo(name, method.getReturnType(), info != null ? info.field :  null, method, null));
			}
		}
		for (Method method : c.getDeclaredMethods()) {
			String name = getPropertyName(method, "set");
			PropertyInfo info = result.get(name);
			if (info != null && method.getParameterTypes().length == 1 && method.getReturnType().equals(Void.TYPE)) {
				result.put(name, new PropertyInfo(name, info.type, info.field, info.getter, method));
			}
		}
	}
	
	private String getPropertyName(Method method, String prefix) {
		String name = method.getName();
		if (name.startsWith(prefix) && name.length() > prefix.length() && Character.isUpperCase(name.charAt(prefix.length()))) {
			return Character.toLowerCase(name.charAt(prefix.length())) + name.substring(prefix.length() + 1);
		}
		return null;
	}
}
