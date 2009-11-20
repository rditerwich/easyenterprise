package agilexs.catalogxs.businesslogic;

import agilexs.catalogxs.common.businesslogic.StoreState;
import agilexs.catalogxs.common.businesslogic.StoreView;
import agilexs.catalogxs.jpa.catalog.Catalog;
import agilexs.catalogxs.jpa.catalog.EnumValue;
import agilexs.catalogxs.jpa.catalog.Label;
import agilexs.catalogxs.jpa.catalog.Product;
import agilexs.catalogxs.jpa.catalog.ProductGroup;
import agilexs.catalogxs.jpa.catalog.ProductRelation;
import agilexs.catalogxs.jpa.catalog.Property;
import agilexs.catalogxs.jpa.catalog.PropertyValue;
import agilexs.catalogxs.jpa.catalog.RelatedProduct;
import agilexs.catalogxs.jpa.catalog.Taxonomy;

public interface CatalogStore {
     
    public boolean hasAllIdFields(Catalog object);
    public Catalog store(Catalog oldCatalog, Catalog newCatalog, StoreView requestedProperties);
    public Catalog store(Catalog oldCatalog, Catalog newCatalog, Catalog targetCatalog, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(Product object);
    public Product store(Product oldProduct, Product newProduct, StoreView requestedProperties);
    public Product store(Product oldProduct, Product newProduct, Product targetProduct, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(RelatedProduct object);
    public RelatedProduct store(RelatedProduct oldRelatedProduct, RelatedProduct newRelatedProduct, StoreView requestedProperties);
    public RelatedProduct store(RelatedProduct oldRelatedProduct, RelatedProduct newRelatedProduct, RelatedProduct targetRelatedProduct, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(ProductRelation object);
    public ProductRelation store(ProductRelation oldProductRelation, ProductRelation newProductRelation, StoreView requestedProperties);
    public ProductRelation store(ProductRelation oldProductRelation, ProductRelation newProductRelation, ProductRelation targetProductRelation, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(Property object);
    public Property store(Property oldProperty, Property newProperty, StoreView requestedProperties);
    public Property store(Property oldProperty, Property newProperty, Property targetProperty, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(EnumValue object);
    public EnumValue store(EnumValue oldEnumValue, EnumValue newEnumValue, StoreView requestedProperties);
    public EnumValue store(EnumValue oldEnumValue, EnumValue newEnumValue, EnumValue targetEnumValue, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(Taxonomy object);
    public Taxonomy store(Taxonomy oldTaxonomy, Taxonomy newTaxonomy, StoreView requestedProperties);
    public Taxonomy store(Taxonomy oldTaxonomy, Taxonomy newTaxonomy, Taxonomy targetTaxonomy, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(ProductGroup object);
    public ProductGroup store(ProductGroup oldProductGroup, ProductGroup newProductGroup, StoreView requestedProperties);
    public ProductGroup store(ProductGroup oldProductGroup, ProductGroup newProductGroup, ProductGroup targetProductGroup, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(Label object);
    public Label store(Label oldLabel, Label newLabel, StoreView requestedProperties);
    public Label store(Label oldLabel, Label newLabel, Label targetLabel, StoreView requestedProperties, StoreState storeState);
    public boolean hasAllIdFields(PropertyValue object);
    public PropertyValue store(PropertyValue oldPropertyValue, PropertyValue newPropertyValue, StoreView requestedProperties);
    public PropertyValue store(PropertyValue oldPropertyValue, PropertyValue newPropertyValue, PropertyValue targetPropertyValue, StoreView requestedProperties, StoreState storeState);
}