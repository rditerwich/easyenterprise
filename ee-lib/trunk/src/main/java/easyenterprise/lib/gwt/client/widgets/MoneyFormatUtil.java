package easyenterprise.lib.gwt.client.widgets;

import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.i18n.client.NumberFormat;

import easyenterprise.lib.util.Money;

public class MoneyFormatUtil {
	public static CurrencyData defaultCurrency = CurrencyList.get().lookup("EUR");
	public static String full(Money m) {
		CurrencyData currency = CurrencyList.get().lookup(m.currency);
		return currencySymbol(m, currency) + value(m, currency);
	}
	
	public static String value(Money m) {
		CurrencyData currency = CurrencyList.get().lookup(m.currency);
		return value(m, currency);
	}
	
	public static String value(Money m, CurrencyData currency) {
		if (currency == null) {
			currency = defaultCurrency;
		}
		Double value = m.value;
		if (value == null) {
			return "";
		}
		return NumberFormat.getFormat("0." + "000000".substring(0, Money.getDecimals(currency.getCurrencyCode())), currency).format(value);
	}
	
	public static String currencySymbol(Money m) {
		CurrencyData currency = CurrencyList.get().lookup(m.currency);
		if (currency == null) {
			currency = defaultCurrency;
		}
		return currencySymbol(m, currency);
	}
	public static String currencySymbol(Money m, CurrencyData currency) {
		if (currency == null) {
			currency = defaultCurrency;
		}
		return currency.getCurrencySymbol();
	}
	
}
