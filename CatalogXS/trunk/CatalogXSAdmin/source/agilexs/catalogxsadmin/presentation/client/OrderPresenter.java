package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Order;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrderPresenter implements Presenter<OrderView> {

  private OrderView view = new OrderView();
  private Integer fromIndex;
  private Integer pageSize;
  private Promotion filter;

  @Override
  public OrderView getView() {
    return view;
  }

  public void show() {
    ShopServiceAsync.findActualOrders(fromIndex, pageSize, filter, new AsyncCallback<List<Order>>(){
      @Override public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override public void onSuccess(List<Order> result) {
        for (Order order : result) {
          
        }
      }});
  }
}
