package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.PromotionView.PromoView;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.shop.VolumeDiscountPromotion;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PromotionPresenter implements Presenter<PromotionView> {

  private final PromotionView view = new PromotionView();
  private final List<PromoView> promoViews = new ArrayList<PromoView>();
  private Integer start = 0;
  private Integer range = 50;
  private List<Promotion> promotions;
  private Shop activeShop;

  public PromotionPresenter() {
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
        promoViews.add(new PromoView());
      }
      int i = 0;
      for (Promotion promo : promotions) {
        showPromo((VolumeDiscountPromotion)promo, promoViews.get(i++));
      }
    }
  }

  private void showPromo(VolumeDiscountPromotion p, PromoView pv) {
    view.add(pv);
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
    ShopServiceAsync.findActualPromotions(start, range, filter, new AsyncCallback<List<Promotion>>() {
      @Override public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
      }

      @Override public void onSuccess(List<Promotion> result) {
        //Avoid loop in case result would be null.
        promotions = result == null ? new ArrayList<Promotion>(1) : result;
        show();
      }
    });
  }
}
