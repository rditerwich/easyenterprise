package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.OrderView.ProductOrderView;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Order;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrderPresenter implements Presenter<OrderView> {

  private OrderView view = new OrderView();
  private Integer fromIndex;
  private Integer pageSize;
  private Promotion filter;
  private String currentLanguage;

  @Override
  public OrderView getView() {
    return view;
  }

  public void show() {
    view.clear();
    ShopServiceAsync.findActualOrders(fromIndex, pageSize, filter, new AsyncCallback<List<Order>>(){
      @Override public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override public void onSuccess(List<Order> result) {
        for (Order order : result) {
          
        }
      }});
  }
  
  private void showProductOrder(Order order, ProductOrderView sov) {
//    sov.setId(order.getId());
//    sov.setDate(DateTimeFormat.getMediumDateFormat().format(order.getOrderDate()));
//    sov.setProducts(order.getProductOrders().size());
    //sov.setPrice(Util.formatMoney(order.getPrice()));
    //sov.setVolume(order.getVolume() + "");
  }

}
