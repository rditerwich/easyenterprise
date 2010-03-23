package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.widget.ExtendedTree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  final SplitLayoutPanel panel = new SplitLayoutPanel();
  final ExtendedTree tree = new ExtendedTree();
  final ItemParentsView top = new ItemParentsView();
  final Button saveButton = new Button(i18n.saveChanges());
  
  public NavigationView() {
    initWidget(panel);
    final DockLayoutPanel topLevelPGPanel = new DockLayoutPanel(Unit.PX);
    
    topLevelPGPanel.addNorth(new Label(i18n.explainNavigationList()), 70);
    final FlowPanel fp = new FlowPanel();
    fp.add(top.asWidget());
    fp.add(saveButton);
    topLevelPGPanel.add(fp);
    panel.addWest(tree, 300);
    panel.add(topLevelPGPanel);
  }

  ItemParentsView getItemParentsView() {
    return top;
  }

  @Override
  public Widget asWidget() {
    return this;
  }
  
  public ExtendedTree getTree() {
    return tree;
  }

  public HasClickHandlers getSaveButtonClickHandler() {
    return saveButton;
  }
}
