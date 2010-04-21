package agilexs.catalogxsadmin.presentation.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.core.client.GWT;
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

public class CatalogPresenter implements Presenter<CatalogView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  public enum SHOW {
    PRODUCT_GROUP, PRODUCT
  }

  private final CatalogView view = new CatalogView();
  private final HashMap<TreeItem, Long> treemap = new HashMap<TreeItem, Long>();
  private final int TAB_PRODUCT = 0;
  private final int TAB_GROUP = 1;

  private Shop activeShop;
  private ProductGroup currentProductGroup;
  private String currentLanguage = "en";
  final ProductGroupPresenter pgp = new ProductGroupPresenter();
  final ProductPresenter pp = new ProductPresenter();

  public CatalogPresenter() {
    view.addTab(pp.getView(), i18n.products());
    view.addTab(pgp.getView(), i18n.group());

    view.getNewProductGroupButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final Long lastPG = treemap.get(view.getTree().getSelectedItem());
        
        view.setName("&lt;" + i18n.newGroup() + "&gt;");
        currentProductGroup =
          pgp.setNewProductGroup(activeShop, lastPG != null ? CatalogCache.get().getProductGroup(lastPG) : null);
        pp.show(activeShop, currentProductGroup);

        //view.getTree().deSelectItem();
        view.selectedTab(TAB_GROUP);
      }});
    view.addTabSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        if (event.getSelectedItem().intValue() == TAB_GROUP) {
          pgp.show(currentProductGroup);
        } else {
          pp.show(activeShop, currentProductGroup);
        }
      }});
    view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
      @Override
      public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem item = event.getTarget();

        if (item.getState()) {
          final Long pgId = treemap.get(item);

          if (pgId != null) {
            if (view.getTree().isTreeItemEmpty(item)) {
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
          final Entry<Long, String> name =
              CatalogCache.get().getProductGroupName(pgId, currentLanguage);

          view.setName(name==null ? "" : name.getValue());
          currentProductGroup = CatalogCache.get().getProductGroup(pgId);

          if (Boolean.FALSE.equals(currentProductGroup.getContainsProducts())) {
            view.setTabVisible(TAB_PRODUCT, false);
            view.selectedTab(TAB_GROUP);
          } else {
            view.setTabVisible(TAB_PRODUCT, true);
          }
          if (view.getTree().isTreeItemEmpty(item)) {
            loadChildren(activeShop, item);
          }
          if (view.getSelectedTab() == TAB_GROUP) {
            pgp.show(currentProductGroup);
          } else {
            pp.show(activeShop, currentProductGroup);
          }
        } else {
          //FIXME ?? can this happen?
        }
      }
    });

    view.getLanguageChangeHandler().addChangeHandler(new ChangeHandler(){
      @Override
      public void onChange(ChangeEvent event) {
        currentLanguage = view.getSelectedLanguage();
        pgp.switchLanguage(currentLanguage);
        pp.switchLanguage(currentLanguage);
      }});
    CatalogCache.get().getShop(1L, new AsyncCallback<Shop>() {
      @Override public void onFailure(Throwable caught) {
      }

      @Override public void onSuccess(Shop result) {
        activeShop = result;
        view.setLanguages(CatalogCache.get().getActiveCatalog().getLanguages(), "en");
        CatalogCache.get().loadProductGroupNames(new AsyncCallback<String>(){
        @Override public void onFailure(Throwable caught) {
        }

        @Override public void onSuccess(String result) {
          loadChildren(activeShop, null); //initial tree
          for (ProductGroup pg : activeShop.getTopLevelProductGroups()) {
            CatalogCache.get().put(pg);
          }
        }
      });
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
            if (result.isEmpty()) {
              view.getTree().setTreeItemAsEmpty(parent);
            }
            for (ProductGroup productGroup : result) {
              final PropertyValue value = Util.getPropertyValueByName(productGroup.getPropertyValues(), Util.NAME, currentLanguage);

              if (CatalogCache.get().getProductGroupName() == null && value != null && productGroup.getId().equals(value.getProperty().getItem().getId())) {
                CatalogCache.get().setProductGroupName(productGroup);
                CatalogCache.get().setProductGroupProduct(productGroup);
              }
              CatalogCache.get().put(productGroup);
              for (ProductGroup pPG : productGroup.getParents()) {
                CatalogCache.get().put(pPG);
              }
              treemap.put(
                  view.getTree().addItem(parent, value != null ? value.getStringValue() : "<No Name>"), productGroup.getId());
            }
          }
        });
  }
}
