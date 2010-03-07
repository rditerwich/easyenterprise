package agilexs.catalogxs.presentation.model

import java.util.LinkedHashSet
import scala.xml.NodeSeq 
import scala.xml.Text 
import scala.collection.{mutable, immutable, Set, Map}

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.jpa.{order => jpaOrder}
import agilexs.catalogxs.jpa.order.ProductOrder
import agilexs.catalogxs.jpa.catalog.PropertyType
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.presentation.util.{Delegate, ProjectionMap, KeywordMap}

class Mapping(product : Option[Product], webShopData : WebShopData) {
  lazy val productGroups = ProjectionMap((g : jpa.ProductGroup) => new ProductGroup(g, product, webShopData, this))
  lazy val products = ProjectionMap((p : jpa.Product) => new Product(p, webShopData, this))
  lazy val properties = ProjectionMap((p : jpa.Property) => new Property(p, noPropertyValue, product, webShopData, this))
  lazy val promotions = ProjectionMap( (p : jpa.Promotion) => p match { 
    case p : jpa.VolumeDiscountPromotion => new VolumeDiscountPromotion(p, webShopData, this)
    case _ => new Promotion(p, webShopData, this)
  })
}

class WebShop (val webShopData : WebShopData) extends Delegate(webShopData.catalog) {

  private val mapping = new Mapping(None, webShopData)
  
  val id = webShopData.catalog.getId.longValue
  
  val excludedProperties : Set[Property] = 
    webShopData.excludedProperties map (mapping.properties) toSet

  val promotions : Set[Promotion] = 
    webShopData.promotions map (mapping.promotions) toSet
  
  val productGroups : Set[ProductGroup] = 
	webShopData.productGroups map (mapping.productGroups) toSet

  val productGroupsById : Map[Long, ProductGroup] = 
    productGroups makeMapWithKeys (_.id)
    
  val topLevelProductGroups : Set[ProductGroup] = 
	webShopData.topLevelProductGroups map (mapping.productGroups) toSet

  val products : Set[Product] =
    webShopData.products map (mapping.products) toSet

  val productsById : Map[Long, Product] = 
    products makeMapWithKeys (_.id)
    
  val mediaValues : Map[Long, (String, Array[Byte])] =
    webShopData.mediaValues
  
  val keywordMap =
    KeywordMap(products map (p => (p.properties map (_.valueAsString), p))) 
}

class ProductGroup(productGroup : jpa.ProductGroup, val product : Option[Product], webShopData : WebShopData, mapping : Mapping) extends Delegate(productGroup) {

  // terminate recursion
  mapping.productGroups += (productGroup -> this)
  
  val id = productGroup.getId.longValue
  
  val parents : Set[ProductGroup] = 
    webShopData.productGroupParents(productGroup) map (mapping.productGroups) toSet
  
  val children : Set[ProductGroup] =
    webShopData.productGroupChildGroups(productGroup) map (mapping.productGroups) toSet
    
  val products : Set[Product] =
    webShopData.productGroupProducts(this) map(mapping.products) toSet 
  
  val groupProperties : Seq[Property] = 
	webShopData.productGroupPropertyValues(productGroup) map (v => 
	  new Property(v.getProperty, v, None, webShopData, mapping)) 

  val groupPropertiesByName : Map[String, Property] = 
    groupProperties makeMapWithKeys (_.name)
    
  val properties : Set[Property] =
    productGroup.getProperties map(mapping.properties) toSet
    
  lazy val productExtent : Set[Product] =
    webShopData.productGroupProductExtent(productGroup) map(mapping.products) toSet
    
  lazy val productExtentPromotions : Set[Promotion] = {
    val promotions = webShopData.promotions map(mapping.promotions) filter (p => !(p.products ** productExtent).isEmpty)  
    if (promotions isEmpty) Set.empty else Set(promotions.toSeq first) 
  }
}

class Product(product : jpa.Product, webShopData : WebShopData, var mapping : Mapping) extends Delegate(product) {
  
  // terminate recursion
  mapping.products += (product -> this)
  
  // product has its own mappings
  mapping = new Mapping(Some(this), webShopData)

  val id : Long = product.getId.longValue
  
  val properties : Set[Property] =
	webShopData.productPropertyValues(product) map (v => 
	  new Property(v.getProperty, v, Some(this), webShopData, mapping)) toSet
  
  val productGroups : Set[ProductGroup] = 
    product.getParents filter(!webShopData.excludedItems.contains(_)) map(mapping.productGroups) toSet 
  
