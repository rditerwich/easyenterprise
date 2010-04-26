package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.util.CatalogXSWidgetUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View that displays a table of inherited properties for one parent group.
 * The user can modify the values.
 */
public class ItemValuesView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  public class PGPRowView {
    final Label name = new Label();
    final Label type = new Label();
    final SimplePanel valueWrapper = new SimplePanel();
    final SimplePanel defaultValueWrapper = new SimplePanel();

    public HasText getName() {
      return name;
    }

    public HasText getType() {
      return type;
    }

    public Widget getDefaultValueWidget() {
      return defaultValueWrapper;
    }

    public Widget getValueWidget() {
      return valueWrapper;
    }

    public Widget setDefaultValueWidget(PropertyType type) {
      return CatalogXSWidgetUtil.setPropertyTypeWidget(defaultValueWrapper, type, false);
    }

    public Widget setValueWidget(PropertyType type) {
      return CatalogXSWidgetUtil.setPropertyTypeWidget(valueWrapper, type, false);
    }
  }

  interface ItemValuesUiBinder extends UiBinder<Widget, ItemValuesView> {}
  private static ItemValuesUiBinder uiBinder = GWT.create(ItemValuesUiBinder.class);

  @UiField Grid grid;
  @UiField HTML name;

  private ArrayList<PGPRowView> rowViews = new ArrayList<PGPRowView>();

  public ItemValuesView() {
    initWidget(uiBinder.createAndBindUi(this));
    grid.resize(1, 4);
    addHeader();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public void resizeRows(int rows) {
    grid.resizeRows(rows + 1);
  }

  public PGPRowView setRow(int row) {
    PGPRowView rowView;

    if (rowViews.size() > row) {
      rowView = rowViews.get(row);
    } else {
      rowView = new PGPRowView();
      rowViews.add(rowView);
    }
    row = row + 1; //offset header
    if (row >= grid.getRowCount()) {
      grid.resizeRows(row + 1);
    }
    grid.setWidget(row, 0, (Widget) rowView.getName());
    grid.setWidget(row, 1, (Widget) rowView.getType());
    grid.setWidget(row, 2, rowView.getDefaultValueWidget());
    grid.setWidget(row, 3, rowView.getValueWidget());
    grid.getCellFormatter().addStyleName(row, 3, "languageField");
    return rowView;
  }

  public void setName(String name) {
    this.name.setHTML(i18n.h3(i18n.propertiesFrom(name)));
  }

  private void addHeader() {
    grid.setWidget(0, 0, new InlineLabel(i18n.name()));
    grid.setWidget(0, 1, new InlineLabel(i18n.type()));
    grid.setWidget(0, 2, new InlineLabel(i18n.value()));
    grid.setWidget(0, 3, new InlineLabel(i18n.languageSpecificValue()));
    grid.getCellFormatter().addStyleName(0, 3, "languageField");
  }
}
