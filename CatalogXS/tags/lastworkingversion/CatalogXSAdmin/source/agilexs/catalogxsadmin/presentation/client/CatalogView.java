package agilexs.catalogxsadmin.presentation.client;

import java.util.List;

import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.widget.ExtendedTree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CatalogView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  protected final DockLayoutPanel detailPanel = new DockLayoutPanel(Unit.PX);
  protected final FlowPanel topPanel = new FlowPanel();
  protected final Button publishButton = new Button(i18n.publish());
  protected final ListBox languageList = new ListBox();
  protected final HTML name = new HTML();

  private final SplitLayoutPanel panel = new SplitLayoutPanel();
  private final ExtendedTree tree = new ExtendedTree();
  
  public CatalogView() {
    initWidget(panel);
    panel.addWest(tree, 300);
    panel.add(detailPanel);
    detailPanel.addNorth(topPanel, 75);
    publishButton.setTitle(i18n.explainPublish());
    publishButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
    languageList.getElement().getStyle().setMarginLeft(40, Unit.PX);
    languageList.addStyleName("languageField");
    topPanel.getElement().getStyle().setMargin(10, Unit.PX);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public HasChangeHandlers getLanguageChangeHandler() {
    return languageList;
  }

  public HasClickHandlers getPublishButtonClickHandler() {
    return publishButton;
  }

  public String getSelectedLanguage() {
    return languageList.getValue(languageList.getSelectedIndex());
  }

  public ExtendedTree getTree() {
    return tree;
  }

  /**
   * Sets the languages on the Language ListBox
   * @param languages
   * @param selected
   */
  public void setLanguages(List<Language> languages, String selected) {
    languageList.clear();
    for (Language lang : languages) {
      languageList.addItem(lang.getDisplayName(), lang.getName());
      if (lang.getName().equals(selected)) {
        languageList.setItemSelected(languageList.getItemCount()-1, true);
      }
    }
  }

  public void setName(String name) {
    this.name.setHTML(i18n.h2(name));
  }
}
