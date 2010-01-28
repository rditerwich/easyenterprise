package agilexs.catalogxs.businesslogic;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import agilexs.catalogxs.jpa.catalog.Product;

@Stateless
public class CatalogXsBean extends CatalogXsBeanBase implements CatalogXs {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Collection<Product> findProducts(Long channel) {
		return new ArrayList<Product>();
	}

}