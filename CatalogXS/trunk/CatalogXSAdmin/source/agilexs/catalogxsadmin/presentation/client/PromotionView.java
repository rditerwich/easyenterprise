package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class PromotionView extends Composite implements View {

  @UiTemplate("PromoView.ui.xml")
  interface PromoViewUiBinder extends UiBinder<Widget, PromoView> {}
  private static PromoViewUiBinder uiBinder = GWT.create(PromoViewUiBinder.class);

  public static class PromoView extends Composite {

    //@UiField FlowPanel panel;
    @UiField Image deleteButton;
    @UiField Image editButton;
    @UiField InlineHTML name;
    @UiField InlineHTML startDate;
    @UiField InlineHTML endDate;
    @UiField InlineHTML price;
    //@UiField InlineHTML productPrice;
    @UiField InlineHTML volume;

    public PromoView() {
      initWidget(uiBinder.createAndBindUi(this));
    }
    
    public void setName(String name) {
      this.name.setText(name);
    }

    public void setStartDate(String startDate) {
      this.startDate.setText(startDate);
    }

    public void setEndDate(String endDate) {
      this.endDate.setText(endDate);
    }

    public void setPrice(String price) {
      this.price.setText(price);
    }

//    public void setProductPrice(String productPrice) {
//      this.productPrice.setText(productPrice);
//    }

    public void setVolume(String volume) {
      this.volume.setText(volume);
    }
  }

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
    final FlowPanel fp = new FlowPanel();
    //fp.add(new HTML(i18n.h3(i18n.promotions())));
    final ScrollPanel slp = new ScrollPanel();
    promotions.getElement().getStyle().setPaddingLeft(20, Unit.PX);
    promotions.getElement().getStyle().setPaddingRight(20, Unit.PX);
    slp.add(promotions);
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

  public void clear() {
    promotions.clear();
  }

  public void add(PromoView promoView) {
    promotions.add(promoView);
  }
}
