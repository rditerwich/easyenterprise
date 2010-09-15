package agilexs.catalogxs.presentation.client;


import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  @Override
  public void onModuleLoad() {
    TabPanel tp = new TabPanel();
    RootPanel.get().add(tp);

    ProductGroupPresenter pgp = new ProductGroupPresenter();
    ProductPresenter pp = new ProductPresenter();
    
    tp.add(pgp.getView().getViewWidget(), "Product Groups");
    tp.add(pp.getView().getViewWidget(), "Products");
  }
}
