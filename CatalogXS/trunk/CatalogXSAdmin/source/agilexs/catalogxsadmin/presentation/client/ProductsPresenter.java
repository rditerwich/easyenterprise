package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductView.SHOW;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.query.AllProductsQuery;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ProductsPresenter extends CatalogPresenter<ProductsView> {

  private String filter;

  private final ProductPresenter pp = new ProductPresenter(false) {
    private Integer fromIndex = 0;
    private Integer pageSize = 1000;

    @Override
    protected void loadProducts(Shop shop, ProductGroup pg) {
      final AllProductsQuery query = new AllProductsQuery();

      query.setStringValue(filter);
      query.setShop(activeShop);
      query.setProductGroup(currentProductGroup);
      CatalogServiceAsync.findAllProducts(fromIndex, pageSize, query, new AsyncCallback<List<Product>>() {
        @Override public void onFailure(Throwable caught) {
          show = SHOW.NO_PRODUCTS;
          show(show);
        }
        @Override public void onSuccess(List<Product> result) {
          currentProducts = result;
          for (Product p : result) {
            CatalogCache.get().put(p);
          }
          show = SHOW.PRODUCTS;
          show(show);
        }});
    }
  };

  public ProductsPresenter() {
    super(new ProductsView());
    // use a timer to delay starting query
    final Timer t = new Timer() {
      @Override public void run() {
        filter = view.getFilterText();
        pp.show(activeShop, currentProductGroup, true);
      }
    };

    view.init(pp.getView());
    view.productFilterHandlers().addKeyUpHandler(new KeyUpHandler() {
      @Override public void onKeyUp(KeyUpEvent event) {
        t.cancel();
        t.schedule(2000); //delay 2 seconds before starting quering
      }
    });
  }

  @Override
  protected void show(ProductGroup currentProductGroup) {
    pp.show(activeShop, currentProductGroup);
  }

  @Override
  protected void switchLanguage(String newLang) {
    pp.switchLanguage(currentLanguage);
  }
}