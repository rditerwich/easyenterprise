package agilexs.catalogxs.businesslogic;

import java.lang.Long;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import agilexs.catalogxs.common.businesslogic.StoreView;
import agilexs.catalogxs.jpa.catalog.Product;
import agilexs.catalogxs.jpa.catalog.ProductRelation;

public class CatalogBeanBase implements Catalog {   
    protected static StoreView updateCatalogView = createUpdateCatalogView();

    @EJB
    private CatalogStore catalogStoreSessionBean;

    @PersistenceContext
    protected EntityManager entityManager;
    protected static StoreView updateProductView = createUpdateProductView();
    protected static StoreView updateProductRelationView = createUpdateProductRelationView();


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public agilexs.catalogxs.jpa.catalog.Catalog updateCatalog(agilexs.catalogxs.jpa.catalog.Catalog oldCatalog, agilexs.catalogxs.jpa.catalog.Catalog newCatalog) {
        // Call StoreBean with the appropriate view
        return catalogStoreSessionBean.store(oldCatalog, newCatalog, updateCatalogView);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public agilexs.catalogxs.jpa.catalog.Catalog findCatalogById(Long id) {
        Query query = entityManager.createQuery("select a from Catalog a where a.id = :id");
        query.setParameter("id", id);
        query.setMaxResults(1);
        try {
            return (agilexs.catalogxs.jpa.catalog.Catalog) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product updateProduct(Product oldProduct, Product newProduct) {
        // Call StoreBean with the appropriate view
        return catalogStoreSessionBean.store(oldProduct, newProduct, updateProductView);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Product findProductById(Long id) {
        Query query = entityManager.createQuery("select a from Product a where a.id = :id");
        query.setParameter("id", id);
        query.setMaxResults(1);
        try {
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProductRelation updateProductRelation(ProductRelation oldProductRelation, ProductRelation newProductRelation) {
        // Call StoreBean with the appropriate view
        return catalogStoreSessionBean.store(oldProductRelation, newProductRelation, updateProductRelationView);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProductRelation findProductRelationById(Long id) {
        Query query = entityManager.createQuery("select a from ProductRelation a where a.id = :id");
        query.setParameter("id", id);
        query.setMaxResults(1);
        try {
            return (ProductRelation) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    protected static StoreView createUpdateCatalogView() {
        // Create view
        StoreView result = new StoreView(
            agilexs.catalogxs.jpa.catalog.Catalog.Properties.id, agilexs.catalogxs.jpa.catalog.Catalog.Properties.name, agilexs.catalogxs.jpa.catalog.Catalog.Properties.productGroups, 
            agilexs.catalogxs.jpa.catalog.Catalog.Properties.productRelations, agilexs.catalogxs.jpa.catalog.Catalog.Properties.products, agilexs.catalogxs.jpa.catalog.Catalog.Properties.taxonomies, 
            agilexs.catalogxs.jpa.catalog.Catalog.Properties.properties);

        // View productGroupsPropertyView
        StoreView propertyView = new StoreView();
        result.addView(agilexs.catalogxs.jpa.catalog.Catalog.Properties.productGroups, propertyView, true);

        // View productRelationsPropertyView
        propertyView = new StoreView();
        result.addView(agilexs.catalogxs.jpa.catalog.Catalog.Properties.productRelations, propertyView, true);

        // View productsPropertyView
        propertyView = new StoreView();
        result.addView(agilexs.catalogxs.jpa.catalog.Catalog.Properties.products, propertyView, true);

        // View taxonomiesPropertyView
        propertyView = new StoreView();
        result.addView(agilexs.catalogxs.jpa.catalog.Catalog.Properties.taxonomies, propertyView, true);

        // View propertiesPropertyView
        propertyView = new StoreView();
        result.addView(agilexs.catalogxs.jpa.catalog.Catalog.Properties.properties, propertyView, true);

        return result;
    }

    protected static StoreView createUpdateProductView() {
        // Create view
        StoreView result = new StoreView(
            Product.Properties.id, Product.Properties.catalog, Product.Properties.propertyValues, 
            Product.Properties.relatedProducts);

        // View catalogPropertyView
        StoreView propertyView = new StoreView();
        result.addView(Product.Properties.catalog, propertyView, false);

        // View propertyValuesPropertyView
        propertyView = new StoreView();
        result.addView(Product.Properties.propertyValues, propertyView, true);

        // View relatedProductsPropertyView
        propertyView = new StoreView();
        result.addView(Product.Properties.relatedProducts, propertyView, true);

        return result;
    }

    protected static StoreView createUpdateProductRelationView() {
        // Create view
        StoreView result = new StoreView(ProductRelation.Properties.id, ProductRelation.Properties.name);

        return result;
    }
}