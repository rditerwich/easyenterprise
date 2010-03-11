package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

class ProductGroupPropertiesView extends Composite implements View {

  private final static ResourceBundle rb = GWT.create(ResourceBundle.class);

  public class PGPRowView {
    final TextBox defaultName = new TextBox();
    final ListBox type = new ListBox();
    final SimplePanel defaultValueWrapper = new SimplePanel();
    final TextBox name = new TextBox();
    final SimplePanel valueWrapper = new SimplePanel();
    Widget value;
    final CheckBox pgOnly = new CheckBox();

    public TextBoxBase getDefaultName() {
      return defaultName;
    }

    public TextBoxBase getName() {
      return name;
    }

    public ListBox getType() {
      return type;
    }

    public Widget getDefaultValueWidget() {
      return defaultValueWrapper;
    }

    public Widget getValueWidget() {
      return valueWrapper;
    }

    public CheckBox getPGOnly() {
      return pgOnly;
    }

    public Widget setDefaultValueWidget(PropertyType type) {
      return setWidget(defaultValueWrapper, type);
    }

    public Widget setValueWidget(PropertyType type) {
      return setWidget(valueWrapper, type);
    }

    private Widget setWidget(SimplePanel wrapper, PropertyType type) {
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
      wrapper.setWidget(value);
      return value;
    }
  }

  interface ProductGroupPropertiesUiBinder extends UiBinder<Widget, ProductGroupPropertiesView> {}
  private static ProductGroupPropertiesUiBinder uiBinder = GWT.create(ProductGroupPropertiesUiBinder.class);

  @UiField Button addNew;
  @UiField Grid grid;

  private ArrayList<PGPRowView> rowViews = new ArrayList<PGPRowView>();

  public ProductGroupPropertiesView() {
    initWidget(uiBinder.createAndBindUi(this));
    gridReset();
    addNew.setText("Add new property");
    addHeader();
  }

  @Override
  public Widget getViewWidget() {
    return this;
  }

  private void addHeader() {
    grid.setWidget(0, 0, new InlineLabel("Name"));
    grid.setWidget(0, 1, new InlineLabel("Type"));
    grid.setWidget(0, 2, new InlineLabel("Default value"));
    grid.setWidget(0, 3, new InlineLabel("ProductGroup Only"));
    grid.setWidget(0, 4, new InlineLabel("Language Specific Name"));
    grid.setWidget(0, 5, new InlineLabel("Language Default value"));
    grid.setWidget(0, 6, new InlineHTML("&nbsp;"));
  }

  public Button getNewPropertyButton() {
    return addNew;
  }

  public PGPRowView addRow() {
    return setRow(grid.getRowCount()-1);
  }

  public void gridReset() {
    grid.resize(1, 7);
  }

  /**
   * Initializes the grid at the given row + 1 number. 1 is added because this
   * first row is reserved for the header. This means the  
   * @param row
   * @return
   */
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
    grid.setWidget(row, 0, (Widget) rowView.getDefaultName());
    grid.setWidget(row, 1, rowView.getType());
    grid.setWidget(row, 2, rowView.getDefaultValueWidget());
    grid.setWidget(row, 3, rowView.getPGOnly());
    grid.setWidget(row, 4, (Widget) rowView.getName());
    grid.setWidget(row, 5, rowView.getValueWidget());
    grid.setWidget(row, 6, new Image(rb.deleteImage()));

    return rowView;
  }
}
