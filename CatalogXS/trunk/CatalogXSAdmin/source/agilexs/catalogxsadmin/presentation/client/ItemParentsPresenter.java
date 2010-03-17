package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
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
  private DeleteHandler<Long> deleteHandler;
  private AddHandler<Long> addHandler;

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
        if (addHandler != null) {
          addHandler.onAdd(newPG);
        }
      }});
    view.setDeleteHandler(new DeleteHandler<Integer>(){
      @Override
      public void onDelete(Integer index) {
        final Long removed = currentParents.remove(index.intValue());

        if (removed != null) {
          allPossibleParents.add(removed);
        }
        show(currentLang);
        if (deleteHandler != null) {
          deleteHandler.onDelete(removed);
        }
      }
    });
  }

  public void setAddHandler(AddHandler<Long> addHandler) {
    this.addHandler = addHandler;
  }

  public void setDeleteHandler(DeleteHandler<Long> deleteHandler) {
    this.deleteHandler = deleteHandler;
  }
  
  public List<Long> getValues() {
    return currentParents;
  }

  public void show(ProductGroup productGroup, List<ProductGroup> parents, String lang, Collection<ProductGroup> allParents) {
    currentParents.clear();
    for (ProductGroup pg : parents) {
      currentParents.add(pg.getId());
    }
    allPossibleParents.clear();
    for (ProductGroup pg : allParents) {
      allPossibleParents.add(pg.getId());
    }
    if (productGroup != null) {
      allPossibleParents.remove(productGroup.getId());
    }
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
