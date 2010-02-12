package agilexs.catalogxs.presentation.model

import Conversions._
import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers._
import scala.xml.Text 
import agilexs.catalogxs.presentation.util.Util

object CatalogBindings {

  def catalogBinding(catalog : Catalog) = catalog bindWith params(   
    "id" -> Text(catalog.id.toString),
    "currentProductGroup" -> Complex(productGroupBinding(Model.currentProductGroup orNull)) -> "group",
    "currentProduct" -> Complex(productBinding(Model.currentProduct orNull)) -> "product",
    "currentSearchString" -> Text(Model.currentSearchString.getOrElse("")),
    "currentSearchProducts" -> Complex(Model.currentSearchProducts map (productBinding _)) -> "product",
    "products" -> Complex(catalog.products map (productBinding _)) -> "product",
    "top_level_groups" -> Complex(catalog.topLevelProductGroups map (productGroupBinding _)) -> "group",
    "promotions" -> Complex(catalog.promotions map (promotionBinding _)) -> "promotion")

  def promotionBinding(promotion : Promotion) = promotion match {
  	case p : VolumeDiscountPromotion => volumeDiscountPromotionBinding(p)
  	case _ => NullBinding
  }
  
  def volumeDiscountPromotionBinding(promotion : VolumeDiscountPromotion) = promotion bindWith params(           
      "id" -> Text(promotion.id.toString),
      "start_date" -> Text(Util.slashDate.format(promotion.startDate)),
      "end_date" -> Text(Util.slashDate.format(promotion.endDate)),
      "price" -> Text(promotion.price.toString),
      "currency" -> Text(promotion.priceCurrency.toString),
      "volume_discount" -> Text(promotion.volumeDiscount.toString),
      "product" -> Complex(productBinding(promotion.product)) -> "product")

  def productBinding(product : Product) = product bindWith params(   
    "id" -> Text(product.id.toString),
	"name" -> Text(product.name),
    "properties" -> Complex(product.properties map (propertyBinding _)) -> "property",
	"property" -> Complex(propertyBinding(product.propertiesByName.get(BindAttr("name")) orNull)) -> "property",
	"value" -> Value(product.propertiesByName.get(BindAttr("property"))),
    "groups" -> Complex(product.productGroups map (productGroupBinding _)) -> "group",
  	"link" -> Link(product),
    "href" -> LinkAttr(product) -> "href")
  
  def productGroupBinding(group : ProductGroup) : Binding = group bindWith params(   
    "id" -> Text(group.id.toString),
    "name" -> Text(group.name),
	"sub_groups" -> Complex(group.children map (productGroupBinding(_))) -> "group",
	"parent_groups" -> Complex(group.parents map (productGroupBinding(_))) -> "group",
	"group_properties" -> Complex(group.groupProperties map (propertyBinding(_))) -> "property",
	"group_property" -> Complex(propertyBinding(group.groupPropertiesByName.get(BindAttr("name")) orNull)) -> "property",
	"group_value" -> Value(group.groupPropertiesByName.get(BindAttr("property"))),
	"properties" -> Complex(group.properties map (propertyBinding(_))) -> "property",
	"products" -> Complex(group.products map (productBinding(_))) -> "product",
	"link" -> Link(group),
	"href" -> LinkAttr(group) -> "href")
	//	  "name" -> Text(product.getName)
		
  def propertyBinding(property: Property) : Binding = property bindWith params(  
	"id" -> Text(property.id.toString),
	"name" -> Text(property.name),
	"label" -> Text(property.name),
	"value" -> Value(property))
}
