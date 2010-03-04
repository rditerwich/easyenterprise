package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.CatalogView;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;

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
  private ProductGroupPropertiesPresenter pgpp;
  private CatalogView catalogView;
  private final ArrayList<ProductGroupValuesPresenter> valuesPresenters = new ArrayList<ProductGroupValuesPresenter>();

  public ProductGroupPresenter() {
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem item = event.getSelectedItem();

        //FIXME: handle nodes that have no children in the database anyway
        if (item.getChildCount() == 0) {
          loadChildren(catalogView, item);
        }
        final ProductGroup pg = treemap.get(event.getSelectedItem());

        if (pg != null) {
          final PropertyValue name = Util.getPropertyValueByName(pg.getPropertyValues(),Util.NAME, null);

          view.getName().setText(name==null?"":name.getStringValue());
          pgpp.show(pg.getPropertyValues());
          view.getParentPropertiesPanel().clear();
          final Iterator<ProductGroupValuesPresenter> iterator = valuesPresenters.iterator();

          walkParents(iterator, pg);
        }
      }

      private void walkParents(Iterator<ProductGroupValuesPresenter> iterator, ProductGroup pg) {
        if (pg == null || !parentMap.containsKey(pg.getId())) return;

        for (ProductGroup parent : parentMap.get(pg.getId())) {
          if (parent == null) continue;
          walkParents(iterator, parent);
          ProductGroupValuesPresenter presenter;

          if (iterator.hasNext()) {
            presenter = iterator.next();
          } else {
            presenter = new ProductGroupValuesPresenter();
            valuesPresenters.add(presenter);
          }
          view.getParentPropertiesPanel().add(presenter.getView().getViewWidget());
          //FIXME: this should be a map of parent props to child values 
          presenter.show(parent.getPropertyValues());
        }        
      }
    });
    view.getNewButtonClickHandler().addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            currentProductGroup = new ProductGroup();
            view.setProductGroup(currentProductGroup);
          }});
    pgpp = new ProductGroupPropertiesPresenter(); 
    view.setPropertiesPanel(pgpp.getView().getViewWidget());
    
    Util.getCatalogView(new AsyncCallback<CatalogView>(){
      @Override
      public void onFailure(Throwable caught) {
      }

      @Override
      public void onSuccess(CatalogView result) {
        catalogView = result;
        loadChildren(result, null); //initial tree
      }});
  }

  @Override
  public ProductGroupView getView() {
    return view;
  }

  private void loadChildren(CatalogView catalogView, final TreeItem parent) {
    final ProductGroup parentPG = treemap.get(parent);
    CatalogServiceAsync.findAllProductGroupChildren(
        catalogView, parentPG, new AsyncCallback<List<ProductGroup>>() {
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
                if (pr.equals(parentPG)) {
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
