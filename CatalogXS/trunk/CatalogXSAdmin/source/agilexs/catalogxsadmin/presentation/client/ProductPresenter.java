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
//  private ItemParentsPresenter parentsP = new ItemParentsPresenter(new ItemParentsView());

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
//    view.setParentsPanel(parentsP.getView());
    view.getProductTable().addClickHandler(new ClickHandler(){
      @Override
      public void onClick(ClickEvent event) {
        final Cell c = view.getProductTable().getCellForEvent(event);
        if (c != null) {
          currentProduct = currentProducts.get(c.getRowIndex()-1);
          show(SHOW.PRODUCT);
        }
      }});
    view.hasBackClickHandlers().addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        show(SHOW.PRODUCTS);
      }});
  }

  public void setNewProduct(Shop shop) {
    orgProduct = null;
    currentProduct = new Product();
    currentProduct.setCatalog(shop.getCatalog());
    view.setProductName("");
    show(SHOW.PRODUCT);
  }

  @Override
  public ProductView getView() {
    return view;
  }

  public void save() {
      //clear properties because these don't need to be saved. Relation
      orgProduct.setProperties(null);
      final Product saveProduct = currentProduct.clone(new HashMap());

      saveProduct.setProperties(null);
      final List<PropertyValue> spv = saveProduct.getPropertyValues(); 

      saveProduct.setPropertyValues(new ArrayList<PropertyValue>());
      currentProduct.setPropertyValues(new ArrayList<PropertyValue>());
      for (PropertyValue pv : spv) {
        if (!Util.isEmpty(pv)) {
          saveProduct.getPropertyValues().add(pv);
          currentProduct.getPropertyValues().add(pv);
        }
      }
      CatalogServiceAsync.updateProduct(orgProduct, saveProduct, new AsyncCallback<Product>(){
        @Override public void onFailure(Throwable caught) {}
        @Override public void onSuccess(Product result) {
          StatusMessage.get().show("Product saved", 10);
        }
      });
    }

  public void show(String lang) {
    currentLanguage = lang;
    show(show);
  }

  public void show(Shop shop, ProductGroup productGroup, ProductGroup root) {
    if (currentProductGroup != productGroup) {
      currentProductGroup = productGroup;
//FIXME: if (currentProductGroup.getContainsProducts()) {
        this.root = root;
        loadProducts(shop, currentProductGroup);
//      } else {
//        show(SHOW.NO_PRODUCTS);
//      }
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
      if (orgProduct == null || orgProduct.getId() != currentProduct.getId()) {
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
      final List<PropertyValue[]> header = Util.getProductGroupPropertyValues(CatalogCache.get().getLanguages(), root, currentProducts.get(0));

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

        CatalogCache.get().put(product);
        final List<PropertyValue[]> pvl = Util.getProductGroupPropertyValues(CatalogCache.get().getLanguages(), root, product);

        int j = 0;
        for (PropertyValue[] pvlangs : pvl) {
          for (PropertyValue pv : pvlangs) {
            if (currentLanguage.equals(pv.getLanguage())) {
              view.setProductTableCell(i+1, j, pv);
              j++;
            }
          }
        }
      }
    }
  }

  private void showProduct() {
    //  final PropertyValue pname = Util.getPropertyValueByName(
    //  currentProduct.getPropertyValues(), Util.NAME, null);
    //view.setProductName(Util.getLabel(pname, currentLanguage, true).getLabel());
    //parentsP.show(CatalogCache.get().getParents(currentProductGroup), currentLanguage, CatalogCache.get().getAllProductGroups());
    view.getPropertiesPanel().clear();
    valuesPresenters.clear();
    final List<Long> parents = Util.findParents(currentProductGroup);

    parents.add(currentProductGroup.getId());
    for (Long pid : parents) {
      final ProductGroup parent = CatalogCache.get().getProductGroup(pid);
      final List<PropertyValue[]> pv = Util.getProductGroupPropertyValues(CatalogCache.get().getLanguages(), parent, currentProduct);

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
        show = SHOW.PRODUCTS;
        show(show);
      }
    });
  }
}
