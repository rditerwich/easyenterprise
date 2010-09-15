package agilexs.catalogxs.businesslogic;

import java.lang.Long;
import java.util.Collection;
import java.util.Date;
import agilexs.catalogxs.jpa.catalog.Product;
import agilexs.catalogxs.jpa.catalog.ProductRelation;

public interface CatalogXs {

    void find(Date channel);

    Collection<Product> findProducts(Long channel);

    Collection<ProductRelation> getProductRelations();
}