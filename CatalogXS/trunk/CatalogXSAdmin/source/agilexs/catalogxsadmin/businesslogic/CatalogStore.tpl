package agilexs.catalogxsadmin.businesslogic

import metaphor.psm.javaeeaspects.AdditionalStoreBeanOperations
import metaphor.psm.jpa.IEntityClass

[template CatalogDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Catalog") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetCatalog.setName(newCatalog.getName());  
[/template]

[template ItemDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Item") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetItem.setCatalog(newItem.getCatalog());  
[/template]

[template ProductGroupDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("ProductGroup") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetProductGroup.setCatalog(newProductGroup.getCatalog());  
    targetProductGroup.setContainsProducts(newProductGroup.getContainsProducts());  
[/template]

[template ProductDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Product") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetProduct.setCatalog(newProduct.getCatalog());  
[/template]

[template RelationDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Relation") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetRelation.setItem(newRelation.getItem());  
    targetRelation.setRelatedTo(newRelation.getRelatedTo());  
    targetRelation.setRelationType(newRelation.getRelationType());  
[/template]

[template RelationTypeDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("RelationType") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetRelationType.setName(newRelationType.getName());  
[/template]

[template PropetyDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Property") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetProperty.setProductGroupProperty(newProperty.getProductGroupProperty());  
    targetProperty.setItem(newProperty.getItem());  
    targetProperty.setType(newProperty.getType());  
[/template]

[template EnumValueDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("EnumValue") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetEnumValue.setValue(newEnumValue.getValue());  
    targetEnumValue.setProperty(newEnumValue.getProperty());  
[/template]

/*
[template CatalogViewDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("CatalogView") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetCatalogView.setName(newCatalogView.getName());  
    targetCatalogView.setCatalog(newCatalogView.getCatalog());  
[/template]
*/

[template LabelDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Label") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetLabel.setLabel(newLabel.getLabel());  
[/template]

[template PropetyValueDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("PropertyValue") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetPropertyValue.setProperty(newPropertyValue.getProperty());  
    targetPropertyValue.setLanguage(newPropertyValue.getLanguage());
[/template]

/*
[template PromotionDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Promotion") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetPromotion.setStartDate(newPromotion.getStartDate());  
    targetPromotion.setEndDate(newPromotion.getEndDate());  
    targetPromotion.setView(newPromotion.getView());
[/template]

[template VolumeDiscountPromotionDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("VolumeDiscountPromotion") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetVolumeDiscountPromotion.setProduct(newVolumeDiscountPromotion.getProduct());  
    targetVolumeDiscountPromotion.setPrice(newVolumeDiscountPromotion.getPrice());  
    targetVolumeDiscountPromotion.setPriceCurrency(newVolumeDiscountPromotion.getPriceCurrency());  
    targetVolumeDiscountPromotion.setVolumeDiscount(newVolumeDiscountPromotion.getVolumeDiscount());  
[/template]

[template TemplateDefaultAttributeValues(IEntityClass entityClass) constraint entityClass.name.equals("Template") joins AdditionalStoreBeanOperations.DefaultAttributeValues]
    targetTemplate.setName(newTemplate.getName());  
    targetTemplate.setLanguage(newTemplate.getLanguage());  
    targetTemplate.setTemplateXml(newTemplate.getTemplateXml());  
[/template]
*/
