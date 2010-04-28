package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class OrderView extends Composite implements View {
  public class ProductOrderView {
    
  }

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final FlowPanel panel = new FlowPanel();
  private final Grid orderGrid = new Grid(1, 5);
  
  public OrderView() {
    initWidget(panel);
    panel.add(new HTML(i18n.h3(i18n.orders())));
    panel.add(orderGrid);
  }

  @Override
  public Widget asWidget() {
    return new HTML(i18n.todo());
  }

  public void clear() {
    orderGrid.clear();
  }
  
//  public void addOrder(Date date, String name, Integer volume, Double price) {
//    orderGrid.setWidget(row, 0, new Label(Util.formatDate(date)));
//    orderGrid.setWidget(row, 0, new Label(name));
//    orderGrid.setWidget(row, 0, new Label("" + volume));
//    orderGrid.setWidget(row, 0, new Label(Util.formatMoney(price)));
//    
//  }
}
