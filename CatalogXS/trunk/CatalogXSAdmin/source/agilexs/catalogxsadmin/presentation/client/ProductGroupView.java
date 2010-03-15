package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
  final FlowPanel allPropertiesPanel = new FlowPanel();
  final HTML name = new HTML();
  final SimplePanel propertiesPanel = new SimplePanel();
  final FlowPanel parentPropertiesPanel = new FlowPanel();

  public ProductGroupView() {
    initWidget(detailPanel);
    //top 
    detailPanel.addNorth(name, 60);
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
