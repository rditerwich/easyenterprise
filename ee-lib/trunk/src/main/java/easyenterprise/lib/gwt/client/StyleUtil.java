package easyenterprise.lib.gwt.client;

import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.UIObject;

public class StyleUtil {

	public static void add(UIObject uiObject, Style style) {
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

}
