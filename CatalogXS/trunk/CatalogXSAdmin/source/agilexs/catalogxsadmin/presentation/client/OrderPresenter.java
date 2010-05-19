package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.OrderView.SHOW;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.party.Party;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Order;
import agilexs.catalogxsadmin.presentation.client.shop.OrderStatus;
import agilexs.catalogxsadmin.presentation.client.shop.ProductOrder;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrderPresenter implements Presenter<OrderView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private OrderView view = new OrderView();
  private Integer fromIndex = 0;
  private Integer pageSize = 1000;
  private Order filter;
  private String currentLanguage = null;
  private List<Order> orders;
  private Order currentOrder;

  public OrderPresenter() {
    view.getChangeStatusHandler().addChangeHandler(new ChangeHandler(){
      @Override public void onChange(ChangeEvent event) {
        final OrderStatus nos = view.getSelectedOrderStatus();
        if (currentOrder != null && !currentOrder.getStatus().equals(nos)) {
          final Order no = new Order();
          final Order oo = new Order();

          oo.setId(currentOrder.getId());
          no.setId(currentOrder.getId());
          oo.setStatus(currentOrder.getStatus());
          no.setStatus(nos);
          ShopServiceAsync.updateOrder(oo, no, new AsyncCallback<Order>(){
            @Override public void onFailure(Throwable caught) {
            }

            @Override public void onSuccess(Order result) {
              if (result != null) {
                currentOrder = result;
              }
              StatusMessage.get().show(i18n.orderStatusUpdated());
            }});
        }
      }});
    view.backClickHandlers().addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        view.showPage(SHOW.ORDERS);
      }});
  }

  @Override
  public OrderView getView() {
    return view;
  }

  public void show() {
    view.clear();
    view.clearProductOrders();
    ShopServiceAsync.findActualOrders(fromIndex, pageSize, filter, new AsyncCallback<List<Order>>(){

      @Override public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override public void onSuccess(List<Order> result) {
        orders = result;
        int row = 0;
        view.setHeaderOrders();
        for (Order order : result) {
          showProductOrder(row, order);
          row++;
        }
      }});
  }

  private void showProductOrder(final int row, Order order) {
    view.setDetailHandler(row).addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        view.clearProductOrders();
        ShopServiceAsync.findOrderById(orders.get(row).getId(), new AsyncCallback<Order>() {
          @Override public void onFailure(Throwable caught) {
          }

          @Override public void onSuccess(Order result) {
            currentOrder = result;
            if (result != null) {
              view.setHeaderProductOrders();
              view.setDetailOrderDate(result.getOrderDate());
              if (result.getUser() != null && result.getUser().getParty() != null) {
                final Party p = result.getUser().getParty();

                view.setDetailParty(p.getName());
              } else {
                view.setDetailParty("");
              }
              view.setDetailOrderStatus(result.getStatus());
              int r = 0;
              for (ProductOrder po : result.getProductOrders()) {
                view.setProductName(r, Util.productToString(po.getProduct(), currentLanguage));
                view.setProductOrderVolume(r, po.getVolume());
                view.setProductOrderPrice(r, Util.formatMoney(po.getPrice()));
                r++;
              }
              view.showPage(SHOW.ORDER);
            }
          }});
      }
    });
    view.setDate(row, order.getOrderDate());
    double price = 0;
    int volume = 0;
    for (ProductOrder po : order.getProductOrders()) {
      volume += po.getVolume() != null ? po.getVolume().intValue() : 0;
      price += po.getPrice() != null ? po.getPrice().doubleValue() : 0.0;
    }
    if (order.getUser() != null && order.getUser().getParty() != null) {
      view.setCustomer(row, order.getUser().getParty().getName());
    }
    view.setVolume(row, volume);
    view.setPrice(row, Util.formatMoney(price));
    view.setOrderStatus(row, order.getStatus());
    view.showPage(SHOW.ORDERS);
  }
}
