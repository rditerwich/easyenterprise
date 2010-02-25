package agilexs.catalogxs.presentation.client;

import agilexs.catalogxs.presentation.client.page.Presenter;

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
