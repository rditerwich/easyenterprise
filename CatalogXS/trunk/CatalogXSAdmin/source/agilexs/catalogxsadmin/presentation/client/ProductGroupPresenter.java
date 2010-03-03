package agilexs.catalogxsadmin.presentation.client;

import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.CatalogView;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
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
  private ProductGroup currentProductGroup;
  private ProductGroupPropertiesPresenter pgpp;
  private CatalogView catalogView;
  
  public ProductGroupPresenter() {
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem item = event.getSelectedItem();

        //FIXME: handle nodes that have no children in the database anyway
        if (item.getChildCount() == 0) {
          loadChildren(catalogView, item);
        }
      }
    });
    view.getTree().addSelectionHandler(new SelectionHandler<TreeItem>() {
      @Override
      public void onSelection(SelectionEvent<TreeItem> event) {
        final ProductGroup pg = treemap.get(event.getSelectedItem());

        if (pg != null) {
          loadGrid(pg);
        }
      }});
    view.getNewButtonClickHandler().addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            currentProductGroup = new ProductGroup();
            view.setProductGroup(currentProductGroup);
          }});
    pgpp = new ProductGroupPropertiesPresenter(); 
    view.getPropertiesPanel().add(pgpp.getView().getViewWidget());
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
    CatalogServiceAsync.findAllProductGroupChildren(
        catalogView, treemap.get(parent), new AsyncCallback<List<ProductGroup>>() {
          @Override
          public void onFailure(Throwable caught) {
            //FIXME implement handling failure
          }

          @Override
          public void onSuccess(List<ProductGroup> result) {
            for (ProductGroup productGroup : result) {
              final PropertyValue value = Util.getPropertyValueByName(productGroup.getPropertyValues(), Util.NAME, null);

              treemap.put(
                  view.addTreeItem(parent, value != null ? value.getStringValue() : "<No Name>"), productGroup);
            }
          }
        });
  }

  private void loadGrid(final ProductGroup pg) {
    CatalogServiceAsync.findProductGroupById(pg.getId(),
        new AsyncCallback<ProductGroup>() {
          @Override
          public void onFailure(Throwable caught) {
            //FIXME implement handling failure
          }

          @Override
          public void onSuccess(ProductGroup pg) {
            //view.setProductGroup(pg);
            final List<Property> properties = pg.getProperties();
            pgpp.show("TODO:Name", properties);
          }
    });
  }
}
