package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductView.SHOW;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class ProductPresenter implements Presenter<ProductView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
  private final static ResourceBundle rb = GWT.create(ResourceBundle.class);
  private final static int EDIT_COL = 0; // when update, change FIRST_DATA_COL
  private final static int DELETE_COL = 1; // when update, change FIRST_DATA_COL
  private final static int FIRST_DATA_COL = 2;

  private final ProductView view = new ProductView();
  private final ArrayList<ItemValuesPresenter> valuesPresenters = new ArrayList<ItemValuesPresenter>();

  private String currentLanguage = "en";
  private ProductGroup currentProductGroup;
  private Product currentProduct;
  private Product orgProduct;
  private List<Product> currentProducts;
  private int fromIndex = 0;
  private int pageSize = 1000;
  private SHOW show = SHOW.PRODUCTS;
  private Shop shop;

  public ProductPresenter() {
    view.getProductTable().addClickHandler(new ClickHandler(){
      @Override
      public void onClick(ClickEvent event) {
        final Cell c = view.getProductTable().getCellForEvent(event);

        if (c != null) {
          final int colIndex = c.getCellIndex();
          if (colIndex == EDIT_COL) {
            currentProduct = currentProducts.get(c.getRowIndex());
            show(SHOW.PRODUCT);
          } else if (colIndex == DELETE_COL &&
              Window.confirm(i18n.deleteProductQuestion())) {
            delete(currentProducts.get(c.getRowIndex()));
            show(SHOW.PRODUCTS);
          }
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

  private void delete(Product product) {
    if (product != null) {
      final Product saveProduct = currentProduct.clone(new HashMap());
      CatalogServiceAsync.updateProduct(orgProduct, saveProduct, new AsyncCallback<Product>(){
        @Override public void onFailure(Throwable caught) {
        }
        @Override public void onSuccess(Product result) {
          StatusMessage.get().show(i18n.productDeleted());
          currentProduct = null;
          orgProduct = null;
          show(SHOW.PRODUCTS);
        }
      });
    }
  }

  private void save() {
    view.setSaving(true);
    //clear field not needed to be stored: properties and empty property value
    // field
    if (orgProduct != null) {
      orgProduct.setProperties(null);
      orgProduct.setPropertyValues(Util.filterEmpty(orgProduct.getPropertyValues()));
    }
    final Product saveProduct = currentProduct.clone(new HashMap());

    saveProduct.setProperties(null);
    saveProduct.setPropertyValues(Util.filterEmpty(saveProduct.getPropertyValues()));
    currentProducts = null;
    CatalogServiceAsync.updateProduct(orgProduct, saveProduct, new AsyncCallback<Product>(){
      @Override public void onFailure(Throwable caught) {
        view.setSaving(false);
      }
      @Override public void onSuccess(Product result) {
        view.setSaving(false);
        StatusMessage.get().show(i18n.productSaved());
        if (result != null) {
          CatalogCache.get().put(result);
          currentProduct = result;
          orgProduct = null;
          show(SHOW.PRODUCT);
        }
      }
    });
  }

  public void switchLanguage(String lang) {
    currentLanguage = lang;
    show(show);
  }

  public void show(Shop shop, ProductGroup productGroup) {
    this.shop = shop;
    if (currentProductGroup != productGroup) {
      currentProductGroup = productGroup;
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
    if (currentProducts == null) {
      loadProducts(shop, currentProductGroup);
    } else if (currentProducts.size() > 0) {
      view.setProductsTableEmpty(false);
      view.getProductTable().resizeRows(currentProducts.size());
      final List<PropertyValue[]> header = Util.getProductGroupPropertyValues(CatalogCache.get().getActiveCatalog().getLanguages(), CatalogCache.get().getProductGroupProduct(), currentProducts.get(0));

      view.setProductTableHeader(EDIT_COL, " ");
      view.setProductTableHeader(DELETE_COL, " "); 
      int h = FIRST_DATA_COL;
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
        final List<PropertyValue[]> pvl = Util.getProductGroupPropertyValues(CatalogCache.get().getActiveCatalog().getLanguages(), CatalogCache.get().getProductGroupProduct(), product);

        view.setProductTableCellImage(i, EDIT_COL, rb.editImage());
        view.setProductTableCellImage(i, DELETE_COL, rb.deleteImage());
        int j = FIRST_DATA_COL;
        for (PropertyValue[] pvlangs : pvl) {
          PropertyValue dpv = null;
          PropertyValue lpv = null;

          for (PropertyValue pv : pvlangs) {
            if (currentLanguage.equals(pv.getLanguage())) {
              lpv = pv;
            } else if (pv.getLanguage() ==  null) {
              dpv = pv;
            }
          }
          view.setProductTableCell(i, j, Util.isEmpty(lpv) ? dpv : lpv);
          j++;
        }
      }
    } else {
      view.setProductsTableEmpty(true);
    }
  }

  private void showProduct() {
    view.clear();
    valuesPresenters.clear();
    final List<Long> parents = Util.findParents(currentProductGroup);
    final Long nameGId = CatalogCache.get().getProductGroupName().getId();
    
    parents.add(currentProductGroup.getId());
    for (Long pid : parents) {
      final ProductGroup parent = CatalogCache.get().getProductGroup(pid);
      final List<PropertyValue[]> pv = Util.getProductGroupPropertyValues(CatalogCache.get().getActiveCatalog().getLanguages(), parent, currentProduct);

      if (!pv.isEmpty()) {
        final ItemValuesPresenter presenter = new ItemValuesPresenter();
        final PropertyValue pvName = Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage);
        final PropertyValue pvDName = Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, null);
        final String name = nameGId.equals(pid) ? "" : (pvName == null || Util.isEmpty(pvName) ? (pvDName == null ?  "" : pvDName.getStringValue()) : pvName.getStringValue());

        valuesPresenters.add(presenter);
        view.addPropertyValues(name, presenter.getView());
        //Util.getPropertyValueByName(parent.getPropertyValues(), Util.NAME, currentLanguage).getStringValue()
        presenter.show(currentLanguage, pv);
      }
    }
  }

  private void loadProducts(Shop shop, ProductGroup pg) {
    CatalogServiceAsync.findAllByProductGroupProducts(fromIndex, pageSize, pg, new AsyncCallback<List<Product>>() {

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
      }
    });
  }
}
