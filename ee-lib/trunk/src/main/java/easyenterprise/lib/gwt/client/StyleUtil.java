package easyenterprise.lib.gwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.UIObject;

import easyenterprise.lib.util.StringUtil;

public class StyleUtil {

	private static Map<UIObject, String> simpleClassNames = new HashMap<UIObject, String>();
	
	public static void addStyle(UIObject uiObject, Style style) {
		uiObject.addStyleName(style.toString());
	}

	public static void remove(UIObject uiObject, Style style) {
		uiObject.removeStyleName(style.toString());
	}

	public static void add(RowFormatter formatter, int row, Style style) {
		formatter.addStyleName(row, style.toString());
	}

	public static void remove(RowFormatter formatter, int row, Style style) {
		formatter.removeStyleName(row, style.toString());
	}

	public static void setStyle(UIObject uiObject, Style style) {
		uiObject.setStylePrimaryName(style.toString());
	}
	
	public static Style createStyle(UIObject o) {
		return createStyle("", getDefaultStyleName(o));
	}
	
	public static Style createStyle(String style) {
		return createStyle("", style);
	}
		
		public static Style createStyle(Style base, String postfix) {
		return createStyle(base.toString(), postfix);
	}
	
	public static Style createStyle(final String prefix, final String style) {
		return new Style() {
			@Override
			public String toString() {
				return prefix + style;
			}
		};
	}
	
	public static void setStyle(UIObject uiObject) {
		uiObject.setStylePrimaryName(getDefaultStyleName(uiObject));
	}
	
	public static String getDefaultStyleName(UIObject uiObject) {
		String name = simpleClassNames.get(uiObject);
		if (name == null) {
			name = uiObject.getClass().getName();
			name = StringUtil.afterLast(name, '.', name);
			name = StringUtil.beforeFirst(name, '$', name);
			simpleClassNames.put(uiObject, name);
		}
		return name;
	}

	public native static void setBackGround(
	      Element panel, String imgPath) /*-{
	if(panel != null) {
	panel.style.background  = " transparent url(" + imgPath + ") repeat-x 20px 10px";
	}	
	}-*/;

	public static void addClass(com.google.gwt.dom.client.Element element, Style style) {
		element.addClassName(style.toString());
	}
}
