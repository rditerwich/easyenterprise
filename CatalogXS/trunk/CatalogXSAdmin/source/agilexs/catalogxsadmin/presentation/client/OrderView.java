package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class OrderView extends Composite implements View {
  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private FlowPanel panel = new FlowPanel();

  public OrderView() {
    initWidget(panel);
  }

  @Override
  public Widget asWidget() {
    return new HTML(i18n.todo());
  }
}
