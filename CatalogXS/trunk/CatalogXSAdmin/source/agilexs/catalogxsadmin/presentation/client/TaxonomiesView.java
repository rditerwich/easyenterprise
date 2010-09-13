package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class TaxonomiesView extends CatalogView {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final TabLayoutPanel tp = new TabLayoutPanel(40, Unit.PX);
  private final Button newCategoryButton = new Button(i18n.newGroup());

  public TaxonomiesView() {
    super();
    topPanel.add(newCategoryButton);
    topPanel.add(languageList);
    topPanel.add(publishButton);
    topPanel.add(name);
    detailPanel.add(tp);
  }

  public HasClickHandlers getNewCategoryButtonClickHandler() {
    return newCategoryButton;
  }

  public void addTab(View view, String text) {
    tp.add(view.asWidget(), text);
  }

  public void addTabSelectionHandler(SelectionHandler<Integer> selectionHandler) {
    tp.addSelectionHandler(selectionHandler);
  }
  
  public int getSelectedTab() {
    return tp.getSelectedIndex();
  }
  
  public void selectedTab(int i) {
    tp.selectTab(i);
  }
  
  public void setTabVisible(int index, boolean visible) {
    tp.getTabWidget(index).getParent().setVisible(visible);
  }
}
