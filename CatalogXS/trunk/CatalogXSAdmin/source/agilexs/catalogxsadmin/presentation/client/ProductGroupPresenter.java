package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;

/**
 * Presenter for the ProductGroup page
 */
public class ProductGroupPresenter implements Presenter<ProductGroupView> {

  private final ProductGroupView view = new ProductGroupView();

  public ProductGroupPresenter() {
  }

  @Override
  public ProductGroupView getView() {
    return view;
  }
  
  
}
