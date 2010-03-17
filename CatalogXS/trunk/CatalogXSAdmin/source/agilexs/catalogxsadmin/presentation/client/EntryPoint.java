package agilexs.catalogxsadmin.presentation.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  private final CatalogPresenter cp = new CatalogPresenter();
  private final NavigationPresenter np = new NavigationPresenter();

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

    tp.add(cp.getView().asWidget(), "Catalog");
    tp.add(np.getView().asWidget(), "Navigation");
    tp.add(new Label("To be implemented"), "Promotions");
    tp.add(new Label("To be implemented"), "Settings");
    tp.addSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        switch (event.getSelectedItem().intValue()) {
        case 0:
          break;
        case 1:
          np.show();
          break;
        case 2:
        case 3:
        default:
        }
      }
    });
  }
}
