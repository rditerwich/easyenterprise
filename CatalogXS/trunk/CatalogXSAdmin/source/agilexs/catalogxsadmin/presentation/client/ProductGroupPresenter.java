package agilexs.catalogxsadmin.presentation.client;

import java.util.HashMap;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.CatalogView;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Presenter for the ProductGroup page
 */
public class ProductGroupPresenter implements Presenter<ProductGroupView> {

  private final ProductGroupView view = new ProductGroupView();
  private CatalogView catalogView; //FIXME handle catalog view
  private final HashMap<TreeItem, ProductGroup> treemap = new HashMap<TreeItem, ProductGroup>();

  public ProductGroupPresenter() {
    view.getTree().addOpenHandler(new OpenHandler<TreeItem>() {
      @Override
      public void onOpen(OpenEvent<TreeItem> event) {
        final TreeItem item = event.getTarget();

        //FIXME: handle nodes that have no children in the database anyway
        if (item.getChildCount() == 0) {
          loadChildren(item);
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
            
          }});
  }

  @Override
  public ProductGroupView getView() {
    return view;
  }

  private void loadChildren(final TreeItem parent) {
    CatalogServiceAsync.findAllProductGroupChildren(
        catalogView, treemap.get(parent), new AsyncCallback<List<ProductGroup>>() {
          @Override
          public void onFailure(Throwable caught) {
            //FIXME implement handling failure
          }

          @Override
          public void onSuccess(List<ProductGroup> result) {
            for (ProductGroup productGroup : result) {
              treemap.put(
                  view.addTreeItem(parent, productGroup.getName()), productGroup);
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
            view.setProductGroup(pg);
            final List<PropertyValue> result = pg.getDefaultValues();
            final Grid g = view.getGrid();

            if (g.getRowCount() != result.size()) {
              g.resizeRows(result.size());
            }
            //FIXME set header grid
            for (int i = 0; i < result.size(); i++) {
              PropertyValue propertyValue = result.get(i);
              g.setWidget(i, 0, new InlineHTML(propertyValue.getProperty().getName()));
            //  g.setWidget(i, 1, new InlineHTML(propertyValue.getDefaultValue());
              g.setWidget(i, 2, new InlineHTML());
            }
          }
    });
  }
}
