package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.binding.CheckBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListPropertyBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyType;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupPropertiesView extends Composite implements View {

  private static final List<String> propertyTypeList = new ArrayList<String>();
  static {
    for (PropertyType i : PropertyType.values()) {
      propertyTypeList.add(i.toString());
    }
  }

  interface ProductGroupPropertiesUiBinder extends UiBinder<Widget, ProductGroupPropertiesView> {}
  private static ProductGroupPropertiesUiBinder uiBinder = GWT.create(ProductGroupPropertiesUiBinder.class);
 
  @UiField InlineHTML name;
  @UiField Button addNew;
  @UiField ListBox languages;
  @UiField Grid grid;

  public ProductGroupPropertiesView() {
    initWidget(uiBinder.createAndBindUi(this));
    grid.resize(1, 4);
    addHeader();
  }

  @Override
  public Widget getViewWidget() {
    return this;
  }

  private void addHeader() {
    grid.setWidget(0, 0, new InlineLabel("Default Name"));
    grid.setWidget(0, 1, new InlineLabel("Type"));
    grid.setWidget(0, 2, new InlineLabel("Name"));
    grid.setWidget(0, 3, new InlineLabel("Value"));
    grid.setWidget(0, 3, new InlineLabel("ProductGroup Only"));
  }

  public PropertyBinding addRow(int row) {
    final PropertyBinding pb = new PropertyBinding();
    final TextBox name = new TextBox();
    final ListBox type = new ListBox();
    
    ListBoxBinding.bind(type, new ListPropertyBinding<String>() {
        @Override
        protected List<String> doGetData() {
          return propertyTypeList;
        }
        @Override
        protected void doSetData(List<String> data) {
          //Nothing to do, list doesn't change.
        }
      }, pb.type());
    type.addChangeHandler(new ChangeHandler(){
      @Override
      public void onChange(ChangeEvent event) {
        pb.type().getData();
        //final Widget value = valueWidget(event.getValue());
        //grid.setWidget(row, 3, value);
      }});
    final TextBox label = new TextBox();
    //HasTextBinding.bind(label, )
    final CheckBox pgOnly = new CheckBox();
    CheckBoxBinding.bind(pgOnly, pb.productGroupProperty());
    //HasTextBinding.bind(name, pb.name());
    //gegeven het type wordt iets anders getoond.
    grid.setWidget(row, 0, name);
    grid.setWidget(row, 1, type);
    grid.setWidget(row, 2, label);
    grid.setWidget(row, 4, pgOnly);
    return pb;
  }

  public void setName(String productGroupName) {
    name.setHTML(productGroupName);
  }
  
  private Widget valueWidget(PropertyType type) {
    Widget w;
    switch (type) {
    case Enum:
      w = new TextBox();
      break;
    case FormattedText:
      w = new TextBox();
      break;
    case Media:
      w = new TextBox();
      break;
    case String:
      w = new TextArea();
      break;
    case Boolean:
      w = new CheckBox();
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
      w = new TextBox();
    }
    return w;
  }
}
