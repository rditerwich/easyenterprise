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

  public TreeItem addItem(TreeItem parent, String text) {
    final TreeItem item = new TreeItem(text);
    if (parent == null) {
      addItem(item);
    } else {
      if (isTreeItemEmpty(parent)) {
        setTreeItemAsEmpty(parent);
      }
      parent.addItem(item);
    }
    //add dummy to always show +/- icons...
    item.addItem(new InlineLabel("Loading..."));
    return item;
  }

  public void deSelectItem() {
    super.setSelectedItem(null);    
  }
  
  public boolean isTreeItemEmpty(TreeItem item) {
    return item != null && item.getChild(0) != null && item.getChild(0).getWidget() != null;
  }

  public void setTreeItemAsEmpty(TreeItem item) {
    if (item.getChildCount() == 1) {
      item.removeItem(item.getChild(0));
    }
  }
}
