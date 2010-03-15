package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductView extends Composite implements View {

  public enum SHOW {
    NO_PRODUCTS, PRODUCTS, PRODUCT
  }

  final DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
  
  final DeckPanel deck = new DeckPanel();

  final Anchor back = new Anchor();
  final HTML pname = new HTML();
  final HTML pgname = new HTML();
  final FlowPanel propertiesPanel = new FlowPanel();

  final Grid productTable = new Grid(); 
  private HTML productGroupName = new HTML();

  public ProductView() {
    initWidget(deck);

    deck.add(new HTML("No products in this group"));
    //Overview table
    final DockLayoutPanel overviewPanel = new DockLayoutPanel(Unit.PX);
    deck.add(overviewPanel);
    overviewPanel.addNorth(productGroupName, 20);
    final ScrollPanel spo = new ScrollPanel(productTable);
    spo.getElement().getStyle().setPadding(10, Unit.PX);
    overviewPanel.add(spo);

    //Detail page
    final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
    deck.add(detailPanel);
    final VerticalPanel top = new VerticalPanel();
    back.setHTML("&laquo; Back to product overview");
    top.add(back);
    top.add(pgname);
    detailPanel.addNorth(top, 40);
    final FlowPanel productPanel = new FlowPanel();
    final ScrollPanel sp = new ScrollPanel(productPanel);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
    detailPanel.add(sp);

    productPanel.add(pname);
    productPanel.add(propertiesPanel);
  }

  public HasClickHandlers hasBackClickHandlers() {
    return back;
  }

  public FlowPanel getPropertiesPanel() {
    return propertiesPanel;
  }

  public void setProductName(String name) {
    pname.setHTML("<h2>" + name + "</h2>");
  }

  public void setProductGroupName(String name) {
    pgname.setHTML("<h2>" + name + "</h2>");
  }

  public Grid getProductTable() {
    return productTable;
  }

  public void setProductTableHeader(int column, String text) {
    if (column >= productTable.getColumnCount()) {
      productTable.resizeColumns(column+1);
    }
    productTable.setWidget(0, column, new Label(text));
  }

  public void setProductTableCell(int row, int column, PropertyValue pv) {
    if (column >= productTable.getColumnCount()) {
      productTable.resizeColumns(column+1);
    }
    productTable.setWidget(row, column, getWidget(pv));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public void showPage(SHOW show) {
    int i = 0;
    switch (show) {
    case NO_PRODUCTS: i = 0; break;
    case PRODUCTS: i = 1; break;
    case PRODUCT: i = 2; break;
    }
    deck.showWidget(i);
  }

  private Widget getWidget(PropertyValue pv) {
    Widget value = null;
    switch (pv.getProperty().getType()) {
    case Enum:
      value = new Label();
      break;
    case FormattedText:
      value = new Label();
      break;
    case Media:
      value = new Label();//FIXME: media should be different
      break;
    case String:
      value = new Label();
      ((Label) value).setText(Util.stringValueOf(pv.getStringValue()));
      break;
    case Boolean:
      value = new CheckBox();
      ((CheckBox)value).setEnabled(false);
      ((CheckBox)value).setValue(Boolean.valueOf(pv.getBooleanValue()));
      break;
    case Money:
      value = new HTML();
      ((HTML) value).setHTML(Util.formatMoney(pv.getMoneyValue()));
      break;
    case Real:
      value = new Label();
      ((Label) value).setText(Util.stringValueOf(pv.getRealValue()));
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
    case Power:
    case Mass:
    case Temperature:
    case Time:
    case Velocity:
    case Voltage:
    case Volume:
    default:
      value = new Label();
      ((Label) value).setText(Util.stringValueOf(pv.getIntegerValue()));
    }
    return value;
  }
}
