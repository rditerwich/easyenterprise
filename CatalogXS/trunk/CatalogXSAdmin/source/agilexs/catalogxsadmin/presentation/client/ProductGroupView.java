package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroupBinding;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  FlowPanel panel = new FlowPanel();
  final Tree tree = new Tree();
  final Grid grid = new Grid();
  final Label name = new Label();
  final ProductGroupBinding pgBinding = new ProductGroupBinding();
  final Button button = new Button("Add new product group");
  
  public ProductGroupView() {
    initWidget(panel);
    final SplitLayoutPanel slp = new SplitLayoutPanel();

    panel.add(slp);
    slp.addWest(tree, 300);
    final FlowPanel detailPanel = new FlowPanel();

    detailPanel.add(button);
    detailPanel.add(name);
    detailPanel.add(grid);
    slp.add(detailPanel);

    //Bindings
    HasTextBinding.bind(name, pgBinding.name());
  }

  public Tree getTree() {
    return tree;
  }

  public Grid getGrid() {
    return grid;
  }

  @Override
  public Widget getViewWidget() {
    return this;
  }

  public void setProductGroup(ProductGroup pg) {
    pgBinding.setData(pg);
  }

  HasClickHandlers getNewButtonClickHandler() {
    return button;
  }
  
  public TreeItem addTreeItem(TreeItem parent, String text) {
    final TreeItem item = new TreeItem(text);

    if (parent == null) {
      tree.addItem(item);
    } else {
      parent.addItem(item);
    }
    return item;
  }
}
