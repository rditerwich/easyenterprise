package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Presenter for the ProductGroup page
 */
public class ProductGroupPresenter implements Presenter<ProductGroupView> {

  private final ProductGroupView view = new ProductGroupView();
  private ItemParentsPresenter parentsP = new ItemParentsPresenter(new ItemParentsView());
  private ProductGroupPropertiesPresenter pgpp;

  private String currentLanguage = "en";
  private ProductGroup currentProductGroup;
  private ProductGroup orgProductGroup;
  private final ArrayList<ItemValuesPresenter> valuesPresenters = new ArrayList<ItemValuesPresenter>();

  public ProductGroupPresenter() {
    pgpp = new ProductGroupPropertiesPresenter();
    view.setParentsPanel(parentsP.getView());
    view.setPropertiesPanel(pgpp.getView());
    view.containsProductsClickHandler().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (currentProductGroup != null) {
          currentProductGroup.setContainsProducts(view.containsProducts().getValue());
        }
      }});
  }

  public void setNewProductGroup(Shop shop) {
      orgProductGroup = null;
      currentProductGroup = new ProductGroup();
      currentProductGroup.setCatalog(shop.getCatalog());
      currentProductGroup.setContainsProducts(Boolean.FALSE);
      view.setName("");
      view.containsProducts().setValue(currentProductGroup.getContainsProducts());
      pgpp.show(currentLanguage, currentProductGroup.getPropertyValues());
      view.getParentPropertiesPanel().clear();
  }

  @Override
  public ProductGroupView getView() {
    return view;
  }

  public void save() {
    //
    orgProductGroup.getChildren().clear();
    final ProductGroup saveProductGroup = currentProductGroup.clone(new HashMap());

    saveProductGroup.getChildren().clear();
    CatalogServiceAsync.updateProductGroup(orgProductGroup,
        saveProductGroup, new AsyncCallback() {
          @Override
          public void onFailure(Throwable caught) {
            //TODO message on save fail
          }

          @Override
          public void onSuccess(Object result) {
            final Label name = Util.getLabel(Util.getPropertyValueByName(currentProductGroup.getPropertyValues(),Util.NAME, null), currentLanguage);
            StatusMessage.get().show("Product group "+ name.getLabel()+ " saved.", 15);
          }
        });
  }

  public void show(String lang) {
    currentLanguage = lang;
    show(currentProductGroup);
  }

  public void show(ProductGroup productGroup) {
    final List<String> langs = CatalogCache.get().getLanguages();

    if (currentProductGroup != productGroup) {
      orgProductGroup = currentProductGroup = productGroup;
      if (currentProductGroup != null) {
        orgProductGroup = currentProductGroup.clone(new HashMap());
        final PropertyValue name = Util.getPropertyValueByName(currentProductGroup.getPropertyValues(),Util.NAME, null);

        view.setName(name==null?"":name.getStringValue());
        view.containsProducts().setValue(currentProductGroup.getContainsProducts());
        //parents product groups
        final List<Map.Entry<Long, String>> curParents = new ArrayList<Map.Entry<Long, String>>();

        for (ProductGroup cp : currentProductGroup.getParents()) {
          curParents.add(CatalogCache.get().getProductGroupName(cp.getId(), currentLanguage));
        }
        parentsP.show(currentProductGroup, curParents, currentLanguage, CatalogCache.get().getProductGroupNamesByLang(currentLanguage));
        //own properties with default values
        pgpp.show(currentLanguage, Util.getProductGroupPropertyValues(langs, currentProductGroup, currentProductGroup));
        //inherited properties from parents
        view.getParentPropertiesPanel().clear();
        valuesPresenters.clear();
        final List<Long> parents = Util.findParents(currentProductGroup);

        for (Long pid : parents) {
          final ProductGroup parent = CatalogCache.get().getProductGroup(pid);
          final List<PropertyValue> pv = Util.getProductGroupPropertyValues(langs, parent, currentProductGroup);

          if (!pv.isEmpty()) {
            final ItemValuesPresenter presenter = new ItemValuesPresenter();

            valuesPresenters.add(presenter);
            view.getParentPropertiesPanel().add(presenter.getView().asWidget());
            presenter.show(Util.getPropertyValueByName(parent.getPropertyValues(),Util.NAME, currentLanguage).getStringValue(), currentLanguage, pv);
          }
        }
      } else {

      }
    }
  }
}
