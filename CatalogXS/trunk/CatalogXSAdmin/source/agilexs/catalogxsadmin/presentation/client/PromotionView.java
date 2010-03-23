package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PromotionView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
  private final FlowPanel top = new FlowPanel();
  private final FlowPanel promotions = new FlowPanel();
  private final Button addButton = new Button(i18n.add());
  private final Label nrOfActivePromotions = new Label(); 

  public PromotionView() {
    initWidget(panel);
    top.add(nrOfActivePromotions);
    top.add(addButton);
    panel.addNorth(top, 40);
    panel.add(promotions);
  }

  @Override
  public Widget asWidget() {
//  return this;
    return new HTML(i18n.todo());
  }
  
  public void setActivePromotions(int number) {
    nrOfActivePromotions.setText(i18n.h3(i18n.nrOfPromotions(number)));
  }
}
