package agilexs.catalogxsadmin.presentation.client;

import java.util.Date;

import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
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

    @UiField Image deleteButton;
    @UiField Image editButton;
    @UiField InlineHTML name;
    @UiField InlineHTML startDate;
    @UiField InlineHTML endDate;
    @UiField InlineHTML price;
    //@UiField InlineHTML productPrice;
    @UiField InlineHTML volume;
    @UiField SimplePanel editPanel;

    private final PromotionView parent;
    private Long id;

    public PromoView(PromotionView parent) {
      this.parent = parent;
      initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("editButton")
    void editClickHandler(ClickEvent event) {
      editPanel.setWidget(parent.editView);
      parent.editView.setVisible(true);
      parent.editView.setPromoView(this);
    }

    @UiHandler("deleteButton")
    void deleteClickHandler(ClickEvent event) {
      if (Window.confirm(i18n.deletePromotionQuestion())) {
        parent.deleteHandler.onDelete(this);
      }
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
      this.name.setHTML(name);
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

    @UiField Button saveButton;
    @UiField Anchor cancelButton;
    @UiField SimplePanel productFilterWrapper;
    @UiField DateBox startDate;
    @UiField InlineHTML endDate;
    @UiField TextBox price;
    @UiField TextBox volume;

    private final SuggestBox productFilter;

    private PromoView promoView;  

    public PromoEditView(SuggestOracle oracle) {
      initWidget(uiEditBinder.createAndBindUi(this));
      startDate.setFormat(new DefaultFormat(DateTimeFormat.getMediumDateFormat()));
      productFilter = new SuggestBox(oracle);
      productFilterWrapper.setWidget(productFilter);
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

//    public void setSuggestions(List<String> suggestions) {
//      oracle.clear();
//      oracle.addAll(suggestions);
////      this.name.setText(name);
//    }

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
    
    public void setName(String name) {
      productFilter.setValue(name);
    }
  }

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final DeckPanel mainPanel = new DeckPanel();
  private final DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
  private final FlowPanel top = new FlowPanel();
  private final FlowPanel promotions = new FlowPanel();
  private final Button addButton = new Button(i18n.add());
  private final Label nrOfActivePromotions = new Label();
  private final SimplePanel newPromotion = new SimplePanel();
  private final PromoEditView editView;

  private DeleteHandler<PromoView> deleteHandler;

  public PromotionView(SuggestOracle oracle) {
    editView = new PromoEditView(oracle);
    initWidget(mainPanel);
    mainPanel.add(new Label(i18n.loading()));
    mainPanel.add(panel);
    mainPanel.getElement().getStyle().setPaddingLeft(20, Unit.PX);
    mainPanel.getElement().getStyle().setPaddingRight(20, Unit.PX);
    top.add(nrOfActivePromotions);
    top.add(addButton);
    panel.addNorth(top, 40);
    final ScrollPanel slp = new ScrollPanel();

    panel.add(slp);
    final VerticalPanel fp = new VerticalPanel();

    slp.add(fp);
    fp.add(newPromotion);
    fp.add(promotions);
  }

  @Override
  public Widget asWidget() {
//  return this;
    return new HTML(i18n.todo());
  }

  public HasClickHandlers addClickHandlers() {
    return addButton;
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

  public PromoView createPromoView() {
    return new PromoView(this);
  }

  public void remove(PromoView promoView) {
    promotions.remove(promoView);
  }

  public void setEditNewPromo() {
    newPromotion.setWidget(editView);
    editView.setVisible(true);
  }

  public void setActivePromotions(int number) {
    nrOfActivePromotions.setText(i18n.h3(i18n.nrOfPromotions(number)));
  }
  
  public void setDeleteHandler(DeleteHandler<PromoView> deleteHandler) {
    this.deleteHandler = deleteHandler;
  }

  public void showLoading(boolean showLoading) {
    mainPanel.showWidget(showLoading ? 0 : 1);
  }
}
