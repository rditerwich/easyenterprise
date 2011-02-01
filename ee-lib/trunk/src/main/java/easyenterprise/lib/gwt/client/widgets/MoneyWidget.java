package easyenterprise.lib.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.CurrencyData;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.util.Money;

public abstract class MoneyWidget extends Composite {
	private List<CurrencyData> currencies = new ArrayList<CurrencyData>();
	
	private Label currencySymbol;
	private TextBox moneyText;
	private Money value;
	private ListBox moneyListbox;

	
	public MoneyWidget() {
		initWidget(new FlowPanel(){{
			add(currencySymbol = new InlineLabel()); // Currency symbol
			add(moneyText = new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						Money newValue = new Money();
						newValue.value = Double.parseDouble(moneyText.getText());
						newValue.currency = getSelectedCurrency().getCurrencyCode();
						if (!newValue.equals(value)) {
							MoneyWidget.this.setValue(newValue);
							valueChanged(value);
						}
					}
				});
			}});
			add(moneyListbox = new ListBox(){{
				for (CurrencyData currency : CurrencyList.get()) {
					currencies.add(currency);
				}
				Collections.sort(currencies, new Comparator<CurrencyData>() {
					public int compare(CurrencyData o1, CurrencyData o2) {
						return o1.getCurrencyCode().compareTo(o2.getCurrencyCode());
					}
				});
				for (CurrencyData currency : currencies) {
					addItem(currency.getCurrencyCode());
				}
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						CurrencyData currency = getSelectedCurrency();
						
						if (value != null) {
							Money newValue = new Money(value.value, currency != null ? currency.getCurrencyCode() : null);
							if (!newValue.equals(value)) {
								MoneyWidget.this.setValue(newValue);
								valueChanged(value);
							}
						} else {
							setSelectedCurrency(currency);
						}
					}
				});
			}});
		}});
		
		setEmptyValue();
	}
	
	public void setValue(Money value) {
		this.value = value;
		if (value != null && value.currency != null && value.value != null) {
			CurrencyData currency = CurrencyList.get().lookup(value.currency);
			setSelectedCurrency(currency);
			moneyText.setText(MoneyFormatUtil.value(value, currency));
		} else {
			setEmptyValue();
		}
	}

	public Money getValue() {
		return value;
	}
	
	abstract protected void valueChanged(Money newValue);
	
	private void setEmptyValue() {
		setSelectedCurrency(CurrencyList.get().lookup("EUR"));
		moneyText.setText("");
	}

	private void setSelectedCurrency(CurrencyData currency) {
		currencySymbol.setText(currency != null? currency.getCurrencySymbol() : null);
		moneyListbox.setSelectedIndex(currencies.indexOf(currency));
	}

	private CurrencyData getSelectedCurrency() {
		if (moneyListbox.getSelectedIndex() < 0 || moneyListbox.getSelectedIndex() >= currencies.size()) {
			return currencies.get(moneyListbox.getSelectedIndex());
		} else {
			return CurrencyList.get().lookup("EUR");
		}
	}
}

