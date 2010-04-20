package claro.cms.webshop

import net.liftweb.http.{RequestVar,SessionVar,Req,S}
import net.liftweb.util.{Box,Full}
import java.util.LinkedHashSet
import scala.xml.NodeSeq 
import scala.xml.Text 
import scala.collection.{mutable, immutable, Set, Map}

import agilexs.catalogxs.jpa
import agilexs.catalogxs.presentation.util.{ProjectionMap}
import claro.common.util.{Delegate,Lazy,KeywordMap}
import claro.common.util.Conversions._
import claro.cms.Request

object WebshopModel {

  var shopCache = Lazy(new WebshopCache) 
  object shop extends RequestVar[Shop](shopCache.get.shopsById(Request.website.config.properties("shop.id", "1"))) 

  object currentProductVar extends RequestVar[Option[String]](None)
  object currentProductGroupVar extends RequestVar[Option[String]](None)
  object currentSearchStringVar extends RequestVar[Option[String]](None)
  
  def currentProduct : Option[Product] = currentProductVar.is match {
    case Some(id) => Some(shop.productsById(id.toLong))
    case None => None
  }
  
  def currentProductGroup : Option[ProductGroup] = currentProductGroupVar.is match {
    case Some(id) => Some(shop.productGroupsById(id.toLong))
    case None => None
  }
    
  def currentSearchProducts : Set[Product] = currentSearchStringVar.is match {
	  case Some(searchString) => shop.keywordMap.find(searchString) 
	  case _ => Set.empty
  }
}

class Mapping(product : Option[Product], cacheData : WebshopCacheData) {
  lazy val productGroups = ProjectionMap((g : jpa.catalog.ProductGroup) => new ProductGroup(g, product, cacheData, this))
  lazy val products = ProjectionMap((p : jpa.catalog.Product) => new Product(p, cacheData, this))
  lazy val properties = ProjectionMap((p : jpa.catalog.Property) => new Property(p, noPropertyValue, product, cacheData, this))
  lazy val promotions = ProjectionMap( (p : jpa.shop.Promotion) => p match { 
    case p : jpa.shop.VolumeDiscountPromotion => new VolumeDiscountPromotion(p, cacheData, this)
    case _ => new Promotion(p, cacheData, this)
  })
}

class Shop (val cacheData : WebshopCacheData) extends Delegate(cacheData.catalog) {

  val mapping = new Mapping(None, cacheData)
  
  val shop = cacheData.shop
  val catalogId = shop.getCatalog.getId
  val id = shop.getId.longValue
  val serverName : String = (shop.getUrlPrefix getOrElse ("") split ("/"))(0)
  val prefixPath : List[String] = (shop.getUrlPrefix getOrElse ("") split ("/") toList) drop(0)
  val defaultLanguage = shop.getDefaultLanguage getOrElse "en"
  
  val excludedProperties : Set[Property] = 
    cacheData.excludedProperties map (mapping.properties) toSet

  val promotions : Set[Promotion] = 
    cacheData.promotions map (mapping.promotions) toSet
  
  val productGroups : Set[ProductGroup] = 
	cacheData.productGroups map (mapping.productGroups) toSet

  val productGroupsById : Map[Long, ProductGroup] = 
    productGroups mapBy (_.id)
    
  val topLevelProductGroups : Set[ProductGroup] = 
	cacheData.topLevelProductGroups map (mapping.productGroups) toSet

  val products : Set[Product] =
    cacheData.products map (mapping.products) toSet

  val productsById : Map[Long, Product] = 
    products mapBy (_.id)
    
  val productGroupsByName : Map[String, ProductGroup] = 
    productGroups mapBy (_.name)
  
  val mediaValues : Map[Long, (String, Array[Byte])] =
    cacheData.mediaValues
  
  val keywordMap =
    KeywordMap(products map (p => (p.properties map (_.valueAsString), p))) 
}

class ProductGroup(productGroup : jpa.catalog.ProductGroup, val product : Option[Product], cacheData : WebshopCacheData, mapping : Mapping) extends Delegate(productGroup) {

  // terminate recursion
  mapping.productGroups += (productGroup -> this)
  
  val id = productGroup.getId.longValue
  
  val parents : Set[ProductGroup] = 
    cacheData.productGroupParents(productGroup) map (mapping.productGroups) toSet
  
  val children : Set[ProductGroup] =
    cacheData.productGroupChildGroups(productGroup) map (mapping.productGroups) toSet
    
  val products : Set[Product] =
    cacheData.productGroupProducts(this) map(mapping.products) toSet 
  
  val groupProperties : Seq[Property] = 
	cacheData.productGroupPropertyValues(productGroup) map (v => 
	  new Property(v.getProperty, v, None, cacheData, mapping)) 

  val groupPropertiesByName : Map[String, Property] = 
    groupProperties mapBy (_.name)
    
  val properties : Set[Property] =
    productGroup.getProperties map(mapping.properties) toSet
    
