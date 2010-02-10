package agilexs.catalogxs.presentation.model

import Conversions._
import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers._
import scala.xml.Text 
import agilexs.catalogxs.presentation.util.Util

object CatalogBindings {

  def catalogBinding(catalog : Catalog) = Binding(catalog,   
    "id" -> Text(catalog.id.toString),
    "products" -> Complex(catalog.products map (productBinding _)) -> "product",
    "top_level_groups" -> Complex(catalog.topLevelProductGroups map (productGroupBinding _)) -> "group",
    "promotions" -> Complex(catalog.promotions map (promotionBinding _)) -> "promotion")

  def promotionBinding(promotion : Promotion) = promotion match {
  	case p : VolumeDiscountPromotion => volumeDiscountPromotionBinding(p)
  	case _ => Binding()
  }
  
  def volumeDiscountPromotionBinding(promotion : VolumeDiscountPromotion) = Binding(promotion,           
      "id" -> Text(promotion.id.toString),
      "start_date" -> Text(Util.slashDate.format(promotion.startDate)),
      "end_date" -> Text(Util.slashDate.format(promotion.endDate)),
      "price" -> Text(promotion.price.toString),
      "currency" -> Text(promotion.priceCurrency.toString),
      "volume_discount" -> Text(promotion.volumeDiscount.toString),
      "product" -> Complex(productBinding(promotion.product)) -> "product")

  def productBinding(product : Product) = Binding(product,   
	  "id" -> Text(product.id.toString),
	  "name" -> Text(product.name),
      "properties" -> Complex(product.properties map (propertyBinding _)) -> "property",
	  "property" -> Complex(propertyBinding(product.propertiesByName(BindAttr("name")))) -> "property",
	  "value" -> Value(product.propertiesByName.get(BindAttr("property"))),
      "groups" -> Complex(product.productGroups map (productGroupBinding _)) -> "group")
  
  def productGroupBinding(group : ProductGroup) : Binding[ProductGroup] = Binding(group,   
    "id" -> Text(group.id.toString),
    "name" -> Text(group.name),
	"properties" -> Complex(group.properties map (propertyBinding(_))) -> "property",
	"sub_groups" -> Complex(group.children map (productGroupBinding(_))) -> "group",
	"parent_groups" -> Complex(group.parents map (productGroupBinding(_))) -> "group")
	//	  "name" -> Text(product.getName)
		
  def propertyBinding(property: Property) : Binding[Property] = Binding(property,  
	"id" -> Text(property.id.toString),
	"name" -> Text(property.name),
	"label" -> Text(property.name),
	"value" -> Value(property))
}
