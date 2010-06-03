package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

public class TaxonomiesPresenter extends CatalogPresenter<TaxonomiesView> {

    private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

    private final int TAB_PRODUCT = 0;
    private final int TAB_GROUP = 1;

    final ProductGroupPresenter pgp = new ProductGroupPresenter();
    final ProductPresenter pp = new ProductPresenter(true);

    public TaxonomiesPresenter() {
      super(new TaxonomiesView());
      view.addTab(pp.getView(), i18n.products());
      view.addTab(pgp.getView(), i18n.group());

      view.getNewProductGroupButtonClickHandler().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          final Long lastPG = treemap.get(view.getTree().getSelectedItem());
          
          view.setName("&lt;" + i18n.newGroup() + "&gt;");
          currentProductGroup =
            pgp.setNewProductGroup(activeShop, lastPG != null ? CatalogCache.get().getProductGroup(lastPG) : null);
          pp.show(activeShop, currentProductGroup);

          //view.getTree().deSelectItem();
          view.selectedTab(TAB_GROUP);
        }});
      view.addTabSelectionHandler(new SelectionHandler<Integer>() {
        @Override
        public void onSelection(SelectionEvent<Integer> event) {
          if (event.getSelectedItem().intValue() == TAB_GROUP) {
            pgp.show(currentProductGroup);
          } else {
            pp.show(activeShop, currentProductGroup);
          }
        }});
    }

    protected void show(ProductGroup currentProductGroup){
      if (Boolean.FALSE.equals(currentProductGroup.getContainsProducts())) {
        view.setTabVisible(TAB_PRODUCT, false);
        view.selectedTab(TAB_GROUP);
      } else {
        view.setTabVisible(TAB_PRODUCT, true);
      }
      if (view.getSelectedTab() == TAB_GROUP) {
        pgp.show(currentProductGroup);
      } else {
        pp.show(activeShop, currentProductGroup);
      }
    }

    protected void switchLanguage(String newLang) {
      pgp.switchLanguage(currentLanguage);
      pp.switchLanguage(currentLanguage);
    }
}
