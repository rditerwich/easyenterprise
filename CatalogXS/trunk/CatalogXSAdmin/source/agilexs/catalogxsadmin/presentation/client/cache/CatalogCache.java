package agilexs.catalogxsadmin.presentation.client.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.catalog.Catalog;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.ProductGroup;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.util.Entry;

import com.google.gwt.user.client.rpc.AsyncCallback;

//TODO cache related objects:  propertyvalues in productgroup
//Singleton
public class CatalogCache {

  private static CatalogCache instance = new CatalogCache();
  public static CatalogCache get() {
    return instance;
  }

  private Catalog activeCatalog;
  private final Map<Long, Map<String, String>> productGroupNamesCache = new HashMap<Long, Map<String, String>>();

  private final Map<Long, Shop> shopCache = new HashMap<Long, Shop>();
  private final Map<Long, ProductGroup> productGroupCache = new HashMap<Long, ProductGroup>();
  private final Map<Long, Product> productCache = new HashMap<Long, Product>();
  private final Map<Long, Property> propertyCache = new HashMap<Long, Property>();
  private final Map<Long, PropertyValue> propertyValueCache = new HashMap<Long, PropertyValue>();
  private final Map<Long, Promotion> promotionCache = new HashMap<Long, Promotion>();
  private final Map<Long, Label> labelCache = new HashMap<Long, Label>();
  private ProductGroup productGroupName;
  private ProductGroup productGroupProduct;

  private CatalogCache() {
  }

  public Catalog getActiveCatalog() {
    return activeCatalog;
  }

  /**
   * Returns the ProductGroup that contains the Property with the label 'Name'.
   * 
   * @return
   */
  public ProductGroup getProductGroupName() {
    return productGroupName;
  }

  /**
   * Returns the ProductGroup that contains the general product properties, like
   * article number, description, price and image. These properties are required
   * for every product and these properties are used to display a product in a
   * table, etc. 
   *
   * @return
   */
  public ProductGroup getProductGroupProduct() {
    return productGroupProduct;
  }

  public void setProductGroupName(ProductGroup productGroupName) {
    this.productGroupName = productGroupName;
  }

  public void setProductGroupProduct(ProductGroup productGroupProduct) {
    this.productGroupProduct = productGroupProduct;
  }

  public void loadProductGroupNames(final AsyncCallback callback) {
    final Catalog c = new Catalog();
    c.setId(getActiveCatalog().getId());
    CatalogServiceAsync.findAllProductGroupNames(c, new AsyncCallback<List<PropertyValue>>() {
      @Override public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }
      @Override public void onSuccess(List<PropertyValue> result) {
        for (PropertyValue pv : result) {
          final String lang = pv.getLanguage() == null ? "" : pv.getLanguage();

          updateProductGroupName(pv.getItem().getId(), lang, pv.getStringValue());
        }
        callback.onSuccess("loaded");
      }
    });
  }

  public Map.Entry<Long, String> getProductGroupName(ProductGroup pg, String lang) {
    return getProductGroupName(pg.getId(), lang);
  }

  public Map.Entry<Long, String> getProductGroupName(Long pid, String lang) {
    final Map.Entry<Long, String> tp = new Entry<Long, String>(pid);
    if (productGroupNamesCache.containsKey(pid)) {
      if (productGroupNamesCache.get(pid).containsKey(lang)) {
        tp.setValue(productGroupNamesCache.get(pid).get(lang)); 
      } else {
        tp.setValue(productGroupNamesCache.get(pid).get("")); 
      }
    } 
    return tp;
  }

  public ArrayList<Map.Entry<Long, String>> getProductGroupNamesByLang(String lang) {
    final ArrayList<Map.Entry<Long, String>> list = new ArrayList<Map.Entry<Long, String>>();
    for (Long pid : productGroupNamesCache.keySet()) {
      list.add(getProductGroupName(pid, lang));
    }
    return list;
  }

  public void updateProductGroupName(Long pid, String lang, String name) {
    if (!productGroupNamesCache.containsKey(pid)) {
      productGroupNamesCache.put(pid, new HashMap<String, String>());
    }
    productGroupNamesCache.get(pid).put(lang, name);
  }
  
  public void getCatalog(Long id, final AsyncCallback callback) {
    if (shopCache.containsKey(id)) {
      callback.onSuccess(getShop(id));
    } else {
      CatalogServiceAsync.findCatalogById(id, new AsyncCallback<Catalog>(){
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        @Override
        public void onSuccess(Catalog result) {
          put(result);
          callback.onSuccess(result);
        }});
    }
  }

  public Collection<ProductGroup> getAllProductGroups() {
    return productGroupCache.values();
  }

  public Shop getShop(Long id) {
    return shopCache.get(id);
  }

  public void getShop(Long id, final AsyncCallback callback) {
    if (shopCache.containsKey(id)) {
      callback.onSuccess(getShop(id));
    } else {
      ShopServiceAsync.findShopById(id, new AsyncCallback<Shop>(){
        @Override public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        @Override
        public void onSuccess(Shop result) {
          put(result);
          put(result.getCatalog());
          getActiveCatalog().getShops().add(result);
          callback.onSuccess(result);
        }});
    }
  }

  public ProductGroup getProductGroup(Long id) {
    return productGroupCache.get(id);
  }

  public void getproductGroupCache(Long id, final AsyncCallback callback) {
    if (productGroupCache.containsKey(id)) {
      callback.onSuccess(getProductGroup(id));
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

  public Promotion getPromotion(Long id) {
    return promotionCache.get(id);
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

  public void put(Catalog catalog) {
    activeCatalog = catalog;
    for (Shop shop : catalog.getShops()) {
      put(shop);
    }
    for (Item item : catalog.getItems()) {
      put(item);
    }
  }

  public void put(Shop shop) {
    if (shop != null) {
      shopCache.put(shop.getId(), shop);
    }
  }

  public void put(ProductGroup pg) {
    if (pg != null) {
      productGroupCache.put(pg.getId(), pg);
    }
  }

  public void put(Product p) {
    if (p != null) {
      productCache.put(p.getId(), p);
    }
  }

  public void put(Item item) {
    if (item instanceof Product) {
      put((Product)item);
    } else if (item instanceof ProductGroup) {
      put((ProductGroup)item);
    }
  }

  public void put(Property p) {
    if (p != null) {
      propertyCache.put(p.getId(), p);
    }
  }

  public void put(Promotion p) {
    if (p != null) {
      promotionCache.put(p.getId(), p);
    }
  }

  public void put(PropertyValue pv) {
    if (pv != null) {
      propertyValueCache.put(pv.getId(), pv);
    }
  }

  public void put(Label l) {
    if (l != null) {
      labelCache.put(l.getId(), l);
    }
  }
}
