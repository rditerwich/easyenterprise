package agilexs.catalogxs.presentation.client;

import agilexs.catalogxs.presentation.client.page.View;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  FlowPanel panel = new FlowPanel();
  
  public ProductGroupView() {
    initWidget(panel);
    
  }

  @Override
  public Widget getViewWidget() {
    return this;
  }
}
