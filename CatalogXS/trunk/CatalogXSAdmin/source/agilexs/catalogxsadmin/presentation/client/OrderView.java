package agilexs.catalogxsadmin.presentation.client;

import java.util.Date;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.shop.OrderStatus;
import agilexs.catalogxsadmin.presentation.client.widget.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrderView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private final static ResourceBundle rb = GWT.create(ResourceBundle.class);

  private final SplitLayoutPanel panel = new SplitLayoutPanel();
  private final Table orderGrid = new Table(1, 6);
  private final Table productOrderGrid = new Table(1, 3);

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
    return this;
  }

  public void clear() {
    orderGrid.clear();
  }

  public void clearProductOrders() {
    productOrderGrid.clear();
    productOrderGrid.setVisible(false);
  }

  public HasClickHandlers setDetailHandler(int row) {
    if (row >= orderGrid.getRowCount()) {
      orderGrid.resizeRows(row+1);
    }
    final Image i = new Image(rb.editImage());

    orderGrid.setWidget(row, 0, i);
    return i;
  }

  public void setDate(int row, Date orderDate) {
    orderGrid.setText(row, 1, DateTimeFormat.getMediumDateFormat().format(orderDate));
  }

  public void setCustomer(int row, String customer) {
    orderGrid.setText(row, 2, customer);
  }
  public void setVolume(int row, int volume) {
    orderGrid.setText(row, 3, "" + volume);
  }

  public void setPrice(int row, String price) {
    orderGrid.setText(row, 4, price);
  }

  public void setOrderStatus(int row, OrderStatus status) {
    orderGrid.setText(row, 5, status.toString());
  }

  public void setHeaderOrders() {
    //orderGrid.setHeaderHTML(0, 0, "&nbsp;");
    orderGrid.setHeaderHTML(0, 1, i18n.orderDate());
    orderGrid.setHeaderHTML(0, 2, i18n.orderCustomer());
    orderGrid.setHeaderHTML(0, 3, i18n.orderVolume());
    orderGrid.setHeaderHTML(0, 4, i18n.orderValue());
    orderGrid.setHeaderHTML(0, 5, i18n.orderStatus());
  }

  public void setHeaderProductOrders() {
    productOrderGrid.setVisible(true);
    productOrderGrid.setHeaderHTML(0, 0, i18n.name());
    productOrderGrid.setHeaderHTML(0, 1, i18n.orderVolume());
    productOrderGrid.setHeaderHTML(0, 2, i18n.orderValue());
  }

  public void setProductName(int row, String name) {
    ensureProductOrderRowCount(row);
    productOrderGrid.setText(row, 0, name);
  }

  public void setProductOrderPrice(int row, String price){
    ensureProductOrderRowCount(row);
    productOrderGrid.setText(row, 2, price);
  }

  public void setProductOrderVolume(int row, int volume){
    ensureProductOrderRowCount(row);
    productOrderGrid.setText(row, 1, "" + volume);
  }

  private void ensureProductOrderRowCount(int row) {
    if (row >= productOrderGrid.getRowCount()) {
      productOrderGrid.resizeRows(row+1);
    }
  }
}
