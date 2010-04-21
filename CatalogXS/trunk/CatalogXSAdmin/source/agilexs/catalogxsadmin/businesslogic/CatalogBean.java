package agilexs.catalogxsadmin.businesslogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import agilexs.catalogxsadmin.jpa.catalog.Catalog;
import agilexs.catalogxsadmin.jpa.catalog.Label;
import agilexs.catalogxsadmin.jpa.catalog.Language;
import agilexs.catalogxsadmin.jpa.catalog.Product;
import agilexs.catalogxsadmin.jpa.catalog.ProductGroup;
import agilexs.catalogxsadmin.jpa.catalog.Property;
import agilexs.catalogxsadmin.jpa.catalog.PropertyValue;
import agilexs.catalogxsadmin.jpa.shop.Shop;

@Stateless
public class CatalogBean extends CatalogBeanBase implements agilexs.catalogxsadmin.businesslogic.Catalog {

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public List<PropertyValue> findAllProductGroupNames(Catalog catalog) {
    final Query query = entityManager.createQuery("select pv from PropertyValue pv, in(pv.property.labels) lbl where type(pv.item) in (ProductGroup) and lbl.label = 'Name' and pv.item.catalog = :catalog");

    query.setParameter("catalog", catalog);
    final List<PropertyValue> result = query.getResultList();

    for (PropertyValue pv: result) {
      pv.getProperty().setLabels(pv.getProperty().getLabels());
    }
    return result;
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public Catalog findCatalogById(Long id) {
    final Catalog catalog = super.findCatalogById(id);

    if (catalog != null) {
      final Query query = entityManager.createQuery("select pg from ProductGroup pg where pg.catalog = :catalog");

      query.setParameter("catalog", catalog);
      final Collection result = query.getResultList();

      catalog.setItems(result);
      for (ProductGroup productGroup : (Collection<ProductGroup>)result) {
        //check, because db containsProduct table may be null
        if (productGroup.getContainsProducts() == null) {
          productGroup.setContainsProducts(Boolean.FALSE);
        }
        productGroup.setCatalog(productGroup.getCatalog());
        for (Property property : productGroup.getProperties()) {
          property.setLabels(property.getLabels());
          for (Label lbl : property.getLabels()) {
            lbl.setEnumValue(lbl.getEnumValue());
          }
          property.setEnumValues(property.getEnumValues());
          property.setItem(property.getItem());
        }
        for (PropertyValue value : productGroup.getPropertyValues()) {
          value.setItem(value.getItem());
          value.setProperty(value.getProperty());
        }
      }
    }
    return catalog;
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public List<Language> getAllLanguages() {
    final Locale list[] = SimpleDateFormat.getAvailableLocales();
    final List<Language> languages = new ArrayList<Language>();

    Arrays.sort(list, new Comparator<Locale>(){
      @Override
      public int compare(Locale o1, Locale o2) {
        return o1.getDisplayName().compareTo(o2.getDisplayName());
      }});
    for (int i = 0; i < list.length; i++) {
      final Language lang = new Language();

      lang.setId(Long.valueOf(i));
      lang.setName(list[i].getLanguage());
      lang.setDisplayName(list[i].getDisplayName());
      languages.add(lang);
    }
    return languages;
  }

  @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Product> findProductsByCatalogId(agilexs.catalogxsadmin.jpa.catalog.Catalog catalog) {
    final ArrayList<Product> products = new ArrayList<Product>();

    if (catalog != null) {
      final Query query = entityManager.createQuery("select p from Product p where p.catalog = :catalog");

      query.setParameter("catalog", catalog);
      for (Product p : (Collection<Product>)query.getResultList()) {
        products.add(findProductById(p.getId()));
      }
    }
    return products;
    }

  @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Product> findAllByProductGroupProducts(Integer fromIndex, Integer pageSize, ProductGroup filter) {
        // Validate the input arguments
        if (fromIndex == null || fromIndex < 0) {
            throw new IllegalArgumentException("fromIndex < 0, from index must be 0 at least");
        }
        if (pageSize == null || pageSize < 1) {
            throw new IllegalArgumentException("pageSize < 1, page size must be 1 at least");
        }
        final String queryString = "select p from Product p, IN(p.parents) pg where pg = :productgroup";
        final Query query = entityManager.createQuery(queryString);

        query.setParameter("productgroup", filter);
        query.setFirstResult(fromIndex);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

  @Override
  @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product findProductById(Long id) {
    final Product p = super.findProductById(id);

    p.setCatalog(p.getCatalog());
    for (PropertyValue pv : p.getPropertyValues()) {
      pv.setProperty(pv.getProperty());
    }
    return p;
  }

  @Override
  @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<ProductGroup> findAllProductGroupChildren(Shop shop, ProductGroup parent) {
      Query query;
      if (parent == null) {
        //query = entityManager.createQuery("select p from ProductGroup p where p.view = :view");
        query = entityManager.createQuery("select p from ProductGroup p where p.parents is empty");

        //query.setParameter("view", view);
      } else {
        //FIXME: trying to do exclusions in sql not easy, so we skip that part for now, better put in cache.
        //invalid => query = entityManager.createQuery("select p from ProductGroup p, Taxonomy t, not in(t.excludedProductGroups) teg where p.parent = :parent and t = :taxonomy and teg = p");
        query = entityManager.createQuery("select distinct p from ProductGroup p, in(p.parents) parent where parent = :parent");

        //query.setParameter("taxonomy", taxonomy);
        query.setParameter("parent", parent);
      }
      final List<ProductGroup> result = query.getResultList();
      if (result != null) {
        for (ProductGroup productGroup : result) {
          //check, because db containsProduct table may be null
          if (productGroup.getContainsProducts() == null) {
            productGroup.setContainsProducts(Boolean.FALSE);
          }
          productGroup.setCatalog(productGroup.getCatalog());
          for (Property property : productGroup.getProperties()) {
            property.setLabels(property.getLabels());
            for (Label lbl : property.getLabels()) {
              lbl.setEnumValue(lbl.getEnumValue());
            }
            property.setEnumValues(property.getEnumValues());
            property.setItem(property.getItem());
          }
          for (PropertyValue value : productGroup.getPropertyValues()) {
            value.setItem(value.getItem());
            value.setProperty(value.getProperty());
          }
        }
      }
      return result;
    }
}
