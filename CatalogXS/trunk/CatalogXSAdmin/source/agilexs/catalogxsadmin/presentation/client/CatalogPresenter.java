package agilexs.catalogxsadmin.presentation.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

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

public abstract class CatalogPresenter<V extends CatalogView> implements Presenter<V> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  protected final V view;
  protected final HashMap<TreeItem, Long> treemap = new HashMap<TreeItem, Long>();

  protected Shop activeShop;
  protected Category currentCategory;
  protected String currentLanguage = "en";

  public CatalogPresenter(V cview) {
    this.view = cview;
    view.getPublishButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        CatalogServiceAsync.publish(new AsyncCallback<String>(){
          @Override public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
          }

          @Override public void onSuccess(String result) {
            StatusMessage.get().show(i18n.publishSucess());
          }});
    }});
    view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
      @Override
      public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem item = event.getTarget();

        if (item.getState()) {
          final Long pgId = treemap.get(item);

          if (pgId != null) {
            loadChildren(activeShop, item);
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
              CatalogCache.get().getCategoryName(pgId, currentLanguage);

          view.setName(name==null ? "" : name.getValue());
          currentCategory = CatalogCache.get().getCategory(pgId);
          show(currentCategory);
          loadChildren(activeShop, item);
        } else {
          //FIXME ?? can this happen?
        }
      }

    });
    view.getLanguageChangeHandler().addChangeHandler(new ChangeHandler(){
      @Override
      public void onChange(ChangeEvent event) {
        currentLanguage = view.getSelectedLanguage();
        switchLanguage(currentLanguage);
      }});
    CatalogCache.get().getShop(1L, new AsyncCallback<Shop>() {
      @Override public void onFailure(Throwable caught) {
      }

      @Override public void onSuccess(Shop result) {
        activeShop = result;
        view.setLanguages(CatalogCache.get().getActiveCatalog().getLanguages(), "en");
        CatalogCache.get().loadCategoryNames(new AsyncCallback<String>() {
          @Override public void onFailure(Throwable caught) {
          }

          @Override public void onSuccess(String result) {
            loadChildren(activeShop, null); //initial tree
            for (Category pg : Util.categories(activeShop.getNavigation())) {
              CatalogCache.get().put(pg);
            }
          }
      });
    }});
  }
  
  @Override
  public V getView() {
    return view;
  }

  public void show() {
    view.setLanguages(CatalogCache.get().getActiveCatalog().getLanguages(), "en");
  }

  protected abstract void show(Category currentCategory);

  protected abstract void switchLanguage(String newLang);

  protected void loadChildren(Shop shop, final TreeItem parent) {
    if (!view.getTree().isTreeItemEmpty(parent)) {
      return;
    }
    final Category parentPG = CatalogCache.get().getCategory(treemap.get(parent));

    CatalogServiceAsync.findAllCategoryChildren(
        shop, parentPG, new AsyncCallback<List<Category>>() {
          @Override
          public void onFailure(Throwable caught) {
            //StatusMessage.get().show(caught.getMessage(), 30);
          }

          @Override
          public void onSuccess(List<Category> result) {
            if (result.isEmpty()) {
              view.getTree().setTreeItemAsEmpty(parent);
            }
            for (Category productGroup : result) {
              final PropertyValue value = Util.getPropertyValueByName(productGroup.getPropertyValues(), Util.NAME, currentLanguage);

              if (CatalogCache.get().getCategoryName() == null && value != null && productGroup.getId().equals(value.getProperty().getItem().getId())) {
                CatalogCache.get().setCategoryName(productGroup);
                CatalogCache.get().setCategoryProduct(productGroup);
              }
              CatalogCache.get().put(productGroup);
              for (Item pPG : productGroup.getParents()) {
                CatalogCache.get().put(pPG);
              }
              boolean present = false;
              for (int i = 0; i < view.getTree().getItemCount(parent); i++) {
                final TreeItem item = view.getTree().getItem(parent, i);

                if (treemap.containsKey(item)
                    && productGroup.getId().equals(treemap.get(item))) {
                  present = true;
                }
              }
              if (!present) {
                treemap.put(view.getTree().addItem(parent,
                    value != null ? value.getStringValue() : "<No Name>"),
                    productGroup.getId());
              }
            }
          }
        });
  }
}
