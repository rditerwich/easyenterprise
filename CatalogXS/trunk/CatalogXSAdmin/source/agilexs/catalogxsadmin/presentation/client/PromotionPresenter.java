package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.Presenter;

public class PromotionPresenter implements Presenter<PromotionView> {

  private final PromotionView view = new PromotionView();

  public PromotionPresenter() {
    
  }

  @Override
  public PromotionView getView() {
    return view;
  }

  public void show() {
  }
}
