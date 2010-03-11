package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
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

/**
 * Presenter for the ProductGroup page
 */
public class ProductGroupPresenter implements Presenter<ProductGroupView> {

  private final ProductGroupView view = new ProductGroupView();
  private final HashMap<TreeItem, ProductGroup> treemap = new HashMap<TreeItem, ProductGroup>();
  private final HashMap<Long, List<ProductGroup>> parentMap = new HashMap<Long, List<ProductGroup>>();
  private ProductGroup currentProductGroup;
  private ProductGroup orgProductGroup;
  private String currentLanguage = "en";
  private Shop shop;
  private ProductGroupPropertiesPresenter pgpp;
  private final ArrayList<ProductGroupValuesPresenter> valuesPresenters = new ArrayList<ProductGroupValuesPresenter>();

  public ProductGroupPresenter() {
    final List<String> langs = new ArrayList<String>(2);
    langs.add("en");
    langs.add("de");

    view.getNewButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        orgProductGroup = null;
        currentProductGroup = new ProductGroup();
        view.getName().setText("");
        pgpp.show(currentLanguage, currentProductGroup.getPropertyValues());
        view.getParentPropertiesPanel().clear();
        view.getTree().setSelectedItem(null);
      }});
    view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        //update properties
        currentProductGroup.getProperties().clear();
        currentProductGroup.setProperties(pgpp.getProperties());
        for (Property np : pgpp.getProperties()) {
          //TODO remove deleted properties 
          boolean found = false;
          for (Property op : orgProductGroup.getProperties()) {
            if (np.getId() == op.getId()) {
              found = true;
//              CatalogServiceAsync.updateProperty(op, np, new AsyncCallback(){
//                @Override public void onFailure(Throwable caught) {}
//                @Override public void onSuccess(Object result) {}
//              });
              break;
            }
          }
          if (!found) {
//            CatalogServiceAsync.updateProperty(null, np, new AsyncCallback(){
//              @Override public void onFailure(Throwable caught) {}
//              @Override public void onSuccess(Object result) {}
//            });
          }
        }
        //update property values
        currentProductGroup.getPropertyValues().clear();
        currentProductGroup.setPropertyValues(Util.filterEmpty(pgpp.getPropertyValues()));
        //update values
        for (ProductGroupValuesPresenter presenter : valuesPresenters) {
          currentProductGroup.getPropertyValues().addAll(Util.filterEmpty(presenter.getPropertyValues()));  
        }
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
        CatalogServiceAsync.updateProductGroup(orgProductGroup, currentProductGroup, new AsyncCallback(){
          @Override public void onFailure(Throwable caught) {}
          @Override public void onSuccess(Object result) {}
        });
      }});
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem item = event.getSelectedItem();
        //FIXME: handle nodes that have no children in the database anyway
        if (item.getChildCount() == 0) {
          loadChildren(shop, item);
        }
        currentProductGroup = treemap.get(event.getSelectedItem());
        orgProductGroup = currentProductGroup.clone(new HashMap());  

        if (currentProductGroup != null) {
          final PropertyValue name = Util.getPropertyValueByName(currentProductGroup.getPropertyValues(),Util.NAME, null);

          view.getName().setText(name==null?"":name.getStringValue());
          pgpp.show(currentLanguage, Util.getProductGroupPropertyValues(langs, currentProductGroup, currentProductGroup.getPropertyValues()));
          view.getParentPropertiesPanel().clear();
          walkParents(valuesPresenters.iterator(), currentProductGroup, currentProductGroup);
        }
      }

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
            view.getParentPropertiesPanel().add(presenter.getView().getViewWidget());
            //FIXME: this should be a map of parent props to child values 
            presenter.setValues(Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage).getStringValue(), pv);
            presenter.show(currentLanguage);
          }
        }        
      }
    });
    pgpp = new ProductGroupPropertiesPresenter(); 
    view.setPropertiesPanel(pgpp.getView().getViewWidget());

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
  public ProductGroupView getView() {
    return view;
  }

  private void setLanguage(String lang) {
    currentLanguage = lang;
    pgpp.setLanguage(lang);
    for (ProductGroupValuesPresenter pgvp : valuesPresenters) {
      pgvp.show(lang);
    }
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
