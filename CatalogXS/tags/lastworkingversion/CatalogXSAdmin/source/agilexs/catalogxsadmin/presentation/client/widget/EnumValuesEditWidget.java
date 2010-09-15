package agilexs.catalogxsadmin.presentation.client.widget;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ResourceBundle;
import agilexs.catalogxsadmin.presentation.client.Util;
import agilexs.catalogxsadmin.presentation.client.binding.Binding;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.EnumValue;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;

public class EnumValuesEditWidget extends Composite {

  private static final I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private static final ResourceBundle rb = GWT.create(ResourceBundle.class);
  private static final String NO_VALUE = "-1";

  private final FlowPanel panel = new FlowPanel();
  private final ListBox listBox = new ListBox();
  private final Image edit = new Image(rb.editImage());
  private final EnumValuesEditPopupPanel popup;
  private String language;
  private PropertyValueBinding pvb;
//  private List<EnumValue> enumValues;

  public EnumValuesEditWidget(boolean editMode) {
    initWidget(panel);
    panel.add(listBox);
    if (editMode) {
      panel.add(edit);
      edit.addClickHandler(new ClickHandler() {
        @Override public void onClick(ClickEvent event) {
          popup.show((Property) pvb.property().getData());
        }});
      edit.setTitle(i18n.editEnumTitle());
      popup = new EnumValuesEditPopupPanel(panel);
      popup.addValueChangeHandler(new ValueChangeHandler<List<EnumValue>>(){
        @Override public void onValueChange(ValueChangeEvent<List<EnumValue>> event) {
          ((PropertyValue) pvb.getData()).getProperty().setEnumValues(event.getValue());
          setListBox(getSelected());
        }});
    } else {
      popup = null;
    }
  }

  public Binding bind(final PropertyValueBinding pvb) {
    this.pvb = pvb;
    listBox.addChangeHandler(new ChangeHandler() {
      @Override public void onChange(ChangeEvent event) {
        final String s = getSelected();

        pvb.enumValue().setData(s == null ? null : Integer.valueOf(s));
      }});
    return HasTextBinding.bind(new HasText() {
      @Override public String getText() {
        return getSelected();
      }
      @Override public void setText(String text) {
        setListBox(text);
      }}, pvb.enumValue(), BindingConverters.INTEGER_CONVERTER);
  }

  public void show(String language) {
    this.language = language;
  }

  private void setListBox(String selected) {
    listBox.clear();
    listBox.addItem(i18n.noValue(), NO_VALUE);
    final List<EnumValue> enumValues =
      ((PropertyValue) pvb.getData()).getProperty().getEnumValues();

    if (enumValues != null) {
      for (EnumValue enumValue : enumValues) {
        listBox.addItem(Util
            .getLabel(enumValue.getLabels(), language, false).getLabel(), ""
            + enumValue.getValue());
      }
    }
    setSelected(selected);
  }

  private String getSelected() {
    final int s = listBox.getSelectedIndex();

    return s >= 0 && !NO_VALUE.equals(listBox.getValue(s)) ? listBox.getValue(s) : null;
  }

  private void setSelected(String value) {
    for (int i = 0; i < listBox.getItemCount(); i++) {
      if (value != null && value.equals(listBox.getValue(i))) {
        listBox.setSelectedIndex(i);
        break;
      }
    }
  }
}
