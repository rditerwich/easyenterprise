package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.catalog.Relation;
import agilexs.catalogxsadmin.presentation.client.catalog.RelationType;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class RelatedToPresenter implements Presenter<ItemParentsView> {

  private ItemParentsView view;
  private final List<Relation> relatedTo = new ArrayList<Relation>();
  private final List<Map.Entry<Long, String>> allGroups = new ArrayList<Map.Entry<Long, String>>();
  private String currentLang = "en";
  private DeleteHandler<Relation> deleteHandler;
  private AddHandler<Long> addHandler;
  private Category productGroup;

  public RelatedToPresenter(final ItemParentsView view) {
    this.view = view;
    view.buttonAddHasClickHandlers().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final Relation r = new Relation();
        final Long newPG = Long.valueOf(view.getAllParentsListBox().getValue(view.getSelectedNewParent()));
        final Category pg = new Category();
        final RelationType rt = new RelationType();
        
        pg.setId(newPG);
        r.setRelatedTo(pg);
        r.setItem(productGroup);
        rt.setName("");
        r.setRelationType(rt);
        relatedTo.add(r);
        allGroups.remove(CatalogCache.get().getCategoryName(newPG, currentLang));
        show(currentLang);
        if (addHandler != null) {
          addHandler.onAdd(newPG);
        }
      }});
    view.setDeleteHandler(new DeleteHandler<Integer>(){
      @Override
      public void onDelete(Integer index) {
        final Relation removed = relatedTo.remove(index.intValue());

        if (removed != null) {
          allGroups.add(CatalogCache.get().getCategoryName(removed.getRelatedTo().getId(), currentLang));
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

  public void setDeleteHandler(DeleteHandler<Relation> deleteHandler) {
    this.deleteHandler = deleteHandler;
  }
  
  public List<Relation> getValues() {
    return relatedTo;
  }

  public void show(Category productGroup, String lang, ArrayList<Map.Entry<Long, String>> allItems) {
    this.productGroup = productGroup;
    currentLang = lang;
    relatedTo.clear();
    for (Relation relation : productGroup.getRelations()) {
      relatedTo.add(relation);
    }
    allGroups.clear();
    allGroups.addAll(allItems);
    if (productGroup != null) {
      allGroups.remove(CatalogCache.get().getCategoryName(productGroup.getId(), currentLang));
    }
    for (Relation r : relatedTo) {
      allGroups.remove(CatalogCache.get().getCategoryName(r.getRelatedTo().getId(), lang));
    }
    show(lang);
  }

  private void show(String lang) {
    currentLang = lang;
    view.clearParentTable();
    for (Relation pg : relatedTo) {
      view.addParentToList(CatalogCache.get().getCategoryName(pg.getRelatedTo().getId(), lang).getValue());
    }
    view.getAllParentsListBox().clear();
    for (Map.Entry<Long, String> pp : allGroups) {
      view.getAllParentsListBox().addItem(pp.getValue(), pp.getKey() + "");
    }
  }

  @Override
  public ItemParentsView getView() {
    return view;
  }
}
