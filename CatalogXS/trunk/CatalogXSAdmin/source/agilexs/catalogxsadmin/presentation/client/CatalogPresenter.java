package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

//binds
public class CatalogPresenter implements Presenter<CatalogView> {

  public enum SHOW {
    PRODUCT_GROUP, PRODUCT
  }

  private final CatalogView view = new CatalogView();
  private final HashMap<TreeItem, Long> treemap = new HashMap<TreeItem, Long>();

  private Shop activeShop;
  private ProductGroup currentProductGroup;
  private ProductGroup root;
  final ProductGroupPresenter pgp = new ProductGroupPresenter();
  final ProductPresenter pp = new ProductPresenter();

  public CatalogPresenter() {
    final List<String> langs = new ArrayList<String>(2);
    langs.add("en");
    langs.add("de");

    view.addTab(pgp.getView(), "Product Group");
    view.addTab(pp.getView(), "Products");

    view.getNewProductGroupButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        pgp.setNewProductGroup(activeShop);
        view.getTree().deSelectItem();
        view.selectedTab(0);
      }});
    view.getNewProductButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        pp.setNewProduct(activeShop);
        view.getTree().deSelectItem();
        view.selectedTab(1);
      }});
    view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (view.getSelectedTab() == 0) {
          pgp.save();
        } else {
          pp.save();
        }
      }});
    view.addTabSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        if (event.getSelectedItem().intValue() == 0) {
          pgp.show(currentProductGroup);
        } else {
          pp.show(activeShop, currentProductGroup, root);
        }
      }});
    view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
      @Override
      public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem item = event.getTarget();

        if (item.getState()) {
          final Long pgId = treemap.get(item);
          
          if (pgId != null) {
            if (view.getTree().isTreeItemEmpty(item)) { //FIXME: && Boolean.FALSE.equals(currentProductGroup.getContainsProducts())) {
              loadChildren(activeShop, item);
            }
          }
        }
      }
    });
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem item = event.getSelectedItem();
        final Long pgId = treemap.get(item);
        
        if (pgId != null) {
          currentProductGroup = CatalogCache.get().getProductGroup(pgId);
          if (view.getTree().isTreeItemEmpty(item)) { //FIXME: && Boolean.FALSE.equals(currentProductGroup.getContainsProducts())) {
            loadChildren(activeShop, item);
          }
          if (view.getSelectedTab() == 0) {
            pgp.show(currentProductGroup);
          } else {
            pp.show(activeShop, currentProductGroup, root);
          }
        } else {
          //FIXME ?? can this happen?
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
        pgp.show(view.getSelectedLanguage());
        pp.setLanguage(view.getSelectedLanguage());
      }});

    CatalogCache.get().getShop(1L, new AsyncCallback<Shop>() {
      @Override public void onFailure(Throwable caught) {
      }

      @Override
      public void onSuccess(Shop result) {
        activeShop = result;
        loadChildren(result, null); //initial tree
        for (ProductGroup pg : result.getTopLevelProductGroups()) {
          CatalogCache.get().put(pg);
        }
      }});
  }

  @Override
  public CatalogView getView() {
    return view;
  }

  private void loadChildren(Shop shop, final TreeItem parent) {
    final ProductGroup parentPG = CatalogCache.get().getProductGroup(treemap.get(parent));

    CatalogServiceAsync.findAllProductGroupChildren(
        shop, parentPG, new AsyncCallback<List<ProductGroup>>() {
          @Override
          public void onFailure(Throwable caught) {
            //StatusMessage.get().show(caught.getMessage(), 30);
          }

          @Override
          public void onSuccess(List<ProductGroup> result) {
            if (result.size() == 0) {
              view.getTree().setTreeItemAsEmpty(parent);
            }
            for (ProductGroup productGroup : result) {
              final PropertyValue value = Util.getPropertyValueByName(productGroup.getPropertyValues(), Util.NAME, null);

              if (root == null && Util.ROOT.equals(value.getStringValue())) {
                root = productGroup;
              }
              CatalogCache.get().put(productGroup);
              for (ProductGroup pPG : productGroup.getParents()) {
                CatalogCache.get().put(pPG);
              }
//              CatalogCache.get().putParent(productGroup, parentPG);
              treemap.put(
                  view.getTree().addItem(parent, value != null ? value.getStringValue() : "<No Name>"), productGroup.getId());
            }
          }
        });
  }
}
