package agilexs.catalogxs.businesslogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import agilexs.catalogxs.jpa.catalog.Product;
import agilexs.catalogxs.jpa.catalog.ProductGroup;
import agilexs.catalogxs.jpa.catalog.Taxonomy;

@Stateless
public class CatalogBean extends CatalogBeanBase implements agilexs.catalogxs.businesslogic.Catalog {
/*
	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Catalog> listCatalogs() {
        final Query query = entityManager.createQuery("select c from Catalog c");

        return query.getResultList();
    }
*/
	@SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Product> findProductsByCatalogId(agilexs.catalogxs.jpa.catalog.Catalog catalog) {
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
        final String queryString = "select p from Product p, IN(p.productGroups) pg where pg = :productgroup";
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

		if (p != null) {
	        final Query query = entityManager.createQuery("select a from PropertyValue a join fetch a.property where a.product = :product");

	        query.setParameter("product", p);
	        p.setPropertyValues(query.getResultList());
		}
		return p;
	}
	
	@Override
	@SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<ProductGroup> findAllProductGroupChildren(Taxonomy taxonomy, ProductGroup parent) {
		Query query;
		if (parent == null) {
			query = entityManager.createQuery("select p from ProductGroup p where p.taxonomy = :taxonomy");

			query.setParameter("taxonomy", taxonomy);
		} else {
			//FIXME: trying to do exclusions in sql not easy, so we skip that part for now, better put in cache. 
			//invalid => query = entityManager.createQuery("select p from ProductGroup p, Taxonomy t, not in(t.excludedProductGroups) teg where p.parent = :parent and t = :taxonomy and teg = p");
			query = entityManager.createQuery("select p from ProductGroup p where p.parent = :parent"); 

			//query.setParameter("taxonomy", taxonomy);
			query.setParameter("parent", parent);
		}
		return query.getResultList();
    }

//	private final static org.apache.log4j.Logger LOGGER = eu.future.earth.logging.ExtendedLog.getLogger(CatalogBean.class);

//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	public Product updateProduct(Product oldProduct, Product newProduct) {
//		Product result = super.updateProduct(oldProduct, newProduct);
//		LuceneHelper helper = new LuceneHelper();
//		try {
//			IndexWriter writer = helper.getWriter();
//			ProductAnalyzer analyzer = new ProductAnalyzer();
//			Document doc = analyzer.createDocument(result);
//			if (oldProduct != null) {
//				writer.updateDocument(analyzer.createKey(result), doc);
//			} else {
//				writer.addDocument(doc);
//			}
//			writer.optimize();
//			writer.close();
//		} catch (CorruptIndexException e) {
//			LOGGER.error("Corrupted Index.", e);
//		} catch (LockObtainFailedException e) {
//			LOGGER.error("Could not get lock.", e);
//		} catch (IOException e) {
//			LOGGER.error("File Error.", e);
//		}
//		return result;
//	}
}