package agilexs.catalogxs.businesslogic;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import agilexs.catalogxs.jpa.catalog.Catalog;
import agilexs.catalogxs.jpa.catalog.Product;

@Stateless
public class CatalogBean extends CatalogBeanBase implements agilexs.catalogxs.businesslogic.Catalog {

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Catalog> listCatalogs() {
        final Query query = entityManager.createQuery("select c from Catalog c");

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