package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.catalog.Relation;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Presenter for the Category page
 */
public class CategoryPresenter implements Presenter<CategoryView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final CategoryView view = new CategoryView();
  private ItemParentsPresenter parentsP = new ItemParentsPresenter(new ItemParentsView());
  private RelatedToPresenter relatedToP = new RelatedToPresenter (new ItemParentsView());
  private ItemPropertiesPresenter pgpp;
  private String currentLanguage = "en";
  private Category currentCategory;
  private Category orgCategory;
  private final ArrayList<ItemValuesPresenter> valuesPresenters = new ArrayList<ItemValuesPresenter>();

  public CategoryPresenter() {
    pgpp = new ItemPropertiesPresenter(currentLanguage);
    parentsP.setDeleteHandler(new DeleteHandler<Long>() {
      @Override public void onDelete(Long data) {
        for (Item parent : currentCategory.getParents()) {
          if (data.equals(parent.getId())) {
            currentCategory.getParents().remove(parent);
            break;
          }
        }
      }
    });
    parentsP.setAddHandler(new AddHandler<Long>(){
      @Override public void onAdd(Long pid) {
        boolean present = false;

        for (Item parent : currentCategory.getParents()) {
          if (pid.equals(parent.getId())) {
            present = true;
          }
        }
        if (!present) {
          //Create an empty product group, so only this group is send to the
          //server which will then not be checked for changes.
          final Category pg = new Category();

          pg.setId(pid);
          currentCategory.getParents().add(pg);
        }
      }
    });
    view.containsProductsClickHandlers().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (currentCategory != null) {
          currentCategory.setContainsProducts(view.containsProducts().getValue());
        }
      }});
    view.saveButtonClickHandlers().addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        save();
      }});
  }

  public Category setNewCategory(Shop shop, Category parent) {
      orgCategory = null;
      final Category newPG = new Category();
      newPG.setCatalog(shop.getCatalog());
      newPG.setPropertyValues(new ArrayList<PropertyValue>());
      newPG.setProperties(new ArrayList<Property>());
      newPG.setContainsProducts(Boolean.FALSE);
      if (parent != null) {
        final List<Item> parents = new ArrayList<Item>();

        parents.add(parent);
        newPG.setParents(parents);
      } else {
        newPG.setParents(new ArrayList<Item>());
      }
      view.containsProducts().setValue(newPG.getContainsProducts());
      show(newPG);
      return newPG;
  }

  @Override
  public CategoryView getView() {
    return view;
  }

  private void save() {
    view.setSaving(true);
    if (orgCategory != null) {
      orgCategory.getChildren().clear();
      orgCategory.setProperties(Util.filterEmpty(orgCategory.getProperties()));
      orgCategory.setPropertyValues(Util.filterEmpty(orgCategory.getPropertyValues()));
    }
    final Category saveCategory = currentCategory.clone(new HashMap());

    saveCategory.getChildren().clear();
    saveCategory.setProperties(Util.filterEmpty(currentCategory.getProperties()));
    for (Property p : saveCategory.getProperties()) {
      if (currentCategory.equals(p.getItem())) {
        p.setItem(saveCategory);
      }
    }
    saveCategory.getParents().clear();
    for (Long parent : parentsP.getValues()) {
      final Category p = new Category();
      
      p.setId(parent);
      saveCategory.getParents().add(p);
    }
    saveCategory.getRelations().clear();
    for (Relation relation : relatedToP.getValues()) {
      saveCategory.getRelations().add(relation);
    }
    saveCategory.setPropertyValues(Util.filterEmpty(saveCategory.getPropertyValues()));
    CatalogServiceAsync.updateCategory(orgCategory,
        saveCategory, new AsyncCallback<Category>() {
          @Override
          public void onFailure(Throwable caught) {
            view.setSaving(false);
          }

          @Override
          public void onSuccess(Category result) {
            view.setSaving(false);
            if (result != null) {
              final PropertyValue name = Util.getPropertyValueByName(result.getPropertyValues(), Util.NAME, currentLanguage);
              
              StatusMessage.get().show(i18n.productGroupSaved(name==null?"":name.getStringValue()));
              CatalogCache.get().put(result);
              currentCategory = null; //force redraw of product group
              orgCategory = null;
              show(result);
            } else {
              StatusMessage.get().show(i18n.productGroupSaved(""));
            }
          }
        });
  }

  public void switchLanguage(String lang) {
    currentLanguage = lang;
    show(currentCategory);
  }

  public void show(Category productGroup) {
    final List<Language> langs = CatalogCache.get().getActiveCatalog().getLanguages();

    if (currentCategory != productGroup) {
      currentCategory = productGroup;
      if (currentCategory != null) {
        if ((orgCategory == null || orgCategory.getId() != currentCategory.getId())
            && currentCategory.getId() != null) {
          orgCategory = currentCategory.clone(new HashMap());
        }
      }
    }
      if (currentCategory != null) {
        view.containsProducts().setValue(currentCategory.getContainsProducts());
        //parents product groups
        final List<Map.Entry<Long, String>> curParents = new ArrayList<Map.Entry<Long, String>>();

        for (Item cp : currentCategory.getParents()) {
          curParents.add(CatalogCache.get().getCategoryName(cp.getId(), currentLanguage));
        }
        parentsP.show(currentCategory, curParents, currentLanguage, CatalogCache.get().getCategoryNamesByLang(currentLanguage));
        relatedToP.show(currentCategory, currentLanguage, CatalogCache.get().getCategoryNamesByLang(currentLanguage));
        //own properties with default values
        pgpp.show(langs, currentLanguage, currentCategory);
        //inherited properties from parents
        view.clear();
        valuesPresenters.clear();
        view.add(i18n.parents(), parentsP.getView());
        view.add(i18n.relatedTo(), relatedToP.getView());
        view.add(i18n.properties(), pgpp.getView());
        final List<Long> parents = Util.findParents(currentCategory);
        final Long nameGId = CatalogCache.get().getCategoryName().getId();

        for (Long pid : parents) {
          final Category parent = CatalogCache.get().getCategory(pid);
          final List<PropertyValue[]> pv = Util.getCategoryPropertyValues(langs, parent, currentCategory);

          if (!pv.isEmpty()) {
            final ItemValuesPresenter presenter = new ItemValuesPresenter();

            valuesPresenters.add(presenter);
            final PropertyValue pvName = Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage);
            final PropertyValue pvDName = Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, null);
            final String name = nameGId.equals(pid) ? "" : (pvName == null || Util.isEmpty(pvName) ? (pvDName == null ?  "" : pvDName.getStringValue()) : pvName.getStringValue());

            view.addPropertyValues(name, presenter.getView());
            presenter.show(currentLanguage, pv);
          }
        }
      } else {

      }
  }
}
