package claro.cms.webshop

import net.liftweb.http.{RequestVar,SessionVar,Req,S}
import net.liftweb.common.{Box,Full}
import java.util.{Locale,LinkedHashSet}

import scala.xml.NodeSeq 
import scala.xml.Text 
import scala.collection.{mutable, immutable}
import scala.collection.JavaConversions._

import claro.jpa
import claro.common.util.{Delegate,Lazy,KeywordMap,Locales,ProjectionMap}
import claro.common.util.Conversions._
import claro.cms.Cms

object WebshopModel {

  var shopCache = Lazy(new WebshopCache) 
  object shop extends RequestVar[Shop](shopCache.get.shopsById(rich(Cms.website.config.properties)("shop.id", "1"))) 

  object currentProductVar extends RequestVar[Option[String]](None)
  object currentProductGroupVar extends RequestVar[Option[String]](None)
  object currentSearchStringVar extends RequestVar[Option[String]](None)
  object currentUserVar extends SessionVar[Option[jpa.party.User]](None)
  
  def currentProduct : Option[Product] = currentProductVar.is match {
    case Some(id) => Some(shop.productsById(id.toLong))
    case None => None
  }
  
  def currentProductGroup : Option[ProductGroup] = currentProductGroupVar.is match {
    case Some(id) => shop.productGroupsById.get(id.toLong)
    case None => None
  }
    
  def currentSearchProducts : Seq[Product] = currentSearchStringVar.is match {
	  case Some(searchString) => 
      val products = shop.keywordMap.find(searchString) 
      currentProductGroup match {
        case Some(group) => products filter group.products toSeq
        case None => products.toSeq
      }
      
	  case _ => Seq.empty
  }
  
  def flush = {
    shopCache.reset
    shop.remove()
    shop.get
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

  val productGroupsById : collection.Map[Long, ProductGroup] = 
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
  
  val groupPropertyNames : Set[String] = 
  	groupProperties.map(_.name).toSet
  	
  val groupPropertiesByLocaleName : Map[(Locale,String), Property] = 
    groupProperties mapBy (p => (p.locale, p.name))

  def groupProperties(locale : Locale) =
    groupPropertyNames.map(groupProperty(locale, _))
  
  def groupProperty(locale : Locale, name : String) : Option[Property] = {
    for (alt <- Locales.getAlternatives(locale)) {
      groupPropertiesByLocaleName.get(alt, name) match {
        case Some(property) => return Some(property)
        case _ =>
      }
    }
    None
  }

  val properties : Seq[Property] =
    productGroup.getProperties map(mapping.properties) toSeq

  val propertiesByName : Map[String, Property] = 
    properties mapBy (_.name)

//  val propertiesByLocale = groupByLocale(properties)
//
//  val propertiesByNameByLocale : Map[String, Map[Locale, Seq[Property]]] = {
//    val propertiesByName = properties.groupBy(_.name).toSeq
//    Map(propertiesByName.map(byName => (byName._1, groupByLocale(byName._2))) :_*)
//  }

  val name : String = 
    groupPropertiesByLocaleName.get(Locales.empty, "Name") match {
      case Some(property) => property.value.getStringValue
      case None => ""
    }
    
  lazy val productExtent : Set[Product] =
    cacheData.productGroupProductExtent(productGroup) map(mapping.products) toSet
    
  lazy val productExtentPromotions : Set[Promotion] = {
    val promotions = cacheData.promotions map(mapping.promotions) filter (p => !(p.products ** productExtent).isEmpty)  
    if (promotions isEmpty) Set.empty else Set(promotions.toSeq first) 
  }

  override def toString = name
}

class Product(product : jpa.catalog.Product, cacheData : WebshopCacheData, var mapping : Mapping) extends Delegate(product) {
  
  // terminate recursion
  mapping.products += (product -> this)
  
  // product has its own mappings
  mapping = new Mapping(Some(this), cacheData)

  val id : Long = product.getId.longValue
  
  val properties : Seq[Property] =
  	cacheData.productPropertyValues(product) map (v => 
  	  new Property(v.getProperty, v, Some(this), cacheData, mapping)) 

  val productGroups : Set[ProductGroup] = 
    product.getParents filter(!cacheData.excludedItems.contains(_)) map(mapping.productGroups) toSet 
  
  val productGroupExtent : Set[ProductGroup] = 
    product.getParents filter(!cacheData.excludedItems.contains(_)) map(mapping.productGroups) toSet

  val propertyNames : Set[String] = 
  	properties.map(_.name).toSet
  	
  val propertiesByLocaleName : Map[(Locale,String), Property] = 
    properties mapBy (p => (p.locale, p.name))

  def properties(locale : Locale) =
    propertyNames.map(property(locale, _))
  
  def property(locale : Locale, name : String) : Option[Property] = {
    for (alt <- Locales.getAlternatives(locale)) {
      propertiesByLocaleName.get(alt, name) match {
        case Some(property) => return Some(property)
        case _ =>
      }
    }
    None
  }

  val priceProperty : Option[Property] = property(Locales.empty, "Price")
}

class Property(property : jpa.catalog.Property, val value : jpa.catalog.PropertyValue, val product : Option[Product], cacheData : WebshopCacheData, mapping : Mapping) extends Delegate(property)  {
  // terminate recursion
  mapping.properties(property) = this

  val id : Long = property.getId.longValue
  val propertyType : jpa.catalog.PropertyType = property.getType

  val namesByLanguage : Map[Option[String], String] = 
    Map(property.getLabels.toSeq map (l => (l.getLanguage asOption, l.getLabel )):_*) 
  
  val name : String = namesByLanguage.get(None) getOrElse ""
  
  val valueId : Long = value.getId.longValue
  val mimeType : String = value.getMimeType getOrElse ""
  val mediaValue : Array[Byte] = value.getMediaValue
  val moneyValue : Double = value.getMoneyValue.getOrElse(0)
  val moneyCurrency : String = value.getMoneyCurrency
  
  val locale = Locales(value.getLanguage)
  
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

object groupByLocale {
  
  def apply(properties : Seq[Property]) : Map[Locale, Seq[Property]] = {
    val loc_prop : Set[(Locale, Property)] = Set(properties.map(p => Locales(p.value.getLanguage getOrElse("")) -> p):_*)
    val all_loc_prop = new mutable.ArrayBuffer[(Locale, Property)]
    for ((locale, property) <- loc_prop) {
      all_loc_prop += ((locale, property)) 
      var alt = Locales.getAlternatives(locale).tail
      while (alt != Nil && !loc_prop.contains((alt.head, property))) {
        all_loc_prop += ((alt.head, property)) 
        alt = alt.tail
      }
    }
    all_loc_prop.map(_._2).groupBy(p => Locales(p.value.getLanguage)).toMap
  }
}

object noPropertyValue extends jpa.catalog.PropertyValue {
  setId(-1l)
}

case class Money(amount : Double, currency : String) {}

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
  val volumeDiscount = promotion.getVolumeDiscount.getOrElse(0)
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
  
  def currencies = productOrders map (_.currency) toSet
  
  def totalPrice(currency: String) : Double = {
    (0.0 /: productOrders.filter(_.currency == currency).map(_.totalPrice)) (_ + _)
  }
  
  def totalPrices : Seq[Money] = currencies.toSeq map(c => Money(totalPrice(c), c)) 

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
        productOrder.setVolume(volume)
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
  def volume = productOrder.getVolume.getOrElse(0)
  def volume_=(v : Int) = productOrder.setVolume(v)
  
  def remove = {
    order.delegate.getProductOrders.filter(_.getProduct.getId != product.id)
  }

}

