package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.widget.PropertyValueWidget;
import agilexs.catalogxsadmin.presentation.client.widget.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  public enum SHOW {
    NO_PRODUCTS, PRODUCTS, PRODUCT
  }

  private final DeckPanel deck = new DeckPanel();
  private final Button newProductButton = new Button(i18n.newProduct());
  private final Button saveButton = new Button(i18n.saveChanges());
  private final Anchor back = new Anchor();
  private final HTML pname = new HTML();
  private final FlowPanel propertiesDeck = new FlowPanel();
  private final FlowPanel propertyValuesPanel = new FlowPanel();
  private final HTML pvHeader = new HTML(i18n.h3(i18n.propertyValues()));
  final ListBox productGroup = new ListBox();

//  private final FlowPanel labels = new FlowPanel();
  private final Table productTable = new Table();

//  private Label lastSelected;

  public ProductView(boolean inlineNewProductButton) {
    initWidget(deck);

    deck.add(new HTML(i18n.noProducts()));
    //Overview table
    final DockLayoutPanel overviewPanel = new DockLayoutPanel(Unit.PX);

    deck.add(overviewPanel);
    final HorizontalPanel tophp = new HorizontalPanel();
    if (inlineNewProductButton) {
      tophp.add(newProductButton);
    }
    overviewPanel.addNorth(tophp, 30);
    final ScrollPanel spo = new ScrollPanel(productTable);

    spo.getElement().getStyle().setPadding(10, Unit.PX);
    overviewPanel.add(spo);

    //Detail page
    final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
    deck.add(detailPanel);
    final HorizontalPanel toph = new HorizontalPanel();
    back.setHTML(i18n.backToProductOverview());
    toph.add(back);
    saveButton.getElement().getStyle().setMarginLeft(200, Unit.PX);
    toph.add(saveButton);
    final VerticalPanel top = new VerticalPanel();
    top.add(toph);
    top.add(pname);
    detailPanel.addNorth(top, 40);
    final FlowPanel productPanel = new FlowPanel();
    final ScrollPanel sp = new ScrollPanel(productPanel);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
    detailPanel.add(sp);
    productPanel.add(pname);
//    final HorizontalPanel properties = new HorizontalPanel();
//
//    productPanel.add(properties);
//    properties.add(propertiesDeck);
    final FlowPanel fpp = new FlowPanel();
    final FlowPanel fp = new FlowPanel();

    fp.add(new InlineLabel(i18n.explainOwnerGroup()));
    fp.add(productGroup);
    productGroup.getElement().getStyle().setMarginLeft(10, Unit.PX);
    add(fpp, i18n.group(), fp);
    productPanel.add(fpp);
    productPanel.add(propertiesDeck);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
  }

  public void clear() {
    propertiesDeck.clear();
    productGroup.clear();
    propertyValuesPanel.clear();
    propertyValuesPanel.removeFromParent();
  }

  public void addPropertyValues(String name, Widget widget) {
    if (!propertyValuesPanel.isAttached()) {
      propertiesDeck.add(propertyValuesPanel);
      propertyValuesPanel.add(pvHeader);
    }
    add(propertyValuesPanel, name, widget);
  }

  private void add(FlowPanel parent, String name, Widget widget) {
    final HTML lbl = new HTML(i18n.h3(name));

    parent.getElement().getStyle().setPadding(8, Unit.PX);
    parent.getElement().getStyle().setMarginBottom(8, Unit.PX);
    parent.setStyleName("properties");
    parent.add(lbl);
    parent.add(widget);
  }

  public HasClickHandlers backClickHandlers() {
    return back;
  }

  public Button getNewProductButton() {
    return newProductButton;
  }

  public HasClickHandlers newProductButtonClickHandlers() {
    return newProductButton;
  }

  public HasClickHandlers saveButtonClickHandlers() {
    return saveButton;
  }

  public ListBox getGroupListBox() {
    return productGroup;
  }

  /**
   * Diplays button for status when saving in progress.
   */
  public void setSaving(boolean saving) {
    if (saving) {
      saveButton.setText(i18n.saving());
      saveButton.setEnabled(false);
    } else {
      saveButton.setText(i18n.saveChanges());
      saveButton.setEnabled(true);
    }
  }

  public void setProductName(String name) {
    pname.setHTML(i18n.h2(name));
  }

  public Table getProductTable() {
    return productTable;
  }

  public void setProductTableHeader(int column, String text) {
    if (column >= productTable.getColumnCount()) {
      productTable.resizeColumns(column+1);
    }
    productTable.setHeaderHTML(0, column, text);
  }

  public void setProductTableCellImage(int row, int column, ImageResource image) {
    productTable.getCellFormatter().getElement(row, column).getStyle().setCursor(Cursor.POINTER);
    if (column >= productTable.getColumnCount()) {
      productTable.resizeColumns(column+1);
    }
    productTable.setWidget(row, column, new Image(image));
  }

  public void setProductTableCell(int row, int column, PropertyValue pv) {
    if (column >= productTable.getColumnCount()) {
      productTable.resizeColumns(column+1);
    }
    productTable.setWidget(row, column, new PropertyValueWidget(pv));
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public void showPage(SHOW show) {
    setSaving(false); //reset save button
    int i = 0;
    switch (show) {
    case NO_PRODUCTS: i = 0; productTable.setVisible(false); break;
    case PRODUCTS: i = 1; productTable.setVisible(true); break;
    case PRODUCT: i = 2; break;
    }
    deck.showWidget(i);
  }
}
