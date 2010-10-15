package easyenterprise.lib.cloner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A view is a set of properties that represent a subset of an object graph.
 * Properties are specified as a comma-separated list of property paths. An 
 * exclamation mark (!) in front of a property path denotes an exclusion.
 * <p>Some examples:
 * <ul><li><code>name</code> Includes the name property</li>  
 * <li><code>!name</code> Excludes the name property</li>  
 * <li><code>parent</code> Includes the parent property</li>  
 * <li><code>parent/name</code> Includes both the parent property and it's name property</li>  
 * <li><code>!parent/name</code> Excludes the name property of a parent object</li>  
 * </ul>  
 * @author Ruud Diterwich
 */
public class View {

	private static final Pattern COMMA = Pattern.compile("\\,");

	public static final View NULL = new View("") {
		public List<PropertyInfo> getProperties(ClassInfo c) {
			return Collections.emptyList();
		}
	};
	
	private Set<String> includedProperties = Collections.emptySet();
	private Set<String> excludedProperties = Collections.emptySet();
	private Map<String, View> subViews = Collections.emptyMap();
	
	public View(String... properties) {
		for (String properties2 : properties) {
			for (String property : COMMA.split(properties2)) {
				property = property.trim();
				if (!property.equals("")) {
					addProperty(property);
				}
			}
		}
	}
	
	public Set<String> getIncludedProperties() {
		return Collections.unmodifiableSet(includedProperties);
	}
	
	public Set<String> getExcludedProperties() {
		return Collections.unmodifiableSet(excludedProperties);
	}
	
	public List<PropertyInfo> getProperties(ClassInfo info) throws Exception {
		List<PropertyInfo> result = new ArrayList<PropertyInfo>();
		for (String property : getIncludedProperties()) {
			PropertyInfo p = info.properties.get(property);
			if (p == null) {
				throw new Exception("Property not found: " + property);
			}
			result.add(p);
		}
		return result;		
	}

	public final View getSubView(String property) {
		View subView = subViews.get(property);
		return subView != null ? subView : getDefaultView();
	}

	protected View getDefaultView() {
		return NULL;
	}
	
	protected View createSubView() {
		return NULL;
	}
	
	private void addProperty(String property) {
		if (property.startsWith("!")) {
			addProperty(property.substring(1), true);
		} else {
			addProperty(property, false);
		}
	}
	private void addProperty(String property, boolean exclude) {
		int slash = property.indexOf('/');
		if (slash >= 0) {
			String remaining = property.substring(slash + 1);
			property = property.substring(0, slash);
			View subView = subViews.get(property);
			if (subView == null) {
				subView = createSubView();
				if (subViews.isEmpty()) {
					subViews = new HashMap<String, View>();
				}
				subViews.put(property.substring(0, slash), subView);
			}
			subView.addProperty(remaining, exclude);
		}

		if (!isValidPropertyName(property)) {
			throw new IllegalArgumentException("Not a valid property name: " + property);
		}
		
		if (exclude) {
			property = property.substring(1);
			if (excludedProperties.isEmpty()) {
				excludedProperties = new HashSet<String>();
			}
			excludedProperties.add(property);
			includedProperties.remove(property);
			
		} else {
			if (includedProperties.isEmpty()) {
				includedProperties = new HashSet<String>();
			}
			includedProperties.add(property);
			excludedProperties.remove(property);
		}
	}

	private boolean isValidPropertyName(String property) {
		if (property.length() == 0 || ! Character.isJavaIdentifierStart(property.charAt(0))) {
			return false;
		}
		for (int i = property.length() - 1; i > 0; i--) {
			if (!Character.isJavaIdentifierPart(property.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
