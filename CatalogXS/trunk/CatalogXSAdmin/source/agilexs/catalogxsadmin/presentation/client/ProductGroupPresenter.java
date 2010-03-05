package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.CatalogView;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroupBinding;
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
  private final ProductGroupBinding pgBinding = new ProductGroupBinding();
  private ProductGroup orgProductGroup;

  public ProductGroupPresenter() {
    view.getNewButtonClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        orgProductGroup = null;
        currentProductGroup = new ProductGroup();
        view.getName().setText("");
        pgpp.show(currentProductGroup.getPropertyValues());
        view.getParentPropertiesPanel().clear();
        view.getTree().setSelectedItem(null);
      }});
    view.getSaveButtonClickHandler().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
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
          loadChildren(catalogView, item);
        }
        currentProductGroup = treemap.get(event.getSelectedItem());
        orgProductGroup = currentProductGroup.clone(new HashMap());  

        if (currentProductGroup != null) {
          final PropertyValue name = Util.getPropertyValueByName(currentProductGroup.getPropertyValues(),Util.NAME, null);

          view.getName().setText(name==null?"":name.getStringValue());
          pgpp.show(Util.getProductGroupPropertyValues(currentProductGroup, currentProductGroup.getPropertyValues()));
          view.getParentPropertiesPanel().clear();
          walkParents(valuesPresenters.iterator(), currentProductGroup);
        }
      }

      private void walkParents(Iterator<ProductGroupValuesPresenter> iterator, ProductGroup pg) {
        if (pg == null || !parentMap.containsKey(pg.getId())) return;

        for (ProductGroup parent : parentMap.get(pg.getId())) {
          if (parent == null) continue;
          walkParents(iterator, parent);
          final List<PropertyValue> pv = Util.getProductGroupPropertyValues(parent, pg.getPropertyValues());
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
            presenter.show(Util.getLabel(parent.getPropertyValues(),Util.NAME, null).getLabel(), pv);
          }
        }        
      }
    });
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
