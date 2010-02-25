package agilexs.catalogxs.presentation.client;

import agilexs.catalogxs.presentation.client.page.View;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductView extends Composite implements View {

  private FlowPanel panel = new FlowPanel();
  
  public ProductView() {
    initWidget(panel);
  }

  @Override
  public Widget getViewWidget() {
    return this;
  }
}
