package agilexs.catalogxs.businesslogic;

import java.lang.IllegalStateException;
import java.lang.Object;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import agilexs.catalogxs.common.businesslogic.ProcessedStoreView;
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

@Stateless
public class CatalogStoreBean implements CatalogStore {   

    @PersistenceContext
    protected EntityManager entityManager;

    @EJB
    private CatalogStore catalogStoreSessionBean;


    private static boolean isChanged(Object oldObject, Object newObject) {
        if (oldObject == null) {
            return newObject != null;
        }
        return !oldObject.equals(newObject);
    }    
     

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(Catalog object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store Catalog
     */
    public Catalog store(Catalog oldCatalog, Catalog newCatalog, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        Catalog result = store(oldCatalog, newCatalog, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldCatalog, newCatalog, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store Catalog
     */
    public Catalog store(Catalog oldCatalog, Catalog newCatalog, Catalog targetCatalog, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetCatalog;
        }
        // Create new object or get the actual object from the database 
        if(targetCatalog == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldCatalog == null && newCatalog == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldCatalog == null) {
                targetCatalog = (Catalog) storeState.getTargetFromNewToTargetMap(newCatalog);
                if (targetCatalog == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldCatalog = new Catalog();
                    targetCatalog = new Catalog();

                    targetCatalog.setId(newCatalog.getId());
                    entityManager.persist(targetCatalog);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newCatalog, targetCatalog);
                } else {
                    // The targetCatalog has already been created during the first run
                    oldCatalog = new Catalog();
                }
            } else {
                if (newCatalog != null && isChanged(oldCatalog.getId(), newCatalog.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldCatalog.getId() + ", new value: " + newCatalog.getId());
                }
                if (!hasAllIdFields(oldCatalog)) {
                    throw new RuntimeException("No key exception: oldCatalog has no key.");
                } else {
                    targetCatalog = entityManager.find(Catalog.class, oldCatalog.getId());
                    if (targetCatalog == null) {
                        throw new ConcurrentModificationException("Catalog with key " + oldCatalog.getId() + " does not exist, while oldCatalog != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetCatalog);

        if (newCatalog != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(Catalog.Properties.name)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.name)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property name
                        // Update name field
                        if (isChanged(oldCatalog.getName(), newCatalog.getName())) {
                            if (!isChanged(targetCatalog.getName(), oldCatalog.getName())) {
                                targetCatalog.setName(newCatalog.getName());
                            } else {
                                throw new ConcurrentModificationException("name: database value is: " + targetCatalog.getName() + " while old value is: " + oldCatalog.getName() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.products)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.products)) {
                    StoreView productsView = requestedProperties.findView(Catalog.Properties.products);
                    if (productsView != null) {
                        // Handle collection of references products

                        // Remove
                        for (Product oldProducts : new ArrayList<Product>(oldCatalog.getProducts())) {
                            if (!newCatalog.getProducts().contains(oldProducts)) {
                                // Check if oldProducts, which will be removed, still exists in the database
                                Product targetProducts = entityManager.find(Product.class, oldProducts.getId());
                                if (targetProducts == null) {
                                    throw new ConcurrentModificationException("Product with key " + oldProducts.getId() +  " does not exist.");
                                }

                                // Ensure targetCatalog still contains the reference to targetProducts
                                // otherwise it is not valid to nullify the reference from targetProducts to targetCatalog
                                if (!targetCatalog.getProducts().contains(targetProducts)) {
                                    throw new ConcurrentModificationException("Product with key " + targetProducts.getId() +  " is not associated to targetCatalog.");
                                }

                                targetCatalog.getProducts().remove(targetProducts);
                                if (requestedProperties.forceComposite(Catalog.Properties.products)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Product)oldProducts, (Product) null, (Product)targetProducts, productsView, storeState);
                                } else {
                                    targetProducts.setCatalog(null);
                                }
                            }
                        }

                        // Add or update
                        for (Product newProducts : newCatalog.getProducts()) {
                            if (!oldCatalog.getProducts().contains(newProducts)) {

                                if (requestedProperties.forceComposite(Catalog.Properties.products)) {
                                    // Add or update as composite (add if targetProducts == null)
                                    Product targetProducts = catalogStoreSessionBean.store((Product)null, (Product) newProducts, (Product)null, productsView, storeState);
                                    targetCatalog.getProducts().add(targetProducts);
                                    // Update bidirectional reference: 'catalog'
                                    if (targetProducts != null) { 
                                        targetProducts.setCatalog(targetCatalog);
                                    }
                                } else {
                                    Product targetProducts = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProducts)) {
                                        // The newProducts id is given, find the object 
                                        targetProducts = entityManager.find(Product.class, newProducts.getId());
                                        if (targetProducts == null) {
                                            throw new ConcurrentModificationException("Product with key " + newProducts.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProducts != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProducts = (Product) storeState.getTargetFromNewToTargetMap(newProducts);
                                        if (targetProducts == null || !catalogStoreSessionBean.hasAllIdFields(targetProducts)) {
                                            throw new IllegalStateException("Product " + newProducts + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProducts != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetCatalog.getProducts().add(targetProducts);
                                        // Update bidirectional reference: 'catalog'
                                        targetProducts.setCatalog(targetCatalog);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Product " + newProducts + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProducts is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Catalog.Properties.products)) {
                                    for (Product oldProducts : oldCatalog.getProducts()) {
                                        if (newProducts != null && newProducts.equals(oldProducts)) {
                                            // Update
                                            Product targetProducts = entityManager.find(Product.class, newProducts.getId());
                                            if (targetProducts == null) {
                                                throw new ConcurrentModificationException("Product with key " + newProducts.getId() + " does not exist.");
                                            }
                                            targetProducts = catalogStoreSessionBean.store((Product)oldProducts, (Product) newProducts, (Product)targetProducts, productsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.productGroups)) {
                    StoreView productGroupsView = requestedProperties.findView(Catalog.Properties.productGroups);
                    if (productGroupsView != null) {
                        // Handle collection of references productGroups

                        // Remove
                        for (ProductGroup oldProductGroups : new ArrayList<ProductGroup>(oldCatalog.getProductGroups())) {
                            if (!newCatalog.getProductGroups().contains(oldProductGroups)) {
                                // Check if oldProductGroups, which will be removed, still exists in the database
                                ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, oldProductGroups.getId());
                                if (targetProductGroups == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldProductGroups.getId() +  " does not exist.");
                                }

                                // Ensure targetCatalog still contains the reference to targetProductGroups
                                // otherwise it is not valid to nullify the reference from targetProductGroups to targetCatalog
                                if (!targetCatalog.getProductGroups().contains(targetProductGroups)) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + targetProductGroups.getId() +  " is not associated to targetCatalog.");
                                }

                                targetCatalog.getProductGroups().remove(targetProductGroups);
                                if (requestedProperties.forceComposite(Catalog.Properties.productGroups)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                } else {
                                    targetProductGroups.setCatalog(null);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newProductGroups : newCatalog.getProductGroups()) {
                            if (!oldCatalog.getProductGroups().contains(newProductGroups)) {

                                if (requestedProperties.forceComposite(Catalog.Properties.productGroups)) {
                                    // Add or update as composite (add if targetProductGroups == null)
                                    ProductGroup targetProductGroups = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newProductGroups, (ProductGroup)null, productGroupsView, storeState);
                                    targetCatalog.getProductGroups().add(targetProductGroups);
                                    // Update bidirectional reference: 'catalog'
                                    if (targetProductGroups != null) { 
                                        targetProductGroups.setCatalog(targetCatalog);
                                    }
                                } else {
                                    ProductGroup targetProductGroups = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProductGroups)) {
                                        // The newProductGroups id is given, find the object 
                                        targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                        if (targetProductGroups == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProductGroups != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProductGroups = (ProductGroup) storeState.getTargetFromNewToTargetMap(newProductGroups);
                                        if (targetProductGroups == null || !catalogStoreSessionBean.hasAllIdFields(targetProductGroups)) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProductGroups != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetCatalog.getProductGroups().add(targetProductGroups);
                                        // Update bidirectional reference: 'catalog'
                                        targetProductGroups.setCatalog(targetCatalog);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProductGroups is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Catalog.Properties.productGroups)) {
                                    for (ProductGroup oldProductGroups : oldCatalog.getProductGroups()) {
                                        if (newProductGroups != null && newProductGroups.equals(oldProductGroups)) {
                                            // Update
                                            ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                            if (targetProductGroups == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                            }
                                            targetProductGroups = catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) newProductGroups, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.properties)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.properties)) {
                    StoreView propertiesView = requestedProperties.findView(Catalog.Properties.properties);
                    if (propertiesView != null) {
                        // Handle collection of references properties

                        // Remove
                        for (Property oldProperties : new ArrayList<Property>(oldCatalog.getProperties())) {
                            if (!newCatalog.getProperties().contains(oldProperties)) {
                                // Check if oldProperties, which will be removed, still exists in the database
                                Property targetProperties = entityManager.find(Property.class, oldProperties.getId());
                                if (targetProperties == null) {
                                    throw new ConcurrentModificationException("Property with key " + oldProperties.getId() +  " does not exist.");
                                }

                                // Ensure targetCatalog still contains the reference to targetProperties
                                // otherwise it is not valid to nullify the reference from targetProperties to targetCatalog
                                if (!targetCatalog.getProperties().contains(targetProperties)) {
                                    throw new ConcurrentModificationException("Property with key " + targetProperties.getId() +  " is not associated to targetCatalog.");
                                }

                                targetCatalog.getProperties().remove(targetProperties);
                                if (requestedProperties.forceComposite(Catalog.Properties.properties)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Property)oldProperties, (Property) null, (Property)targetProperties, propertiesView, storeState);
                                } else {
                                    targetProperties.setCatalog(null);
                                }
                            }
                        }

                        // Add or update
                        for (Property newProperties : newCatalog.getProperties()) {
                            if (!oldCatalog.getProperties().contains(newProperties)) {

                                if (requestedProperties.forceComposite(Catalog.Properties.properties)) {
                                    // Add or update as composite (add if targetProperties == null)
                                    Property targetProperties = catalogStoreSessionBean.store((Property)null, (Property) newProperties, (Property)null, propertiesView, storeState);
                                    targetCatalog.getProperties().add(targetProperties);
                                    // Update bidirectional reference: 'catalog'
                                    if (targetProperties != null) { 
                                        targetProperties.setCatalog(targetCatalog);
                                    }
                                } else {
                                    Property targetProperties = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProperties)) {
                                        // The newProperties id is given, find the object 
                                        targetProperties = entityManager.find(Property.class, newProperties.getId());
                                        if (targetProperties == null) {
                                            throw new ConcurrentModificationException("Property with key " + newProperties.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProperties != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProperties = (Property) storeState.getTargetFromNewToTargetMap(newProperties);
                                        if (targetProperties == null || !catalogStoreSessionBean.hasAllIdFields(targetProperties)) {
                                            throw new IllegalStateException("Property " + newProperties + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProperties != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetCatalog.getProperties().add(targetProperties);
                                        // Update bidirectional reference: 'catalog'
                                        targetProperties.setCatalog(targetCatalog);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Property " + newProperties + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProperties is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Catalog.Properties.properties)) {
                                    for (Property oldProperties : oldCatalog.getProperties()) {
                                        if (newProperties != null && newProperties.equals(oldProperties)) {
                                            // Update
                                            Property targetProperties = entityManager.find(Property.class, newProperties.getId());
                                            if (targetProperties == null) {
                                                throw new ConcurrentModificationException("Property with key " + newProperties.getId() + " does not exist.");
                                            }
                                            targetProperties = catalogStoreSessionBean.store((Property)oldProperties, (Property) newProperties, (Property)targetProperties, propertiesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.taxonomies)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.taxonomies)) {
                    StoreView taxonomiesView = requestedProperties.findView(Catalog.Properties.taxonomies);
                    if (taxonomiesView != null) {
                        // Handle collection of references taxonomies

                        // Remove
                        for (Taxonomy oldTaxonomies : new ArrayList<Taxonomy>(oldCatalog.getTaxonomies())) {
                            if (!newCatalog.getTaxonomies().contains(oldTaxonomies)) {
                                // Check if oldTaxonomies, which will be removed, still exists in the database
                                Taxonomy targetTaxonomies = entityManager.find(Taxonomy.class, oldTaxonomies.getId());
                                if (targetTaxonomies == null) {
                                    throw new ConcurrentModificationException("Taxonomy with key " + oldTaxonomies.getId() +  " does not exist.");
                                }

                                // Ensure targetCatalog still contains the reference to targetTaxonomies
                                // otherwise it is not valid to nullify the reference from targetTaxonomies to targetCatalog
                                if (!targetCatalog.getTaxonomies().contains(targetTaxonomies)) {
                                    throw new ConcurrentModificationException("Taxonomy with key " + targetTaxonomies.getId() +  " is not associated to targetCatalog.");
                                }

                                targetCatalog.getTaxonomies().remove(targetTaxonomies);
                                if (requestedProperties.forceComposite(Catalog.Properties.taxonomies)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Taxonomy)oldTaxonomies, (Taxonomy) null, (Taxonomy)targetTaxonomies, taxonomiesView, storeState);
                                } else {
                                    targetTaxonomies.setCatalog(null);
                                }
                            }
                        }

                        // Add or update
                        for (Taxonomy newTaxonomies : newCatalog.getTaxonomies()) {
                            if (!oldCatalog.getTaxonomies().contains(newTaxonomies)) {

                                if (requestedProperties.forceComposite(Catalog.Properties.taxonomies)) {
                                    // Add or update as composite (add if targetTaxonomies == null)
                                    Taxonomy targetTaxonomies = catalogStoreSessionBean.store((Taxonomy)null, (Taxonomy) newTaxonomies, (Taxonomy)null, taxonomiesView, storeState);
                                    targetCatalog.getTaxonomies().add(targetTaxonomies);
                                    // Update bidirectional reference: 'catalog'
                                    if (targetTaxonomies != null) { 
                                        targetTaxonomies.setCatalog(targetCatalog);
                                    }
                                } else {
                                    Taxonomy targetTaxonomies = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newTaxonomies)) {
                                        // The newTaxonomies id is given, find the object 
                                        targetTaxonomies = entityManager.find(Taxonomy.class, newTaxonomies.getId());
                                        if (targetTaxonomies == null) {
                                            throw new ConcurrentModificationException("Taxonomy with key " + newTaxonomies.getId() + " does not exist.");
                                        }
                                    }

                                    if (newTaxonomies != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetTaxonomies = (Taxonomy) storeState.getTargetFromNewToTargetMap(newTaxonomies);
                                        if (targetTaxonomies == null || !catalogStoreSessionBean.hasAllIdFields(targetTaxonomies)) {
                                            throw new IllegalStateException("Taxonomy " + newTaxonomies + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetTaxonomies != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetCatalog.getTaxonomies().add(targetTaxonomies);
                                        // Update bidirectional reference: 'catalog'
                                        targetTaxonomies.setCatalog(targetCatalog);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Taxonomy " + newTaxonomies + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetTaxonomies is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Catalog.Properties.taxonomies)) {
                                    for (Taxonomy oldTaxonomies : oldCatalog.getTaxonomies()) {
                                        if (newTaxonomies != null && newTaxonomies.equals(oldTaxonomies)) {
                                            // Update
                                            Taxonomy targetTaxonomies = entityManager.find(Taxonomy.class, newTaxonomies.getId());
                                            if (targetTaxonomies == null) {
                                                throw new ConcurrentModificationException("Taxonomy with key " + newTaxonomies.getId() + " does not exist.");
                                            }
                                            targetTaxonomies = catalogStoreSessionBean.store((Taxonomy)oldTaxonomies, (Taxonomy) newTaxonomies, (Taxonomy)targetTaxonomies, taxonomiesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.productRelations)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.productRelations)) {
                    StoreView productRelationsView = requestedProperties.findView(Catalog.Properties.productRelations);
                    if (productRelationsView != null) {
                        // Handle collection of references productRelations

                        // Remove
                        for (ProductRelation oldProductRelations : new ArrayList<ProductRelation>(oldCatalog.getProductRelations())) {
                            if (!newCatalog.getProductRelations().contains(oldProductRelations)) {
                                // Check if oldProductRelations, which will be removed, still exists in the database
                                ProductRelation targetProductRelations = entityManager.find(ProductRelation.class, oldProductRelations.getId());
                                if (targetProductRelations == null) {
                                    throw new ConcurrentModificationException("ProductRelation with key " + oldProductRelations.getId() +  " does not exist.");
                                }


                                targetCatalog.getProductRelations().remove(targetProductRelations);
                                if (requestedProperties.forceComposite(Catalog.Properties.productRelations)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductRelation)oldProductRelations, (ProductRelation) null, (ProductRelation)targetProductRelations, productRelationsView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (ProductRelation newProductRelations : newCatalog.getProductRelations()) {
                            if (!oldCatalog.getProductRelations().contains(newProductRelations)) {

                                if (requestedProperties.forceComposite(Catalog.Properties.productRelations)) {
                                    // Add or update as composite (add if targetProductRelations == null)
                                    ProductRelation targetProductRelations = catalogStoreSessionBean.store((ProductRelation)null, (ProductRelation) newProductRelations, (ProductRelation)null, productRelationsView, storeState);
                                    targetCatalog.getProductRelations().add(targetProductRelations);
                                } else {
                                    ProductRelation targetProductRelations = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProductRelations)) {
                                        // The newProductRelations id is given, find the object 
                                        targetProductRelations = entityManager.find(ProductRelation.class, newProductRelations.getId());
                                        if (targetProductRelations == null) {
                                            throw new ConcurrentModificationException("ProductRelation with key " + newProductRelations.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProductRelations != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProductRelations = (ProductRelation) storeState.getTargetFromNewToTargetMap(newProductRelations);
                                        if (targetProductRelations == null || !catalogStoreSessionBean.hasAllIdFields(targetProductRelations)) {
                                            throw new IllegalStateException("ProductRelation " + newProductRelations + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProductRelations != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetCatalog.getProductRelations().add(targetProductRelations);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductRelation " + newProductRelations + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProductRelations is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Catalog.Properties.productRelations)) {
                                    for (ProductRelation oldProductRelations : oldCatalog.getProductRelations()) {
                                        if (newProductRelations != null && newProductRelations.equals(oldProductRelations)) {
                                            // Update
                                            ProductRelation targetProductRelations = entityManager.find(ProductRelation.class, newProductRelations.getId());
                                            if (targetProductRelations == null) {
                                                throw new ConcurrentModificationException("ProductRelation with key " + newProductRelations.getId() + " does not exist.");
                                            }
                                            targetProductRelations = catalogStoreSessionBean.store((ProductRelation)oldProductRelations, (ProductRelation) newProductRelations, (ProductRelation)targetProductRelations, productRelationsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetCatalog);
            if (requestedProperties.includedProperties().contains(Catalog.Properties.products)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.products)) {
                    // Remove reference: products
                    for (Product targetProducts : targetCatalog.getProducts()) {
                        if (requestedProperties.forceComposite(Catalog.Properties.products)) {
                            // Remove as composite
                            StoreView productsView = requestedProperties.findView(Catalog.Properties.products);
                            catalogStoreSessionBean.store((Product)targetProducts, (Product) null, (Product)targetProducts, productsView, storeState);
                        } else {
                            targetProducts.setCatalog(null);
                        }
                    }
                    targetCatalog.getProducts().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.productGroups)) {
                    // Remove reference: productGroups
                    for (ProductGroup targetProductGroups : targetCatalog.getProductGroups()) {
                        if (requestedProperties.forceComposite(Catalog.Properties.productGroups)) {
                            // Remove as composite
                            StoreView productGroupsView = requestedProperties.findView(Catalog.Properties.productGroups);
                            catalogStoreSessionBean.store((ProductGroup)targetProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                        } else {
                            targetProductGroups.setCatalog(null);
                        }
                    }
                    targetCatalog.getProductGroups().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.properties)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.properties)) {
                    // Remove reference: properties
                    for (Property targetProperties : targetCatalog.getProperties()) {
                        if (requestedProperties.forceComposite(Catalog.Properties.properties)) {
                            // Remove as composite
                            StoreView propertiesView = requestedProperties.findView(Catalog.Properties.properties);
                            catalogStoreSessionBean.store((Property)targetProperties, (Property) null, (Property)targetProperties, propertiesView, storeState);
                        } else {
                            targetProperties.setCatalog(null);
                        }
                    }
                    targetCatalog.getProperties().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.taxonomies)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.taxonomies)) {
                    // Remove reference: taxonomies
                    for (Taxonomy targetTaxonomies : targetCatalog.getTaxonomies()) {
                        if (requestedProperties.forceComposite(Catalog.Properties.taxonomies)) {
                            // Remove as composite
                            StoreView taxonomiesView = requestedProperties.findView(Catalog.Properties.taxonomies);
                            catalogStoreSessionBean.store((Taxonomy)targetTaxonomies, (Taxonomy) null, (Taxonomy)targetTaxonomies, taxonomiesView, storeState);
                        } else {
                            targetTaxonomies.setCatalog(null);
                        }
                    }
                    targetCatalog.getTaxonomies().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Catalog.Properties.productRelations)) {
                if (processedProperties.includedProperties().add(Catalog.Properties.productRelations)) {
                    // Remove reference: productRelations
                    targetCatalog.getProductRelations().clear();
                }
            }
            targetCatalog = null;
        }

        return targetCatalog;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(Product object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store Product
     */
    public Product store(Product oldProduct, Product newProduct, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        Product result = store(oldProduct, newProduct, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldProduct, newProduct, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store Product
     */
    public Product store(Product oldProduct, Product newProduct, Product targetProduct, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetProduct;
        }
        // Create new object or get the actual object from the database 
        if(targetProduct == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldProduct == null && newProduct == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldProduct == null) {
                targetProduct = (Product) storeState.getTargetFromNewToTargetMap(newProduct);
                if (targetProduct == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldProduct = new Product();
                    targetProduct = new Product();

                    targetProduct.setId(newProduct.getId());
                    entityManager.persist(targetProduct);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newProduct, targetProduct);
                } else {
                    // The targetProduct has already been created during the first run
                    oldProduct = new Product();
                }
            } else {
                if (newProduct != null && isChanged(oldProduct.getId(), newProduct.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldProduct.getId() + ", new value: " + newProduct.getId());
                }
                if (!hasAllIdFields(oldProduct)) {
                    throw new RuntimeException("No key exception: oldProduct has no key.");
                } else {
                    targetProduct = entityManager.find(Product.class, oldProduct.getId());
                    if (targetProduct == null) {
                        throw new ConcurrentModificationException("Product with key " + oldProduct.getId() + " does not exist, while oldProduct != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetProduct);

        if (newProduct != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(Product.Properties.catalog)) {
                if (processedProperties.includedProperties().add(Product.Properties.catalog)) {
                    StoreView catalogView = requestedProperties.findView(Product.Properties.catalog);
                    if (catalogView != null) {
                        // Handle reference catalog
                        if (oldProduct.getCatalog() != null && newProduct.getCatalog() == null) {
                            // Remove
                            Catalog targetCatalog = targetProduct.getCatalog();

                            if (targetCatalog == null) {
                                throw new ConcurrentModificationException("oldProduct.getCatalog() with value: " + oldProduct.getCatalog() + " does not exist in the database.");
                            } else if(!targetCatalog.equals(oldProduct.getCatalog())) {
                                throw new ConcurrentModificationException("targetProduct.getCatalog() with value: " + targetProduct.getCatalog() + " is not equal to oldProduct.getCatalog() with value: " + oldProduct.getCatalog() + ".");
                            }

                            if (requestedProperties.forceComposite(Product.Properties.catalog)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((Catalog)oldProduct.getCatalog(), (Catalog) null, (Catalog)targetCatalog, catalogView, storeState);
                                targetProduct.setCatalog(null);
                            } else {
                                targetProduct.setCatalog(null);
                            }
                        } else {
                            // Add or update
                            Catalog newCatalog = newProduct.getCatalog();

                            if (newCatalog != null) {
                                if (requestedProperties.forceComposite(Product.Properties.catalog)) {
                                    // Update as composite
                                    if (oldProduct.getCatalog() != null && oldProduct.getCatalog().equals(newCatalog)) {
                                        catalogStoreSessionBean.store((Catalog)oldProduct.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldProduct.getCatalog() != null) {
                                            catalogStoreSessionBean.store((Catalog)oldProduct.getCatalog(), (Catalog) null, (Catalog)null, catalogView, storeState);
                                        }

                                        // Add 
                                        Catalog targetCatalog = catalogStoreSessionBean.store((Catalog)null, (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                        targetProduct.setCatalog(targetCatalog);
                                        // Update bidirectional reference: 'products'
                                        if (targetCatalog != null) { 
                                            targetCatalog.getProducts().add(targetProduct);
                                        }
                                    }
                                } else {
                                    if (oldProduct.getCatalog() != null && oldProduct.getCatalog().equals(newCatalog)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((Catalog)oldProduct.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        Catalog targetCatalog = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newCatalog)) {
                                            // The newCatalog id is given, find the object 
                                            targetCatalog = entityManager.find(Catalog.class, newCatalog.getId());
                                            if (targetCatalog == null) {
                                                throw new ConcurrentModificationException("Catalog with key " + newCatalog.getId() + " does not exist.");
                                            }
                                        }

                                        if (newCatalog != null) {
                                            // The reference is updated
                                            if (targetCatalog == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetCatalog = (Catalog) storeState.getTargetFromNewToTargetMap(newCatalog);
                                                if (targetCatalog == null || !catalogStoreSessionBean.hasAllIdFields(targetCatalog)) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetCatalog != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetProduct.setCatalog(targetCatalog);
                                                // Update bidirectional reference: 'products'
                                                targetCatalog.getProducts().add(targetProduct);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetCatalog is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetProduct.setCatalog(null);
                                            // Update bidirectional reference: 'products'
                                            if (targetCatalog != null) {
                                                targetCatalog.getProducts().remove(targetProduct);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.propertyValues)) {
                if (processedProperties.includedProperties().add(Product.Properties.propertyValues)) {
                    StoreView propertyValuesView = requestedProperties.findView(Product.Properties.propertyValues);
                    if (propertyValuesView != null) {
                        // Handle collection of references propertyValues

                        // Remove
                        for (PropertyValue oldPropertyValues : new ArrayList<PropertyValue>(oldProduct.getPropertyValues())) {
                            if (!newProduct.getPropertyValues().contains(oldPropertyValues)) {
                                // Check if oldPropertyValues, which will be removed, still exists in the database
                                PropertyValue targetPropertyValues = entityManager.find(PropertyValue.class, oldPropertyValues.getId());
                                if (targetPropertyValues == null) {
                                    throw new ConcurrentModificationException("PropertyValue with key " + oldPropertyValues.getId() +  " does not exist.");
                                }

                                // Ensure targetProduct still contains the reference to targetPropertyValues
                                // otherwise it is not valid to nullify the reference from targetPropertyValues to targetProduct
                                if (!targetProduct.getPropertyValues().contains(targetPropertyValues)) {
                                    throw new ConcurrentModificationException("PropertyValue with key " + targetPropertyValues.getId() +  " is not associated to targetProduct.");
                                }

                                targetProduct.getPropertyValues().remove(targetPropertyValues);
                                if (requestedProperties.forceComposite(Product.Properties.propertyValues)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((PropertyValue)oldPropertyValues, (PropertyValue) null, (PropertyValue)targetPropertyValues, propertyValuesView, storeState);
                                } else {
                                    targetPropertyValues.setProduct(null);
                                }
                            }
                        }

                        // Add or update
                        for (PropertyValue newPropertyValues : newProduct.getPropertyValues()) {
                            if (!oldProduct.getPropertyValues().contains(newPropertyValues)) {

                                if (requestedProperties.forceComposite(Product.Properties.propertyValues)) {
                                    // Add or update as composite (add if targetPropertyValues == null)
                                    PropertyValue targetPropertyValues = catalogStoreSessionBean.store((PropertyValue)null, (PropertyValue) newPropertyValues, (PropertyValue)null, propertyValuesView, storeState);
                                    targetProduct.getPropertyValues().add(targetPropertyValues);
                                    // Update bidirectional reference: 'product'
                                    if (targetPropertyValues != null) { 
                                        targetPropertyValues.setProduct(targetProduct);
                                    }
                                } else {
                                    PropertyValue targetPropertyValues = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newPropertyValues)) {
                                        // The newPropertyValues id is given, find the object 
                                        targetPropertyValues = entityManager.find(PropertyValue.class, newPropertyValues.getId());
                                        if (targetPropertyValues == null) {
                                            throw new ConcurrentModificationException("PropertyValue with key " + newPropertyValues.getId() + " does not exist.");
                                        }
                                    }

                                    if (newPropertyValues != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetPropertyValues = (PropertyValue) storeState.getTargetFromNewToTargetMap(newPropertyValues);
                                        if (targetPropertyValues == null || !catalogStoreSessionBean.hasAllIdFields(targetPropertyValues)) {
                                            throw new IllegalStateException("PropertyValue " + newPropertyValues + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetPropertyValues != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProduct.getPropertyValues().add(targetPropertyValues);
                                        // Update bidirectional reference: 'product'
                                        targetPropertyValues.setProduct(targetProduct);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("PropertyValue " + newPropertyValues + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetPropertyValues is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Product.Properties.propertyValues)) {
                                    for (PropertyValue oldPropertyValues : oldProduct.getPropertyValues()) {
                                        if (newPropertyValues != null && newPropertyValues.equals(oldPropertyValues)) {
                                            // Update
                                            PropertyValue targetPropertyValues = entityManager.find(PropertyValue.class, newPropertyValues.getId());
                                            if (targetPropertyValues == null) {
                                                throw new ConcurrentModificationException("PropertyValue with key " + newPropertyValues.getId() + " does not exist.");
                                            }
                                            targetPropertyValues = catalogStoreSessionBean.store((PropertyValue)oldPropertyValues, (PropertyValue) newPropertyValues, (PropertyValue)targetPropertyValues, propertyValuesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Product.Properties.productGroups)) {
                    StoreView productGroupsView = requestedProperties.findView(Product.Properties.productGroups);
                    if (productGroupsView != null) {
                        // Handle collection of references productGroups

                        // Remove
                        for (ProductGroup oldProductGroups : new ArrayList<ProductGroup>(oldProduct.getProductGroups())) {
                            if (!newProduct.getProductGroups().contains(oldProductGroups)) {
                                // Check if oldProductGroups, which will be removed, still exists in the database
                                ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, oldProductGroups.getId());
                                if (targetProductGroups == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldProductGroups.getId() +  " does not exist.");
                                }

                                // Ensure targetProduct still contains the reference to targetProductGroups
                                // otherwise it is not valid to nullify the reference from targetProductGroups to targetProduct
                                if (!targetProduct.getProductGroups().contains(targetProductGroups)) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + targetProductGroups.getId() +  " is not associated to targetProduct.");
                                }

                                targetProduct.getProductGroups().remove(targetProductGroups);
                                if (requestedProperties.forceComposite(Product.Properties.productGroups)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                } else {
                                    targetProductGroups.getProducts().remove(targetProduct);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newProductGroups : newProduct.getProductGroups()) {
                            if (!oldProduct.getProductGroups().contains(newProductGroups)) {

                                if (requestedProperties.forceComposite(Product.Properties.productGroups)) {
                                    // Add or update as composite (add if targetProductGroups == null)
                                    ProductGroup targetProductGroups = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newProductGroups, (ProductGroup)null, productGroupsView, storeState);
                                    targetProduct.getProductGroups().add(targetProductGroups);
                                    // Update bidirectional reference: 'products'
                                    if (targetProductGroups != null) { 
                                        targetProductGroups.getProducts().add(targetProduct);
                                    }
                                } else {
                                    ProductGroup targetProductGroups = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProductGroups)) {
                                        // The newProductGroups id is given, find the object 
                                        targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                        if (targetProductGroups == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProductGroups != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProductGroups = (ProductGroup) storeState.getTargetFromNewToTargetMap(newProductGroups);
                                        if (targetProductGroups == null || !catalogStoreSessionBean.hasAllIdFields(targetProductGroups)) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProductGroups != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProduct.getProductGroups().add(targetProductGroups);
                                        // Update bidirectional reference: 'products'
                                        targetProductGroups.getProducts().add(targetProduct);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProductGroups is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Product.Properties.productGroups)) {
                                    for (ProductGroup oldProductGroups : oldProduct.getProductGroups()) {
                                        if (newProductGroups != null && newProductGroups.equals(oldProductGroups)) {
                                            // Update
                                            ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                            if (targetProductGroups == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                            }
                                            targetProductGroups = catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) newProductGroups, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.relatedProducts)) {
                if (processedProperties.includedProperties().add(Product.Properties.relatedProducts)) {
                    StoreView relatedProductsView = requestedProperties.findView(Product.Properties.relatedProducts);
                    if (relatedProductsView != null) {
                        // Handle collection of references relatedProducts

                        // Remove
                        for (RelatedProduct oldRelatedProducts : new ArrayList<RelatedProduct>(oldProduct.getRelatedProducts())) {
                            if (!newProduct.getRelatedProducts().contains(oldRelatedProducts)) {
                                // Check if oldRelatedProducts, which will be removed, still exists in the database
                                RelatedProduct targetRelatedProducts = entityManager.find(RelatedProduct.class, oldRelatedProducts.getId());
                                if (targetRelatedProducts == null) {
                                    throw new ConcurrentModificationException("RelatedProduct with key " + oldRelatedProducts.getId() +  " does not exist.");
                                }


                                targetProduct.getRelatedProducts().remove(targetRelatedProducts);
                                if (requestedProperties.forceComposite(Product.Properties.relatedProducts)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((RelatedProduct)oldRelatedProducts, (RelatedProduct) null, (RelatedProduct)targetRelatedProducts, relatedProductsView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (RelatedProduct newRelatedProducts : newProduct.getRelatedProducts()) {
                            if (!oldProduct.getRelatedProducts().contains(newRelatedProducts)) {

                                if (requestedProperties.forceComposite(Product.Properties.relatedProducts)) {
                                    // Add or update as composite (add if targetRelatedProducts == null)
                                    RelatedProduct targetRelatedProducts = catalogStoreSessionBean.store((RelatedProduct)null, (RelatedProduct) newRelatedProducts, (RelatedProduct)null, relatedProductsView, storeState);
                                    targetProduct.getRelatedProducts().add(targetRelatedProducts);
                                } else {
                                    RelatedProduct targetRelatedProducts = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newRelatedProducts)) {
                                        // The newRelatedProducts id is given, find the object 
                                        targetRelatedProducts = entityManager.find(RelatedProduct.class, newRelatedProducts.getId());
                                        if (targetRelatedProducts == null) {
                                            throw new ConcurrentModificationException("RelatedProduct with key " + newRelatedProducts.getId() + " does not exist.");
                                        }
                                    }

                                    if (newRelatedProducts != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetRelatedProducts = (RelatedProduct) storeState.getTargetFromNewToTargetMap(newRelatedProducts);
                                        if (targetRelatedProducts == null || !catalogStoreSessionBean.hasAllIdFields(targetRelatedProducts)) {
                                            throw new IllegalStateException("RelatedProduct " + newRelatedProducts + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetRelatedProducts != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProduct.getRelatedProducts().add(targetRelatedProducts);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("RelatedProduct " + newRelatedProducts + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetRelatedProducts is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Product.Properties.relatedProducts)) {
                                    for (RelatedProduct oldRelatedProducts : oldProduct.getRelatedProducts()) {
                                        if (newRelatedProducts != null && newRelatedProducts.equals(oldRelatedProducts)) {
                                            // Update
                                            RelatedProduct targetRelatedProducts = entityManager.find(RelatedProduct.class, newRelatedProducts.getId());
                                            if (targetRelatedProducts == null) {
                                                throw new ConcurrentModificationException("RelatedProduct with key " + newRelatedProducts.getId() + " does not exist.");
                                            }
                                            targetRelatedProducts = catalogStoreSessionBean.store((RelatedProduct)oldRelatedProducts, (RelatedProduct) newRelatedProducts, (RelatedProduct)targetRelatedProducts, relatedProductsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetProduct);

            // remove RelatedProduct.product references
            Query relatedProductProductQuery = entityManager.createQuery("SELECT r FROM RelatedProduct r WHERE ?1 MEMBER OF r.product"); 
            relatedProductProductQuery.setParameter(1, targetProduct); 
            for (Object object : relatedProductProductQuery.getResultList()) { 
                ((RelatedProduct) object).getProduct().remove(targetProduct);
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.catalog)) {
                if (processedProperties.includedProperties().add(Product.Properties.catalog)) {
                    // Remove referring reference: catalog
                    targetProduct.getCatalog().getProducts().remove(targetProduct);
                }
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.propertyValues)) {
                if (processedProperties.includedProperties().add(Product.Properties.propertyValues)) {
                    // Remove reference: propertyValues
                    for (PropertyValue targetPropertyValues : targetProduct.getPropertyValues()) {
                        if (requestedProperties.forceComposite(Product.Properties.propertyValues)) {
                            // Remove as composite
                            StoreView propertyValuesView = requestedProperties.findView(Product.Properties.propertyValues);
                            catalogStoreSessionBean.store((PropertyValue)targetPropertyValues, (PropertyValue) null, (PropertyValue)targetPropertyValues, propertyValuesView, storeState);
                        } else {
                            targetPropertyValues.setProduct(null);
                        }
                    }
                    targetProduct.getPropertyValues().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Product.Properties.productGroups)) {
                    // Remove reference: productGroups
                    for (ProductGroup targetProductGroups : targetProduct.getProductGroups()) {
                        if (requestedProperties.forceComposite(Product.Properties.productGroups)) {
                            // Remove as composite
                            StoreView productGroupsView = requestedProperties.findView(Product.Properties.productGroups);
                            catalogStoreSessionBean.store((ProductGroup)targetProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                        } else {
                            targetProductGroups.getProducts().remove(targetProduct);
                        }
                    }
                    targetProduct.getProductGroups().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Product.Properties.relatedProducts)) {
                if (processedProperties.includedProperties().add(Product.Properties.relatedProducts)) {
                    // Remove reference: relatedProducts
                    targetProduct.getRelatedProducts().clear();
                }
            }
            targetProduct = null;
        }

        return targetProduct;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(RelatedProduct object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store RelatedProduct
     */
    public RelatedProduct store(RelatedProduct oldRelatedProduct, RelatedProduct newRelatedProduct, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        RelatedProduct result = store(oldRelatedProduct, newRelatedProduct, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldRelatedProduct, newRelatedProduct, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store RelatedProduct
     */
    public RelatedProduct store(RelatedProduct oldRelatedProduct, RelatedProduct newRelatedProduct, RelatedProduct targetRelatedProduct, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetRelatedProduct;
        }
        // Create new object or get the actual object from the database 
        if(targetRelatedProduct == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldRelatedProduct == null && newRelatedProduct == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldRelatedProduct == null) {
                targetRelatedProduct = (RelatedProduct) storeState.getTargetFromNewToTargetMap(newRelatedProduct);
                if (targetRelatedProduct == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldRelatedProduct = new RelatedProduct();
                    targetRelatedProduct = new RelatedProduct();

                    targetRelatedProduct.setId(newRelatedProduct.getId());
                    entityManager.persist(targetRelatedProduct);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newRelatedProduct, targetRelatedProduct);
                } else {
                    // The targetRelatedProduct has already been created during the first run
                    oldRelatedProduct = new RelatedProduct();
                }
            } else {
                if (newRelatedProduct != null && isChanged(oldRelatedProduct.getId(), newRelatedProduct.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldRelatedProduct.getId() + ", new value: " + newRelatedProduct.getId());
                }
                if (!hasAllIdFields(oldRelatedProduct)) {
                    throw new RuntimeException("No key exception: oldRelatedProduct has no key.");
                } else {
                    targetRelatedProduct = entityManager.find(RelatedProduct.class, oldRelatedProduct.getId());
                    if (targetRelatedProduct == null) {
                        throw new ConcurrentModificationException("RelatedProduct with key " + oldRelatedProduct.getId() + " does not exist, while oldRelatedProduct != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetRelatedProduct);

        if (newRelatedProduct != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(RelatedProduct.Properties.product)) {
                if (processedProperties.includedProperties().add(RelatedProduct.Properties.product)) {
                    StoreView productView = requestedProperties.findView(RelatedProduct.Properties.product);
                    if (productView != null) {
                        // Handle collection of references product

                        // Remove
                        for (Product oldProduct : new ArrayList<Product>(oldRelatedProduct.getProduct())) {
                            if (!newRelatedProduct.getProduct().contains(oldProduct)) {
                                // Check if oldProduct, which will be removed, still exists in the database
                                Product targetProduct = entityManager.find(Product.class, oldProduct.getId());
                                if (targetProduct == null) {
                                    throw new ConcurrentModificationException("Product with key " + oldProduct.getId() +  " does not exist.");
                                }


                                targetRelatedProduct.getProduct().remove(targetProduct);
                                if (requestedProperties.forceComposite(RelatedProduct.Properties.product)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Product)oldProduct, (Product) null, (Product)targetProduct, productView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (Product newProduct : newRelatedProduct.getProduct()) {
                            if (!oldRelatedProduct.getProduct().contains(newProduct)) {

                                if (requestedProperties.forceComposite(RelatedProduct.Properties.product)) {
                                    // Add or update as composite (add if targetProduct == null)
                                    Product targetProduct = catalogStoreSessionBean.store((Product)null, (Product) newProduct, (Product)null, productView, storeState);
                                    targetRelatedProduct.getProduct().add(targetProduct);
                                } else {
                                    Product targetProduct = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProduct)) {
                                        // The newProduct id is given, find the object 
                                        targetProduct = entityManager.find(Product.class, newProduct.getId());
                                        if (targetProduct == null) {
                                            throw new ConcurrentModificationException("Product with key " + newProduct.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProduct != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProduct = (Product) storeState.getTargetFromNewToTargetMap(newProduct);
                                        if (targetProduct == null || !catalogStoreSessionBean.hasAllIdFields(targetProduct)) {
                                            throw new IllegalStateException("Product " + newProduct + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProduct != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetRelatedProduct.getProduct().add(targetProduct);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Product " + newProduct + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProduct is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(RelatedProduct.Properties.product)) {
                                    for (Product oldProduct : oldRelatedProduct.getProduct()) {
                                        if (newProduct != null && newProduct.equals(oldProduct)) {
                                            // Update
                                            Product targetProduct = entityManager.find(Product.class, newProduct.getId());
                                            if (targetProduct == null) {
                                                throw new ConcurrentModificationException("Product with key " + newProduct.getId() + " does not exist.");
                                            }
                                            targetProduct = catalogStoreSessionBean.store((Product)oldProduct, (Product) newProduct, (Product)targetProduct, productView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(RelatedProduct.Properties.relation)) {
                if (processedProperties.includedProperties().add(RelatedProduct.Properties.relation)) {
                    StoreView relationView = requestedProperties.findView(RelatedProduct.Properties.relation);
                    if (relationView != null) {
                        // Handle reference relation
                        if (oldRelatedProduct.getRelation() != null && newRelatedProduct.getRelation() == null) {
                            // Remove
                            ProductRelation targetRelation = targetRelatedProduct.getRelation();

                            if (targetRelation == null) {
                                throw new ConcurrentModificationException("oldRelatedProduct.getRelation() with value: " + oldRelatedProduct.getRelation() + " does not exist in the database.");
                            } else if(!targetRelation.equals(oldRelatedProduct.getRelation())) {
                                throw new ConcurrentModificationException("targetRelatedProduct.getRelation() with value: " + targetRelatedProduct.getRelation() + " is not equal to oldRelatedProduct.getRelation() with value: " + oldRelatedProduct.getRelation() + ".");
                            }

                            if (requestedProperties.forceComposite(RelatedProduct.Properties.relation)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((ProductRelation)oldRelatedProduct.getRelation(), (ProductRelation) null, (ProductRelation)targetRelation, relationView, storeState);
                                targetRelatedProduct.setRelation(null);
                            } else {
                                targetRelatedProduct.setRelation(null);
                            }
                        } else {
                            // Add or update
                            ProductRelation newRelation = newRelatedProduct.getRelation();

                            if (newRelation != null) {
                                if (requestedProperties.forceComposite(RelatedProduct.Properties.relation)) {
                                    // Update as composite
                                    if (oldRelatedProduct.getRelation() != null && oldRelatedProduct.getRelation().equals(newRelation)) {
                                        catalogStoreSessionBean.store((ProductRelation)oldRelatedProduct.getRelation(), (ProductRelation) newRelation, (ProductRelation)null, relationView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldRelatedProduct.getRelation() != null) {
                                            catalogStoreSessionBean.store((ProductRelation)oldRelatedProduct.getRelation(), (ProductRelation) null, (ProductRelation)null, relationView, storeState);
                                        }

                                        // Add 
                                        ProductRelation targetRelation = catalogStoreSessionBean.store((ProductRelation)null, (ProductRelation) newRelation, (ProductRelation)null, relationView, storeState);
                                        targetRelatedProduct.setRelation(targetRelation);
                                    }
                                } else {
                                    if (oldRelatedProduct.getRelation() != null && oldRelatedProduct.getRelation().equals(newRelation)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((ProductRelation)oldRelatedProduct.getRelation(), (ProductRelation) newRelation, (ProductRelation)null, relationView, storeState);
                                    } else {
                                        ProductRelation targetRelation = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newRelation)) {
                                            // The newRelation id is given, find the object 
                                            targetRelation = entityManager.find(ProductRelation.class, newRelation.getId());
                                            if (targetRelation == null) {
                                                throw new ConcurrentModificationException("ProductRelation with key " + newRelation.getId() + " does not exist.");
                                            }
                                        }

                                        if (newRelation != null) {
                                            // The reference is updated
                                            if (targetRelation == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetRelation = (ProductRelation) storeState.getTargetFromNewToTargetMap(newRelation);
                                                if (targetRelation == null || !catalogStoreSessionBean.hasAllIdFields(targetRelation)) {
                                                    throw new IllegalStateException("ProductRelation " + newRelation + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetRelation != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetRelatedProduct.setRelation(targetRelation);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("ProductRelation " + newRelation + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetRelation is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetRelatedProduct.setRelation(null);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetRelatedProduct);

            // remove Product.relatedProducts references
            Query productRelatedProductsQuery = entityManager.createQuery("SELECT p FROM Product p WHERE ?1 MEMBER OF p.relatedProducts"); 
            productRelatedProductsQuery.setParameter(1, targetRelatedProduct); 
            for (Object object : productRelatedProductsQuery.getResultList()) { 
                ((Product) object).getRelatedProducts().remove(targetRelatedProduct);
            }
            if (requestedProperties.includedProperties().contains(RelatedProduct.Properties.product)) {
                if (processedProperties.includedProperties().add(RelatedProduct.Properties.product)) {
                    // Remove reference: product
                    targetRelatedProduct.getProduct().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(RelatedProduct.Properties.relation)) {
                if (processedProperties.includedProperties().add(RelatedProduct.Properties.relation)) {
                }
            }
            targetRelatedProduct = null;
        }

        return targetRelatedProduct;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(ProductRelation object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store ProductRelation
     */
    public ProductRelation store(ProductRelation oldProductRelation, ProductRelation newProductRelation, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        ProductRelation result = store(oldProductRelation, newProductRelation, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldProductRelation, newProductRelation, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store ProductRelation
     */
    public ProductRelation store(ProductRelation oldProductRelation, ProductRelation newProductRelation, ProductRelation targetProductRelation, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetProductRelation;
        }
        // Create new object or get the actual object from the database 
        if(targetProductRelation == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldProductRelation == null && newProductRelation == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldProductRelation == null) {
                targetProductRelation = (ProductRelation) storeState.getTargetFromNewToTargetMap(newProductRelation);
                if (targetProductRelation == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldProductRelation = new ProductRelation();
                    targetProductRelation = new ProductRelation();

                    targetProductRelation.setId(newProductRelation.getId());
                    entityManager.persist(targetProductRelation);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newProductRelation, targetProductRelation);
                } else {
                    // The targetProductRelation has already been created during the first run
                    oldProductRelation = new ProductRelation();
                }
            } else {
                if (newProductRelation != null && isChanged(oldProductRelation.getId(), newProductRelation.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldProductRelation.getId() + ", new value: " + newProductRelation.getId());
                }
                if (!hasAllIdFields(oldProductRelation)) {
                    throw new RuntimeException("No key exception: oldProductRelation has no key.");
                } else {
                    targetProductRelation = entityManager.find(ProductRelation.class, oldProductRelation.getId());
                    if (targetProductRelation == null) {
                        throw new ConcurrentModificationException("ProductRelation with key " + oldProductRelation.getId() + " does not exist, while oldProductRelation != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetProductRelation);

        if (newProductRelation != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(ProductRelation.Properties.name)) {
                if (processedProperties.includedProperties().add(ProductRelation.Properties.name)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property name
                        // Update name field
                        if (isChanged(oldProductRelation.getName(), newProductRelation.getName())) {
                            if (!isChanged(targetProductRelation.getName(), oldProductRelation.getName())) {
                                targetProductRelation.setName(newProductRelation.getName());
                            } else {
                                throw new ConcurrentModificationException("name: database value is: " + targetProductRelation.getName() + " while old value is: " + oldProductRelation.getName() + ".");
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetProductRelation);

            // remove RelatedProduct.relation references
            Query relatedProductRelationQuery = entityManager.createQuery("SELECT r FROM RelatedProduct r WHERE r.relation = ?1");
            relatedProductRelationQuery.setParameter(1, targetProductRelation); 
            for (Object object : relatedProductRelationQuery.getResultList()) { 
                ((RelatedProduct) object).setRelation(null);
            }

            // remove Catalog.productRelations references
            Query catalogProductRelationsQuery = entityManager.createQuery("SELECT c FROM Catalog c WHERE ?1 MEMBER OF c.productRelations"); 
            catalogProductRelationsQuery.setParameter(1, targetProductRelation); 
            for (Object object : catalogProductRelationsQuery.getResultList()) { 
                ((Catalog) object).getProductRelations().remove(targetProductRelation);
            }
            targetProductRelation = null;
        }

        return targetProductRelation;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(Property object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store Property
     */
    public Property store(Property oldProperty, Property newProperty, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        Property result = store(oldProperty, newProperty, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldProperty, newProperty, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store Property
     */
    public Property store(Property oldProperty, Property newProperty, Property targetProperty, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetProperty;
        }
        // Create new object or get the actual object from the database 
        if(targetProperty == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldProperty == null && newProperty == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldProperty == null) {
                targetProperty = (Property) storeState.getTargetFromNewToTargetMap(newProperty);
                if (targetProperty == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldProperty = new Property();
                    targetProperty = new Property();

                    targetProperty.setId(newProperty.getId());
                    entityManager.persist(targetProperty);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newProperty, targetProperty);
                } else {
                    // The targetProperty has already been created during the first run
                    oldProperty = new Property();
                }
            } else {
                if (newProperty != null && isChanged(oldProperty.getId(), newProperty.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldProperty.getId() + ", new value: " + newProperty.getId());
                }
                if (!hasAllIdFields(oldProperty)) {
                    throw new RuntimeException("No key exception: oldProperty has no key.");
                } else {
                    targetProperty = entityManager.find(Property.class, oldProperty.getId());
                    if (targetProperty == null) {
                        throw new ConcurrentModificationException("Property with key " + oldProperty.getId() + " does not exist, while oldProperty != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetProperty);

        if (newProperty != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(Property.Properties.catalog)) {
                if (processedProperties.includedProperties().add(Property.Properties.catalog)) {
                    StoreView catalogView = requestedProperties.findView(Property.Properties.catalog);
                    if (catalogView != null) {
                        // Handle reference catalog
                        if (oldProperty.getCatalog() != null && newProperty.getCatalog() == null) {
                            // Remove
                            Catalog targetCatalog = targetProperty.getCatalog();

                            if (targetCatalog == null) {
                                throw new ConcurrentModificationException("oldProperty.getCatalog() with value: " + oldProperty.getCatalog() + " does not exist in the database.");
                            } else if(!targetCatalog.equals(oldProperty.getCatalog())) {
                                throw new ConcurrentModificationException("targetProperty.getCatalog() with value: " + targetProperty.getCatalog() + " is not equal to oldProperty.getCatalog() with value: " + oldProperty.getCatalog() + ".");
                            }

                            if (requestedProperties.forceComposite(Property.Properties.catalog)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((Catalog)oldProperty.getCatalog(), (Catalog) null, (Catalog)targetCatalog, catalogView, storeState);
                                targetProperty.setCatalog(null);
                            } else {
                                targetProperty.setCatalog(null);
                            }
                        } else {
                            // Add or update
                            Catalog newCatalog = newProperty.getCatalog();

                            if (newCatalog != null) {
                                if (requestedProperties.forceComposite(Property.Properties.catalog)) {
                                    // Update as composite
                                    if (oldProperty.getCatalog() != null && oldProperty.getCatalog().equals(newCatalog)) {
                                        catalogStoreSessionBean.store((Catalog)oldProperty.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldProperty.getCatalog() != null) {
                                            catalogStoreSessionBean.store((Catalog)oldProperty.getCatalog(), (Catalog) null, (Catalog)null, catalogView, storeState);
                                        }

                                        // Add 
                                        Catalog targetCatalog = catalogStoreSessionBean.store((Catalog)null, (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                        targetProperty.setCatalog(targetCatalog);
                                        // Update bidirectional reference: 'properties'
                                        if (targetCatalog != null) { 
                                            targetCatalog.getProperties().add(targetProperty);
                                        }
                                    }
                                } else {
                                    if (oldProperty.getCatalog() != null && oldProperty.getCatalog().equals(newCatalog)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((Catalog)oldProperty.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        Catalog targetCatalog = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newCatalog)) {
                                            // The newCatalog id is given, find the object 
                                            targetCatalog = entityManager.find(Catalog.class, newCatalog.getId());
                                            if (targetCatalog == null) {
                                                throw new ConcurrentModificationException("Catalog with key " + newCatalog.getId() + " does not exist.");
                                            }
                                        }

                                        if (newCatalog != null) {
                                            // The reference is updated
                                            if (targetCatalog == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetCatalog = (Catalog) storeState.getTargetFromNewToTargetMap(newCatalog);
                                                if (targetCatalog == null || !catalogStoreSessionBean.hasAllIdFields(targetCatalog)) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetCatalog != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetProperty.setCatalog(targetCatalog);
                                                // Update bidirectional reference: 'properties'
                                                targetCatalog.getProperties().add(targetProperty);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetCatalog is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetProperty.setCatalog(null);
                                            // Update bidirectional reference: 'properties'
                                            if (targetCatalog != null) {
                                                targetCatalog.getProperties().remove(targetProperty);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.name)) {
                if (processedProperties.includedProperties().add(Property.Properties.name)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property name
                        // Update name field
                        if (isChanged(oldProperty.getName(), newProperty.getName())) {
                            if (!isChanged(targetProperty.getName(), oldProperty.getName())) {
                                targetProperty.setName(newProperty.getName());
                            } else {
                                throw new ConcurrentModificationException("name: database value is: " + targetProperty.getName() + " while old value is: " + oldProperty.getName() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Property.Properties.productGroups)) {
                    StoreView productGroupsView = requestedProperties.findView(Property.Properties.productGroups);
                    if (productGroupsView != null) {
                        // Handle collection of references productGroups

                        // Remove
                        for (ProductGroup oldProductGroups : new ArrayList<ProductGroup>(oldProperty.getProductGroups())) {
                            if (!newProperty.getProductGroups().contains(oldProductGroups)) {
                                // Check if oldProductGroups, which will be removed, still exists in the database
                                ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, oldProductGroups.getId());
                                if (targetProductGroups == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldProductGroups.getId() +  " does not exist.");
                                }

                                // Ensure targetProperty still contains the reference to targetProductGroups
                                // otherwise it is not valid to nullify the reference from targetProductGroups to targetProperty
                                if (!targetProperty.getProductGroups().contains(targetProductGroups)) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + targetProductGroups.getId() +  " is not associated to targetProperty.");
                                }

                                targetProperty.getProductGroups().remove(targetProductGroups);
                                if (requestedProperties.forceComposite(Property.Properties.productGroups)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                } else {
                                    targetProductGroups.getProperties().remove(targetProperty);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newProductGroups : newProperty.getProductGroups()) {
                            if (!oldProperty.getProductGroups().contains(newProductGroups)) {

                                if (requestedProperties.forceComposite(Property.Properties.productGroups)) {
                                    // Add or update as composite (add if targetProductGroups == null)
                                    ProductGroup targetProductGroups = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newProductGroups, (ProductGroup)null, productGroupsView, storeState);
                                    targetProperty.getProductGroups().add(targetProductGroups);
                                    // Update bidirectional reference: 'properties'
                                    if (targetProductGroups != null) { 
                                        targetProductGroups.getProperties().add(targetProperty);
                                    }
                                } else {
                                    ProductGroup targetProductGroups = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProductGroups)) {
                                        // The newProductGroups id is given, find the object 
                                        targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                        if (targetProductGroups == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProductGroups != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProductGroups = (ProductGroup) storeState.getTargetFromNewToTargetMap(newProductGroups);
                                        if (targetProductGroups == null || !catalogStoreSessionBean.hasAllIdFields(targetProductGroups)) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProductGroups != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProperty.getProductGroups().add(targetProductGroups);
                                        // Update bidirectional reference: 'properties'
                                        targetProductGroups.getProperties().add(targetProperty);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProductGroups is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Property.Properties.productGroups)) {
                                    for (ProductGroup oldProductGroups : oldProperty.getProductGroups()) {
                                        if (newProductGroups != null && newProductGroups.equals(oldProductGroups)) {
                                            // Update
                                            ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                            if (targetProductGroups == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                            }
                                            targetProductGroups = catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) newProductGroups, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.type)) {
                if (processedProperties.includedProperties().add(Property.Properties.type)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property type
                        // Update type field
                        if (isChanged(oldProperty.getType(), newProperty.getType())) {
                            if (!isChanged(targetProperty.getType(), oldProperty.getType())) {
                                targetProperty.setType(newProperty.getType());
                            } else {
                                throw new ConcurrentModificationException("type: database value is: " + targetProperty.getType() + " while old value is: " + oldProperty.getType() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.enumValues)) {
                if (processedProperties.includedProperties().add(Property.Properties.enumValues)) {
                    StoreView enumValuesView = requestedProperties.findView(Property.Properties.enumValues);
                    if (enumValuesView != null) {
                        // Handle collection of references enumValues

                        // Remove
                        for (EnumValue oldEnumValues : new ArrayList<EnumValue>(oldProperty.getEnumValues())) {
                            if (!newProperty.getEnumValues().contains(oldEnumValues)) {
                                // Check if oldEnumValues, which will be removed, still exists in the database
                                EnumValue targetEnumValues = entityManager.find(EnumValue.class, oldEnumValues.getId());
                                if (targetEnumValues == null) {
                                    throw new ConcurrentModificationException("EnumValue with key " + oldEnumValues.getId() +  " does not exist.");
                                }


                                targetProperty.getEnumValues().remove(targetEnumValues);
                                if (requestedProperties.forceComposite(Property.Properties.enumValues)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((EnumValue)oldEnumValues, (EnumValue) null, (EnumValue)targetEnumValues, enumValuesView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (EnumValue newEnumValues : newProperty.getEnumValues()) {
                            if (!oldProperty.getEnumValues().contains(newEnumValues)) {

                                if (requestedProperties.forceComposite(Property.Properties.enumValues)) {
                                    // Add or update as composite (add if targetEnumValues == null)
                                    EnumValue targetEnumValues = catalogStoreSessionBean.store((EnumValue)null, (EnumValue) newEnumValues, (EnumValue)null, enumValuesView, storeState);
                                    targetProperty.getEnumValues().add(targetEnumValues);
                                } else {
                                    EnumValue targetEnumValues = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newEnumValues)) {
                                        // The newEnumValues id is given, find the object 
                                        targetEnumValues = entityManager.find(EnumValue.class, newEnumValues.getId());
                                        if (targetEnumValues == null) {
                                            throw new ConcurrentModificationException("EnumValue with key " + newEnumValues.getId() + " does not exist.");
                                        }
                                    }

                                    if (newEnumValues != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetEnumValues = (EnumValue) storeState.getTargetFromNewToTargetMap(newEnumValues);
                                        if (targetEnumValues == null || !catalogStoreSessionBean.hasAllIdFields(targetEnumValues)) {
                                            throw new IllegalStateException("EnumValue " + newEnumValues + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetEnumValues != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProperty.getEnumValues().add(targetEnumValues);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("EnumValue " + newEnumValues + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetEnumValues is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Property.Properties.enumValues)) {
                                    for (EnumValue oldEnumValues : oldProperty.getEnumValues()) {
                                        if (newEnumValues != null && newEnumValues.equals(oldEnumValues)) {
                                            // Update
                                            EnumValue targetEnumValues = entityManager.find(EnumValue.class, newEnumValues.getId());
                                            if (targetEnumValues == null) {
                                                throw new ConcurrentModificationException("EnumValue with key " + newEnumValues.getId() + " does not exist.");
                                            }
                                            targetEnumValues = catalogStoreSessionBean.store((EnumValue)oldEnumValues, (EnumValue) newEnumValues, (EnumValue)targetEnumValues, enumValuesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetProperty);

            // remove PropertyValue.property references
            Query propertyValuePropertyQuery = entityManager.createQuery("SELECT p FROM PropertyValue p WHERE p.property = ?1");
            propertyValuePropertyQuery.setParameter(1, targetProperty); 
            for (Object object : propertyValuePropertyQuery.getResultList()) { 
                ((PropertyValue) object).setProperty(null);
            }

            // remove ProductGroup.excludedProperties references
            Query productGroupExcludedPropertiesQuery = entityManager.createQuery("SELECT p FROM ProductGroup p WHERE ?1 MEMBER OF p.excludedProperties"); 
            productGroupExcludedPropertiesQuery.setParameter(1, targetProperty); 
            for (Object object : productGroupExcludedPropertiesQuery.getResultList()) { 
                ((ProductGroup) object).getExcludedProperties().remove(targetProperty);
            }

            // remove Taxonomy.excludedProperties references
            Query taxonomyExcludedPropertiesQuery = entityManager.createQuery("SELECT t FROM Taxonomy t WHERE ?1 MEMBER OF t.excludedProperties"); 
            taxonomyExcludedPropertiesQuery.setParameter(1, targetProperty); 
            for (Object object : taxonomyExcludedPropertiesQuery.getResultList()) { 
                ((Taxonomy) object).getExcludedProperties().remove(targetProperty);
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.catalog)) {
                if (processedProperties.includedProperties().add(Property.Properties.catalog)) {
                    // Remove referring reference: catalog
                    targetProperty.getCatalog().getProperties().remove(targetProperty);
                }
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Property.Properties.productGroups)) {
                    // Remove reference: productGroups
                    for (ProductGroup targetProductGroups : targetProperty.getProductGroups()) {
                        if (requestedProperties.forceComposite(Property.Properties.productGroups)) {
                            // Remove as composite
                            StoreView productGroupsView = requestedProperties.findView(Property.Properties.productGroups);
                            catalogStoreSessionBean.store((ProductGroup)targetProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                        } else {
                            targetProductGroups.getProperties().remove(targetProperty);
                        }
                    }
                    targetProperty.getProductGroups().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Property.Properties.enumValues)) {
                if (processedProperties.includedProperties().add(Property.Properties.enumValues)) {
                    // Remove reference: enumValues
                    targetProperty.getEnumValues().clear();
                }
            }
            targetProperty = null;
        }

        return targetProperty;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(EnumValue object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store EnumValue
     */
    public EnumValue store(EnumValue oldEnumValue, EnumValue newEnumValue, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        EnumValue result = store(oldEnumValue, newEnumValue, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldEnumValue, newEnumValue, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store EnumValue
     */
    public EnumValue store(EnumValue oldEnumValue, EnumValue newEnumValue, EnumValue targetEnumValue, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetEnumValue;
        }
        // Create new object or get the actual object from the database 
        if(targetEnumValue == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldEnumValue == null && newEnumValue == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldEnumValue == null) {
                targetEnumValue = (EnumValue) storeState.getTargetFromNewToTargetMap(newEnumValue);
                if (targetEnumValue == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldEnumValue = new EnumValue();
                    targetEnumValue = new EnumValue();

                    targetEnumValue.setId(newEnumValue.getId());
                    entityManager.persist(targetEnumValue);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newEnumValue, targetEnumValue);
                } else {
                    // The targetEnumValue has already been created during the first run
                    oldEnumValue = new EnumValue();
                }
            } else {
                if (newEnumValue != null && isChanged(oldEnumValue.getId(), newEnumValue.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldEnumValue.getId() + ", new value: " + newEnumValue.getId());
                }
                if (!hasAllIdFields(oldEnumValue)) {
                    throw new RuntimeException("No key exception: oldEnumValue has no key.");
                } else {
                    targetEnumValue = entityManager.find(EnumValue.class, oldEnumValue.getId());
                    if (targetEnumValue == null) {
                        throw new ConcurrentModificationException("EnumValue with key " + oldEnumValue.getId() + " does not exist, while oldEnumValue != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetEnumValue);

        if (newEnumValue != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(EnumValue.Properties.value)) {
                if (processedProperties.includedProperties().add(EnumValue.Properties.value)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property value
                        // Update value field
                        if (isChanged(oldEnumValue.getValue(), newEnumValue.getValue())) {
                            if (!isChanged(targetEnumValue.getValue(), oldEnumValue.getValue())) {
                                targetEnumValue.setValue(newEnumValue.getValue());
                            } else {
                                throw new ConcurrentModificationException("value: database value is: " + targetEnumValue.getValue() + " while old value is: " + oldEnumValue.getValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(EnumValue.Properties.label)) {
                if (processedProperties.includedProperties().add(EnumValue.Properties.label)) {
                    StoreView labelView = requestedProperties.findView(EnumValue.Properties.label);
                    if (labelView != null) {
                        // Handle collection of references label

                        // Remove
                        for (Label oldLabel : new ArrayList<Label>(oldEnumValue.getLabel())) {
                            if (!newEnumValue.getLabel().contains(oldLabel)) {
                                // Check if oldLabel, which will be removed, still exists in the database
                                Label targetLabel = entityManager.find(Label.class, oldLabel.getId());
                                if (targetLabel == null) {
                                    throw new ConcurrentModificationException("Label with key " + oldLabel.getId() +  " does not exist.");
                                }


                                targetEnumValue.getLabel().remove(targetLabel);
                                if (requestedProperties.forceComposite(EnumValue.Properties.label)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Label)oldLabel, (Label) null, (Label)targetLabel, labelView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (Label newLabel : newEnumValue.getLabel()) {
                            if (!oldEnumValue.getLabel().contains(newLabel)) {

                                if (requestedProperties.forceComposite(EnumValue.Properties.label)) {
                                    // Add or update as composite (add if targetLabel == null)
                                    Label targetLabel = catalogStoreSessionBean.store((Label)null, (Label) newLabel, (Label)null, labelView, storeState);
                                    targetEnumValue.getLabel().add(targetLabel);
                                } else {
                                    Label targetLabel = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newLabel)) {
                                        // The newLabel id is given, find the object 
                                        targetLabel = entityManager.find(Label.class, newLabel.getId());
                                        if (targetLabel == null) {
                                            throw new ConcurrentModificationException("Label with key " + newLabel.getId() + " does not exist.");
                                        }
                                    }

                                    if (newLabel != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetLabel = (Label) storeState.getTargetFromNewToTargetMap(newLabel);
                                        if (targetLabel == null || !catalogStoreSessionBean.hasAllIdFields(targetLabel)) {
                                            throw new IllegalStateException("Label " + newLabel + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetLabel != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetEnumValue.getLabel().add(targetLabel);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Label " + newLabel + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetLabel is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(EnumValue.Properties.label)) {
                                    for (Label oldLabel : oldEnumValue.getLabel()) {
                                        if (newLabel != null && newLabel.equals(oldLabel)) {
                                            // Update
                                            Label targetLabel = entityManager.find(Label.class, newLabel.getId());
                                            if (targetLabel == null) {
                                                throw new ConcurrentModificationException("Label with key " + newLabel.getId() + " does not exist.");
                                            }
                                            targetLabel = catalogStoreSessionBean.store((Label)oldLabel, (Label) newLabel, (Label)targetLabel, labelView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetEnumValue);

            // remove Property.enumValues references
            Query propertyEnumValuesQuery = entityManager.createQuery("SELECT p FROM Property p WHERE ?1 MEMBER OF p.enumValues"); 
            propertyEnumValuesQuery.setParameter(1, targetEnumValue); 
            for (Object object : propertyEnumValuesQuery.getResultList()) { 
                ((Property) object).getEnumValues().remove(targetEnumValue);
            }
            if (requestedProperties.includedProperties().contains(EnumValue.Properties.label)) {
                if (processedProperties.includedProperties().add(EnumValue.Properties.label)) {
                    // Remove reference: label
                    targetEnumValue.getLabel().clear();
                }
            }
            targetEnumValue = null;
        }

        return targetEnumValue;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(Taxonomy object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store Taxonomy
     */
    public Taxonomy store(Taxonomy oldTaxonomy, Taxonomy newTaxonomy, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        Taxonomy result = store(oldTaxonomy, newTaxonomy, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldTaxonomy, newTaxonomy, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store Taxonomy
     */
    public Taxonomy store(Taxonomy oldTaxonomy, Taxonomy newTaxonomy, Taxonomy targetTaxonomy, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetTaxonomy;
        }
        // Create new object or get the actual object from the database 
        if(targetTaxonomy == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldTaxonomy == null && newTaxonomy == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldTaxonomy == null) {
                targetTaxonomy = (Taxonomy) storeState.getTargetFromNewToTargetMap(newTaxonomy);
                if (targetTaxonomy == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldTaxonomy = new Taxonomy();
                    targetTaxonomy = new Taxonomy();

                    targetTaxonomy.setId(newTaxonomy.getId());
                    entityManager.persist(targetTaxonomy);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newTaxonomy, targetTaxonomy);
                } else {
                    // The targetTaxonomy has already been created during the first run
                    oldTaxonomy = new Taxonomy();
                }
            } else {
                if (newTaxonomy != null && isChanged(oldTaxonomy.getId(), newTaxonomy.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldTaxonomy.getId() + ", new value: " + newTaxonomy.getId());
                }
                if (!hasAllIdFields(oldTaxonomy)) {
                    throw new RuntimeException("No key exception: oldTaxonomy has no key.");
                } else {
                    targetTaxonomy = entityManager.find(Taxonomy.class, oldTaxonomy.getId());
                    if (targetTaxonomy == null) {
                        throw new ConcurrentModificationException("Taxonomy with key " + oldTaxonomy.getId() + " does not exist, while oldTaxonomy != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetTaxonomy);

        if (newTaxonomy != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.name)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.name)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property name
                        // Update name field
                        if (isChanged(oldTaxonomy.getName(), newTaxonomy.getName())) {
                            if (!isChanged(targetTaxonomy.getName(), oldTaxonomy.getName())) {
                                targetTaxonomy.setName(newTaxonomy.getName());
                            } else {
                                throw new ConcurrentModificationException("name: database value is: " + targetTaxonomy.getName() + " while old value is: " + oldTaxonomy.getName() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.catalog)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.catalog)) {
                    StoreView catalogView = requestedProperties.findView(Taxonomy.Properties.catalog);
                    if (catalogView != null) {
                        // Handle reference catalog
                        if (oldTaxonomy.getCatalog() != null && newTaxonomy.getCatalog() == null) {
                            // Remove
                            Catalog targetCatalog = targetTaxonomy.getCatalog();

                            if (targetCatalog == null) {
                                throw new ConcurrentModificationException("oldTaxonomy.getCatalog() with value: " + oldTaxonomy.getCatalog() + " does not exist in the database.");
                            } else if(!targetCatalog.equals(oldTaxonomy.getCatalog())) {
                                throw new ConcurrentModificationException("targetTaxonomy.getCatalog() with value: " + targetTaxonomy.getCatalog() + " is not equal to oldTaxonomy.getCatalog() with value: " + oldTaxonomy.getCatalog() + ".");
                            }

                            if (requestedProperties.forceComposite(Taxonomy.Properties.catalog)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((Catalog)oldTaxonomy.getCatalog(), (Catalog) null, (Catalog)targetCatalog, catalogView, storeState);
                                targetTaxonomy.setCatalog(null);
                            } else {
                                targetTaxonomy.setCatalog(null);
                            }
                        } else {
                            // Add or update
                            Catalog newCatalog = newTaxonomy.getCatalog();

                            if (newCatalog != null) {
                                if (requestedProperties.forceComposite(Taxonomy.Properties.catalog)) {
                                    // Update as composite
                                    if (oldTaxonomy.getCatalog() != null && oldTaxonomy.getCatalog().equals(newCatalog)) {
                                        catalogStoreSessionBean.store((Catalog)oldTaxonomy.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldTaxonomy.getCatalog() != null) {
                                            catalogStoreSessionBean.store((Catalog)oldTaxonomy.getCatalog(), (Catalog) null, (Catalog)null, catalogView, storeState);
                                        }

                                        // Add 
                                        Catalog targetCatalog = catalogStoreSessionBean.store((Catalog)null, (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                        targetTaxonomy.setCatalog(targetCatalog);
                                        // Update bidirectional reference: 'taxonomies'
                                        if (targetCatalog != null) { 
                                            targetCatalog.getTaxonomies().add(targetTaxonomy);
                                        }
                                    }
                                } else {
                                    if (oldTaxonomy.getCatalog() != null && oldTaxonomy.getCatalog().equals(newCatalog)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((Catalog)oldTaxonomy.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        Catalog targetCatalog = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newCatalog)) {
                                            // The newCatalog id is given, find the object 
                                            targetCatalog = entityManager.find(Catalog.class, newCatalog.getId());
                                            if (targetCatalog == null) {
                                                throw new ConcurrentModificationException("Catalog with key " + newCatalog.getId() + " does not exist.");
                                            }
                                        }

                                        if (newCatalog != null) {
                                            // The reference is updated
                                            if (targetCatalog == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetCatalog = (Catalog) storeState.getTargetFromNewToTargetMap(newCatalog);
                                                if (targetCatalog == null || !catalogStoreSessionBean.hasAllIdFields(targetCatalog)) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetCatalog != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetTaxonomy.setCatalog(targetCatalog);
                                                // Update bidirectional reference: 'taxonomies'
                                                targetCatalog.getTaxonomies().add(targetTaxonomy);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetCatalog is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetTaxonomy.setCatalog(null);
                                            // Update bidirectional reference: 'taxonomies'
                                            if (targetCatalog != null) {
                                                targetCatalog.getTaxonomies().remove(targetTaxonomy);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.productGroups)) {
                    StoreView productGroupsView = requestedProperties.findView(Taxonomy.Properties.productGroups);
                    if (productGroupsView != null) {
                        // Handle collection of references productGroups

                        // Remove
                        for (ProductGroup oldProductGroups : new ArrayList<ProductGroup>(oldTaxonomy.getProductGroups())) {
                            if (!newTaxonomy.getProductGroups().contains(oldProductGroups)) {
                                // Check if oldProductGroups, which will be removed, still exists in the database
                                ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, oldProductGroups.getId());
                                if (targetProductGroups == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldProductGroups.getId() +  " does not exist.");
                                }

                                // Ensure targetTaxonomy still contains the reference to targetProductGroups
                                // otherwise it is not valid to nullify the reference from targetProductGroups to targetTaxonomy
                                if (!targetTaxonomy.getProductGroups().contains(targetProductGroups)) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + targetProductGroups.getId() +  " is not associated to targetTaxonomy.");
                                }

                                targetTaxonomy.getProductGroups().remove(targetProductGroups);
                                if (requestedProperties.forceComposite(Taxonomy.Properties.productGroups)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                } else {
                                    targetProductGroups.getTaxonomy().remove(targetTaxonomy);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newProductGroups : newTaxonomy.getProductGroups()) {
                            if (!oldTaxonomy.getProductGroups().contains(newProductGroups)) {

                                if (requestedProperties.forceComposite(Taxonomy.Properties.productGroups)) {
                                    // Add or update as composite (add if targetProductGroups == null)
                                    ProductGroup targetProductGroups = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newProductGroups, (ProductGroup)null, productGroupsView, storeState);
                                    targetTaxonomy.getProductGroups().add(targetProductGroups);
                                    // Update bidirectional reference: 'taxonomy'
                                    if (targetProductGroups != null) { 
                                        targetProductGroups.getTaxonomy().add(targetTaxonomy);
                                    }
                                } else {
                                    ProductGroup targetProductGroups = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProductGroups)) {
                                        // The newProductGroups id is given, find the object 
                                        targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                        if (targetProductGroups == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProductGroups != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProductGroups = (ProductGroup) storeState.getTargetFromNewToTargetMap(newProductGroups);
                                        if (targetProductGroups == null || !catalogStoreSessionBean.hasAllIdFields(targetProductGroups)) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProductGroups != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetTaxonomy.getProductGroups().add(targetProductGroups);
                                        // Update bidirectional reference: 'taxonomy'
                                        targetProductGroups.getTaxonomy().add(targetTaxonomy);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newProductGroups + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProductGroups is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Taxonomy.Properties.productGroups)) {
                                    for (ProductGroup oldProductGroups : oldTaxonomy.getProductGroups()) {
                                        if (newProductGroups != null && newProductGroups.equals(oldProductGroups)) {
                                            // Update
                                            ProductGroup targetProductGroups = entityManager.find(ProductGroup.class, newProductGroups.getId());
                                            if (targetProductGroups == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newProductGroups.getId() + " does not exist.");
                                            }
                                            targetProductGroups = catalogStoreSessionBean.store((ProductGroup)oldProductGroups, (ProductGroup) newProductGroups, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.excludedProperties)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.excludedProperties)) {
                    StoreView excludedPropertiesView = requestedProperties.findView(Taxonomy.Properties.excludedProperties);
                    if (excludedPropertiesView != null) {
                        // Handle collection of references excludedProperties

                        // Remove
                        for (Property oldExcludedProperties : new ArrayList<Property>(oldTaxonomy.getExcludedProperties())) {
                            if (!newTaxonomy.getExcludedProperties().contains(oldExcludedProperties)) {
                                // Check if oldExcludedProperties, which will be removed, still exists in the database
                                Property targetExcludedProperties = entityManager.find(Property.class, oldExcludedProperties.getId());
                                if (targetExcludedProperties == null) {
                                    throw new ConcurrentModificationException("Property with key " + oldExcludedProperties.getId() +  " does not exist.");
                                }


                                targetTaxonomy.getExcludedProperties().remove(targetExcludedProperties);
                                if (requestedProperties.forceComposite(Taxonomy.Properties.excludedProperties)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Property)oldExcludedProperties, (Property) null, (Property)targetExcludedProperties, excludedPropertiesView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (Property newExcludedProperties : newTaxonomy.getExcludedProperties()) {
                            if (!oldTaxonomy.getExcludedProperties().contains(newExcludedProperties)) {

                                if (requestedProperties.forceComposite(Taxonomy.Properties.excludedProperties)) {
                                    // Add or update as composite (add if targetExcludedProperties == null)
                                    Property targetExcludedProperties = catalogStoreSessionBean.store((Property)null, (Property) newExcludedProperties, (Property)null, excludedPropertiesView, storeState);
                                    targetTaxonomy.getExcludedProperties().add(targetExcludedProperties);
                                } else {
                                    Property targetExcludedProperties = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newExcludedProperties)) {
                                        // The newExcludedProperties id is given, find the object 
                                        targetExcludedProperties = entityManager.find(Property.class, newExcludedProperties.getId());
                                        if (targetExcludedProperties == null) {
                                            throw new ConcurrentModificationException("Property with key " + newExcludedProperties.getId() + " does not exist.");
                                        }
                                    }

                                    if (newExcludedProperties != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetExcludedProperties = (Property) storeState.getTargetFromNewToTargetMap(newExcludedProperties);
                                        if (targetExcludedProperties == null || !catalogStoreSessionBean.hasAllIdFields(targetExcludedProperties)) {
                                            throw new IllegalStateException("Property " + newExcludedProperties + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetExcludedProperties != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetTaxonomy.getExcludedProperties().add(targetExcludedProperties);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Property " + newExcludedProperties + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetExcludedProperties is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Taxonomy.Properties.excludedProperties)) {
                                    for (Property oldExcludedProperties : oldTaxonomy.getExcludedProperties()) {
                                        if (newExcludedProperties != null && newExcludedProperties.equals(oldExcludedProperties)) {
                                            // Update
                                            Property targetExcludedProperties = entityManager.find(Property.class, newExcludedProperties.getId());
                                            if (targetExcludedProperties == null) {
                                                throw new ConcurrentModificationException("Property with key " + newExcludedProperties.getId() + " does not exist.");
                                            }
                                            targetExcludedProperties = catalogStoreSessionBean.store((Property)oldExcludedProperties, (Property) newExcludedProperties, (Property)targetExcludedProperties, excludedPropertiesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.excludedProductGroups)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.excludedProductGroups)) {
                    StoreView excludedProductGroupsView = requestedProperties.findView(Taxonomy.Properties.excludedProductGroups);
                    if (excludedProductGroupsView != null) {
                        // Handle collection of references excludedProductGroups

                        // Remove
                        for (ProductGroup oldExcludedProductGroups : new ArrayList<ProductGroup>(oldTaxonomy.getExcludedProductGroups())) {
                            if (!newTaxonomy.getExcludedProductGroups().contains(oldExcludedProductGroups)) {
                                // Check if oldExcludedProductGroups, which will be removed, still exists in the database
                                ProductGroup targetExcludedProductGroups = entityManager.find(ProductGroup.class, oldExcludedProductGroups.getId());
                                if (targetExcludedProductGroups == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldExcludedProductGroups.getId() +  " does not exist.");
                                }


                                targetTaxonomy.getExcludedProductGroups().remove(targetExcludedProductGroups);
                                if (requestedProperties.forceComposite(Taxonomy.Properties.excludedProductGroups)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldExcludedProductGroups, (ProductGroup) null, (ProductGroup)targetExcludedProductGroups, excludedProductGroupsView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newExcludedProductGroups : newTaxonomy.getExcludedProductGroups()) {
                            if (!oldTaxonomy.getExcludedProductGroups().contains(newExcludedProductGroups)) {

                                if (requestedProperties.forceComposite(Taxonomy.Properties.excludedProductGroups)) {
                                    // Add or update as composite (add if targetExcludedProductGroups == null)
                                    ProductGroup targetExcludedProductGroups = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newExcludedProductGroups, (ProductGroup)null, excludedProductGroupsView, storeState);
                                    targetTaxonomy.getExcludedProductGroups().add(targetExcludedProductGroups);
                                } else {
                                    ProductGroup targetExcludedProductGroups = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newExcludedProductGroups)) {
                                        // The newExcludedProductGroups id is given, find the object 
                                        targetExcludedProductGroups = entityManager.find(ProductGroup.class, newExcludedProductGroups.getId());
                                        if (targetExcludedProductGroups == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newExcludedProductGroups.getId() + " does not exist.");
                                        }
                                    }

                                    if (newExcludedProductGroups != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetExcludedProductGroups = (ProductGroup) storeState.getTargetFromNewToTargetMap(newExcludedProductGroups);
                                        if (targetExcludedProductGroups == null || !catalogStoreSessionBean.hasAllIdFields(targetExcludedProductGroups)) {
                                            throw new IllegalStateException("ProductGroup " + newExcludedProductGroups + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetExcludedProductGroups != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetTaxonomy.getExcludedProductGroups().add(targetExcludedProductGroups);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newExcludedProductGroups + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetExcludedProductGroups is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(Taxonomy.Properties.excludedProductGroups)) {
                                    for (ProductGroup oldExcludedProductGroups : oldTaxonomy.getExcludedProductGroups()) {
                                        if (newExcludedProductGroups != null && newExcludedProductGroups.equals(oldExcludedProductGroups)) {
                                            // Update
                                            ProductGroup targetExcludedProductGroups = entityManager.find(ProductGroup.class, newExcludedProductGroups.getId());
                                            if (targetExcludedProductGroups == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newExcludedProductGroups.getId() + " does not exist.");
                                            }
                                            targetExcludedProductGroups = catalogStoreSessionBean.store((ProductGroup)oldExcludedProductGroups, (ProductGroup) newExcludedProductGroups, (ProductGroup)targetExcludedProductGroups, excludedProductGroupsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetTaxonomy);
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.catalog)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.catalog)) {
                    // Remove referring reference: catalog
                    targetTaxonomy.getCatalog().getTaxonomies().remove(targetTaxonomy);
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.productGroups)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.productGroups)) {
                    // Remove reference: productGroups
                    for (ProductGroup targetProductGroups : targetTaxonomy.getProductGroups()) {
                        if (requestedProperties.forceComposite(Taxonomy.Properties.productGroups)) {
                            // Remove as composite
                            StoreView productGroupsView = requestedProperties.findView(Taxonomy.Properties.productGroups);
                            catalogStoreSessionBean.store((ProductGroup)targetProductGroups, (ProductGroup) null, (ProductGroup)targetProductGroups, productGroupsView, storeState);
                        } else {
                            targetProductGroups.getTaxonomy().remove(targetTaxonomy);
                        }
                    }
                    targetTaxonomy.getProductGroups().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.excludedProperties)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.excludedProperties)) {
                    // Remove reference: excludedProperties
                    targetTaxonomy.getExcludedProperties().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(Taxonomy.Properties.excludedProductGroups)) {
                if (processedProperties.includedProperties().add(Taxonomy.Properties.excludedProductGroups)) {
                    // Remove reference: excludedProductGroups
                    targetTaxonomy.getExcludedProductGroups().clear();
                }
            }
            targetTaxonomy = null;
        }

        return targetTaxonomy;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(ProductGroup object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store ProductGroup
     */
    public ProductGroup store(ProductGroup oldProductGroup, ProductGroup newProductGroup, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        ProductGroup result = store(oldProductGroup, newProductGroup, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldProductGroup, newProductGroup, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store ProductGroup
     */
    public ProductGroup store(ProductGroup oldProductGroup, ProductGroup newProductGroup, ProductGroup targetProductGroup, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetProductGroup;
        }
        // Create new object or get the actual object from the database 
        if(targetProductGroup == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldProductGroup == null && newProductGroup == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldProductGroup == null) {
                targetProductGroup = (ProductGroup) storeState.getTargetFromNewToTargetMap(newProductGroup);
                if (targetProductGroup == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldProductGroup = new ProductGroup();
                    targetProductGroup = new ProductGroup();

                    targetProductGroup.setId(newProductGroup.getId());
                    entityManager.persist(targetProductGroup);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newProductGroup, targetProductGroup);
                } else {
                    // The targetProductGroup has already been created during the first run
                    oldProductGroup = new ProductGroup();
                }
            } else {
                if (newProductGroup != null && isChanged(oldProductGroup.getId(), newProductGroup.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldProductGroup.getId() + ", new value: " + newProductGroup.getId());
                }
                if (!hasAllIdFields(oldProductGroup)) {
                    throw new RuntimeException("No key exception: oldProductGroup has no key.");
                } else {
                    targetProductGroup = entityManager.find(ProductGroup.class, oldProductGroup.getId());
                    if (targetProductGroup == null) {
                        throw new ConcurrentModificationException("ProductGroup with key " + oldProductGroup.getId() + " does not exist, while oldProductGroup != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetProductGroup);

        if (newProductGroup != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.catalog)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.catalog)) {
                    StoreView catalogView = requestedProperties.findView(ProductGroup.Properties.catalog);
                    if (catalogView != null) {
                        // Handle reference catalog
                        if (oldProductGroup.getCatalog() != null && newProductGroup.getCatalog() == null) {
                            // Remove
                            Catalog targetCatalog = targetProductGroup.getCatalog();

                            if (targetCatalog == null) {
                                throw new ConcurrentModificationException("oldProductGroup.getCatalog() with value: " + oldProductGroup.getCatalog() + " does not exist in the database.");
                            } else if(!targetCatalog.equals(oldProductGroup.getCatalog())) {
                                throw new ConcurrentModificationException("targetProductGroup.getCatalog() with value: " + targetProductGroup.getCatalog() + " is not equal to oldProductGroup.getCatalog() with value: " + oldProductGroup.getCatalog() + ".");
                            }

                            if (requestedProperties.forceComposite(ProductGroup.Properties.catalog)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((Catalog)oldProductGroup.getCatalog(), (Catalog) null, (Catalog)targetCatalog, catalogView, storeState);
                                targetProductGroup.setCatalog(null);
                            } else {
                                targetProductGroup.setCatalog(null);
                            }
                        } else {
                            // Add or update
                            Catalog newCatalog = newProductGroup.getCatalog();

                            if (newCatalog != null) {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.catalog)) {
                                    // Update as composite
                                    if (oldProductGroup.getCatalog() != null && oldProductGroup.getCatalog().equals(newCatalog)) {
                                        catalogStoreSessionBean.store((Catalog)oldProductGroup.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldProductGroup.getCatalog() != null) {
                                            catalogStoreSessionBean.store((Catalog)oldProductGroup.getCatalog(), (Catalog) null, (Catalog)null, catalogView, storeState);
                                        }

                                        // Add 
                                        Catalog targetCatalog = catalogStoreSessionBean.store((Catalog)null, (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                        targetProductGroup.setCatalog(targetCatalog);
                                        // Update bidirectional reference: 'productGroups'
                                        if (targetCatalog != null) { 
                                            targetCatalog.getProductGroups().add(targetProductGroup);
                                        }
                                    }
                                } else {
                                    if (oldProductGroup.getCatalog() != null && oldProductGroup.getCatalog().equals(newCatalog)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((Catalog)oldProductGroup.getCatalog(), (Catalog) newCatalog, (Catalog)null, catalogView, storeState);
                                    } else {
                                        Catalog targetCatalog = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newCatalog)) {
                                            // The newCatalog id is given, find the object 
                                            targetCatalog = entityManager.find(Catalog.class, newCatalog.getId());
                                            if (targetCatalog == null) {
                                                throw new ConcurrentModificationException("Catalog with key " + newCatalog.getId() + " does not exist.");
                                            }
                                        }

                                        if (newCatalog != null) {
                                            // The reference is updated
                                            if (targetCatalog == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetCatalog = (Catalog) storeState.getTargetFromNewToTargetMap(newCatalog);
                                                if (targetCatalog == null || !catalogStoreSessionBean.hasAllIdFields(targetCatalog)) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetCatalog != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetProductGroup.setCatalog(targetCatalog);
                                                // Update bidirectional reference: 'productGroups'
                                                targetCatalog.getProductGroups().add(targetProductGroup);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("Catalog " + newCatalog + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetCatalog is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetProductGroup.setCatalog(null);
                                            // Update bidirectional reference: 'productGroups'
                                            if (targetCatalog != null) {
                                                targetCatalog.getProductGroups().remove(targetProductGroup);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.taxonomy)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.taxonomy)) {
                    StoreView taxonomyView = requestedProperties.findView(ProductGroup.Properties.taxonomy);
                    if (taxonomyView != null) {
                        // Handle collection of references taxonomy

                        // Remove
                        for (Taxonomy oldTaxonomy : new ArrayList<Taxonomy>(oldProductGroup.getTaxonomy())) {
                            if (!newProductGroup.getTaxonomy().contains(oldTaxonomy)) {
                                // Check if oldTaxonomy, which will be removed, still exists in the database
                                Taxonomy targetTaxonomy = entityManager.find(Taxonomy.class, oldTaxonomy.getId());
                                if (targetTaxonomy == null) {
                                    throw new ConcurrentModificationException("Taxonomy with key " + oldTaxonomy.getId() +  " does not exist.");
                                }

                                // Ensure targetProductGroup still contains the reference to targetTaxonomy
                                // otherwise it is not valid to nullify the reference from targetTaxonomy to targetProductGroup
                                if (!targetProductGroup.getTaxonomy().contains(targetTaxonomy)) {
                                    throw new ConcurrentModificationException("Taxonomy with key " + targetTaxonomy.getId() +  " is not associated to targetProductGroup.");
                                }

                                targetProductGroup.getTaxonomy().remove(targetTaxonomy);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.taxonomy)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Taxonomy)oldTaxonomy, (Taxonomy) null, (Taxonomy)targetTaxonomy, taxonomyView, storeState);
                                } else {
                                    targetTaxonomy.getProductGroups().remove(targetProductGroup);
                                }
                            }
                        }

                        // Add or update
                        for (Taxonomy newTaxonomy : newProductGroup.getTaxonomy()) {
                            if (!oldProductGroup.getTaxonomy().contains(newTaxonomy)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.taxonomy)) {
                                    // Add or update as composite (add if targetTaxonomy == null)
                                    Taxonomy targetTaxonomy = catalogStoreSessionBean.store((Taxonomy)null, (Taxonomy) newTaxonomy, (Taxonomy)null, taxonomyView, storeState);
                                    targetProductGroup.getTaxonomy().add(targetTaxonomy);
                                    // Update bidirectional reference: 'productGroups'
                                    if (targetTaxonomy != null) { 
                                        targetTaxonomy.getProductGroups().add(targetProductGroup);
                                    }
                                } else {
                                    Taxonomy targetTaxonomy = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newTaxonomy)) {
                                        // The newTaxonomy id is given, find the object 
                                        targetTaxonomy = entityManager.find(Taxonomy.class, newTaxonomy.getId());
                                        if (targetTaxonomy == null) {
                                            throw new ConcurrentModificationException("Taxonomy with key " + newTaxonomy.getId() + " does not exist.");
                                        }
                                    }

                                    if (newTaxonomy != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetTaxonomy = (Taxonomy) storeState.getTargetFromNewToTargetMap(newTaxonomy);
                                        if (targetTaxonomy == null || !catalogStoreSessionBean.hasAllIdFields(targetTaxonomy)) {
                                            throw new IllegalStateException("Taxonomy " + newTaxonomy + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetTaxonomy != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getTaxonomy().add(targetTaxonomy);
                                        // Update bidirectional reference: 'productGroups'
                                        targetTaxonomy.getProductGroups().add(targetProductGroup);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Taxonomy " + newTaxonomy + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetTaxonomy is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.taxonomy)) {
                                    for (Taxonomy oldTaxonomy : oldProductGroup.getTaxonomy()) {
                                        if (newTaxonomy != null && newTaxonomy.equals(oldTaxonomy)) {
                                            // Update
                                            Taxonomy targetTaxonomy = entityManager.find(Taxonomy.class, newTaxonomy.getId());
                                            if (targetTaxonomy == null) {
                                                throw new ConcurrentModificationException("Taxonomy with key " + newTaxonomy.getId() + " does not exist.");
                                            }
                                            targetTaxonomy = catalogStoreSessionBean.store((Taxonomy)oldTaxonomy, (Taxonomy) newTaxonomy, (Taxonomy)targetTaxonomy, taxonomyView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.label)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.label)) {
                    StoreView labelView = requestedProperties.findView(ProductGroup.Properties.label);
                    if (labelView != null) {
                        // Handle collection of references label

                        // Remove
                        for (Label oldLabel : new ArrayList<Label>(oldProductGroup.getLabel())) {
                            if (!newProductGroup.getLabel().contains(oldLabel)) {
                                // Check if oldLabel, which will be removed, still exists in the database
                                Label targetLabel = entityManager.find(Label.class, oldLabel.getId());
                                if (targetLabel == null) {
                                    throw new ConcurrentModificationException("Label with key " + oldLabel.getId() +  " does not exist.");
                                }


                                targetProductGroup.getLabel().remove(targetLabel);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.label)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Label)oldLabel, (Label) null, (Label)targetLabel, labelView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (Label newLabel : newProductGroup.getLabel()) {
                            if (!oldProductGroup.getLabel().contains(newLabel)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.label)) {
                                    // Add or update as composite (add if targetLabel == null)
                                    Label targetLabel = catalogStoreSessionBean.store((Label)null, (Label) newLabel, (Label)null, labelView, storeState);
                                    targetProductGroup.getLabel().add(targetLabel);
                                } else {
                                    Label targetLabel = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newLabel)) {
                                        // The newLabel id is given, find the object 
                                        targetLabel = entityManager.find(Label.class, newLabel.getId());
                                        if (targetLabel == null) {
                                            throw new ConcurrentModificationException("Label with key " + newLabel.getId() + " does not exist.");
                                        }
                                    }

                                    if (newLabel != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetLabel = (Label) storeState.getTargetFromNewToTargetMap(newLabel);
                                        if (targetLabel == null || !catalogStoreSessionBean.hasAllIdFields(targetLabel)) {
                                            throw new IllegalStateException("Label " + newLabel + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetLabel != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getLabel().add(targetLabel);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Label " + newLabel + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetLabel is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.label)) {
                                    for (Label oldLabel : oldProductGroup.getLabel()) {
                                        if (newLabel != null && newLabel.equals(oldLabel)) {
                                            // Update
                                            Label targetLabel = entityManager.find(Label.class, newLabel.getId());
                                            if (targetLabel == null) {
                                                throw new ConcurrentModificationException("Label with key " + newLabel.getId() + " does not exist.");
                                            }
                                            targetLabel = catalogStoreSessionBean.store((Label)oldLabel, (Label) newLabel, (Label)targetLabel, labelView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.children)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.children)) {
                    StoreView childrenView = requestedProperties.findView(ProductGroup.Properties.children);
                    if (childrenView != null) {
                        // Handle collection of references children

                        // Remove
                        for (ProductGroup oldChildren : new ArrayList<ProductGroup>(oldProductGroup.getChildren())) {
                            if (!newProductGroup.getChildren().contains(oldChildren)) {
                                // Check if oldChildren, which will be removed, still exists in the database
                                ProductGroup targetChildren = entityManager.find(ProductGroup.class, oldChildren.getId());
                                if (targetChildren == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldChildren.getId() +  " does not exist.");
                                }

                                // Ensure targetProductGroup still contains the reference to targetChildren
                                // otherwise it is not valid to nullify the reference from targetChildren to targetProductGroup
                                if (!targetProductGroup.getChildren().contains(targetChildren)) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + targetChildren.getId() +  " is not associated to targetProductGroup.");
                                }

                                targetProductGroup.getChildren().remove(targetChildren);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.children)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldChildren, (ProductGroup) null, (ProductGroup)targetChildren, childrenView, storeState);
                                } else {
                                    targetChildren.getParent().remove(targetProductGroup);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newChildren : newProductGroup.getChildren()) {
                            if (!oldProductGroup.getChildren().contains(newChildren)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.children)) {
                                    // Add or update as composite (add if targetChildren == null)
                                    ProductGroup targetChildren = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newChildren, (ProductGroup)null, childrenView, storeState);
                                    targetProductGroup.getChildren().add(targetChildren);
                                    // Update bidirectional reference: 'parent'
                                    if (targetChildren != null) { 
                                        targetChildren.getParent().add(targetProductGroup);
                                    }
                                } else {
                                    ProductGroup targetChildren = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newChildren)) {
                                        // The newChildren id is given, find the object 
                                        targetChildren = entityManager.find(ProductGroup.class, newChildren.getId());
                                        if (targetChildren == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newChildren.getId() + " does not exist.");
                                        }
                                    }

                                    if (newChildren != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetChildren = (ProductGroup) storeState.getTargetFromNewToTargetMap(newChildren);
                                        if (targetChildren == null || !catalogStoreSessionBean.hasAllIdFields(targetChildren)) {
                                            throw new IllegalStateException("ProductGroup " + newChildren + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetChildren != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getChildren().add(targetChildren);
                                        // Update bidirectional reference: 'parent'
                                        targetChildren.getParent().add(targetProductGroup);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newChildren + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetChildren is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.children)) {
                                    for (ProductGroup oldChildren : oldProductGroup.getChildren()) {
                                        if (newChildren != null && newChildren.equals(oldChildren)) {
                                            // Update
                                            ProductGroup targetChildren = entityManager.find(ProductGroup.class, newChildren.getId());
                                            if (targetChildren == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newChildren.getId() + " does not exist.");
                                            }
                                            targetChildren = catalogStoreSessionBean.store((ProductGroup)oldChildren, (ProductGroup) newChildren, (ProductGroup)targetChildren, childrenView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.parent)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.parent)) {
                    StoreView parentView = requestedProperties.findView(ProductGroup.Properties.parent);
                    if (parentView != null) {
                        // Handle collection of references parent

                        // Remove
                        for (ProductGroup oldParent : new ArrayList<ProductGroup>(oldProductGroup.getParent())) {
                            if (!newProductGroup.getParent().contains(oldParent)) {
                                // Check if oldParent, which will be removed, still exists in the database
                                ProductGroup targetParent = entityManager.find(ProductGroup.class, oldParent.getId());
                                if (targetParent == null) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + oldParent.getId() +  " does not exist.");
                                }

                                // Ensure targetProductGroup still contains the reference to targetParent
                                // otherwise it is not valid to nullify the reference from targetParent to targetProductGroup
                                if (!targetProductGroup.getParent().contains(targetParent)) {
                                    throw new ConcurrentModificationException("ProductGroup with key " + targetParent.getId() +  " is not associated to targetProductGroup.");
                                }

                                targetProductGroup.getParent().remove(targetParent);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.parent)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((ProductGroup)oldParent, (ProductGroup) null, (ProductGroup)targetParent, parentView, storeState);
                                } else {
                                    targetParent.getChildren().remove(targetProductGroup);
                                }
                            }
                        }

                        // Add or update
                        for (ProductGroup newParent : newProductGroup.getParent()) {
                            if (!oldProductGroup.getParent().contains(newParent)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.parent)) {
                                    // Add or update as composite (add if targetParent == null)
                                    ProductGroup targetParent = catalogStoreSessionBean.store((ProductGroup)null, (ProductGroup) newParent, (ProductGroup)null, parentView, storeState);
                                    targetProductGroup.getParent().add(targetParent);
                                    // Update bidirectional reference: 'children'
                                    if (targetParent != null) { 
                                        targetParent.getChildren().add(targetProductGroup);
                                    }
                                } else {
                                    ProductGroup targetParent = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newParent)) {
                                        // The newParent id is given, find the object 
                                        targetParent = entityManager.find(ProductGroup.class, newParent.getId());
                                        if (targetParent == null) {
                                            throw new ConcurrentModificationException("ProductGroup with key " + newParent.getId() + " does not exist.");
                                        }
                                    }

                                    if (newParent != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetParent = (ProductGroup) storeState.getTargetFromNewToTargetMap(newParent);
                                        if (targetParent == null || !catalogStoreSessionBean.hasAllIdFields(targetParent)) {
                                            throw new IllegalStateException("ProductGroup " + newParent + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetParent != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getParent().add(targetParent);
                                        // Update bidirectional reference: 'children'
                                        targetParent.getChildren().add(targetProductGroup);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("ProductGroup " + newParent + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetParent is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.parent)) {
                                    for (ProductGroup oldParent : oldProductGroup.getParent()) {
                                        if (newParent != null && newParent.equals(oldParent)) {
                                            // Update
                                            ProductGroup targetParent = entityManager.find(ProductGroup.class, newParent.getId());
                                            if (targetParent == null) {
                                                throw new ConcurrentModificationException("ProductGroup with key " + newParent.getId() + " does not exist.");
                                            }
                                            targetParent = catalogStoreSessionBean.store((ProductGroup)oldParent, (ProductGroup) newParent, (ProductGroup)targetParent, parentView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.products)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.products)) {
                    StoreView productsView = requestedProperties.findView(ProductGroup.Properties.products);
                    if (productsView != null) {
                        // Handle collection of references products

                        // Remove
                        for (Product oldProducts : new ArrayList<Product>(oldProductGroup.getProducts())) {
                            if (!newProductGroup.getProducts().contains(oldProducts)) {
                                // Check if oldProducts, which will be removed, still exists in the database
                                Product targetProducts = entityManager.find(Product.class, oldProducts.getId());
                                if (targetProducts == null) {
                                    throw new ConcurrentModificationException("Product with key " + oldProducts.getId() +  " does not exist.");
                                }

                                // Ensure targetProductGroup still contains the reference to targetProducts
                                // otherwise it is not valid to nullify the reference from targetProducts to targetProductGroup
                                if (!targetProductGroup.getProducts().contains(targetProducts)) {
                                    throw new ConcurrentModificationException("Product with key " + targetProducts.getId() +  " is not associated to targetProductGroup.");
                                }

                                targetProductGroup.getProducts().remove(targetProducts);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.products)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Product)oldProducts, (Product) null, (Product)targetProducts, productsView, storeState);
                                } else {
                                    targetProducts.getProductGroups().remove(targetProductGroup);
                                }
                            }
                        }

                        // Add or update
                        for (Product newProducts : newProductGroup.getProducts()) {
                            if (!oldProductGroup.getProducts().contains(newProducts)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.products)) {
                                    // Add or update as composite (add if targetProducts == null)
                                    Product targetProducts = catalogStoreSessionBean.store((Product)null, (Product) newProducts, (Product)null, productsView, storeState);
                                    targetProductGroup.getProducts().add(targetProducts);
                                    // Update bidirectional reference: 'productGroups'
                                    if (targetProducts != null) { 
                                        targetProducts.getProductGroups().add(targetProductGroup);
                                    }
                                } else {
                                    Product targetProducts = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProducts)) {
                                        // The newProducts id is given, find the object 
                                        targetProducts = entityManager.find(Product.class, newProducts.getId());
                                        if (targetProducts == null) {
                                            throw new ConcurrentModificationException("Product with key " + newProducts.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProducts != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProducts = (Product) storeState.getTargetFromNewToTargetMap(newProducts);
                                        if (targetProducts == null || !catalogStoreSessionBean.hasAllIdFields(targetProducts)) {
                                            throw new IllegalStateException("Product " + newProducts + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProducts != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getProducts().add(targetProducts);
                                        // Update bidirectional reference: 'productGroups'
                                        targetProducts.getProductGroups().add(targetProductGroup);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Product " + newProducts + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProducts is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.products)) {
                                    for (Product oldProducts : oldProductGroup.getProducts()) {
                                        if (newProducts != null && newProducts.equals(oldProducts)) {
                                            // Update
                                            Product targetProducts = entityManager.find(Product.class, newProducts.getId());
                                            if (targetProducts == null) {
                                                throw new ConcurrentModificationException("Product with key " + newProducts.getId() + " does not exist.");
                                            }
                                            targetProducts = catalogStoreSessionBean.store((Product)oldProducts, (Product) newProducts, (Product)targetProducts, productsView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.properties)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.properties)) {
                    StoreView propertiesView = requestedProperties.findView(ProductGroup.Properties.properties);
                    if (propertiesView != null) {
                        // Handle collection of references properties

                        // Remove
                        for (Property oldProperties : new ArrayList<Property>(oldProductGroup.getProperties())) {
                            if (!newProductGroup.getProperties().contains(oldProperties)) {
                                // Check if oldProperties, which will be removed, still exists in the database
                                Property targetProperties = entityManager.find(Property.class, oldProperties.getId());
                                if (targetProperties == null) {
                                    throw new ConcurrentModificationException("Property with key " + oldProperties.getId() +  " does not exist.");
                                }

                                // Ensure targetProductGroup still contains the reference to targetProperties
                                // otherwise it is not valid to nullify the reference from targetProperties to targetProductGroup
                                if (!targetProductGroup.getProperties().contains(targetProperties)) {
                                    throw new ConcurrentModificationException("Property with key " + targetProperties.getId() +  " is not associated to targetProductGroup.");
                                }

                                targetProductGroup.getProperties().remove(targetProperties);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.properties)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Property)oldProperties, (Property) null, (Property)targetProperties, propertiesView, storeState);
                                } else {
                                    targetProperties.getProductGroups().remove(targetProductGroup);
                                }
                            }
                        }

                        // Add or update
                        for (Property newProperties : newProductGroup.getProperties()) {
                            if (!oldProductGroup.getProperties().contains(newProperties)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.properties)) {
                                    // Add or update as composite (add if targetProperties == null)
                                    Property targetProperties = catalogStoreSessionBean.store((Property)null, (Property) newProperties, (Property)null, propertiesView, storeState);
                                    targetProductGroup.getProperties().add(targetProperties);
                                    // Update bidirectional reference: 'productGroups'
                                    if (targetProperties != null) { 
                                        targetProperties.getProductGroups().add(targetProductGroup);
                                    }
                                } else {
                                    Property targetProperties = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newProperties)) {
                                        // The newProperties id is given, find the object 
                                        targetProperties = entityManager.find(Property.class, newProperties.getId());
                                        if (targetProperties == null) {
                                            throw new ConcurrentModificationException("Property with key " + newProperties.getId() + " does not exist.");
                                        }
                                    }

                                    if (newProperties != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetProperties = (Property) storeState.getTargetFromNewToTargetMap(newProperties);
                                        if (targetProperties == null || !catalogStoreSessionBean.hasAllIdFields(targetProperties)) {
                                            throw new IllegalStateException("Property " + newProperties + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetProperties != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getProperties().add(targetProperties);
                                        // Update bidirectional reference: 'productGroups'
                                        targetProperties.getProductGroups().add(targetProductGroup);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Property " + newProperties + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetProperties is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.properties)) {
                                    for (Property oldProperties : oldProductGroup.getProperties()) {
                                        if (newProperties != null && newProperties.equals(oldProperties)) {
                                            // Update
                                            Property targetProperties = entityManager.find(Property.class, newProperties.getId());
                                            if (targetProperties == null) {
                                                throw new ConcurrentModificationException("Property with key " + newProperties.getId() + " does not exist.");
                                            }
                                            targetProperties = catalogStoreSessionBean.store((Property)oldProperties, (Property) newProperties, (Property)targetProperties, propertiesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.excludedProperties)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.excludedProperties)) {
                    StoreView excludedPropertiesView = requestedProperties.findView(ProductGroup.Properties.excludedProperties);
                    if (excludedPropertiesView != null) {
                        // Handle collection of references excludedProperties

                        // Remove
                        for (Property oldExcludedProperties : new ArrayList<Property>(oldProductGroup.getExcludedProperties())) {
                            if (!newProductGroup.getExcludedProperties().contains(oldExcludedProperties)) {
                                // Check if oldExcludedProperties, which will be removed, still exists in the database
                                Property targetExcludedProperties = entityManager.find(Property.class, oldExcludedProperties.getId());
                                if (targetExcludedProperties == null) {
                                    throw new ConcurrentModificationException("Property with key " + oldExcludedProperties.getId() +  " does not exist.");
                                }


                                targetProductGroup.getExcludedProperties().remove(targetExcludedProperties);
                                if (requestedProperties.forceComposite(ProductGroup.Properties.excludedProperties)) {
                                    // Remove as composite
                                    catalogStoreSessionBean.store((Property)oldExcludedProperties, (Property) null, (Property)targetExcludedProperties, excludedPropertiesView, storeState);
                                }
                            }
                        }

                        // Add or update
                        for (Property newExcludedProperties : newProductGroup.getExcludedProperties()) {
                            if (!oldProductGroup.getExcludedProperties().contains(newExcludedProperties)) {

                                if (requestedProperties.forceComposite(ProductGroup.Properties.excludedProperties)) {
                                    // Add or update as composite (add if targetExcludedProperties == null)
                                    Property targetExcludedProperties = catalogStoreSessionBean.store((Property)null, (Property) newExcludedProperties, (Property)null, excludedPropertiesView, storeState);
                                    targetProductGroup.getExcludedProperties().add(targetExcludedProperties);
                                } else {
                                    Property targetExcludedProperties = null;
                                    if (catalogStoreSessionBean.hasAllIdFields(newExcludedProperties)) {
                                        // The newExcludedProperties id is given, find the object 
                                        targetExcludedProperties = entityManager.find(Property.class, newExcludedProperties.getId());
                                        if (targetExcludedProperties == null) {
                                            throw new ConcurrentModificationException("Property with key " + newExcludedProperties.getId() + " does not exist.");
                                        }
                                    }

                                    if (newExcludedProperties != null && storeState.isSecondRun()) {
                                        // Validate the state
                                        targetExcludedProperties = (Property) storeState.getTargetFromNewToTargetMap(newExcludedProperties);
                                        if (targetExcludedProperties == null || !catalogStoreSessionBean.hasAllIdFields(targetExcludedProperties)) {
                                            throw new IllegalStateException("Property " + newExcludedProperties + " is not marked to be handled as a composite.");
                                        }
                                    }

                                    if (targetExcludedProperties != null) {
                                        // The old and new objects differ, update the reference. If the store 
                                        // order is in the right order it could happen during the first run.
                                        targetProductGroup.getExcludedProperties().add(targetExcludedProperties);
                                    } else {
                                        if (storeState.isSecondRun()) {
                                            throw new IllegalStateException("Property " + newExcludedProperties + " is not handled as a composite during the first run.");
                                        } else {
                                            // The targetExcludedProperties is still null, expecting it to be created during this first run.
                                            storeState.setRequiresSecondRun();
                                        }
                                    }
                                }
                            } else {
                                if (requestedProperties.forceComposite(ProductGroup.Properties.excludedProperties)) {
                                    for (Property oldExcludedProperties : oldProductGroup.getExcludedProperties()) {
                                        if (newExcludedProperties != null && newExcludedProperties.equals(oldExcludedProperties)) {
                                            // Update
                                            Property targetExcludedProperties = entityManager.find(Property.class, newExcludedProperties.getId());
                                            if (targetExcludedProperties == null) {
                                                throw new ConcurrentModificationException("Property with key " + newExcludedProperties.getId() + " does not exist.");
                                            }
                                            targetExcludedProperties = catalogStoreSessionBean.store((Property)oldExcludedProperties, (Property) newExcludedProperties, (Property)targetExcludedProperties, excludedPropertiesView, storeState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetProductGroup);

            // remove Taxonomy.excludedProductGroups references
            Query taxonomyExcludedProductGroupsQuery = entityManager.createQuery("SELECT t FROM Taxonomy t WHERE ?1 MEMBER OF t.excludedProductGroups"); 
            taxonomyExcludedProductGroupsQuery.setParameter(1, targetProductGroup); 
            for (Object object : taxonomyExcludedProductGroupsQuery.getResultList()) { 
                ((Taxonomy) object).getExcludedProductGroups().remove(targetProductGroup);
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.catalog)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.catalog)) {
                    // Remove referring reference: catalog
                    targetProductGroup.getCatalog().getProductGroups().remove(targetProductGroup);
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.taxonomy)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.taxonomy)) {
                    // Remove reference: taxonomy
                    for (Taxonomy targetTaxonomy : targetProductGroup.getTaxonomy()) {
                        if (requestedProperties.forceComposite(ProductGroup.Properties.taxonomy)) {
                            // Remove as composite
                            StoreView taxonomyView = requestedProperties.findView(ProductGroup.Properties.taxonomy);
                            catalogStoreSessionBean.store((Taxonomy)targetTaxonomy, (Taxonomy) null, (Taxonomy)targetTaxonomy, taxonomyView, storeState);
                        } else {
                            targetTaxonomy.getProductGroups().remove(targetProductGroup);
                        }
                    }
                    targetProductGroup.getTaxonomy().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.label)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.label)) {
                    // Remove reference: label
                    targetProductGroup.getLabel().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.children)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.children)) {
                    // Remove reference: children
                    for (ProductGroup targetChildren : targetProductGroup.getChildren()) {
                        if (requestedProperties.forceComposite(ProductGroup.Properties.children)) {
                            // Remove as composite
                            StoreView childrenView = requestedProperties.findView(ProductGroup.Properties.children);
                            catalogStoreSessionBean.store((ProductGroup)targetChildren, (ProductGroup) null, (ProductGroup)targetChildren, childrenView, storeState);
                        } else {
                            targetChildren.getParent().remove(targetProductGroup);
                        }
                    }
                    targetProductGroup.getChildren().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.parent)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.parent)) {
                    // Remove reference: parent
                    for (ProductGroup targetParent : targetProductGroup.getParent()) {
                        if (requestedProperties.forceComposite(ProductGroup.Properties.parent)) {
                            // Remove as composite
                            StoreView parentView = requestedProperties.findView(ProductGroup.Properties.parent);
                            catalogStoreSessionBean.store((ProductGroup)targetParent, (ProductGroup) null, (ProductGroup)targetParent, parentView, storeState);
                        } else {
                            targetParent.getChildren().remove(targetProductGroup);
                        }
                    }
                    targetProductGroup.getParent().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.products)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.products)) {
                    // Remove reference: products
                    for (Product targetProducts : targetProductGroup.getProducts()) {
                        if (requestedProperties.forceComposite(ProductGroup.Properties.products)) {
                            // Remove as composite
                            StoreView productsView = requestedProperties.findView(ProductGroup.Properties.products);
                            catalogStoreSessionBean.store((Product)targetProducts, (Product) null, (Product)targetProducts, productsView, storeState);
                        } else {
                            targetProducts.getProductGroups().remove(targetProductGroup);
                        }
                    }
                    targetProductGroup.getProducts().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.properties)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.properties)) {
                    // Remove reference: properties
                    for (Property targetProperties : targetProductGroup.getProperties()) {
                        if (requestedProperties.forceComposite(ProductGroup.Properties.properties)) {
                            // Remove as composite
                            StoreView propertiesView = requestedProperties.findView(ProductGroup.Properties.properties);
                            catalogStoreSessionBean.store((Property)targetProperties, (Property) null, (Property)targetProperties, propertiesView, storeState);
                        } else {
                            targetProperties.getProductGroups().remove(targetProductGroup);
                        }
                    }
                    targetProductGroup.getProperties().clear();
                }
            }
            if (requestedProperties.includedProperties().contains(ProductGroup.Properties.excludedProperties)) {
                if (processedProperties.includedProperties().add(ProductGroup.Properties.excludedProperties)) {
                    // Remove reference: excludedProperties
                    targetProductGroup.getExcludedProperties().clear();
                }
            }
            targetProductGroup = null;
        }

        return targetProductGroup;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(Label object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store Label
     */
    public Label store(Label oldLabel, Label newLabel, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        Label result = store(oldLabel, newLabel, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldLabel, newLabel, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store Label
     */
    public Label store(Label oldLabel, Label newLabel, Label targetLabel, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetLabel;
        }
        // Create new object or get the actual object from the database 
        if(targetLabel == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldLabel == null && newLabel == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldLabel == null) {
                targetLabel = (Label) storeState.getTargetFromNewToTargetMap(newLabel);
                if (targetLabel == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldLabel = new Label();
                    targetLabel = new Label();

                    targetLabel.setId(newLabel.getId());
                    entityManager.persist(targetLabel);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newLabel, targetLabel);
                } else {
                    // The targetLabel has already been created during the first run
                    oldLabel = new Label();
                }
            } else {
                if (newLabel != null && isChanged(oldLabel.getId(), newLabel.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldLabel.getId() + ", new value: " + newLabel.getId());
                }
                if (!hasAllIdFields(oldLabel)) {
                    throw new RuntimeException("No key exception: oldLabel has no key.");
                } else {
                    targetLabel = entityManager.find(Label.class, oldLabel.getId());
                    if (targetLabel == null) {
                        throw new ConcurrentModificationException("Label with key " + oldLabel.getId() + " does not exist, while oldLabel != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetLabel);

        if (newLabel != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(Label.Properties.language)) {
                if (processedProperties.includedProperties().add(Label.Properties.language)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property language
                        // Update language field
                        if (isChanged(oldLabel.getLanguage(), newLabel.getLanguage())) {
                            if (!isChanged(targetLabel.getLanguage(), oldLabel.getLanguage())) {
                                targetLabel.setLanguage(newLabel.getLanguage());
                            } else {
                                throw new ConcurrentModificationException("language: database value is: " + targetLabel.getLanguage() + " while old value is: " + oldLabel.getLanguage() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(Label.Properties.label)) {
                if (processedProperties.includedProperties().add(Label.Properties.label)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property label
                        // Update label field
                        if (isChanged(oldLabel.getLabel(), newLabel.getLabel())) {
                            if (!isChanged(targetLabel.getLabel(), oldLabel.getLabel())) {
                                targetLabel.setLabel(newLabel.getLabel());
                            } else {
                                throw new ConcurrentModificationException("label: database value is: " + targetLabel.getLabel() + " while old value is: " + oldLabel.getLabel() + ".");
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetLabel);

            // remove ProductGroup.label references
            Query productGroupLabelQuery = entityManager.createQuery("SELECT p FROM ProductGroup p WHERE ?1 MEMBER OF p.label"); 
            productGroupLabelQuery.setParameter(1, targetLabel); 
            for (Object object : productGroupLabelQuery.getResultList()) { 
                ((ProductGroup) object).getLabel().remove(targetLabel);
            }

            // remove EnumValue.label references
            Query enumValueLabelQuery = entityManager.createQuery("SELECT e FROM EnumValue e WHERE ?1 MEMBER OF e.label"); 
            enumValueLabelQuery.setParameter(1, targetLabel); 
            for (Object object : enumValueLabelQuery.getResultList()) { 
                ((EnumValue) object).getLabel().remove(targetLabel);
            }
            targetLabel = null;
        }

        return targetLabel;
    }

    /**
     * Returns true if all the id fields are set
     */
    public boolean hasAllIdFields(PropertyValue object) {
        if (object.getId() == null) return false;
        return true;
    }

    /**
     * Store PropertyValue
     */
    public PropertyValue store(PropertyValue oldPropertyValue, PropertyValue newPropertyValue, StoreView requestedProperties) {
        StoreState storeState = new StoreState();
        PropertyValue result = store(oldPropertyValue, newPropertyValue, null, requestedProperties, storeState);
        if (storeState.requiresSecondRun()) {
            // A second run might be required to update references 
            // to objects that have been created in the first run.
            storeState.setSecondRun();
            storeState.resetProcessedProperties();
            result = store(oldPropertyValue, newPropertyValue, null, requestedProperties, storeState);
        }
        return result;
    }

    /**
     * Store PropertyValue
     */
    public PropertyValue store(PropertyValue oldPropertyValue, PropertyValue newPropertyValue, PropertyValue targetPropertyValue, StoreView requestedProperties, StoreState storeState) {

        if (requestedProperties == null) {
            return targetPropertyValue;
        }
        // Create new object or get the actual object from the database 
        if(targetPropertyValue == null) {
            // The target was not yet created or retrieved (by a specialization store method)
            if (oldPropertyValue == null && newPropertyValue == null) {
                throw new RuntimeException("Old and new objects are both null");
            } else if (oldPropertyValue == null) {
                targetPropertyValue = (PropertyValue) storeState.getTargetFromNewToTargetMap(newPropertyValue);
                if (targetPropertyValue == null) {
                    if (storeState.isSecondRun()) {
                        throw new IllegalStateException("New objects can only be persisted during the first run.");
                    }
                    // Create
                    oldPropertyValue = new PropertyValue();
                    targetPropertyValue = new PropertyValue();

                    targetPropertyValue.setId(newPropertyValue.getId());
                    entityManager.persist(targetPropertyValue);

                    // Add the persisted object to the state to enable a find in this run or in a second run
                    storeState.addToNewToTargetMap(newPropertyValue, targetPropertyValue);
                } else {
                    // The targetPropertyValue has already been created during the first run
                    oldPropertyValue = new PropertyValue();
                }
            } else {
                if (newPropertyValue != null && isChanged(oldPropertyValue.getId(), newPropertyValue.getId())) {
                    // TODO: throw correct exception
                    throw new RuntimeException("Id field id has been changed. Old value: " + oldPropertyValue.getId() + ", new value: " + newPropertyValue.getId());
                }
                if (!hasAllIdFields(oldPropertyValue)) {
                    throw new RuntimeException("No key exception: oldPropertyValue has no key.");
                } else {
                    targetPropertyValue = entityManager.find(PropertyValue.class, oldPropertyValue.getId());
                    if (targetPropertyValue == null) {
                        throw new ConcurrentModificationException("PropertyValue with key " + oldPropertyValue.getId() + " does not exist, while oldPropertyValue != null.");
                    }
                }
            }
        }

        ProcessedStoreView processedProperties = storeState.getOrCreate(targetPropertyValue);

        if (newPropertyValue != null) {

            // Update the properties based on the given requestedProperties
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.property)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.property)) {
                    StoreView propertyView = requestedProperties.findView(PropertyValue.Properties.property);
                    if (propertyView != null) {
                        // Handle reference property
                        if (oldPropertyValue.getProperty() != null && newPropertyValue.getProperty() == null) {
                            // Remove
                            Property targetProperty = targetPropertyValue.getProperty();

                            if (targetProperty == null) {
                                throw new ConcurrentModificationException("oldPropertyValue.getProperty() with value: " + oldPropertyValue.getProperty() + " does not exist in the database.");
                            } else if(!targetProperty.equals(oldPropertyValue.getProperty())) {
                                throw new ConcurrentModificationException("targetPropertyValue.getProperty() with value: " + targetPropertyValue.getProperty() + " is not equal to oldPropertyValue.getProperty() with value: " + oldPropertyValue.getProperty() + ".");
                            }

                            if (requestedProperties.forceComposite(PropertyValue.Properties.property)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((Property)oldPropertyValue.getProperty(), (Property) null, (Property)targetProperty, propertyView, storeState);
                                targetPropertyValue.setProperty(null);
                            } else {
                                targetPropertyValue.setProperty(null);
                            }
                        } else {
                            // Add or update
                            Property newProperty = newPropertyValue.getProperty();

                            if (newProperty != null) {
                                if (requestedProperties.forceComposite(PropertyValue.Properties.property)) {
                                    // Update as composite
                                    if (oldPropertyValue.getProperty() != null && oldPropertyValue.getProperty().equals(newProperty)) {
                                        catalogStoreSessionBean.store((Property)oldPropertyValue.getProperty(), (Property) newProperty, (Property)null, propertyView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldPropertyValue.getProperty() != null) {
                                            catalogStoreSessionBean.store((Property)oldPropertyValue.getProperty(), (Property) null, (Property)null, propertyView, storeState);
                                        }

                                        // Add 
                                        Property targetProperty = catalogStoreSessionBean.store((Property)null, (Property) newProperty, (Property)null, propertyView, storeState);
                                        targetPropertyValue.setProperty(targetProperty);
                                    }
                                } else {
                                    if (oldPropertyValue.getProperty() != null && oldPropertyValue.getProperty().equals(newProperty)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((Property)oldPropertyValue.getProperty(), (Property) newProperty, (Property)null, propertyView, storeState);
                                    } else {
                                        Property targetProperty = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newProperty)) {
                                            // The newProperty id is given, find the object 
                                            targetProperty = entityManager.find(Property.class, newProperty.getId());
                                            if (targetProperty == null) {
                                                throw new ConcurrentModificationException("Property with key " + newProperty.getId() + " does not exist.");
                                            }
                                        }

                                        if (newProperty != null) {
                                            // The reference is updated
                                            if (targetProperty == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetProperty = (Property) storeState.getTargetFromNewToTargetMap(newProperty);
                                                if (targetProperty == null || !catalogStoreSessionBean.hasAllIdFields(targetProperty)) {
                                                    throw new IllegalStateException("Property " + newProperty + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetProperty != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetPropertyValue.setProperty(targetProperty);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("Property " + newProperty + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetProperty is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetPropertyValue.setProperty(null);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.language)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.language)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property language
                        // Update language field
                        if (isChanged(oldPropertyValue.getLanguage(), newPropertyValue.getLanguage())) {
                            if (!isChanged(targetPropertyValue.getLanguage(), oldPropertyValue.getLanguage())) {
                                targetPropertyValue.setLanguage(newPropertyValue.getLanguage());
                            } else {
                                throw new ConcurrentModificationException("language: database value is: " + targetPropertyValue.getLanguage() + " while old value is: " + oldPropertyValue.getLanguage() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.stringValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.stringValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property stringValue
                        // Update stringValue field
                        if (isChanged(oldPropertyValue.getStringValue(), newPropertyValue.getStringValue())) {
                            if (!isChanged(targetPropertyValue.getStringValue(), oldPropertyValue.getStringValue())) {
                                targetPropertyValue.setStringValue(newPropertyValue.getStringValue());
                            } else {
                                throw new ConcurrentModificationException("stringValue: database value is: " + targetPropertyValue.getStringValue() + " while old value is: " + oldPropertyValue.getStringValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.integerValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.integerValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property integerValue
                        // Update integerValue field
                        if (isChanged(oldPropertyValue.getIntegerValue(), newPropertyValue.getIntegerValue())) {
                            if (!isChanged(targetPropertyValue.getIntegerValue(), oldPropertyValue.getIntegerValue())) {
                                targetPropertyValue.setIntegerValue(newPropertyValue.getIntegerValue());
                            } else {
                                throw new ConcurrentModificationException("integerValue: database value is: " + targetPropertyValue.getIntegerValue() + " while old value is: " + oldPropertyValue.getIntegerValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.enumValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.enumValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property enumValue
                        // Update enumValue field
                        if (isChanged(oldPropertyValue.getEnumValue(), newPropertyValue.getEnumValue())) {
                            if (!isChanged(targetPropertyValue.getEnumValue(), oldPropertyValue.getEnumValue())) {
                                targetPropertyValue.setEnumValue(newPropertyValue.getEnumValue());
                            } else {
                                throw new ConcurrentModificationException("enumValue: database value is: " + targetPropertyValue.getEnumValue() + " while old value is: " + oldPropertyValue.getEnumValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.realValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.realValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property realValue
                        // Update realValue field
                        if (isChanged(oldPropertyValue.getRealValue(), newPropertyValue.getRealValue())) {
                            if (!isChanged(targetPropertyValue.getRealValue(), oldPropertyValue.getRealValue())) {
                                targetPropertyValue.setRealValue(newPropertyValue.getRealValue());
                            } else {
                                throw new ConcurrentModificationException("realValue: database value is: " + targetPropertyValue.getRealValue() + " while old value is: " + oldPropertyValue.getRealValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.booleanValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.booleanValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property booleanValue
                        // Update booleanValue field
                        if (isChanged(oldPropertyValue.getBooleanValue(), newPropertyValue.getBooleanValue())) {
                            if (!isChanged(targetPropertyValue.getBooleanValue(), oldPropertyValue.getBooleanValue())) {
                                targetPropertyValue.setBooleanValue(newPropertyValue.getBooleanValue());
                            } else {
                                throw new ConcurrentModificationException("booleanValue: database value is: " + targetPropertyValue.getBooleanValue() + " while old value is: " + oldPropertyValue.getBooleanValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.moneyValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.moneyValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property moneyValue
                        // Update moneyValue field
                        if (isChanged(oldPropertyValue.getMoneyValue(), newPropertyValue.getMoneyValue())) {
                            if (!isChanged(targetPropertyValue.getMoneyValue(), oldPropertyValue.getMoneyValue())) {
                                targetPropertyValue.setMoneyValue(newPropertyValue.getMoneyValue());
                            } else {
                                throw new ConcurrentModificationException("moneyValue: database value is: " + targetPropertyValue.getMoneyValue() + " while old value is: " + oldPropertyValue.getMoneyValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.moneyCurrency)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.moneyCurrency)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property moneyCurrency
                        // Update moneyCurrency field
                        if (isChanged(oldPropertyValue.getMoneyCurrency(), newPropertyValue.getMoneyCurrency())) {
                            if (!isChanged(targetPropertyValue.getMoneyCurrency(), oldPropertyValue.getMoneyCurrency())) {
                                targetPropertyValue.setMoneyCurrency(newPropertyValue.getMoneyCurrency());
                            } else {
                                throw new ConcurrentModificationException("moneyCurrency: database value is: " + targetPropertyValue.getMoneyCurrency() + " while old value is: " + oldPropertyValue.getMoneyCurrency() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.mediaValue)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.mediaValue)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property mediaValue
                        // Update mediaValue field
                        if (isChanged(oldPropertyValue.getMediaValue(), newPropertyValue.getMediaValue())) {
                            if (!isChanged(targetPropertyValue.getMediaValue(), oldPropertyValue.getMediaValue())) {
                                targetPropertyValue.setMediaValue(newPropertyValue.getMediaValue());
                            } else {
                                throw new ConcurrentModificationException("mediaValue: database value is: " + targetPropertyValue.getMediaValue() + " while old value is: " + oldPropertyValue.getMediaValue() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.mimeType)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.mimeType)) {
                    if (!storeState.isSecondRun()) {
                        // Handle field property mimeType
                        // Update mimeType field
                        if (isChanged(oldPropertyValue.getMimeType(), newPropertyValue.getMimeType())) {
                            if (!isChanged(targetPropertyValue.getMimeType(), oldPropertyValue.getMimeType())) {
                                targetPropertyValue.setMimeType(newPropertyValue.getMimeType());
                            } else {
                                throw new ConcurrentModificationException("mimeType: database value is: " + targetPropertyValue.getMimeType() + " while old value is: " + oldPropertyValue.getMimeType() + ".");
                            }
                        }
                    }
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.product)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.product)) {
                    StoreView productView = requestedProperties.findView(PropertyValue.Properties.product);
                    if (productView != null) {
                        // Handle reference product
                        if (oldPropertyValue.getProduct() != null && newPropertyValue.getProduct() == null) {
                            // Remove
                            Product targetProduct = targetPropertyValue.getProduct();

                            if (targetProduct == null) {
                                throw new ConcurrentModificationException("oldPropertyValue.getProduct() with value: " + oldPropertyValue.getProduct() + " does not exist in the database.");
                            } else if(!targetProduct.equals(oldPropertyValue.getProduct())) {
                                throw new ConcurrentModificationException("targetPropertyValue.getProduct() with value: " + targetPropertyValue.getProduct() + " is not equal to oldPropertyValue.getProduct() with value: " + oldPropertyValue.getProduct() + ".");
                            }

                            if (requestedProperties.forceComposite(PropertyValue.Properties.product)) {
                                // Remove as composite
                                catalogStoreSessionBean.store((Product)oldPropertyValue.getProduct(), (Product) null, (Product)targetProduct, productView, storeState);
                                targetPropertyValue.setProduct(null);
                            } else {
                                targetPropertyValue.setProduct(null);
                            }
                        } else {
                            // Add or update
                            Product newProduct = newPropertyValue.getProduct();

                            if (newProduct != null) {
                                if (requestedProperties.forceComposite(PropertyValue.Properties.product)) {
                                    // Update as composite
                                    if (oldPropertyValue.getProduct() != null && oldPropertyValue.getProduct().equals(newProduct)) {
                                        catalogStoreSessionBean.store((Product)oldPropertyValue.getProduct(), (Product) newProduct, (Product)null, productView, storeState);
                                    } else {
                                        // Remove 
                                        if (oldPropertyValue.getProduct() != null) {
                                            catalogStoreSessionBean.store((Product)oldPropertyValue.getProduct(), (Product) null, (Product)null, productView, storeState);
                                        }

                                        // Add 
                                        Product targetProduct = catalogStoreSessionBean.store((Product)null, (Product) newProduct, (Product)null, productView, storeState);
                                        targetPropertyValue.setProduct(targetProduct);
                                        // Update bidirectional reference: 'propertyValues'
                                        if (targetProduct != null) { 
                                            targetProduct.getPropertyValues().add(targetPropertyValue);
                                        }
                                    }
                                } else {
                                    if (oldPropertyValue.getProduct() != null && oldPropertyValue.getProduct().equals(newProduct)) {
                                        // Although force composite is false, the new object is the same so we can update the 
                                        // fields of the object. There is no need to update the reference itself.
                                        catalogStoreSessionBean.store((Product)oldPropertyValue.getProduct(), (Product) newProduct, (Product)null, productView, storeState);
                                    } else {
                                        Product targetProduct = null;
                                        if (catalogStoreSessionBean.hasAllIdFields(newProduct)) {
                                            // The newProduct id is given, find the object 
                                            targetProduct = entityManager.find(Product.class, newProduct.getId());
                                            if (targetProduct == null) {
                                                throw new ConcurrentModificationException("Product with key " + newProduct.getId() + " does not exist.");
                                            }
                                        }

                                        if (newProduct != null) {
                                            // The reference is updated
                                            if (targetProduct == null && storeState.isSecondRun()) {
                                                // Validate the state
                                                targetProduct = (Product) storeState.getTargetFromNewToTargetMap(newProduct);
                                                if (targetProduct == null || !catalogStoreSessionBean.hasAllIdFields(targetProduct)) {
                                                    throw new IllegalStateException("Product " + newProduct + " is not handled as a composite.");
                                                }
                                            }

                                            if (targetProduct != null) {
                                                // The old and new objects differ, update the reference. If the store 
                                                // order is in the right order it could happen during the first run.
                                                 
                                                targetPropertyValue.setProduct(targetProduct);
                                                // Update bidirectional reference: 'propertyValues'
                                                targetProduct.getPropertyValues().add(targetPropertyValue);
                                            } else {
                                                if (storeState.isSecondRun()) {
                                                    throw new IllegalStateException("Product " + newProduct + " is not handled as a composite during the first run.");
                                                } else {
                                                    // The targetProduct is still null, expecting it to be created during this first run.
                                                    storeState.setRequiresSecondRun();
                                                }
                                            }
                                        } else {
                                            // Reference is removed, nullify the reference
                                            targetPropertyValue.setProduct(null);
                                            // Update bidirectional reference: 'propertyValues'
                                            if (targetProduct != null) {
                                                targetProduct.getPropertyValues().remove(targetPropertyValue);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Remove the object

            entityManager.remove(targetPropertyValue);
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.property)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.property)) {
                }
            }
            if (requestedProperties.includedProperties().contains(PropertyValue.Properties.product)) {
                if (processedProperties.includedProperties().add(PropertyValue.Properties.product)) {
                    // Remove referring reference: product
                    targetPropertyValue.getProduct().getPropertyValues().remove(targetPropertyValue);
                }
            }
            targetPropertyValue = null;
        }

        return targetPropertyValue;
    }
}