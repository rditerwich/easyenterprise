package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;

public class NavigationPresenter implements Presenter<NavigationView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final HashMap<TreeItem, Long> treemap = new HashMap<TreeItem, Long>();

  private final NavigationView view;
  private final ItemParentsPresenter pp;
  private String currentLanguage = "en";
  private List<Map.Entry<Long, String>> topLevelCategorys = new ArrayList<Map.Entry<Long, String>>();
  private Shop activeShop;

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
            if (view.getTree().isTreeItemEmpty(item)) {
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
      public void onAdd(Long pid) {
        final TreeItem ti = findTreeItem(pid);

        if (ti == null) {
          final Map.Entry<Long, String> productGroupEntry = CatalogCache.get().getCategoryName(pid, currentLanguage);

          addTreeItem(null, productGroupEntry);
          topLevelCategorys.add(productGroupEntry);
        }
      }
    });
    pp.setDeleteHandler(new DeleteHandler<Long>() {
      @Override
      public void onDelete(Long pid) {
        final TreeItem ti = findTreeItem(pid);

        if (ti != null) {
          treemap.remove(ti);
          view.getTree().removeItem(ti);
        }
        topLevelCategorys.remove(CatalogCache.get().getCategoryName(pid, currentLanguage));
      }
    });
    view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final Shop oldShop = activeShop.clone(new HashMap());
        final List<Category> p = new ArrayList<Category>();

        for (Entry<Long, String> pge : topLevelCategorys) {
          Category pg = CatalogCache.get().getCategory(pge.getKey());
          if (pg == null) {
            pg = new Category();
            pg.setId(pge.getKey());
          }
          p.add(pg);
        }
        // 	TODO Change this:
//        activeShop.setTopLevelCategorys(p);

        ShopServiceAsync.updateShop(oldShop, activeShop, new AsyncCallback<Shop>() {
          @Override public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
          }

          @Override public void onSuccess(Shop result) {
            CatalogCache.get().put(result);
            StatusMessage.get().show(i18n.navigationSaved());
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
    topLevelCategorys.clear();
    for (Category pg : Util.categories(activeShop.getNavigation())) {
      topLevelCategorys.add(CatalogCache.get().getCategoryName(pg.getId(), currentLanguage));
    }
    pp.show(null, topLevelCategorys, currentLanguage, CatalogCache.get().getCategoryNamesByLang(currentLanguage));
    for (Category topLPG : Util.categories(activeShop.getNavigation())) {
      final TreeItem ti = findTreeItem(topLPG.getId());

      if (ti == null) {
        addTreeItem(null, CatalogCache.get().getCategoryName(topLPG.getId(), currentLanguage));
      }
    }
  }

  private void addTreeItem(TreeItem parent, Map.Entry<Long, String> productGroup) {
    treemap.put(view.getTree().addItem(parent, productGroup.getValue()), productGroup.getKey());
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
    final Category parentPG = CatalogCache.get().getCategory(treemap.get(parent));

    CatalogServiceAsync.findAllCategoryChildren(
        shop, parentPG, new AsyncCallback<List<Category>>() {
          @Override
          public void onFailure(Throwable caught) {
            //StatusMessage.get().show(caught.getMessage(), 30);
          }

          @Override
          public void onSuccess(List<Category> result) {
            if (result.size() == 0) {
              view.getTree().setTreeItemAsEmpty(parent);
            }
            for (Category productGroup : result) {
              CatalogCache.get().put(productGroup);
              for (Item pPG : productGroup.getParents()) {
                CatalogCache.get().put(pPG);
              }
//              CatalogCache.get().putParent(productGroup, parentPG);
              addTreeItem(parent, CatalogCache.get().getCategoryName(productGroup.getId(), currentLanguage));
            }
          }
        });
  }
}
