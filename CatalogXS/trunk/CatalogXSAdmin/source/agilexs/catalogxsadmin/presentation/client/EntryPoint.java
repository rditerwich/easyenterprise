package agilexs.catalogxsadmin.presentation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  @Override
  public void onModuleLoad() {
    ResourceBundle rb = GWT.create(ResourceBundle.class);

    final DockLayoutPanel dlp = new DockLayoutPanel(Unit.PX);
    final FlowPanel topPanel = new FlowPanel();
    final TabLayoutPanel tp = new TabLayoutPanel(30, Unit.PX);

    RootLayoutPanel.get().add(dlp);
    topPanel.getElement().getStyle().setHeight(100, Unit.PC);
    topPanel.add(new Image(rb.logo()));
    dlp.addNorth(topPanel, 70);
    dlp.add(tp);

    CatalogPresenter cp = new CatalogPresenter();

    tp.add(cp.getView().asWidget(), "Products");
    tp.add(new Label("To be implemented"), "Navigation");
    tp.add(new Label("To be implemented"), "Promotions");
    tp.add(new Label("To be implemented"), "Settings");
  }
}
