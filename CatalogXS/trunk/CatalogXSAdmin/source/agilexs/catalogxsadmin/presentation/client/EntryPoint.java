package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final CatalogPresenter cp = new CatalogPresenter();
  private final NavigationPresenter np = new NavigationPresenter();
  private final PromotionPresenter p = new PromotionPresenter();
  private final SettingsPresenter s = new SettingsPresenter();

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

    tp.add(cp.getView(), i18n.catalog());
    tp.add(np.getView(), i18n.navigation());
    tp.add(p.getView(), i18n.promotions());
    tp.add(s.getView().asWidget(), i18n.settings());
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
          p.show();
          break;
        case 3:
          s.show();
          break;
        default:
        }
      }
    });
  }
}
