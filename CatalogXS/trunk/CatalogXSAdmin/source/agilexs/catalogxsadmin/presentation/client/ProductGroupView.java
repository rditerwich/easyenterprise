package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroupBinding;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductGroupView extends Composite implements View {

  final SplitLayoutPanel panel = new SplitLayoutPanel();
  final Tree tree = new Tree();
  final FlowPanel allPropertiesPanel = new FlowPanel();
  final TextBox name = new TextBox();
  final ProductGroupBinding pgBinding = new ProductGroupBinding();
  final Button newButton = new Button("Add new product group");
  final Button saveButton = new Button("Save changes");
  final ListBox languageList = new ListBox();
  final SimplePanel propertiesPanel = new SimplePanel();
  final FlowPanel parentPropertiesPanel = new FlowPanel();

  public ProductGroupView() {
    initWidget(panel);

    panel.addWest(tree, 300);
    final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
    panel.add(detailPanel);
    //top 
    final VerticalPanel topBar = new VerticalPanel();
    final FlowPanel buttonBar = new FlowPanel();
    buttonBar.add(newButton);
    buttonBar.add(languageList);
    buttonBar.add(saveButton);
    //TODO auto enable save button on changes: saveButton.setEnabled(false);
    //TODO build language listbox items dynamic instead of static 
    languageList.getElement().getStyle().setMarginLeft(40, Unit.PX);

    topBar.add(buttonBar);
    topBar.add(name);
    detailPanel.addNorth(topBar, 60);
    //top
    final ScrollPanel sp = new ScrollPanel(allPropertiesPanel);
    allPropertiesPanel.add(propertiesPanel);
    //allPropertiesPanel.add(new HTML("<h3>Relations</h3>"));
    //allPropertiesPanel..add(relations);
    allPropertiesPanel.add(new HTML("<h3>Inherited Properties</h3>"));
    allPropertiesPanel.add(parentPropertiesPanel);
    sp.getElement().getStyle().setPadding(10, Unit.PX);
    detailPanel.add(sp);
  }

  public Tree getTree() {
    return tree;
  }

  public void setPropertiesPanel(Widget w) {
    propertiesPanel.setWidget(w);
  }

  public FlowPanel getParentPropertiesPanel() {
    return parentPropertiesPanel;
  }

  public HasText getName() {
    return name;
  }

  public HasClickHandlers getNewButtonClickHandler() {
    return newButton;
  }

  public HasClickHandlers getSaveButtonClickHandler() {
    return saveButton;
  }

  @Override
  public Widget getViewWidget() {
    return this;
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
  
  public HasChangeHandlers getLanguageChangeHandler() {
    return languageList;
  }

  public String getSelectedLanguage() {
    return languageList.getValue(languageList.getSelectedIndex());
  }
  
  public void setProductGroup(ProductGroup pg) {
    pgBinding.setData(pg);
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
