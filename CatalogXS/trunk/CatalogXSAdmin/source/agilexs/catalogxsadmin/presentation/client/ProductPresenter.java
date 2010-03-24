package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductView.SHOW;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class ProductPresenter implements Presenter<ProductView> {

  private final ProductView view = new ProductView();
  private final ArrayList<ItemValuesPresenter> valuesPresenters = new ArrayList<ItemValuesPresenter>();

  private String currentLanguage = "en";
  private ProductGroup currentProductGroup;
  private Product currentProduct;
  private Product orgProduct;
  private List<Product> currentProducts;
  private Integer fromIndex = 0;
  private Integer pageSize = 50;
  private SHOW show = SHOW.PRODUCTS;
  private ProductGroup root;

  public ProductPresenter() {
    view.getProductTable().addClickHandler(new ClickHandler(){
      @Override
      public void onClick(ClickEvent event) {
        final Cell c = view.getProductTable().getCellForEvent(event);
        if (c != null) {
          currentProduct = currentProducts.get(c.getRowIndex()-1);
          show(SHOW.PRODUCT);
        }
      }});
    view.backClickHandlers().addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        show(SHOW.PRODUCTS);
      }});
    view.saveButtonClickHandlers().addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        save();
      }});
    view.newProductButtonClickHandlers().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        orgProduct = null;
        currentProduct = new Product();
        currentProduct.setCatalog(currentProductGroup.getCatalog());
        currentProduct.setParents(new ArrayList<ProductGroup>());
        currentProduct.getParents().add(currentProductGroup);
        show(SHOW.PRODUCT);
      }});
  }

  @Override
  public ProductView getView() {
    return view;
  }

  private void save() {
    //clear field not needed to be stored: properties and empty property value
    // field
    if (orgProduct != null) {
      orgProduct.setProperties(null);
      orgProduct.setPropertyValues(Util.filterEmpty(orgProduct.getPropertyValues()));
    }
    final Product saveProduct = currentProduct.clone(new HashMap());

    saveProduct.setProperties(null);
    saveProduct.setPropertyValues(Util.filterEmpty(saveProduct.getPropertyValues()));
    CatalogServiceAsync.updateProduct(orgProduct, saveProduct, new AsyncCallback<Product>(){
      @Override public void onFailure(Throwable caught) {}
      @Override public void onSuccess(Product result) {
        StatusMessage.get().show("Product saved", 10);
        if (result != null) {
          CatalogCache.get().put(result);
          currentProduct = result;
          show(SHOW.PRODUCT);
        }
      }
    });
  }

  public void switchLanguage(String lang) {
    currentLanguage = lang;
    show(show);
  }

  public void show(Shop shop, ProductGroup productGroup, ProductGroup root) {
    if (currentProductGroup != productGroup) {
      currentProductGroup = productGroup;
      this.root = root;
      if (currentProductGroup != null) {
        loadProducts(shop, currentProductGroup);
      } else {
        show(SHOW.NO_PRODUCTS);
      }
    } else {
      // FIXME ?? can this happen?
    }
  }

  private void show(SHOW show) {
    this.show = show;
    switch(show) {
    case NO_PRODUCTS:
      break;
    case PRODUCTS:
      showProducts();
      break;
    case PRODUCT:
      if ((orgProduct == null || orgProduct.getId() != currentProduct.getId())
          && currentProduct.getId() != null) {
        orgProduct = currentProduct.clone(new HashMap());
      }
      showProduct();
      break;
    }
    view.showPage(show);
  }

  private void showProducts() {
    view.getProductTable().clear();
    if (currentProducts.size() > 0) {
      view.getProductTable().resizeRows(currentProducts.size() + 1);
      final List<PropertyValue[]> header = Util.getProductGroupPropertyValues(CatalogCache.get().getLangNames(), root, currentProducts.get(0));

      int h = 0;
      for (PropertyValue[] pvhlangs : header) {
        for (PropertyValue pvh : pvhlangs) {
          if (currentLanguage.equals(pvh.getLanguage())) {
            view.setProductTableHeader(h, Util.getLabel(pvh, currentLanguage, true).getLabel());
            h++;
          }
        }
      }
      for (int i = 0; i < currentProducts.size(); i++) {
        final Product product = currentProducts.get(i);
        final List<PropertyValue[]> pvl = Util.getProductGroupPropertyValues(CatalogCache.get().getLangNames(), root, product);

        int j = 0;
        for (PropertyValue[] pvlangs : pvl) {
          PropertyValue dpv = null;
          PropertyValue lpv = null;

          for (PropertyValue pv : pvlangs) {
            if (currentLanguage.equals(pv.getLanguage())) {
              lpv = pv;
            } else if (pv.getLanguage() ==  null){
              dpv = pv;
            }
          }
          view.setProductTableCell(i+1, j, Util.isEmpty(lpv) ? dpv : lpv);
          j++;
        }
      }
    }
  }

  private void showProduct() {
    view.getPropertiesPanel().clear();
    valuesPresenters.clear();
    final List<Long> parents = Util.findParents(currentProductGroup);

    parents.add(currentProductGroup.getId());
    for (Long pid : parents) {
      final ProductGroup parent = CatalogCache.get().getProductGroup(pid);
      final List<PropertyValue[]> pv = Util.getProductGroupPropertyValues(CatalogCache.get().getLangNames(), parent, currentProduct);

      if (!pv.isEmpty()) {
        final ItemValuesPresenter presenter = new ItemValuesPresenter();

        valuesPresenters.add(presenter);
        view.getPropertiesPanel().add(presenter.getView().asWidget());
        presenter.show(Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage).getStringValue(), currentLanguage, pv);
      }
    }
  }

  private void loadProducts(Shop shop, ProductGroup pg) {
    CatalogServiceAsync.findAllByProductGroupProducts(fromIndex, pageSize, pg, new AsyncCallback<List<Product>>() {

      @Override public void onFailure(Throwable caught) {
        //FIXME implement handling failure
      }

      @Override public void onSuccess(List<Product> result) {
        currentProducts = result;
        for (Product p : result) {
          CatalogCache.get().put(p);
        }
        show = SHOW.PRODUCTS;
        show(show);
      }
    });
  }
}
