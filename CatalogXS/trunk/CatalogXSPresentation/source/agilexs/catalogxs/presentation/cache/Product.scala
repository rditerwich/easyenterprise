package agilexs.catalogxs.presentation.cache

import scala.collection.Set
import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.model.Conversions._

case class Product(catalog : Catalog, product : jpa.Product) {

  val properties : Set[Property] = 
    Set(product.getPropertyValues map ((value : jpa.PropertyValue) => 
      Property(catalog, value.getProperty) set(this) set(value)):_*)
  
  val productGroups : Set[ProductGroup] = 
    Set(product.getProductGroups map(catalog.productGroupMap) filter(!catalog.excludedProductGroups.contains(_)):_*)
  
  val productGroupExtent : Set[ProductGroup] = 
    Set(product.getProductGroups map(catalog.productGroupMap) filter(!catalog.excludedProductGroups.contains(_)):_*)
}
