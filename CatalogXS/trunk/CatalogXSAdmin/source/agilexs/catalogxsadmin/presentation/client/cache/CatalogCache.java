package agilexs.catalogxsadmin.presentation.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

//TODO cache related objects:  propertyvalues in productgroup
//Singleton
public class CatalogCache {

  public static CatalogCache INSTANCE = new CatalogCache();

  private final Map<Long, ProductGroup> productGroupCache = new HashMap<Long, ProductGroup>(); 
  private final Map<Long, Product> productCache = new HashMap<Long, Product>();
  private final Map<Long, Property> propertyCache = new HashMap<Long, Property>();
  private final Map<Long, PropertyValue> propertyValueCache = new HashMap<Long, PropertyValue>();
  private final Map<Long, Label> labelCache = new HashMap<Long, Label>();
  private final ArrayList<String> languages = new ArrayList<String>(2); 

  private CatalogCache() {
    //TODO Calculate cache languages based on what is in database
    languages.add("en");
    languages.add("de");
  }

  public ArrayList<String> getLanguages() {
    return languages;
  }

  public ProductGroup getProductGroup(Long id) {
    return productGroupCache.get(id);
  }

  public void getproductGroupCache(Long id, final AsyncCallback callback) {
    if (productGroupCache.containsKey(id)) {
      callback.onSuccess(productGroupCache.get(id));
    } else {
      CatalogServiceAsync.findProductGroupById(id, new AsyncCallback<ProductGroup>() {
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }
        @Override public void onSuccess(ProductGroup result) {
          put(result);
          callback.onSuccess(result);
        }});
    }
  }
  
  public Product getProduct(Long id) {
    return productCache.get(id);
  }
  
  public void getProduct(Long id, final AsyncCallback<Product> callback) {
    if (productCache.containsKey(id)) {
      callback.onSuccess(productCache.get(id));
    } else {
      CatalogServiceAsync.findProductById(id, new AsyncCallback<Product>() {
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }
        @Override public void onSuccess(Product result) {
          put(result);
          callback.onSuccess(result);
        }});
    }
  }
  
  public Property getProperty(Long id) {
    return propertyCache.get(id);
  }
  
  public void getProperty(Long id, final AsyncCallback<Property> callback) {
    if (propertyCache.containsKey(id)) {
      callback.onSuccess(propertyCache.get(id));
    } else {
      CatalogServiceAsync.findPropertyById(id, new AsyncCallback<Property>() {
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }
        @Override public void onSuccess(Property result) {
          put(result);
          callback.onSuccess(result);
        }});
    }
  }
  
  public PropertyValue getPropertyValue(Long id) {
    return propertyValueCache.get(id);
  }
  
  public void getPropertyValue(Long id, final AsyncCallback<PropertyValue> callback) {
    if (propertyValueCache.containsKey(id)) {
      callback.onSuccess(propertyValueCache.get(id));
    } else {
      CatalogServiceAsync.findPropertyValueById(id, new AsyncCallback<PropertyValue>() {
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }
        @Override public void onSuccess(PropertyValue result) {
          put(result);
          callback.onSuccess(result);
        }});
    }
  }
  
  public Label getLabel(Long id) {
    return labelCache.get(id);
  }
  
  public void getLabel(Long id, final AsyncCallback<Label> callback) {
    if (labelCache.containsKey(id)) {
      callback.onSuccess(labelCache.get(id));
    } else {
      CatalogServiceAsync.findLabelById(id, new AsyncCallback<Label>() {
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }
        @Override public void onSuccess(Label result) {
          put(result);
          callback.onSuccess(result);
        }});
    }
  }

  public void put(ProductGroup pg) {
    productGroupCache.put(pg.getId(), pg);
  }

  public void put(Product p) {
    productCache.put(p.getId(), p);
  }

  public void put(Property p) {
    propertyCache.put(p.getId(), p);
  }

  public void put(PropertyValue pv) {
    propertyValueCache.put(pv.getId(), pv);
  }

  public void put(Label l) {
    labelCache.put(l.getId(), l);
  }
}