  val name : String = 
    groupPropertiesByName.get("Name") match {
      case Some(property) => property.value.getStringValue
      case None => ""
    }
    
  lazy val productExtent : Set[Product] =
    cacheData.productGroupProductExtent(productGroup) map(mapping.products) toSet
    
  lazy val productExtentPromotions : Set[Promotion] = {
    val promotions = cacheData.promotions map(mapping.promotions) filter (p => !(p.products ** productExtent).isEmpty)  
    if (promotions isEmpty) Set.empty else Set(promotions.toSeq first) 
  }
}

class Product(product : jpa.catalog.Product, cacheData : WebshopCacheData, var mapping : Mapping) extends Delegate(product) {
  
  // terminate recursion
  mapping.products += (product -> this)
  
  // product has its own mappings
  mapping = new Mapping(Some(this), cacheData)

  val id : Long = product.getId.longValue
  
  val properties : Set[Property] =
  	cacheData.productPropertyValues(product) map (v => 
  	  new Property(v.getProperty, v, Some(this), cacheData, mapping)) toSet
  
  val productGroups : Set[ProductGroup] = 
    product.getParents filter(!cacheData.excludedItems.contains(_)) map(mapping.productGroups) toSet 
  
  val productGroupExtent : Set[ProductGroup] = 
    product.getParents filter(!cacheData.excludedItems.contains(_)) map(mapping.productGroups) toSet

  val propertiesByName : Map[String, Property] = 
    properties mapBy (_.name)
  
  val priceProperty : Option[Property] = propertiesByName.get("Price") 
}

class Property(property : jpa.catalog.Property, val value : jpa.catalog.PropertyValue, val product : Option[Product], cacheData : WebshopCacheData, mapping : Mapping) extends Delegate(property)  {
  // terminate recursion
  mapping.properties(property) = this

  val id : Long = property.getId.longValue
  val propertyType : jpa.catalog.PropertyType = property.getType

  val namesByLanguage : Map[Option[String], String] = 
    property.getLabels makeMap (l => Some((l.getLanguage asOption, l.getLabel ))) 
  
  val name : String = namesByLanguage.get(None) getOrElse ""
  
  val valueId : Long = value.getId.longValue
  val mimeType : String = value.getMimeType getOrElse ""
  val mediaValue : Array[Byte] = value.getMediaValue
  val moneyValue : Double = value.getMoneyValue.getOrElse(0)
  val moneyCurrency : String = value.getMoneyCurrency
  
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

object noPropertyValue extends jpa.catalog.PropertyValue {
  setId(-1l)
}

class Promotion(promotion : jpa.shop.Promotion, cacheData : WebshopCacheData, mapping : Mapping) extends Delegate(promotion) {
  // terminate recursion
  mapping.promotions(promotion) = this
  val id = promotion.getId.longValue
  def products : Set[Product] = Set.empty
}

class VolumeDiscountPromotion(promotion : jpa.shop.VolumeDiscountPromotion, cacheData : WebshopCacheData, mapping : Mapping) extends Promotion(promotion, cacheData, mapping) {
  val startDate = promotion.getStartDate
  val endDate = promotion.getEndDate
  val price = promotion.getPrice.getOrElse(0)
  val priceCurrency = promotion.getPriceCurrency
  val volumeDiscount = promotion.getVolumeDiscount
  val product = mapping.products(promotion.getProduct)
  override def products = Set(product)
}

//FIXME calculate promotion price when calculating new price

class Order(val order : jpa.shop.Order, mapping : Mapping) extends Delegate(order) {
  
  def productOrders : Seq[ProductOrder] = 
    order.getProductOrders map(new ProductOrder(_, this, mapping)) toSeq 

  def clear = delegate.getProductOrders.clear

  def isEmpty = delegate.getProductOrders == null || delegate.getProductOrders.isEmpty
    

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
    productOrders.find(_.product.id == product.id) match {
      case Some(productOrder) => productOrder.volume += volume
      case None =>
            val productOrder = new jpa.shop.ProductOrder
            productOrder.setProduct(product.delegate)
            product.priceProperty match {
              case Some(property) => 
                productOrder.setPrice(property.moneyValue)
                productOrder.setPriceCurrency(property.moneyCurrency)
              case None => 
            }
            delegate.getProductOrders.add(productOrder)
    }
  }
}

class ProductOrder(val productOrder : jpa.shop.ProductOrder, val order : Order, mapping : Mapping) extends Delegate(productOrder) {
  val product = mapping.products(productOrder.getProduct)
  def price = productOrder.getPrice.getOrElse(0)
  def totalPrice = price * volume
  def currency = productOrder.getPriceCurrency
  def volume = productOrder.getVolume.intValue
  def volume_=(v : Int) = productOrder.setVolume(v)
  
  def remove = {
    order.delegate.getProductOrders.retainOnly(_.getProduct.getId != product.id)
  }

}

