package agilexs.catalogxsadmin.presentation.client;

import java.util.Date;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

public class PromotionView extends Composite implements View {

  @UiTemplate("PromoView.ui.xml")
  interface PromoViewUiBinder extends UiBinder<Widget, PromoView> {}
  private static PromoViewUiBinder uiBinder = GWT.create(PromoViewUiBinder.class);

  @UiTemplate("PromoEditView.ui.xml")
  interface PromoEditViewUiBinder extends UiBinder<Widget, PromoEditView> {}
  private static PromoEditViewUiBinder uiEditBinder = GWT.create(PromoEditViewUiBinder.class);

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
    @UiField SimplePanel editPanel;

    private final PromoEditView editView;
    private Long id;
    
    @UiHandler("editButton")
    void editClickHandler(ClickEvent event) {
      editPanel.setWidget(editView);
      editView.setVisible(true);
      editView.setPromoView(this);
    }

    public PromoView(PromoEditView editView) {
      this.editView = editView;
      initWidget(uiBinder.createAndBindUi(this));
    }

    public HasClickHandlers deleteClickHandlers() {
      return deleteButton;
    }

    public HasClickHandlers editClickHandlers() {
      return editButton;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
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

  /**
   * Class to display the edit box
   */
  public static class PromoEditView extends Composite {

    //@UiField FlowPanel panel;
    @UiField Button saveButton;
    @UiField Anchor cancelButton;
    @UiField SuggestBox productFilter;
    @UiField DateBox startDate;
    @UiField InlineHTML endDate;
    @UiField TextBox price;
    @UiField TextBox volume;

    final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    private PromoView promoView;  

    public PromoEditView() {
      initWidget(uiEditBinder.createAndBindUi(this));
      startDate.setFormat(new DefaultFormat(DateTimeFormat.getMediumDateFormat()));
    }

    @UiHandler("cancelButton")
    void cancelButtonClick(ClickEvent e) {
      setVisible(false);
    }

    public HasClickHandlers saveClickHandlers() {
      return saveButton;
    }

    public HasClickHandlers cancelClickHandlers() {
      return cancelButton;
    }

    public HasSelectionHandlers<Suggestion> productFilterSelectionHandlers() {
      return productFilter;
    }

    public HasValueChangeHandlers<String> productFilterValueChangeHandlers() {
      return productFilter;
    }

    public void setSuggestions(List<String> suggestions) {
      oracle.clear();
      oracle.addAll(suggestions);
//      this.name.setText(name);
    }

    public HasValue<Date> getStartDate() {
      return startDate;
    }

    public HasText getEndDate() {
      return endDate;
    }

    public TextBoxBase getPrice() {
      return price;
    }

    public TextBoxBase getVolumeDiscount() {
      return volume;
    }

    public PromoView getPromoView() {
      return promoView;
    }

    public void setPromoView(PromoView promoView) {
      this.promoView = promoView;
    }
  }

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
  private final FlowPanel top = new FlowPanel();
  private final FlowPanel promotions = new FlowPanel();
  private final Button addButton = new Button(i18n.add());
  private final Label nrOfActivePromotions = new Label();
  private final PromoEditView editView = new PromoEditView();

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

  public void add(PromoView promoView) {
    promotions.add(promoView);
  }

  public void clear() {
    promotions.clear();
  }

  public PromoEditView getEditView() {
    return editView;
  }

  public PromoView getNewPromoView() {
    return new PromoView(editView);
  }

  public void setActivePromotions(int number) {
    nrOfActivePromotions.setText(i18n.h3(i18n.nrOfPromotions(number)));
  }
}
