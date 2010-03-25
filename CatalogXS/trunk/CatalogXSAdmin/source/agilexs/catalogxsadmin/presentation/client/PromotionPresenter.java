package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.PromotionView.PromoView;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.binding.HasValueBinding;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.shop.VolumeDiscountPromotion;
import agilexs.catalogxsadmin.presentation.client.shop.VolumeDiscountPromotionBinding;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PromotionPresenter implements Presenter<PromotionView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final PromotionView view = new PromotionView();
  private final VolumeDiscountPromotionBinding editPromoBinding = new VolumeDiscountPromotionBinding();
  private final List<PromoView> promoViews = new ArrayList<PromoView>();
  private int fromIndex = 0;
  private int pageSize = 1000;
  private List<Promotion> promotions;
  private Promotion orgPromo;
  private Shop activeShop;

  public PromotionPresenter() {
    //add bindings to editView
    //TODO: SuggestBox bindings
    HasValueBinding.bind(view.getEditView().getStartDate(), editPromoBinding.startDate(), new BindingConverter<Date, Date>() {
      @SuppressWarnings("deprecation")
      @Override public Date convertFrom(Date data) {
        //here we need to set the end date....
        final Date endDate = new Date(data.getTime());

        endDate.setMonth(data.getMonth()+1);
        endDate.setDate(endDate.getDate()-1);
        editPromoBinding.endDate().setData(endDate);
        return data;
      }

      @Override public Date convertTo(Date data) {
        return data;
      }});
    HasTextBinding.bind(view.getEditView().getEndDate(), editPromoBinding.endDate(), new BindingConverter<Date, String>(){
      private Date date;
      @Override public Date convertFrom(String data) {
        return date;
      }
      @Override public String convertTo(Date data) {
        this.date = data;
        return data == null ? "" : DateTimeFormat.getMediumDateFormat().format(data);
      }});
    TextBoxBaseBinding.bind(view.getEditView().getPrice(), editPromoBinding.price(), BindingConverters.DOUBLE_CONVERTER);
    TextBoxBaseBinding.bind(view.getEditView().getVolumeDiscount(), editPromoBinding.volumeDiscount(), BindingConverters.INTEGER_CONVERTER);
    view.getEditView().saveClickHandlers().addClickHandler(new ClickHandler(){
      @Override public void onClick(ClickEvent event) {
        final Promotion pnew = (Promotion) editPromoBinding.getData();

        ShopServiceAsync.updatePromotion(orgPromo, pnew, new AsyncCallback<Promotion>() {
          @Override public void onFailure(Throwable caught) {
            view.getEditView().setVisible(false);
          }
          @Override public void onSuccess(Promotion result) {
            StatusMessage.get().show(i18n.promotionSaved());
            view.getEditView().setVisible(false);
            if (result != null) {
              CatalogCache.get().put(result);
              showPromo((VolumeDiscountPromotion) result, view.getEditView().getPromoView());
            } else {
              CatalogCache.get().put(pnew);
              showPromo((VolumeDiscountPromotion) pnew, view.getEditView().getPromoView());
            }
          }});
      }});
  }

  @Override
  public PromotionView getView() {
    return view;
  }

  public void setShop(Shop activeShop) {
    this.activeShop = activeShop;
  }

  public void show() {
    if (activeShop == null) {
      activeShop = CatalogCache.get().getActiveCatalog().getShops().get(0);
    }
    if (promotions == null) {
      loadPromotions();
    } else {
      view.clear();
      for (int i = promoViews.size(); i < promotions.size(); i++) {
        final PromoView pv = view.getNewPromoView();

        pv.editClickHandlers().addClickHandler(new ClickHandler() {
          @Override public void onClick(ClickEvent event) {
            final Promotion p = CatalogCache.get().getPromotion(pv.getId());
            
            orgPromo = p;  
            editPromoBinding.setData(p.clone(new HashMap()));
          }
        });
        promoViews.add(pv);
      }
      int i = 0;
      for (Promotion promo : promotions) {
        final PromoView pv = promoViews.get(i++);
        
        view.add(pv);
        showPromo((VolumeDiscountPromotion)promo, pv);
      }
    }
  }

  private void showPromo(VolumeDiscountPromotion p, PromoView pv) {
    pv.setId(p.getId());
    final List<PropertyValue[]> pvl = Util.getProductGroupPropertyValues(CatalogCache.get().getLangNames(), CatalogCache.get().getProductGroup(2000L), p.getProduct());
    final StringBuffer s = new StringBuffer();
    for (PropertyValue[] propertyValues : pvl) {
//       s.append.
    }
    final PropertyValue name = Util.getPropertyValueByName(p.getProduct().getPropertyValues(), Util.NAME, null);

    pv.setName(name == null ? "" : name.getStringValue());
    //final PropertyValue price = Util.getPropertyValueByName(p.getProduct().getPropertyValues(), "Price", null);

    //pv.setProductPrice(price == null ? "<no price>" : Util.formatMoney(price.getMoneyValue()));
    pv.setStartDate(DateTimeFormat.getMediumDateFormat().format(p.getStartDate()));
    pv.setEndDate(DateTimeFormat.getMediumDateFormat().format(p.getEndDate()));
    pv.setPrice(Util.formatMoney(p.getPrice()));
    pv.setVolume(p.getVolumeDiscount() + "");
  }

  private void loadPromotions() {
    final Promotion filter = new Promotion();

    filter.setShop(activeShop);
    filter.setEndDate(new Date(System.currentTimeMillis()));
    ShopServiceAsync.findActualPromotions(fromIndex, pageSize, filter, new AsyncCallback<List<Promotion>>() {
      @Override public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override public void onSuccess(List<Promotion> result) {
        //Avoid loop in case result would be null.
        promotions = result == null ? new ArrayList<Promotion>(1) : result;
        for (Promotion promotion : result) {
          CatalogCache.get().put(promotion);
        }
        show();
      }
    });
  }
}
