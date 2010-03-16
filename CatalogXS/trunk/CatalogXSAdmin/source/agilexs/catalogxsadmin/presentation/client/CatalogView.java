package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CatalogView extends Composite implements View {

  final SplitLayoutPanel panel = new SplitLayoutPanel();
  final Tree tree = new Tree();
  final TabLayoutPanel tp = new TabLayoutPanel(40, Unit.PX);
  final Button newProductGroupButton = new Button("New Product Group");
  final Button newProductButton = new Button("New Product");
  final Button saveButton = new Button("Save changes");
  final ListBox languageList = new ListBox();

  public CatalogView() {
    initWidget(panel);
    panel.addWest(tree, 300);
    final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
    panel.add(detailPanel);

    final FlowPanel buttonBar = new FlowPanel();
    buttonBar.add(newProductGroupButton);
    buttonBar.add(newProductButton);
    buttonBar.add(languageList);
    languageList.getElement().getStyle().setMarginLeft(40, Unit.PX);
    //TODO auto enable save button on changes: saveButton.setEnabled(false);
    buttonBar.add(saveButton);
    detailPanel.addNorth(buttonBar, 40);
    detailPanel.add(tp);

  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public void addTab(View view, String text) {
    tp.add(view.asWidget(), text);
  }

  public HasChangeHandlers getLanguageChangeHandler() {
    return languageList;
  }

  public HasClickHandlers getNewProductGroupButtonClickHandler() {
    return newProductGroupButton;
  }

  public HasClickHandlers getNewProductButtonClickHandler() {
    return newProductButton;
  }

  public int getSelectedTab() {
    return tp.getSelectedIndex();
  }

  public HasClickHandlers getSaveButtonClickHandler() {
    return saveButton;
  }

  public String getSelectedLanguage() {
    return languageList.getValue(languageList.getSelectedIndex());
  }

  public Tree getTree() {
    return tree;
  }

  public boolean isTreeItemEmpty(TreeItem item) {
    return item != null && item.getChild(0) != null && item.getChild(0).getWidget() != null;
  }

  public void setTreeItemAsEmpty(TreeItem item) {
    if (item.getChildCount() == 1) {
      item.removeItem(item.getChild(0));
    }
  }

  public TreeItem addTreeItem(TreeItem parent, String text) {
    final TreeItem item = new TreeItem(text);
    if (parent == null) {
      tree.addItem(item);
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

  /**
   * Sets the languages on the Language ListBox
   * @param languages
   * @param selected
   */
  public void setLanguages(List<List<String>> languages, String selected) {
    for (List<String> lang : languages) {
      languageList.addItem(lang.get(1), lang.get(0));
      if (lang.get(0).equals(selected)) {
        languageList.setItemSelected(languageList.getItemCount()-1, true);
      }
    }
  }

  public void addTabSelectionHandler(SelectionHandler<Integer> selectionHandler) {
    tp.addSelectionHandler(selectionHandler);
  }

  public void selectedTab(int i) {
    tp.selectTab(i);
  }
}