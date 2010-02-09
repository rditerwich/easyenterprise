package agilexs.catalogxs.presentation.model

import java.util.LinkedHashSet
import scala.xml.NodeSeq 
import scala.collection.Set

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.presentation.util.{Delegate, ProjectionMap}

class Mapping(product : Option[Product], cache : CatalogCache) {
  lazy val productGroups = ProjectionMap((g : jpa.ProductGroup) => new ProductGroup(g, product, cache, this))
  lazy val products = ProjectionMap((p : jpa.Product) => new Product(p, cache, this))
  lazy val properties = ProjectionMap((p : jpa.Property) => new Property(p, product, None, cache, this))
  lazy val promotions = ProjectionMap( (p : jpa.Promotion) => p match { 
    case p : jpa.VolumeDiscountPromotion => new VolumeDiscountPromotion(p, cache, this)
    case _ => new Promotion(p, cache, this)
  })
}

class Catalog (val cache : CatalogCache) extends Delegate(cache.catalog) {

  private val mapping = new Mapping(None, cache)
  
  val id = cache.catalog.getId.longValue
  
  lazy val excludedProductGroups : Set[ProductGroup] =
   cache.excludedProductGroups map (mapping.productGroups) toSet
  
  lazy val excludedProperties : Set[Property] = 
    cache.excludedProperties map (mapping.properties) toSet

  lazy val promotions : Set[Promotion] = 
    cache.promotions map (mapping.promotions) toSet
  
  lazy val productGroups : Set[ProductGroup] = 
	cache.catalog.getProductGroups map (mapping.productGroups) filter (!excludedProductGroups.contains(_)) toSet

  lazy val topLevelProductGroups : Set[ProductGroup] = 
	cache.topLevelProductGroups map (mapping.productGroups) toSet

  lazy val products : Set[Product] =
    cache.products map (mapping.products) toSet
}

class ProductGroup(productGroup : jpa.ProductGroup, val product : Option[Product], cache : CatalogCache, mapping : Mapping) extends Delegate(productGroup) {

  // terminate recursion
  mapping.productGroups += (productGroup -> this)
  
  val id = productGroup.getId.longValue

  val parents : Set[ProductGroup] = 
    productGroup.getParents map(mapping.productGroups) toSet
  
  val children : Set[ProductGroup] = 
	productGroup.getChildren map(mapping.productGroups) toSet
    
  val products : Set[Product] =
    productGroup.getProducts map(mapping.products) toSet 
  
  val properties : Set[Property] =
    productGroup.getProperties map(mapping.properties) toSet
    
  lazy val productExtent : Set[Product] =
    cache.productGroupProductExtent(productGroup) map(mapping.products) toSet
}

class Product(product : jpa.Product, cache : CatalogCache, var mapping : Mapping) extends Delegate(product) {

  // terminate recursion
  mapping.products += (product -> this)
  
  // product has its own mappings
  mapping = new Mapping(Some(this), cache)

  val id : Long = product.getId.longValue
  
  val properties : Set[Property] = 
    product.getPropertyValues map (v => 
      new Property(v.getProperty, Some(this), Some(v), cache, mapping)) toSet
  
  val productGroups : Set[ProductGroup] = 
    product.getProductGroups filter(!cache.excludedProductGroups.contains(_)) map(mapping.productGroups) toSet 
  
  val productGroupExtent : Set[ProductGroup] = 
    product.getProductGroups filter(!cache.excludedProductGroups.contains(_)) map(mapping.productGroups) toSet 
}

class Property(property : jpa.Property, val product : Option[Product], val value : Option[jpa.PropertyValue], cache : CatalogCache, mapping : Mapping) extends Delegate(property)  {
  // terminate recursion
  mapping.properties(property) = this

  val id = property.getId.longValue
  val name = property.getName
}

class Promotion(promotion : jpa.Promotion, cache : CatalogCache, mapping : Mapping) extends Delegate(promotion) {
  // terminate recursion
  mapping.promotions(promotion) = this
  val id = promotion.getId.longValue
}

class VolumeDiscountPromotion(promotion : jpa.VolumeDiscountPromotion, cache : CatalogCache, mapping : Mapping) extends Promotion(promotion, cache, mapping) {
  val startDate = promotion.getStartDate
  val endDate = promotion.getEndDate
  val price = promotion.getPrice
  val priceCurrency = promotion.getPriceCurrency
  val volumeDiscount = promotion.getVolumeDiscount
  val product = mapping.products(promotion.getProduct)
}
