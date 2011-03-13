package easyenterprise.lib.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;

public class Money implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Map<String, String> symbolMap = createSymbolMap();
	private static Map<String, Integer> decimalsMap = createDecimalsMap();
	private static Map<String, String> localeCurrencyMap = createLocaleCurrencyMap();
	
	public Double value;
	
	/**
   * ISO 4217 currency code.
   * See http://en.wikipedia.org/wiki/ISO_4217.
	 */
	public String currency;
	

	public static Money parse(String value, String defaultCurrency) {
		// parse currency symbol
		String currency = "";
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isDigit(c) || c == ' ' || c == '-' || c == '(') {
				currency = value.substring(0, i).toUpperCase();
				value = value.substring(i).trim();
				break;
			}
		}
		if (currency.equals("")) {
			currency = defaultCurrency;
		}
		// reverse lookup 
		currency = getCurrencyFromSymbol(currency);
		return new Money(Double.parseDouble(value), currency);
		
	}
	
	public Money() {
	}
	
	public Money(Double value, String currency) {
		this.value = value;
		this.currency = currency;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(value, currency);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof Money) {
			Money other = (Money) obj;
			return Objects.equal(value, other.value)
					&& Objects.equal(currency, other.currency);
		}
		return false;
	}
	
	@Override
	public String toString() {
		if (currency != null) {
			return getSymbol(currency) + " " + value;
		} else {
			return "" + value;
		}
	}
	
	public static String getSymbol(String currency) {
		String symbol = symbolMap.get(currency);
		if (symbol != null) {
			return symbol;
		}
		return currency;
	}
	
	public static String getCurrencyFromSymbol(String symbol) {
		for (Entry<String, String> entry : symbolMap.entrySet()) {
			if (entry.getValue().equals(symbol)) {
				return entry.getKey();
			}
		}
		return symbol;
	}
	
	public static int getDecimals(String currency) {
		Integer decimal = decimalsMap.get(currency);
		if (decimal != null) {
			return decimal;
		}
		return 2;
	}
	
	private static Map<String, String> createSymbolMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("EUR", "\u20AC");
		map.put("INR", "\u20B9");
		map.put("GPB", "\u00A3");
		map.put("USD", "$");
		return map;
	}
	
	private static Map<String, String> createLocaleCurrencyMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("nl", "EUR");
		map.put("be", "EUR");
		map.put("de", "EUR");
		map.put("it", "EUR");
		map.put("es", "EUR");
		map.put("fr", "EUR");
		map.put("gr", "EUR");
		return map;
	}
	
	private static Map<String, Integer> createDecimalsMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("BHD", 3);
		map.put("BYR", 0);
		map.put("CLF", 0);
		map.put("CLP", 0);
		map.put("CVE", 0);
		map.put("DJF", 0);
		map.put("GNF", 0);
		map.put("IDR", 0);
		map.put("IQD", 0);
		map.put("IRR", 0);
		map.put("ISK", 0);
		map.put("JOD", 3);
		map.put("JPY", 0);
		map.put("KMF", 0);
		map.put("KPW", 0);
		map.put("KRW", 0);
		map.put("KWD", 3);
		map.put("LAK", 0);
		map.put("LBP", 0);
		map.put("LYD", 3);
		map.put("MGA", 0);
		map.put("MMK", 0);
		map.put("MOP", 1);
		map.put("MRO", 0);
		map.put("OMR", 3);
		map.put("PYG", 0);
		map.put("RWF", 0);
		map.put("SLL", 0);
		map.put("STD", 0);
		map.put("TND", 3);
		map.put("UGX", 0);
		map.put("VND", 0);
		map.put("VUV", 0);
		map.put("XAF", 0);
		map.put("XAG", 0);
		map.put("XAU", 0);
		map.put("XBA", 0);
		map.put("XBB", 0);
		map.put("XBC", 0);
		map.put("XBD", 0);
		map.put("XDR", 0);
		map.put("XFU", 0);
		map.put("XOF", 0);
		map.put("XPD", 0);
		map.put("XPF", 0);
		map.put("XPT", 0);
		map.put("XTS", 0);
		map.put("XXX", 0);
		map.put("YER", 0);
		map.put("ZMK", 0);
		return map;
	}
}
