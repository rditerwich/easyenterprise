package easyenterprise.lib.cloner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class PropertyInfo {

	public final String name;
	public final Class<?> type;
	public final Field field;
	public final Method getter;
	public final Method setter;
	
	public PropertyInfo(String name, Class<?> type, Field field, Method getter, Method setter) {
		this.name = name;
		this.type = type;
		this.field = field;
		this.getter = getter;
		this.setter = setter;
	}
}
