package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.PromotionView.PromoView;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingConverters;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.binding.HasValueBinding;
import agilexs.catalogxsadmin.presentation.client.binding.TextBoxBaseBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.query.ProductShopQuery;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.shop.VolumeDiscountPromotion;
import agilexs.catalogxsadmin.presentation.client.shop.VolumeDiscountPromotionBinding;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * Presenter class to manager Promotions.
 */
public class PromotionPresenter implements Presenter<PromotionView> {

  private class ProductSuggestion implements Suggestion {

    private final Product product;
    private final String displayString;

    public ProductSuggestion(Product product) {
      this.product = product;
      displayString = Util.productToString(product, currentLanguage);
    }

    public Product getProduct() {
      final Product p = new Product();

      p.setId(product.getId());
      
      return p;
    }

    @Override
    public String getDisplayString() {
      return displayString;
    }

    @Override
    public String getReplacementString() {
      return displayString;
    }
  }

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final PromotionView view;
  private final VolumeDiscountPromotionBinding editPromoBinding = new VolumeDiscountPromotionBinding();
  private final List<PromoView> promoViews = new ArrayList<PromoView>();
  private int fromIndex = 0;
  private int pageSize = 1000;
  private List<Promotion> promotions;
  private Promotion orgPromo;
  private Shop activeShop;
  private String currentLanguage = "en";

  public PromotionPresenter() {
    //TODO: SuggestBox bindings
    view = new PromotionView(new SuggestOracle() {
      @Override public void requestSuggestions(final Request request, final Callback callback) {
        final ProductShopQuery query = new ProductShopQuery();

        query.setStringValue(request.getQuery());
        query.setShop(activeShop);
        CatalogServiceAsync.findByStringValueShopProducts(0, request.getLimit(), query, new AsyncCallback<List<Product>>() {
          @Override public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
          }
          @Override public void onSuccess(List<Product> result) {
            final List<ProductSuggestion> suggestions = new ArrayList<ProductSuggestion>();

            for (Product product : result) {
              suggestions.add(new ProductSuggestion(product));
            }
            callback.onSuggestionsReady(request, new Response(suggestions));
          }});
      }});
    view.getEditView().productFilterSelectionHandlers().addSelectionHandler(new SelectionHandler<Suggestion>(){
      @Override public void onSelection(SelectionEvent<Suggestion> event) {
        final ProductSuggestion ps = (ProductSuggestion) event.getSelectedItem();

        if (ps != null) {
          editPromoBinding.product().setData(ps.getProduct());
        }
      }});
    //we need to bind product() otherwise it will crash, because the above usage
    //of bind will create an empty binding and when bindings are propagated, it
    //will crash because in product() the bindings are empty, this is a bug.
    HasTextBinding.bind(new HasText(){
      @Override public String getText() { return null; }
      @Override public void setText(String text) {}
    }, editPromoBinding.product(), new BindingConverter<Product, String>() {
      private Product product;
      @Override public Product convertFrom(String data) {
        return this.product;
      }
      @Override public String convertTo(Product data) {
        product = data;
        return null;
      }}); 
    HasValueBinding.bind(view.getEditView().getStartDate(), editPromoBinding.startDate(), new BindingConverter<Date, Date>() {
      @SuppressWarnings("deprecation")
      @Override public Date convertFrom(Date data) {
        //here we need to set the end date....
        final Date endDate = editPromoBinding.endDate().getData() == null ?
            new Date() : (Date)editPromoBinding.endDate().getData();

        endDate.setTime(data.getTime());
        endDate.setMonth(data.getMonth()+1);
        endDate.setDate(endDate.getDate()-1);
        //Use setData to trigger binding.
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
        final VolumeDiscountPromotion pnew = (VolumeDiscountPromotion) editPromoBinding.getData();

        if (pnew.getProduct()== null) return;
        ShopServiceAsync.updateVolumeDiscountPromotion((VolumeDiscountPromotion) orgPromo, pnew, new AsyncCallback<VolumeDiscountPromotion>() {
          @Override public void onFailure(Throwable caught) {
            view.getEditView().setVisible(false);
          }
          @Override public void onSuccess(VolumeDiscountPromotion result) {
            StatusMessage.get().show(i18n.promotionSaved());
            view.getEditView().setVisible(false);
            if (result != null) {
              CatalogCache.get().put(result);
              showPromo((VolumeDiscountPromotion) result, view.getEditView().getPromoView());
            } else {
              CatalogCache.get().put(pnew);
              //if is new than does'nt work....
              showPromo((VolumeDiscountPromotion) pnew, view.getEditView().getPromoView());
            }
          }});
      }});
    view.addClickHandlers().addClickHandler(new ClickHandler(){
      @Override public void onClick(ClickEvent event) {
        orgPromo = null;
        final VolumeDiscountPromotion newP = new VolumeDiscountPromotion();

        newP.setShop(activeShop);
        newP.setPriceCurrency("EUR");
        editPromoBinding.setData(newP);
        view.getEditView().setName("");
        view.setEditNewPromo();
      }});
    view.setDeleteHandler(new DeleteHandler<PromoView>() {
      @Override
      public void onDelete(final PromoView promoView) {
        final VolumeDiscountPromotion delPromo = new VolumeDiscountPromotion();

        delPromo.setId(promoView.getId());
        ShopServiceAsync.updatePromotion(delPromo, null, new AsyncCallback<Promotion>() {
          @Override public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
          }
          @Override public void onSuccess(Promotion result) {
            StatusMessage.get().show(i18n.promotionDeleted());
            for (Promotion promo : promotions) {
              if (promo.getId().equals(promoView.getId())) {
                promotions.remove(promo);
                break;
              }
            }
            view.remove(promoView);
          }
        });
      }
    });
  }

  @Override
  public PromotionView getView() {
    return view;
  }

  /**
   * Sets the currently active shop. Doesn't show it, call {#show} to display.
   *
   * @param activeShop
   */
  public void setShop(Shop activeShop) {
    this.activeShop = activeShop;
  }

  public void show() {
    if (activeShop == null) {
      activeShop = CatalogCache.get().getActiveCatalog().getShops().get(0);
    }
    if (promotions == null) {
      view.showLoading(true);
      loadPromotions();
    } else {
      view.clear();
      for (int i = promoViews.size(); i < promotions.size(); i++) {
        final PromoView pv = view.createPromoView();

        pv.editClickHandlers().addClickHandler(new ClickHandler() {
          @Override public void onClick(ClickEvent event) {
            final VolumeDiscountPromotion p = (VolumeDiscountPromotion) CatalogCache.get().getPromotion(pv.getId());

            orgPromo = p;
            editPromoBinding.setData(p.clone(new HashMap()));
            view.getEditView().setName(Util.productToString(p.getProduct(), currentLanguage));
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
    pv.setName(Util.productToString(p.getProduct(), currentLanguage));
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
        view.showLoading(false);
      }

      @Override public void onSuccess(List<Promotion> result) {
        //Avoid loop in case result would be null.
        promotions = result == null ? new ArrayList<Promotion>(1) : result;
        for (Promotion promotion : result) {
          CatalogCache.get().put(promotion);
        }
        show();
        view.showLoading(false);
      }
    });
  }
}

