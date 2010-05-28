package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
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
 * Presenter for the ProductGroup page
 */
public class ProductGroupPresenter implements Presenter<ProductGroupView> {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  private final ProductGroupView view = new ProductGroupView();
  private ItemParentsPresenter parentsP = new ItemParentsPresenter(new ItemParentsView());
  private RelatedToPresenter relatedToP = new RelatedToPresenter (new ItemParentsView());
  private ItemPropertiesPresenter pgpp;
  private String currentLanguage = "en";
  private ProductGroup currentProductGroup;
  private ProductGroup orgProductGroup;
  private final ArrayList<ItemValuesPresenter> valuesPresenters = new ArrayList<ItemValuesPresenter>();

  public ProductGroupPresenter() {
    pgpp = new ItemPropertiesPresenter(currentLanguage);
    parentsP.setDeleteHandler(new DeleteHandler<Long>() {
      @Override public void onDelete(Long data) {
        for (ProductGroup parent : currentProductGroup.getParents()) {
          if (data.equals(parent.getId())) {
            currentProductGroup.getParents().remove(parent);
            break;
          }
        }
      }
    });
    parentsP.setAddHandler(new AddHandler<Long>(){
      @Override public void onAdd(Long pid) {
        boolean present = false;

        for (ProductGroup parent : currentProductGroup.getParents()) {
          if (pid.equals(parent.getId())) {
            present = true;
          }
        }
        if (!present) {
          //Create an empty product group, so only this group is send to the
          //server which will then not be checked for changes.
          final ProductGroup pg = new ProductGroup();

          pg.setId(pid);
          currentProductGroup.getParents().add(pg);
        }
      }
    });
    view.containsProductsClickHandlers().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (currentProductGroup != null) {
          currentProductGroup.setContainsProducts(view.containsProducts().getValue());
        }
      }});
    view.saveButtonClickHandlers().addClickHandler(new ClickHandler() {
      @Override public void onClick(ClickEvent event) {
        save();
      }});
  }

  public ProductGroup setNewProductGroup(Shop shop, ProductGroup parent) {
      orgProductGroup = null;
      final ProductGroup newPG = new ProductGroup();
      newPG.setCatalog(shop.getCatalog());
      newPG.setPropertyValues(new ArrayList<PropertyValue>());
      newPG.setProperties(new ArrayList<Property>());
      newPG.setContainsProducts(Boolean.FALSE);
      if (parent != null) {
        final List<ProductGroup> parents = new ArrayList<ProductGroup>();

        parents.add(parent);
        newPG.setParents(parents);
      } else {
        newPG.setParents(new ArrayList<ProductGroup>());
      }
      view.containsProducts().setValue(newPG.getContainsProducts());
      show(newPG);
      return newPG;
  }

  @Override
  public ProductGroupView getView() {
    return view;
  }

  private void save() {
    view.setSaving(true);
    if (orgProductGroup != null) {
      orgProductGroup.getChildren().clear();
      orgProductGroup.setProperties(Util.filterEmpty(orgProductGroup.getProperties()));
      orgProductGroup.setPropertyValues(Util.filterEmpty(orgProductGroup.getPropertyValues()));
    }
    final ProductGroup saveProductGroup = currentProductGroup.clone(new HashMap());

    saveProductGroup.getChildren().clear();
    saveProductGroup.setProperties(Util.filterEmpty(currentProductGroup.getProperties()));
    for (Property p : saveProductGroup.getProperties()) {
      if (currentProductGroup.equals(p.getItem())) {
        p.setItem(saveProductGroup);
      }
    }
    saveProductGroup.getParents().clear();
    for (Long parent : parentsP.getValues()) {
      final ProductGroup p = new ProductGroup();
      
      p.setId(parent);
      saveProductGroup.getParents().add(p);
    }
    saveProductGroup.getRelations().clear();
    for (Relation relation : relatedToP.getValues()) {
      saveProductGroup.getRelations().add(relation);
    }
    saveProductGroup.setPropertyValues(Util.filterEmpty(saveProductGroup.getPropertyValues()));
    CatalogServiceAsync.updateProductGroup(orgProductGroup,
        saveProductGroup, new AsyncCallback<ProductGroup>() {
          @Override
          public void onFailure(Throwable caught) {
            view.setSaving(false);
          }

          @Override
          public void onSuccess(ProductGroup result) {
            view.setSaving(false);
            if (result != null) {
              final PropertyValue name = Util.getPropertyValueByName(result.getPropertyValues(), Util.NAME, currentLanguage);
              
              StatusMessage.get().show(i18n.productGroupSaved(name==null?"":name.getStringValue()));
              CatalogCache.get().put(result);
              currentProductGroup = null; //force redraw of product group
              orgProductGroup = null;
              show(result);
            } else {
              StatusMessage.get().show(i18n.productGroupSaved(""));
            }
          }
        });
  }

  public void switchLanguage(String lang) {
    currentLanguage = lang;
    show(currentProductGroup);
  }

  public void show(ProductGroup productGroup) {
    final List<Language> langs = CatalogCache.get().getActiveCatalog().getLanguages();

    if (currentProductGroup != productGroup) {
      currentProductGroup = productGroup;
      if (currentProductGroup != null) {
        if ((orgProductGroup == null || orgProductGroup.getId() != currentProductGroup.getId())
            && currentProductGroup.getId() != null) {
          orgProductGroup = currentProductGroup.clone(new HashMap());
        }
      }
    }
      if (currentProductGroup != null) {
        view.containsProducts().setValue(currentProductGroup.getContainsProducts());
        //parents product groups
        final List<Map.Entry<Long, String>> curParents = new ArrayList<Map.Entry<Long, String>>();

        for (ProductGroup cp : currentProductGroup.getParents()) {
          curParents.add(CatalogCache.get().getProductGroupName(cp.getId(), currentLanguage));
        }
        parentsP.show(currentProductGroup, curParents, currentLanguage, CatalogCache.get().getProductGroupNamesByLang(currentLanguage));
        relatedToP.show(currentProductGroup, currentLanguage, CatalogCache.get().getProductGroupNamesByLang(currentLanguage));
        //own properties with default values
        pgpp.show(langs, currentLanguage, currentProductGroup);
        //inherited properties from parents
        view.clear();
        valuesPresenters.clear();
        view.add(i18n.parents(), parentsP.getView());
        view.add(i18n.relatedTo(), relatedToP.getView());
        view.add(i18n.properties(), pgpp.getView());
        final List<Long> parents = Util.findParents(currentProductGroup);
        final Long nameGId = CatalogCache.get().getProductGroupName().getId();

        for (Long pid : parents) {
          final ProductGroup parent = CatalogCache.get().getProductGroup(pid);
          final List<PropertyValue[]> pv = Util.getProductGroupPropertyValues(langs, parent, currentProductGroup);

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
