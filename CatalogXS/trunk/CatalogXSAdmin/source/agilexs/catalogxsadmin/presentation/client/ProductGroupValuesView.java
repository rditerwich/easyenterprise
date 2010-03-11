package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupValuesView extends Composite implements View {

  public class PGPRowView {
    final Label name = new Label();
    final Label type = new Label();
    final SimplePanel valueWrapper = new SimplePanel();
    Widget value;

    public HasText getName() {
      return name;
    }

    public HasText getType() {
      return type;
    }

    public Widget getValueWidget() {
      return valueWrapper;
    }

    public Widget setValueWidget(PropertyType type) {
      switch (type) {
      case Enum:
        value = new TextBox();
        break;
      case FormattedText:
        value = new TextBox();
        break;
      case Media:
        value = new TextBox();
        break;
      case String:
        value = new TextArea();
        break;
      case Boolean:
        value = new CheckBox();
        break;
      case Acceleration:
      case AmountOfSubstance:
      case Angle:
      case Area:
      case ElectricCurrent:
      case Energy:
      case Frequency:
      case Integer:
      case Length:
      case LuminousIntensity:
      case Money:
      case Power:
      case Real:
      case Mass:
      case Temperature:
      case Time:
      case Velocity:
      case Voltage:
      case Volume:
      default:
        value = new TextBox();
      }
      valueWrapper.setWidget(value);
      return value;
    }
  }

  interface ProductGroupValuesUiBinder extends UiBinder<Widget, ProductGroupValuesView> {}
  private static ProductGroupValuesUiBinder uiBinder = GWT.create(ProductGroupValuesUiBinder.class);

  @UiField Grid grid;
  @UiField InlineHTML name;

  private ArrayList<PGPRowView> rowViews = new ArrayList<PGPRowView>();

  public ProductGroupValuesView() {
    initWidget(uiBinder.createAndBindUi(this));
    grid.resize(1, 3);
    //addNew.setText("Add new property");
    addHeader();
  }

  @Override
  public Widget getViewWidget() {
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
    grid.setWidget(row, 2, rowView.getValueWidget());

    return rowView;
  }

  public void setName(String name) {
    this.name.setHTML(name);
  }

  private void addHeader() {
    grid.setWidget(0, 0, new InlineLabel("Name"));
    grid.setWidget(0, 1, new InlineLabel("Type"));
    grid.setWidget(0, 2, new InlineLabel("Value"));
  }
}
