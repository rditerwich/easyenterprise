package agilexs.catalogxsadmin.presentation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  @Override
  public void onModuleLoad() {
    ResourceBundle rb = GWT.create(ResourceBundle.class);

    Util.getShop(null); //TODO init of catalogView should be made generic
    final DockLayoutPanel dlp = new DockLayoutPanel(Unit.PX);
    final FlowPanel topPanel = new FlowPanel();
    final TabLayoutPanel tp = new TabLayoutPanel(40, Unit.PX);

    RootLayoutPanel.get().add(dlp);
    topPanel.getElement().getStyle().setHeight(100, Unit.PC);
    topPanel.add(new Image(rb.logo()));
    dlp.addNorth(topPanel, 70);
    dlp.add(tp);

    final ProductGroupPresenter pgp = new ProductGroupPresenter();
    final ProductPresenter pp = new ProductPresenter();

    tp.add(pgp.getView().getViewWidget(), "Product Groups");
    tp.add(pp.getView().getViewWidget(), "Products");
  }
}
