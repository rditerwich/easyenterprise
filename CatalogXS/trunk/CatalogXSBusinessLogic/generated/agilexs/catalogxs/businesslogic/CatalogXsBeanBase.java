package agilexs.catalogxs.businesslogic;

import java.lang.Long;
import java.util.Collection;
import java.util.Date;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import agilexs.catalogxs.jpa.catalog.Product;
import agilexs.catalogxs.jpa.catalog.ProductRelation;

public class CatalogXsBeanBase implements CatalogXs {   


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void find(Date channel) {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Product> findProducts(Long channel) {
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<ProductRelation> getProductRelations() {
        return null;
    }
}