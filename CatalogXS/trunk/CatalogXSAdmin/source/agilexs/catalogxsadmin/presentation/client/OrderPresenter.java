package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;

public class OrderPresenter implements Presenter<OrderView> {

  private OrderView view = new OrderView();

  @Override
  public OrderView getView() {
    return view;
  }

  public void show() {
  }
}
