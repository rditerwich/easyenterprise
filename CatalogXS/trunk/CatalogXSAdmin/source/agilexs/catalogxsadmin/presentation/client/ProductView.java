package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.widget.PropertyValueWidget;

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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  public enum SHOW {
    NO_PRODUCTS, PRODUCTS, PRODUCT
  }

  private final SimplePanel parentsPanel = new SimplePanel();
  private final DeckPanel deck = new DeckPanel();
  private final Button newProductButton = new Button(i18n.newProduct());
  private final Button saveButton = new Button(i18n.saveChanges());
  private final Anchor back = new Anchor();
  private final HTML pname = new HTML();
  private final FlowPanel propertiesPanel = new FlowPanel();

  private final Grid productTable = new Grid();

  public ProductView() {
    initWidget(deck);

    deck.add(new HTML(i18n.noProductsInGroup()));
    //Overview table
    final DockLayoutPanel overviewPanel = new DockLayoutPanel(Unit.PX);

    deck.add(overviewPanel);
    final HorizontalPanel tophp = new HorizontalPanel();
    tophp.add(newProductButton);
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
    productPanel.add(parentsPanel);
    productPanel.add(propertiesPanel);
  }

  public HasClickHandlers backClickHandlers() {
    return back;
  }

  public HasClickHandlers newProductButtonClickHandlers() {
    return newProductButton;
  }

  public HasClickHandlers saveButtonClickHandlers() {
    return saveButton;
  }

  public FlowPanel getPropertiesPanel() {
    return propertiesPanel;
  }

  public void setProductName(String name) {
    pname.setHTML(i18n.h2(name));
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

  public void setParentsPanel(View view) {
    parentsPanel.setWidget(view.asWidget());
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
}
