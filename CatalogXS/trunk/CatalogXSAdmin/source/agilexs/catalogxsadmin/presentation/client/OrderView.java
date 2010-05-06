package agilexs.catalogxsadmin.presentation.client;

import java.util.Date;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrderView extends Composite implements View {
  
  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private final static ResourceBundle rb = GWT.create(ResourceBundle.class);

  private final SplitLayoutPanel panel = new SplitLayoutPanel();
  private final Grid orderGrid = new Grid(1, 5);
  private final Grid productOrderGrid = new Grid(1, 5);
  
  public OrderView() {
    initWidget(panel);
    final ScrollPanel ordersSP = new ScrollPanel();
    final ScrollPanel productOrdersSP = new ScrollPanel();

    panel.addWest(ordersSP, 300);
    panel.add(productOrdersSP);
    ordersSP.add(orderGrid);
    productOrdersSP.add(productOrderGrid);
  }

  @Override
  public Widget asWidget() {
    return this;//new HTML(i18n.todo());
  }

  public void clear() {
    orderGrid.clear();
  }

  public void clearProductOrders() {
    productOrderGrid.clear();
  }

  public HasClickHandlers setDetailHandler(int row) {
    if (row+1 >= orderGrid.getRowCount()) {
      orderGrid.resizeRows(row+2);
    }
    final Image i = new Image(rb.editImage());

    orderGrid.setWidget(row+1, 0, i);
    return i;
  }

  public void setDate(int row, Date orderDate) {
    orderGrid.setWidget(row+1, 1, new Label(DateTimeFormat.getMediumDateFormat().format(orderDate)));
  }

  public void setVolume(int row, int volume) {
    orderGrid.setWidget(row+1, 2, new Label("" + volume));
  }

  public void setPrice(int row, String price) {
    orderGrid.setWidget(row+1, 3, new Label(price));
  }

  public void setHeaderOrders() {
    orderGrid.setWidget(0, 0, new HTML("&nbsp;"));
    orderGrid.setWidget(0, 1, new HTML(i18n.orderDate()));
    orderGrid.setWidget(0, 2, new HTML(i18n.orderVolume()));
    orderGrid.setWidget(0, 3, new HTML(i18n.orderValue()));
  }

  public void setHeaderProductOrders() {
    productOrderGrid.setWidget(0, 0, new HTML("&nbsp;"));
    productOrderGrid.setWidget(0, 1, new HTML(i18n.name()));
    productOrderGrid.setWidget(0, 2, new HTML(i18n.orderVolume()));
    productOrderGrid.setWidget(0, 3, new HTML(i18n.orderValue()));
  }
  
  public void setProductName(int row, String name) {
    ensureProductOrderRowCount(row);
    productOrderGrid.setWidget(row+1, 1, new HTML(name));
  }

  public void setProductOrderPrice(int row, String price){
    ensureProductOrderRowCount(row);
    productOrderGrid.setWidget(row+1, 3, new HTML(price));
  }
  
  public void setProductOrderVolume(int row, int volume){
    ensureProductOrderRowCount(row);
    productOrderGrid.setWidget(row+1, 2, new HTML("" + volume));
  }

  private void ensureProductOrderRowCount(int row) {
    if (row+1 >= productOrderGrid.getRowCount()) {
      productOrderGrid.resizeRows(row+2);
    }
  }
}
