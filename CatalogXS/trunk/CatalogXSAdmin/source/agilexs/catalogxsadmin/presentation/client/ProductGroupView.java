package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
  private final Button saveButton = new Button(i18n.saveChanges()); 
  private final CheckBox containsProducts = new CheckBox();
  private final FlowPanel deck = new FlowPanel();
  private final FlowPanel propertyValuesPanel = new FlowPanel();

  public ProductGroupView() {
    initWidget(detailPanel);
    final FlowPanel fp = new FlowPanel();
    fp.getElement().getStyle().setPadding(8, Unit.PX);
    final HorizontalPanel hpname = new HorizontalPanel();

    saveButton.getElement().getStyle().setMarginLeft(200, Unit.PX);
    hpname.add(saveButton);
    fp.add(hpname);
    final HorizontalPanel hp = new HorizontalPanel();
    hp.add(new Label(i18n.containsProducts()));
    hp.add(containsProducts);
    fp.add(hp);
    //top
    detailPanel.addNorth(fp, 60);
    //top
    final HorizontalPanel properties = new HorizontalPanel();
    final ScrollPanel sp = new ScrollPanel(properties);

    detailPanel.add(sp);
    properties.add(deck);
    sp.getElement().getStyle().setPadding(8, Unit.PX);
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
//    labels.clear();
    deck.clear();
    propertyValuesPanel.clear();
    propertyValuesPanel.removeFromParent();
  }

  public void addPropertyValues(String name, Widget widget) {
    if (!propertyValuesPanel.isAttached()) {
      deck.add(propertyValuesPanel);
    }
    add(propertyValuesPanel, name, widget);
  }

  public void add(String name, Widget widget) {
    final FlowPanel fp = new FlowPanel();

    add(fp, name, widget);
    deck.add(fp);
  }

  private void add(FlowPanel parent, String name, Widget widget) {
    final HTML lbl = new HTML(i18n.h3(name)); 

    parent.getElement().getStyle().setPadding(8, Unit.PX);
    parent.getElement().getStyle().setMarginBottom(8, Unit.PX);
    parent.setStyleName("properties");
    parent.add(lbl);
    parent.add(widget);
  }

  /**
   * Diplays button for status when saving in progress. 
   */
  public void setSaving(boolean saving) {
    if (saving) {
      saveButton.setText(i18n.saving());
      saveButton.setEnabled(false);
    } else {
      saveButton.setText(i18n.saveChanges());
      saveButton.setEnabled(true);
    }
  }

  public HasClickHandlers saveButtonClickHandlers() {
    return saveButton;
  }
}