  val productGroupExtent : Set[ProductGroup] = 
    product.getParents filter(!webShopData.excludedItems.contains(_)) map(mapping.productGroups) toSet

  val propertiesByName : Map[String, Property] = 
    properties makeMapWithKeys (_.name)
}

class Property(property : jpa.Property, val value : jpa.PropertyValue, val product : Option[Product], webShopData : WebShopData, mapping : Mapping) extends Delegate(property)  {
  // terminate recursion
  mapping.properties(property) = this

  val id : Long = property.getId.longValue
  val propertyType : jpa.PropertyType = property.getType

  val namesByLanguage : Map[Option[String], String] = 
    property.getLabels makeMap (l => (l.getLanguage asOption, l.getLabel )) 
  
  val name : String = namesByLanguage.get(None) getOrElse ""
  
  val valueId : Long = value.getId.longValue
  val mimeType : String = value.getMimeType getOrElse ""
  val mediaValue : Array[Byte] = value.getMediaValue
  val pvalue = value
  
  def hasValue = value != noPropertyValue

  //FIXME: check should not be on null check but on property type
  val valueAsString = 
    if (value.getStringValue != null) value.getStringValue
    else if (value.getBooleanValue != null) value.getBooleanValue.toString
    else if (value.getEnumValue != null) value.getEnumValue.toString
    else if (value.getIntegerValue != null) value.getIntegerValue.toString
    else if (value.getMoneyValue != null) "&euro; " + value.getMoneyValue.toString
    else if (value.getRealValue != null) value.getRealValue.toString
    else ""
}

object noPropertyValue extends jpa.PropertyValue {
  setId(-1l)
}

class Promotion(promotion : jpa.Promotion, webShopData : WebShopData, mapping : Mapping) extends Delegate(promotion) {
  // terminate recursion
  mapping.promotions(promotion) = this
  val id = promotion.getId.longValue
  def products : Set[Product] = Set.empty
}

class VolumeDiscountPromotion(promotion : jpa.VolumeDiscountPromotion, webShopData : WebShopData, mapping : Mapping) extends Promotion(promotion, webShopData, mapping) {
  val startDate = promotion.getStartDate
  val endDate = promotion.getEndDate
  val price = promotion.getPrice
  val priceCurrency = promotion.getPriceCurrency
  val volumeDiscount = promotion.getVolumeDiscount
  val product = mapping.products(promotion.getProduct)
  override def products = Set(product)
}

//FIXME calculate promotion price when calculating new price
class Order(jOrder : jpaOrder.Order) extends Delegate(jOrder) {
  def empty = delegate.getProductOrders.clear

  def isEmpty = delegate.getProductOrders == null || delegate.getProductOrders.isEmpty
    
  def updateVolume(productOrder : jpaOrder.ProductOrder, v : Int) : Boolean = {
    if (productOrder.getVolume != v) {
      productOrder.setVolume(v)
      productOrder.setPrice(v * Model.catalog.productsById(productOrder.getProduct().getId().longValue()).propertiesByName("Price").pvalue.getMoneyValue.doubleValue)
      return true
    }
    return false
  }

  def removeProductOrder(productOrder : jpaOrder.ProductOrder) = {
    delegate.getProductOrders.remove(productOrder)
  }

  /**
   * Calculates the total number of articles in the shopping cart, based on
   * volume. 
   */
  def totalProducts : Int = {
    (0 /: delegate.getProductOrders.map(_.getVolume.intValue)) (_ + _)
  }
  
  def totalPrice : Double = {
    (0 /: delegate.getProductOrders.map(_.getPrice.intValue)) (_ + _)
  }

  /**
   * Adds a product to the order list. If the product already is present update
   * the volume count for that product.
   */
  def addProduct(product : Product, volume : Int) = {
    val arn = product.propertiesByName("ArticleNumber").pvalue.getStringValue
    delegate.getProductOrders find((po) =>
      Model.catalog.productsById(po.getProduct().getId().longValue()).propertiesByName("ArticleNumber").pvalue.getStringValue
          == arn) match {
        case None =>
            val productOrder = new jpaOrder.ProductOrder()
            //fake a productOrder Id, otherwise remove will fail, because equals
            //is implemented that if id == null the objects of same type are
            //always equal
            productOrder.setId(product.delegate.getId)
            productOrder.setProduct(product.delegate)
            updateVolume(productOrder, volume)
            delegate.getProductOrders.add(productOrder)
        case Some(p) =>
            p.setVolume(p.getVolume.intValue + volume)
            p.setPrice(p.getVolume.intValue * product.propertiesByName("Price").pvalue.getMoneyValue.doubleValue)
      }
  }
}

