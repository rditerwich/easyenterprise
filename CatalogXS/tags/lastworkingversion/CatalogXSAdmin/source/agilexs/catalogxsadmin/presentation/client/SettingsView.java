package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SettingsView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final DeckPanel mainPanel = new DeckPanel();
  private FlowPanel panel = new FlowPanel();
  private Button save = new Button(i18n.save());
  
  public SettingsView(ItemParentsView languagesPanel) {
    initWidget(mainPanel);
    mainPanel.add(new Label(i18n.loading()));
    mainPanel.add(panel);
    panel.add(new Label(i18n.explainLanguages()));
    panel.add(languagesPanel);
    panel.add(save);
  }

  @Override
  public Widget asWidget() {
    return this;//new HTML(i18n.todo());
  }

  public void showLoading(boolean showLoading) {
    mainPanel.showWidget(showLoading ? 0 : 1);
  }

  public HasClickHandlers buttonSaveHasClickHandlers() {
    return save;
  }
}
