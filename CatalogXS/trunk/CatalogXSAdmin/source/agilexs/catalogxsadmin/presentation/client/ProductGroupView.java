package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
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

  private final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
  private final FlowPanel allPropertiesPanel = new FlowPanel();
  private final HTML name = new HTML();
  final Button saveButton = new Button("Save changes"); 
  private final CheckBox containsProducts = new CheckBox();
  private final SimplePanel propertiesPanel = new SimplePanel();
  private final FlowPanel parentPropertiesPanel = new FlowPanel();
  private final SimplePanel parentsPanel = new SimplePanel();

  public ProductGroupView() {
    initWidget(detailPanel);
    final FlowPanel fp = new FlowPanel();
    final HorizontalPanel hpname = new HorizontalPanel();

    saveButton.getElement().getStyle().setMarginLeft(200, Unit.PX);
    hpname.add(name);
    hpname.add(saveButton);
    fp.add(hpname);
    final HorizontalPanel hp = new HorizontalPanel();
    hp.add(new Label("Product group contains products: "));
    hp.add(containsProducts);
    fp.add(hp);
    //top
    detailPanel.addNorth(fp, 80);
    //top
    final ScrollPanel sp = new ScrollPanel(allPropertiesPanel);
    allPropertiesPanel.add(parentsPanel);
    allPropertiesPanel.add(propertiesPanel);
    //allPropertiesPanel.add(new HTML("<h3>Relations</h3>"));
    //allPropertiesPanel..add(relations);
    allPropertiesPanel.add(new HTML("<h3>Inherited Properties</h3>"));
    allPropertiesPanel.add(parentPropertiesPanel);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
    detailPanel.add(sp);
  }

  public void setPropertiesPanel(View w) {
    propertiesPanel.setWidget(w.asWidget());
  }

  public HasValue<Boolean> containsProducts() {
    return containsProducts;
  }

  public HasClickHandlers containsProductsClickHandler() {
    return containsProducts;
  }

  public HasClickHandlers saveButtonClickHandlers() {
    return saveButton;
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

  public void setParentsPanel(View view) {
    parentsPanel.setWidget(view.asWidget());
  }
}
