package agilexs.catalogxsadmin.presentation.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ResourceBundle;
import agilexs.catalogxsadmin.presentation.client.Util;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.EnumValue;
import agilexs.catalogxsadmin.presentation.client.catalog.EnumValueBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.util.BindingTuple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * Popup panel to edit enum values.
 */
class EnumValuesEditPopupPanel extends PopupPanel implements HasValueChangeHandlers<List<EnumValue>> {

  private static final I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private static final ResourceBundle rb = GWT.create(ResourceBundle.class);
  private static final int DELETE_COLUMN = 2;

  private static class Tuple extends BindingTuple<EnumValue, EnumValueBinding> {
    private static String curLang = null;

    public static void setLanguage(String lang) {
      curLang = lang;
    }

    @Override
    protected String getCurrentLanguage() {
      return curLang;
    }

    @Override
    protected EnumValueBinding newBinding() {
      return new EnumValueBinding();
    }
  }

  private final FlowPanel panel = new FlowPanel();
  private final ListBox languageList = new ListBox();
  private final Grid grid = new Grid(1, 3);
  private final Button ok = new Button(i18n.ok());
  private final Anchor cancel = new Anchor(i18n.cancel());
  private final TextBox newEnum = new TextBox();
  private final Button add = new Button(i18n.add());
  private final List<Tuple> bindings = new ArrayList<Tuple>();
  private final Panel parent;

  private String language = "en";
  private Property currentProperty;
  private Property orgProperty;

  public EnumValuesEditPopupPanel(Panel parent) {
    this.parent = parent;
    setWidget(panel);
    panel.add(grid);

    final HorizontalPanel hp = new HorizontalPanel();
    panel.add(hp);
    hp.add(ok);
    hp.add(cancel);
    //delete
    grid.addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        final Cell cell = grid.getCellForEvent(event);

        if (cell.getCellIndex() == DELETE_COLUMN && cell.getRowIndex() > 0 &&
            cell.getRowIndex() < grid.getRowCount() - 1) {
          currentProperty.getEnumValues().remove(cell.getRowIndex());
          grid.resizeRows(currentProperty.getEnumValues().size()+1);
          setRowAdd();
          refresh();
        }
      }});
    // add
    add.addClickHandler(new ClickHandler(){
      @Override public void onClick(ClickEvent event) {
        final EnumValue addEV = new EnumValue();
        final Label l = new Label();

        int max = 0;
        for (EnumValue ev : currentProperty.getEnumValues()) {
          if (ev.getValue() >  max) {
            max = ev.getValue();
          }
        }
        addEV.setValue(max + 1);
        addEV.setProperty(currentProperty);
        l.setLabel(newEnum.getValue());
        addEV.getLabels().add(l);
        currentProperty.getEnumValues().add(addEV);
        final int size = grid.getRowCount();

        grid.resizeRows(size+1);
        createRow(size-1, addEV);
        setRowAdd();
        newEnum.setText("");
      }});
    ok.addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        orgProperty.setEnumValues(currentProperty.getEnumValues());
        hide();
        ValueChangeEvent.fire(EnumValuesEditPopupPanel.this, orgProperty.getEnumValues());
    }});
    cancel.addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        hide();
    }});
    languageList.addStyleName("languageField");
    languageList.addChangeHandler(new ChangeHandler(){
      @Override public void onChange(ChangeEvent event) {
        language = languageList.getValue(languageList.getSelectedIndex());
        refresh();
      }});
  }
  
  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<EnumValue>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public void show(Property property) {
    orgProperty = property;
    currentProperty = property.clone(new HashMap());
    for (Language lang : CatalogCache.get().getActiveCatalog().getLanguages()) {
      languageList.addItem(lang.getDisplayName(), lang.getName());
      if (lang.getName().equals(language)) {
        languageList.setItemSelected(languageList.getItemCount()-1, true);
      }
    }
    final List<EnumValue> enumValues = currentProperty.getEnumValues();

    grid.clear();
    grid.resizeRows(enumValues.size()+2);
    grid.setWidget(0, 0, new HTML("&nbsp;"));
    grid.setWidget(0, 1, languageList);
    grid.setWidget(0, 2, new HTML("&nbsp;"));
    for (int row = 0; row < enumValues.size(); row++) {
      createRow(row + 1, enumValues.get(row));
    }
    setRowAdd();
    showRelativeTo(parent);
  }

  private void createRow(int row, EnumValue enumValue) {
    final Tuple pb = new Tuple();

    bindings.add(pb);
    final TextBox tb = new TextBox();

    TextBoxBaseBinding.<List<Label>>bind(tb, pb.getDefaultBinding().labels(), new BindingConverter<List<Label>, String>() {
      private List<Label> labels;

      @Override
      public List<Label> convertFrom(String data) {
        Label foundLabel = null;
        for (Label label : labels) {
          if (Util.matchLang(null, label.getLanguage())) {
            foundLabel = label;
          }
        }
        if (foundLabel == null) {
          foundLabel = new Label();
          labels.add(foundLabel);
        }
        foundLabel.setLabel(data);
        return labels;
      }

      @Override
      public String convertTo(List<Label> data) {
        labels = data;
        return Util.getLabel(data, null).getLabel();
      }});
    final TextBox ltb = new TextBox();

    TextBoxBaseBinding.<List<Label>>bind(ltb, pb.getBinding().labels(), new BindingConverter<List<Label>, String>() {
      private List<Label> labels;

      @Override
      public List<Label> convertFrom(String data) {
        Label foundLabel = null;
        for (Label label : labels) {
          if (Util.matchLang(language, label.getLanguage())) {
            foundLabel = label;
          }
        }
        if (foundLabel == null) {
          foundLabel = new Label();
          labels.add(foundLabel);
        }
        foundLabel.setLabel(data);
        return labels;
      }

      @Override
      public String convertTo(List<Label> data) {
        labels = data;
        return Util.getLabel(data, language).getLabel();
      }});
    grid.setWidget(row, 0, tb);
    grid.setWidget(row, 1, ltb);
    grid.getCellFormatter().addStyleName(row, 1, "languageField");
//    ltb.addStyleName("languageField");
    grid.setWidget(row, DELETE_COLUMN, new Image(rb.deleteImage()));
    pb.setValue(enumValue, null);
    pb.setValue(enumValue, language);
  }

  private void refresh() {
    Tuple.setLanguage(language);
    final List<EnumValue> enumValues = currentProperty.getEnumValues();
    final Iterator<Tuple> iterator = bindings.iterator();

    for (EnumValue enumValue : enumValues) {
      final Tuple t = iterator.next();

      t.setValue(enumValue, null);
      t.setValue(enumValue, language);
    }
  }

  private void setRowAdd() {
    final int row = currentProperty.getEnumValues().size()+1;
    grid.setWidget(row, 0, newEnum);
    grid.setWidget(row, 1, add);
    grid.getCellFormatter().removeStyleName(row, 1, "languageField");
    grid.setWidget(row, 2, new HTML("&nbsp;")); 
  }
}
