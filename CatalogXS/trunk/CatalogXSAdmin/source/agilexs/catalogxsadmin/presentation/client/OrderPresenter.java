package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Order;
import agilexs.catalogxsadmin.presentation.client.shop.ProductOrder;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrderPresenter implements Presenter<OrderView> {

  private OrderView view = new OrderView();
  private Integer fromIndex = 0;
  private Integer pageSize = 1000;
  private Order filter;
  private String currentLanguage = null;
  private List<Order> orders;

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
            if (result != null) {
              view.setHeaderProductOrders();
              int r = 0;
              for (ProductOrder po : result.getProductOrders()) {
                view.setProductName(r, Util.productToString(po.getProduct(), currentLanguage));
                view.setProductOrderVolume(r, po.getVolume());
                view.setProductOrderPrice(r, Util.formatMoney(po.getPrice()));
                r++;
              }
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
    view.setVolume(row, volume);
    view.setPrice(row, Util.formatMoney(price));
  }

}
