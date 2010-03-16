package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ItemParentsPresenter implements Presenter<ItemParentsView> {

  private ItemParentsView view;
  private List<Long> currentParents = new ArrayList<Long>();
  private List<Long> allPossibleParents = new ArrayList<Long>();
  private String currentLang;

  public ItemParentsPresenter(final ItemParentsView view) {
    this.view = view;
    view.buttonAddParentHasClickHandlers().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final int i = view.getSelectedNewParent();
        final Long newPG = allPossibleParents.get(i);

        currentParents.add(newPG);
        allPossibleParents.remove(newPG);
        show(currentLang);
      }});
    view.setDeleteHandler(new DeleteHandler(){
      @Override
      public void onDelete(int index) {
        final Long removed = currentParents.remove(index);

        if (removed != null) {
          allPossibleParents.add(removed);
        }
        show(currentLang);
      }
    });
  }

  public List<Long> getValues() {
    return currentParents;
  }

  public void show(ProductGroup productGroup, String lang, Collection<ProductGroup> allParents) {
    currentParents.clear();
    for (ProductGroup pg : productGroup.getParents()) {
      currentParents.add(pg.getId());
    }
    allPossibleParents.clear();
    for (ProductGroup pg : allParents) {
      allPossibleParents.add(pg.getId());
    }
    allPossibleParents.remove(productGroup.getId());
    for (Long pg : currentParents) {
      allPossibleParents.remove(pg);
    }
    show(lang);
  }

  private void show(String lang) {
    currentLang = lang;
    view.clearParentTable();
    for (Long pg : currentParents) {
      view.addParentToList(Util.getPropertyValueByName(CatalogCache.get().getProductGroup(pg).getPropertyValues(), Util.NAME, currentLang).getStringValue());
    }
    view.getAllParentsListBox().clear();
    for (Long pp : allPossibleParents) {
      view.getAllParentsListBox().addItem(Util.getPropertyValueByName(CatalogCache.get().getProductGroup(pp).getPropertyValues(), Util.NAME, currentLang).getStringValue());
    }
  }

  @Override
  public ItemParentsView getView() {
    return view;
  }
}
