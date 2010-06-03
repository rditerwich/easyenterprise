package agilexs.catalogxsadmin.presentation.client;

import java.util.Date;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.shop.OrderStatus;
import agilexs.catalogxsadmin.presentation.client.widget.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class OrderView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private final static ResourceBundle rb = GWT.create(ResourceBundle.class);

  public enum SHOW {
    ORDERS, ORDER
  }

  private final DeckPanel panel = new DeckPanel();
  private final Anchor back = new Anchor();
  private final Table orderGrid = new Table(1, 6);
  private final Table productOrderGrid = new Table(1, 3);
  private final ListBox orderStatus = new ListBox();
  private final FlowPanel orderDetail = new FlowPanel();
  private final Grid customerDetails = new Grid(3, 2);
  private final ListBox statusFilter = new ListBox();
  private final Button refresh = new Button(i18n.refresh());

  public OrderView() {
    initWidget(panel);
    panel.getElement().getStyle().setPadding(10, Unit.PX);
    final FlowPanel ordersFP = new FlowPanel();

    ordersFP.add(refresh);
    refresh.getElement().getStyle().setMarginRight(20, Unit.PX);
    ordersFP.add(new InlineLabel(i18n.filter()));
    ordersFP.add(statusFilter);
    statusFilter.addItem("<no filter>", "");
    for (OrderStatus os : OrderStatus.values()) {
      statusFilter.addItem(os.toString());
    }
    final ScrollPanel ordersSP = new ScrollPanel();

    ordersSP.getElement().getStyle().setMarginTop(15, Unit.PX);
    ordersFP.add(ordersSP);
    panel.add(ordersFP);
    ordersSP.add(orderGrid);
    final ScrollPanel productOrdersSP = new ScrollPanel();

    panel.add(orderDetail);
    orderDetail.add(back);
    back.setHTML(i18n.backToOrdersOverview());
    orderDetail.add(new HTML(i18n.h2("Order Details")));

    orderDetail.add(customerDetails);
    customerDetails.setText(0, 0, i18n.orderDateDetail());
    customerDetails.setText(1, 0, i18n.orderCustomerDetail());
    customerDetails.setText(2, 0, i18n.orderStatusDetail());
    customerDetails.setWidget(2, 1, orderStatus);

    orderDetail.add(new HTML(i18n.h3(i18n.products())));
    orderDetail.add(productOrdersSP);
    productOrdersSP.add(productOrderGrid);
  }

  public HasChangeHandlers getChangeStatusHandler() {
    return orderStatus;
  }

  public HasChangeHandlers getStatusFilterHandler() {
    return statusFilter;
  }

  public HasClickHandlers getRefreshClickHandler() {
    return refresh;
  }

  public OrderStatus getSelectedOrderStatus() {
    final int i = orderStatus.getSelectedIndex();

    if (i > 0) {
      final String s = orderStatus.getValue(i);
      for (OrderStatus os : OrderStatus.values()) {
        if (os.toString().equals(s)) {
          return os;
        }
      }
    }
    return null;
  }

  public OrderStatus getSelectedOrderStatusFilter() {
    final int i = statusFilter.getSelectedIndex();

    if (i > 0) {
      final String s = statusFilter.getValue(i);
      for (OrderStatus os : OrderStatus.values()) {
        if (os.toString().equals(s)) {
          return os;
        }
      }
    }
    return null;
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public HasClickHandlers backClickHandlers() {
    return back;
  }

  public void clear() {
    orderGrid.clear();
    orderGrid.setVisible(false);
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

  public void setDetailOrderDate(Date orderDate) {
    customerDetails.setText(0, 1, DateTimeFormat.getMediumDateFormat().format(orderDate));
  }

  public void setDetailParty(String party) {
    customerDetails.setText(1, 1, party);
  }

  public void setDetailOrderStatus(OrderStatus status) {
    if (orderStatus.getItemCount() == 0) {
      for (OrderStatus os : OrderStatus.values()) {
        orderStatus.addItem(os.toString());
      }
    }
    int i = 0;
    for (OrderStatus os : OrderStatus.values()) {
      if (os.equals(status)) {
        orderStatus.setSelectedIndex(i);
        break;
      }
      i++;
    }
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
    orderGrid.setVisible(true);
    orderGrid.setHeaderHTML(0, 1, i18n.orderDate());
    orderGrid.setHeaderHTML(0, 2, i18n.orderCustomer());
    orderGrid.setHeaderHTML(0, 3, i18n.orderVolume());
    orderGrid.setHeaderHTML(0, 4, i18n.orderPrice());
    orderGrid.setHeaderHTML(0, 5, i18n.orderStatus());
  }

  public void setHeaderProductOrders() {
    productOrderGrid.setVisible(true);
    productOrderGrid.setHeaderHTML(0, 0, i18n.name());
    productOrderGrid.setHeaderHTML(0, 1, i18n.orderVolume());
    productOrderGrid.setHeaderHTML(0, 2, i18n.orderPrice());
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

  public void showPage(SHOW show) {
    int i = 0;
    switch (show) {
    case ORDERS: i = 0; break;
    case ORDER: i = 1; break;
    }
    panel.showWidget(i);
  }
  private void ensureProductOrderRowCount(int row) {
    if (row >= productOrderGrid.getRowCount()) {
      productOrderGrid.resizeRows(row+1);
    }
  }
}
