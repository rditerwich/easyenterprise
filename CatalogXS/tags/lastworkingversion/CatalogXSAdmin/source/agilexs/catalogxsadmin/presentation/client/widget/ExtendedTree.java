package agilexs.catalogxsadmin.presentation.client.widget;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Extended Tree widget which shows the text 'Loading...' in case a tree node is
 * opened that was not yet loaded. The text will be replaced if content is
 * loaded or removed if no children present.
 * 
 * It depends on the specific feature of Tree that when a widget is added as
 * content label this is stored internally in a separate variable. In this case
 * this the status of this variable is used to determine if children are already
 * loaded or not. 
 */
public class ExtendedTree extends Tree {

  public ExtendedTree() {
    addItem(new InlineLabel("Loading..."));
  }

  public TreeItem addItem(TreeItem parent, String text) {
    final TreeItem item = new TreeItem(text);

    if (isTreeItemEmpty(parent)) {
      setTreeItemAsEmpty(parent);
    }
    if (parent == null) {
      addItem(item);
    } else {
      parent.addItem(item);
    }
    //add dummy to always show +/- icons...
    item.addItem(new InlineLabel("Loading..."));
    return item;
  }

  public void deSelectItem() {
    super.setSelectedItem(null);    
  }

  public TreeItem getItem(TreeItem parent, int index) {
    return parent == null ? getItem(index) : parent.getChild(index);
  }

  public int getItemCount(TreeItem item) {
    return item == null ? getItemCount() : item.getChildCount();
  }
  
  public boolean isTreeItemEmpty(TreeItem item) {
    if (item == null) {
      return getItem(0) != null && getItem(0).getWidget() != null;  
    } else {
      return item.getChild(0) != null && item.getChild(0).getWidget() != null;
    }
  }

  public void removeItem(TreeItem parent, TreeItem item) {
    if (parent == null) {
      removeItem(item);
    } else {
      parent.removeItem(item);
    }
  }

  public void setTreeItemAsEmpty(TreeItem item) {
    if (item == null) {
      if (getItemCount() == 1) {
        removeItem(getItem(0));
      }
    } else {
      if (item.getChildCount() == 1) {
        item.removeItem(item.getChild(0));
      }
    }
  }
}
