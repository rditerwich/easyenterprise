package agilexs.catalogxs.presentation.model

import java.util.LinkedHashSet
import scala.xml.NodeSeq 
import scala.collection.{Set, Map}

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.presentation.util.{Delegate, ProjectionMap}

class Mapping(product : Option[Product], cache : CatalogCache) {
  lazy val productGroups = ProjectionMap((g : jpa.ProductGroup) => new ProductGroup(g, product, cache, this))
  lazy val products = ProjectionMap((p : jpa.Product) => new Product(p, cache, this))
  lazy val properties = ProjectionMap((p : jpa.Property) => new Property(p, noPropertyValue, product, cache, this))
  lazy val promotions = ProjectionMap( (p : jpa.Promotion) => p match { 
    case p : jpa.VolumeDiscountPromotion => new VolumeDiscountPromotion(p, cache, this)
    case _ => new Promotion(p, cache, this)
  })
}

class Catalog (val cache : CatalogCache) extends Delegate(cache.catalog) {

  private val mapping = new Mapping(None, cache)
  
  val id = cache.catalog.getId.longValue
  
  val excludedProductGroups : Set[ProductGroup] =
   cache.excludedProductGroups map (mapping.productGroups) toSet
  
  val excludedProperties : Set[Property] = 
    cache.excludedProperties map (mapping.properties) toSet

  val promotions : Set[Promotion] = 
    cache.promotions map (mapping.promotions) toSet
  
  val productGroups : Set[ProductGroup] = 
	cache.catalog.getProductGroups map (mapping.productGroups) filter (!excludedProductGroups.contains(_)) toSet

  val productGroupsById : Map[Long, ProductGroup] = 
    productGroups makeMapReverse (_.id)
    
  val topLevelProductGroups : Set[ProductGroup] = 
	cache.topLevelProductGroups map (mapping.productGroups) toSet

  val products : Set[Product] =
    cache.products map (mapping.products) toSet

  val mediaValues : Map[Long, (String, Array[Byte])] =
    cache.mediaValues
}

class ProductGroup(productGroup : jpa.ProductGroup, val product : Option[Product], cache : CatalogCache, mapping : Mapping) extends Delegate(productGroup) {

  // terminate recursion
  mapping.productGroups += (productGroup -> this)
  
  val id = productGroup.getId.longValue
  val name = productGroup.getName or id.toString
  
  val parents : Set[ProductGroup] = 
    cache.productGroupParents(productGroup) map (mapping.productGroups) toSet
  
  val children : Set[ProductGroup] =
    cache.productGroupChildren(productGroup) map (mapping.productGroups) toSet
    
  val products : Set[Product] =
    productGroup.getProducts map(mapping.products) toSet 
  
  val groupProperties : Seq[Property] = 
	cache.productGroupPropertyValues(productGroup) map (v => 
	  new Property(v.getProperty, v, None, cache, mapping)) toSet

  val groupPropertiesByName : Map[String, Property] = 
    groupProperties makeMapReverse (_.name)
    
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
  val name = product.getName or id.toString
  
  val properties : Set[Property] =
	cache.productPropertyValues(product) map (v => 
	  new Property(v.getProperty, v, Some(this), cache, mapping)) toSet
  
  val productGroups : Set[ProductGroup] = 
    product.getProductGroups filter(!cache.excludedProductGroups.contains(_)) map(mapping.productGroups) toSet 
  
  val productGroupExtent : Set[ProductGroup] = 
    product.getProductGroups filter(!cache.excludedProductGroups.contains(_)) map(mapping.productGroups) toSet

  val propertiesByName : Map[String, Property] = 
    properties makeMapReverse (_.name)
}

class Property(property : jpa.Property, val value : jpa.PropertyValue, val product : Option[Product], cache : CatalogCache, mapping : Mapping) extends Delegate(property)  {
  // terminate recursion
  mapping.properties(property) = this

  val id : Long = property.getId.longValue
  val name : String = property.getName or id.toString
  val propertyType : jpa.PropertyType = property.getType
  
  val valueId : Long = value.getId.longValue
  val mimeType : String = value.getMimeType or ""
  val mediaValue : Array[Byte] = value.getMediaValue
  
  def hasValue = value != noPropertyValue
  
  val valueAsString = 
    if (value.getStringValue != null) value.getStringValue
    else if (value.getStringValue != null) value.getStringValue
    else if (value.getBooleanValue != null) value.getBooleanValue.toString
    else if (value.getEnumValue != null) value.getEnumValue.toString
    else if (value.getIntegerValue != null) value.getIntegerValue.toString
    else if (value.getMoneyValue != null) value.getMoneyValue.toString
    else if (value.getRealValue != null) value.getRealValue.toString
    else ""
}

object noPropertyValue extends jpa.PropertyValue {
  setId(-1l)
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
