package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public class NavigationPresenter implements Presenter<NavigationView> {
//TODO add save button
//TODO add add handler
//TODO add delete handler
//TODO show tree
  private final HashMap<TreeItem, Long> treemap = new HashMap<TreeItem, Long>();

  private final NavigationView view;
  private final ItemParentsPresenter pp;
  private String currentLanguage = "en";
  private List<ProductGroup> topLevelProductGroups = new ArrayList<ProductGroup>();
  private Shop activeShop;

  //private ProductGroup currentProductGroup;

  public NavigationPresenter() {
    view = new NavigationView();
    pp = new ItemParentsPresenter(view.getItemParentsView());
    view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
      @Override
      public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem item = event.getTarget();

        if (item.getState()) {
          final Long pgId = treemap.get(item);
          
          if (pgId != null) {
            //currentProductGroup = CatalogCache.get().getProductGroup(pgId);
            if (view.getTree().isTreeItemEmpty(item)) { //FIXME: && Boolean.FALSE.equals(currentProductGroup.getContainsProducts())) {
              loadChildren(activeShop, item);
            }
          } else {
            //FIXME ?? can this happen?
          }
        }
      }});
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
      }
    });
    pp.setAddHandler(new AddHandler<Long>() {
      @Override
      public void onAdd(Long data) {
        final TreeItem ti = findTreeItem(data);

        if (ti == null) {
          final ProductGroup pg = CatalogCache.get().getProductGroup(data);
          
          addTreeItem(null, pg);
          topLevelProductGroups.add(pg);
        }
      }
    });
    pp.setDeleteHandler(new DeleteHandler<Long>() {
      @Override
      public void onDelete(Long data) {
        final TreeItem ti = findTreeItem(data);

        if (ti != null) {
          treemap.remove(ti);
          view.getTree().removeItem(ti);
        }
        topLevelProductGroups.remove(CatalogCache.get().getProductGroup(data));
      }
    });
    view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final Shop oldShop = activeShop.clone(new HashMap());
        activeShop.setTopLevelProductGroups(topLevelProductGroups);
        
        ShopServiceAsync.updateShop(oldShop, activeShop, new AsyncCallback<Shop>() {
          @Override public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
          }

          @Override public void onSuccess(Shop result) {
            StatusMessage.get().show("New Navigation hierarchy saved.", 15);
          }});
    }});
  }

  @Override
  public NavigationView getView() {
    return view;
  }

  public void show() {
    show(currentLanguage);
  }

  public void show(String lang) {
    currentLanguage  = lang;
    activeShop = CatalogCache.get().getShop(1L);
    topLevelProductGroups.clear();
    topLevelProductGroups.addAll(activeShop.getTopLevelProductGroups());
    pp.show(null, topLevelProductGroups, currentLanguage, CatalogCache.get().getAllProductGroups());
    for (ProductGroup topLPG : activeShop.getTopLevelProductGroups()) {
      final TreeItem ti = findTreeItem(topLPG.getId());

      if (ti == null) {
        addTreeItem(null, topLPG);
      }
    }
  }

  private void addTreeItem(TreeItem parent, ProductGroup pg) {
    final PropertyValue value = Util.getPropertyValueByName(pg.getPropertyValues(), Util.NAME, null);

    treemap.put(view.getTree().addItem(parent, value != null ? value.getStringValue() : "<No Name>"), pg.getId());
  }

  private TreeItem findTreeItem(Long pgId) {
    TreeItem foundTI = null;
    for (int i = 0; i < view.getTree().getItemCount(); i++) {
      final TreeItem ti = view.getTree().getItem(i);

      if (pgId.equals(treemap.get(ti))) {
        foundTI = ti;
        break;
      }
    }
    return foundTI;
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
              CatalogCache.get().put(productGroup);
              for (ProductGroup pPG : productGroup.getParents()) {
                CatalogCache.get().put(pPG);
              }
//              CatalogCache.get().putParent(productGroup, parentPG);
              addTreeItem(parent, productGroup);
            }
          }
        });
  }
}
