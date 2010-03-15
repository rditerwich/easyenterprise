package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductView.SHOW;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class ProductPresenter implements Presenter<ProductView> {

  private final ProductView view = new ProductView();
  private String currentLanguage = "en";
  private ProductGroup currentProductGroup;
  private Product currentProduct;
  private Product orgProduct;
  private List<Product> currentProducts;
  private final ArrayList<ProductGroupValuesPresenter> valuesPresenters = new ArrayList<ProductGroupValuesPresenter>();
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
    view.hasBackClickHandlers(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        show(SHOW.PRODUCTS);
      }});
  }

  public ClickHandler getNewProductClickHandler() {
    return new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        orgProduct = null;
        currentProduct = new Product();
        view.setProductName("");
        //TODO set tree correctly view.getTree().setSelectedItem(null);
        show(SHOW.PRODUCT);
      }};
  }

  @Override
  public ProductView getView() {
    return view;
  }

  public void setLanguage(String lang) {
    currentLanguage = lang;
    show(show);
  }

  public void save() {
      //update properties
      currentProduct.getProperties().clear();
//      currentProductGroup.setProperties(pgpp.getProperties());
      //update property values
      currentProduct.getPropertyValues().clear();
//      currentProductGroup.setPropertyValues(Util.filterEmpty(pgpp.getPropertyValues()));
//      //update values
//      for (ProductGroupValuesPresenter presenter : valuesPresenters) {
//        currentProductGroup.getPropertyValues().addAll(Util.filterEmpty(presenter.getPropertyValues()));
//      }
/*
      for (PropertyValue np : currentProductGroup.getPropertyValues()) {
        //TODO remove deleted properties
        boolean found = false;
        for (PropertyValue op : orgProductGroup.getPropertyValues()) {
          if (np.getId() == op.getId() && !np.equals(op)) {
            found = true;
            CatalogServiceAsync.updatePropertyValue(op, np, new AsyncCallback(){
              @Override public void onFailure(Throwable caught) {}
              @Override public void onSuccess(Object result) {}
            });
            break;
          }
        }
        if (!found) {
          CatalogServiceAsync.updatePropertyValue(null, np, new AsyncCallback(){
            @Override public void onFailure(Throwable caught) {}
            @Override public void onSuccess(Object result) {}
          });
        }
      }
*/
      CatalogServiceAsync.updateProduct(orgProduct, currentProduct, new AsyncCallback(){
        @Override public void onFailure(Throwable caught) {}
        @Override public void onSuccess(Object result) {
          StatusMessage.get().show("Product saved", 10);
        }
      });
    }

  public void show(Shop shop, ProductGroup productGroup, ProductGroup root) {
    if (currentProductGroup != productGroup) {
      currentProductGroup = productGroup;
      if (currentProductGroup.getContainsProducts()) {
        this.root = root;
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
      final PropertyValue name = Util.getPropertyValueByName(
          currentProductGroup.getPropertyValues(), Util.NAME, currentLanguage);

      view.setProductGroupName(Util.stringValueOf(name.getStringValue()));
      showProducts();
      break;
    case PRODUCT:
      if (orgProduct == null || orgProduct.getId() != currentProduct.getId()) {
        orgProduct = currentProduct.clone(new HashMap());
      }
//      final PropertyValue pname = Util.getPropertyValueByName(
//          currentProduct.getPropertyValues(), Util.NAME, null);
//      view.setProductName(Util.getLabel(pname, currentLanguage, true).getLabel());
      view.getPropertiesPanel().clear();
      valuesPresenters.clear();
      walkParents(CatalogCache.get().getLanguages(), currentProductGroup, currentProduct, new ArrayList<Long>());
//      for (ProductGroupValuesPresenter pgvp : valuesPresenters) {
//        pgvp.show(currentLanguage);
//      }
      break;
    }
    view.showPage(show); 
  }

  private void showProducts() {
    view.getProductTable().clear();
    if (currentProducts.size() > 0) {
      view.getProductTable().resizeRows(currentProducts.size() + 1);
      final List<PropertyValue> header = Util.getProductGroupPropertyValues(CatalogCache.get().getLanguages(), root, currentProducts.get(0).getPropertyValues());

      int h = 0;
      for (PropertyValue pvh : header) {
        if (currentLanguage.equals(pvh.getLanguage())) {
          view.setProductTableHeader(h, Util.getLabel(pvh, currentLanguage, true).getLabel());
          h++;
        }
      }
      for (int i = 0; i < currentProducts.size(); i++) {
        final Product product = currentProducts.get(i);

        CatalogCache.get().put(product);
        final List<PropertyValue> pvl = Util.getProductGroupPropertyValues(CatalogCache.get().getLanguages(), root, product.getPropertyValues());

        int j = 0;
        for (PropertyValue pv : pvl) {
          if (currentLanguage.equals(pv.getLanguage())) {
            view.setProductTableCell(i+1, j, pv);
            j++;
          }
        }
      }
    }
  }

  private void walkParents(List<String> langs, ProductGroup pg, Item currentItem, List<Long> traversed) {
    if (pg == null || !CatalogCache.get().parentMapContains(pg)) return;

    for (ProductGroup parent : CatalogCache.get().getParents(pg)) {
      if (traversed.contains(parent.getId())) continue;
      traversed.add(parent.getId());
      walkParents(langs, parent, currentItem, traversed);
      final List<PropertyValue> pv = Util.getProductGroupPropertyValues(langs, parent, currentItem.getPropertyValues());

      if (!pv.isEmpty()) {
        final ProductGroupValuesPresenter presenter = new ProductGroupValuesPresenter();

        valuesPresenters.add(presenter);
        view.getPropertiesPanel().add(presenter.getView().asWidget());
        //FIXME: this should be a map of parent props to child values
        presenter.setValues(Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage).getStringValue(), pv);
        presenter.show(currentLanguage);
      }
    }
  }

  private void loadProducts(Shop shop, ProductGroup pg) {
    CatalogServiceAsync.findAllByProductGroupProducts(fromIndex, pageSize, pg, new AsyncCallback<List<Product>>() {

      @Override public void onFailure(Throwable caught) {
        //FIXME implement handling failure
      }

      @Override
      public void onSuccess(List<Product> result) {
        currentProducts = result;
        show = SHOW.PRODUCTS;
        show(show);
      }
    });
  }
}
