package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
  private final Button saveButton = new Button(i18n.saveChanges()); 
  private final CheckBox containsProducts = new CheckBox();
  private final DeckPanel deck = new DeckPanel();
  private final FlowPanel labels = new FlowPanel();

  private Label lastSelected;

  public ProductGroupView() {
    initWidget(detailPanel);
    final FlowPanel fp = new FlowPanel();
    final HorizontalPanel hpname = new HorizontalPanel();

    saveButton.getElement().getStyle().setMarginLeft(200, Unit.PX);
    hpname.add(saveButton);
    fp.add(hpname);
    final HorizontalPanel hp = new HorizontalPanel();
    hp.add(new Label(i18n.containsProducts()));
    hp.add(containsProducts);
    fp.add(hp);
    //top
    detailPanel.addNorth(fp, 80);
    //top
    final HorizontalPanel properties = new HorizontalPanel();
    final ScrollPanel sp = new ScrollPanel(properties);

    detailPanel.add(sp);
    properties.add(labels);
    labels.addStyleName("propertiesGroupName");
    properties.add(deck);
    deck.getElement().getStyle().setPadding(10, Unit.PX);
    deck.setStyleName("properties");
    sp.getElement().getStyle().setPadding(10, Unit.PX);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public HasValue<Boolean> containsProducts() {
    return containsProducts;
  }

  public HasClickHandlers containsProductsClickHandlers() {
    return containsProducts;
  }

  public void clear() {
    labels.clear();
    deck.clear();
  }

  public void add(String name, Widget widget) {
    final Label lbl = new Label(name); 

    deck.add(widget);
    final int i = deck.getWidgetCount() - 1;

    labels.add(lbl);
    lbl.addStyleName("propertyGroupName");
    lbl.addClickHandler(new ClickHandler(){

      @Override public void onClick(ClickEvent event) {
        lastSelected.removeStyleName("propertyGroupNameSelected");

        lbl.addStyleName("propertyGroupNameSelected");
        lastSelected = lbl;
        deck.showWidget(i);
      }});
    if (i == 0) {
      lastSelected = lbl;
      lbl.addStyleName("propertyGroupNameSelected");
      deck.showWidget(i);
    }
  }

  public HasClickHandlers saveButtonClickHandlers() {
    return saveButton;
  }
}
