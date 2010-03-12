package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductView extends Composite implements View {

  public enum SHOW {
    PRODUCT_GROUP, PRODUCTS, PRODUCT
  }

  
  final SplitLayoutPanel panel = new SplitLayoutPanel();
  final Tree tree = new Tree();
  final Button newButton = new Button("Add new product");
  final Button saveButton = new Button("Save changes");
  final ListBox languageList = new ListBox();
  final DeckPanel deck = new DeckPanel();

  final Anchor back = new Anchor();
  final HTML name = new HTML();
  final FlowPanel propertiesPanel = new FlowPanel();

  final Grid productTable = new Grid(); 
  private HTML productGroupName = new HTML();

  public ProductView() {
    initWidget(panel);

    panel.addWest(tree, 300);
    final DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
    panel.add(mainPanel);

    final FlowPanel buttonBar = new FlowPanel();
    buttonBar.add(newButton);
    buttonBar.add(languageList);
    buttonBar.add(saveButton);
    //TODO auto enable save button on changes: saveButton.setEnabled(false);
    //TODO build language listbox items dynamic instead of static 
    languageList.getElement().getStyle().setMarginLeft(40, Unit.PX);
    mainPanel.addNorth(buttonBar, 40);
    mainPanel.add(deck);

    //ProductGroup page
    deck.add(new FlowPanel());
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
    back.addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        showPage(SHOW.PRODUCTS);
      }});
    top.add(back);
    top.add(name);
    detailPanel.addNorth(top, 40);
    final ScrollPanel sp = new ScrollPanel(propertiesPanel);
    //allPropertiesPanel.add(new HTML("<h3>Relations</h3>"));
    //allPropertiesPanel..add(relations);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
    detailPanel.add(sp);
  }

  public Tree getTree() {
    return tree;
  }

  public FlowPanel getPropertiesPanel() {
    return propertiesPanel;
  }

  public HasText getName() {
    return name;
  }

  public HasText getProductGroupName() {
    return name;
  }

  public Grid getProductTable() {
    return productTable;
  }

  public void setProductTableCell(int row, int column, PropertyValue pv) {
    if (column >= productTable.getColumnCount()) {
      productTable.resizeColumns(column+1);
    }
    productTable.setWidget(row, column, getWidget(pv));
  }

  public HasClickHandlers getNewButtonClickHandler() {
    return newButton;
  }

  public HasClickHandlers getSaveButtonClickHandler() {
    return saveButton;
  }

  @Override
  public Widget getViewWidget() {
    return this;
  }

  public void showPage(SHOW show) {
    int i = 0;
    switch (show) {
    case PRODUCT_GROUP: i = 0; break;
    case PRODUCTS: i = 1; break;
    case PRODUCT: i = 2; break;
    }
    deck.showWidget(i);
  }

  /**
   * Sets the languages on the Language ListBox
   * @param languages
   * @param selected
   */
  public void setLanguages(List<List<String>> languages, String selected) {
    for (List<String> lang : languages) {
      languageList.addItem(lang.get(1), lang.get(0));
      if (lang.get(0).equals(selected)) {
        languageList.setItemSelected(languageList.getItemCount()-1, true);
      }
    }
  }

  public HasChangeHandlers getLanguageChangeHandler() {
    return languageList;
  }

  public String getSelectedLanguage() {
    return languageList.getValue(languageList.getSelectedIndex());
  }
  
  public TreeItem addTreeItem(TreeItem parent, String text) {
    final TreeItem item = new TreeItem(text);

    if (parent == null) {
      tree.addItem(item);
    } else {
      parent.addItem(item);
    }
    return item;
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
      ((Label) value).setText(String.valueOf(pv.getStringValue()));
      break;
    case Boolean:
      value = new CheckBox();
      ((CheckBox)value).setEnabled(false);
      ((CheckBox)value).setValue(Boolean.valueOf(pv.getBooleanValue()));
      break;
    case Money:
      value = new Label();
      ((Label) value).setText(String.valueOf(pv.getMoneyValue()));
      break;
    case Real:
      value = new Label();
      ((Label) value).setText(String.valueOf(pv.getRealValue()));
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
      ((Label) value).setText(String.valueOf(pv.getIntegerValue()));
    }
    return value;
  }
}
