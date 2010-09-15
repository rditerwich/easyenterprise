package agilexs.catalogxs.businesslogic;

import java.lang.Long;
import agilexs.catalogxs.jpa.catalog.Product;
import agilexs.catalogxs.jpa.catalog.ProductRelation;

public interface Catalog {

    agilexs.catalogxs.jpa.catalog.Catalog updateCatalog(agilexs.catalogxs.jpa.catalog.Catalog oldCatalog, agilexs.catalogxs.jpa.catalog.Catalog newCatalog);

    agilexs.catalogxs.jpa.catalog.Catalog findCatalogById(Long id);

    Product updateProduct(Product oldProduct, Product newProduct);

    Product findProductById(Long id);

    ProductRelation updateProductRelation(ProductRelation oldProductRelation, ProductRelation newProductRelation);

    ProductRelation findProductRelationById(Long id);
}