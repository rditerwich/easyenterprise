package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;

public class ProductPresenter implements Presenter<ProductView> {

  private final ProductView view = new ProductView();
  
  public ProductPresenter() {
  }
  
  @Override
  public ProductView getView() {
    return view;
  }
}