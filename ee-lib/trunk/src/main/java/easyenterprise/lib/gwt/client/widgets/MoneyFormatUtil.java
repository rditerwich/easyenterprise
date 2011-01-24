package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.i18n.client.NumberFormat;

import easyenterprise.lib.util.Money;

public class MoneyFormatUtil {
	public static String full(Money m) {
		CurrencyData currency = CurrencyList.get().lookup(m.currency);
		return currencySymbol(m, currency) + value(m, currency);
	}
	
	public static String value(Money m) {
		CurrencyData currency = CurrencyList.get().lookup(m.currency);
		return value(m, currency);
	}
	
	public static String value(Money m, CurrencyData currency) {
		return NumberFormat.getFormat("." + "000000".substring(0, Money.getDecimals(currency.getCurrencyCode())), currency).format(m.value);
	}
	
	public static String currencySymbol(Money m) {
		CurrencyData currency = CurrencyList.get().lookup(m.currency);
		return currencySymbol(m, currency);
	}
	public static String currencySymbol(Money m, CurrencyData currency) {
		return currency.getCurrencySymbol();
	}
}
