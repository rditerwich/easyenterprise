package agilexs.catalogxs.businesslogic;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import agilexs.catalogxs.jpa.catalog.Product;

@Stateless
@NamedQueries( {
	
		@NamedQuery(name = "Product.FindById", query = "SELECT a FROM Product a WHERE a.id = :id"),

		@NamedQuery(name = "Product.DeleteById", query = "DELETE FROM Product a WHERE a.id = :id")

})
public class CatalogXsBean extends CatalogXsBeanBase {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Collection<Product> findProducts(Long channel) {

		return null;
	}

}