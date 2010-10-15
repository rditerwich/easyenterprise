package easyenterprise.lib.cloner;

import java.lang.reflect.Method;

final class PropertyInfo {

	public final String name;
	public final Class<?> type;
	public final Method getter;
	public final Method setter;
	
	public PropertyInfo(String name, Class<?> type, Method getter, Method setter) {
		this.name = name;
		this.type = type;
		this.getter = getter;
		this.setter = setter;
	}
}
