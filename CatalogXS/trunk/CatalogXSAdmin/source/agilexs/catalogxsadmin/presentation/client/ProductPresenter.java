package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.ProductView.SHOW;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public class ProductPresenter implements Presenter<ProductView> {

  private final ProductView view = new ProductView();
  private final HashMap<TreeItem, ProductGroup> treemap = new HashMap<TreeItem, ProductGroup>();
  private final HashMap<Long, List<ProductGroup>> parentMap = new HashMap<Long, List<ProductGroup>>();
  private ProductGroup currentProductGroup;
  private Product currentProduct;
  private Product orgProduct;
  private ProductGroup root;
  private String currentLanguage = "en";
  private Shop shop;
  private final ArrayList<ProductGroupValuesPresenter> valuesPresenters = new ArrayList<ProductGroupValuesPresenter>();
  private Integer fromIndex = 0;
  private Integer pageSize = 50;

  public ProductPresenter() {
    final List<String> langs = new ArrayList<String>(2);
    langs.add("en");
    langs.add("de");

    view.getNewButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        orgProduct = null;
        currentProduct = new Product();
        view.getName().setText("");
        view.getPropertiesPanel().clear();
        view.getTree().setSelectedItem(null);
        view.showPage(SHOW.PRODUCT);
      }});
    view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        //update properties
        currentProduct.getProperties().clear();
//        currentProductGroup.setProperties(pgpp.getProperties());
        //update property values
        currentProduct.getPropertyValues().clear();
//        currentProductGroup.setPropertyValues(Util.filterEmpty(pgpp.getPropertyValues()));
//        //update values
//        for (ProductGroupValuesPresenter presenter : valuesPresenters) {
//          currentProductGroup.getPropertyValues().addAll(Util.filterEmpty(presenter.getPropertyValues()));  
//        }
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
          @Override public void onSuccess(Object result) {}
        });
      }});
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem item = event.getSelectedItem();

        currentProductGroup = treemap.get(item);
        if (currentProductGroup != null) {
          if (Boolean.TRUE.equals(currentProductGroup.getContainsProducts())) {
            loadProducts(shop, currentProductGroup);
          } else {
            if (item.getChildCount() == 0) {
              loadChildren(shop, item);
            }
          }
        //orgProductGroup = currentProductGroup.clone(new HashMap());  
          final PropertyValue name = Util.getPropertyValueByName(currentProductGroup.getPropertyValues(),Util.NAME, null);

          view.getProductGroupName().setText(name==null?"":name.getStringValue());
          view.showPage(currentProductGroup.getContainsProducts() ? SHOW.PRODUCTS : SHOW.PRODUCT_GROUP);
          //view.getPropertiesPanel().clear();
          //walkParents(valuesPresenters.iterator(), currentProductGroup, currentProductGroup);
        } else {
          //FIXME ?? can this happen?
        }
      }
//this is for details
      private void walkParents(Iterator<ProductGroupValuesPresenter> iterator, ProductGroup pg, ProductGroup currentPG) {
        if (pg == null || !parentMap.containsKey(pg.getId())) return;

        for (ProductGroup parent : parentMap.get(pg.getId())) {
          if (parent == null) continue;
          walkParents(iterator, parent, currentPG);
          final List<PropertyValue> pv = Util.getProductGroupPropertyValues(langs, parent, currentPG.getPropertyValues());

          if (!pv.isEmpty()) {
            ProductGroupValuesPresenter presenter;
  
            if (iterator.hasNext()) {
              presenter = iterator.next();
            } else {
              presenter = new ProductGroupValuesPresenter();
              valuesPresenters.add(presenter);
            }
            view.getPropertiesPanel().add(presenter.getView().getViewWidget());
            //FIXME: this should be a map of parent props to child values 
            presenter.setValues(Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage).getStringValue(), pv);
            presenter.show(currentLanguage);
          }
        }        
      }
    });
    final List<List<String>> l2 = new ArrayList<List<String>>(2);
    final List<String> en = new ArrayList<String>(2);
    en.add("en");
    en.add("English");
    l2.add(en);
    final List<String> de = new ArrayList<String>(2);
    de.add("de");
    de.add("Deutsch");
    l2.add(de);
    view.setLanguages(l2, "en");
    view.getLanguageChangeHandler().addChangeHandler(new ChangeHandler(){
      @Override
      public void onChange(ChangeEvent event) {
        setLanguage(view.getSelectedLanguage());
      }});

    Util.getShop(new AsyncCallback<Shop>(){
      @Override
      public void onFailure(Throwable caught) {
      }

      @Override
      public void onSuccess(Shop result) {
        shop = result;
        loadChildren(result, null); //initial tree
      }});
  }

  @Override
  public ProductView getView() {
    return view;
  }

  private void setLanguage(String lang) {
    currentLanguage = lang;
    for (ProductGroupValuesPresenter pgvp : valuesPresenters) {
      pgvp.show(lang);
    }
  }

  private void loadProducts(Shop shop, ProductGroup pg) {
    CatalogServiceAsync.findAllByProductGroupProducts(fromIndex, pageSize, pg, new AsyncCallback<List<Product>>() {
      @Override public void onFailure(Throwable caught) {
        //FIXME implement handling failure
      }

      @Override
      public void onSuccess(List<Product> result) {
        view.getProductTable().clear();
        view.getProductTable().resizeRows(result.size());
        for (int i = 0; i < result.size(); i++) {
          final Product product = result.get(i);
          
          CatalogCache.INSTANCE.put(product);
          final List<PropertyValue> pvl = Util.getProductGroupPropertyValues(CatalogCache.INSTANCE.getLanguages(), root, product.getPropertyValues()); 

          for (int j = 0; j < product.getPropertyValues().size(); j++) {
            view.setProductTableCell(i, j, pvl.get(j));
          }
        }
      }
    });
  }

  private void loadChildren(Shop shop, final TreeItem parent) {
    final ProductGroup parentPG = treemap.get(parent);

    CatalogServiceAsync.findAllProductGroupChildren(
        shop, parentPG, new AsyncCallback<List<ProductGroup>>() {
          @Override
          public void onFailure(Throwable caught) {
            //FIXME implement handling failure
          }

          @Override
          public void onSuccess(List<ProductGroup> result) {
            for (ProductGroup productGroup : result) {
              final PropertyValue value = Util.getPropertyValueByName(productGroup.getPropertyValues(), Util.NAME, null);

              if (root == null && "Root".equals(value.getStringValue())) {
                root = productGroup;
              }
              if (!parentMap.containsKey(productGroup.getId())) {
                parentMap.put(productGroup.getId(), new ArrayList<ProductGroup>());
              }
              boolean found = false;
              for (ProductGroup pr : parentMap.get(productGroup.getId())) {
                if (parentPG != null && pr.getId().equals(parentPG.getId())) {
                  found = true;
                  break;
                }
              }
              if (!found) {
                parentMap.get(productGroup.getId()).add(parentPG);
              }
              treemap.put(
                  view.addTreeItem(parent, value != null ? value.getStringValue() : "<No Name>"), productGroup);
            }
          }
        });
  }
}
