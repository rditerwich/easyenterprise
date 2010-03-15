package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
  final FlowPanel allPropertiesPanel = new FlowPanel();
  final HTML name = new HTML();
  final CheckBox containsProducts = new CheckBox();
  final SimplePanel propertiesPanel = new SimplePanel();
  final FlowPanel parentPropertiesPanel = new FlowPanel();

  public ProductGroupView() {
    initWidget(detailPanel);
    FlowPanel fp = new FlowPanel();
    HorizontalPanel hp = new HorizontalPanel();
    hp.add(new Label("Product group contains products: "));
    hp.add(containsProducts);
    fp.add(name);
    fp.add(hp);
    //top
    detailPanel.addNorth(fp, 80);
    //top
    final ScrollPanel sp = new ScrollPanel(allPropertiesPanel);
    allPropertiesPanel.add(propertiesPanel);
    //allPropertiesPanel.add(new HTML("<h3>Relations</h3>"));
    //allPropertiesPanel..add(relations);
    allPropertiesPanel.add(new HTML("<h3>Inherited Properties</h3>"));
    allPropertiesPanel.add(parentPropertiesPanel);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
    detailPanel.add(sp);
  }

  public void setPropertiesPanel(Widget w) {
    propertiesPanel.setWidget(w);
  }

  public HasValue<Boolean> containsProducts() {
    return containsProducts;
  }

  public HasClickHandlers containsProductsClickHandler() {
    return containsProducts;
  }

  public FlowPanel getParentPropertiesPanel() {
    return parentPropertiesPanel;
  }

  public void setName(String name) {
    this.name.setHTML("<h2>" + name + "</h2>");
  }

  @Override
  public Widget asWidget() {
    return this;
  }
}
