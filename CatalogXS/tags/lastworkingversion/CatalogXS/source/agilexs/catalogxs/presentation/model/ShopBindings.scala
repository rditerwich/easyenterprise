package agilexs.catalogxs.presentation.model

import Conversions._
import net.liftweb.http.S
import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers._
import scala.xml.{Text, NodeSeq}
import agilexs.catalogxs.presentation.util.Util
import agilexs.catalogxs.presentation.snippet.ShoppingCart
import claro.common.util.Conversions._

object ShopBindings {

  def shopBinding(shop : Shop) = shop bindWith params(   
    "id" -> Text(shop.id.toString),
    "current-product-group" -> Complex(productGroupBinding(Model.currentProductGroup getOrNull)) -> "group",
    "current-product" -> Complex(productBinding(Model.currentProduct getOrNull)) -> "product",
    "current-search-string" -> Text(Model.currentSearchString.getOrElse("")),
    "current-search-products" -> Complex(Model.currentSearchProducts map (productBinding _)) -> "product",
    "products" -> Complex(shop.products map (productBinding _)) -> "product",
    "top-level-groups" -> Complex(shop.topLevelProductGroups map (productGroupBinding _)) -> "group",
    "promotions" -> Complex(shop.promotions map (promotionBinding _)) -> "promotion",
    "shopping-cart" -> Complex(shoppingCartBinding(Model.shoppingCart)) -> "shopping-cart")

  def promotionBinding(promotion : Promotion) = promotion match {
    case p : VolumeDiscountPromotion => volumeDiscountPromotionBinding(p)
    case _ => NullBinding
  }
  
  def volumeDiscountPromotionBinding(promotion : VolumeDiscountPromotion) = promotion bindWith params(         
    "id" -> Text(promotion.id.toString),
    "start-date" -> Text(Util.slashDate.format(promotion.startDate)),
    "end-date" -> Text(Util.slashDate.format(promotion.endDate)),
    "price" -> Util.formatMoney(promotion.priceCurrency, promotion.price.doubleValue),
    "volume-discount" -> Text(promotion.volumeDiscount.toString),
    "product" -> Complex(productBinding(promotion.product)) -> "product")

  def productBinding(product : Product) = product bindWith params(   
    "id" -> Text(product.id.toString),
    "properties" -> Complex(product.properties map (propertyBinding _)) -> "property",
    "property" -> Complex(propertyBinding(product.propertiesByName.get(BindAttr("name")) getOrNull)) -> "property",
    "value" -> Value(product.propertiesByName.get(BindAttr("property"))),
    "groups" -> Complex(product.productGroups map (productGroupBinding _)) -> "group",
    "link" -> Link(product),
    "href" -> LinkAttr(product) -> "href")

  def productGroupBinding(group : ProductGroup) : Binding = group bindWith params(   
    "id" -> Text(group.id.toString),
    "sub-groups" -> Complex(group.children map (productGroupBinding(_))) -> "group",
    "parent-groups" -> Complex(group.parents map (productGroupBinding(_))) -> "group",
    "group-properties" -> Complex(group.groupProperties map (propertyBinding(_))) -> "property",
    "group-property" -> Complex(propertyBinding(group.groupPropertiesByName.get(BindAttr("name")) getOrNull)) -> "property",
    "group-value" -> Value(group.groupPropertiesByName.get(BindAttr("property"))),
    "properties" -> Complex(group.properties map (propertyBinding(_))) -> "property",
    "products" -> Complex(IfAttr("include_sub-groups", group.productExtent, group.products) map (productBinding(_))) -> "product",
    "promotions" -> Complex(group.productExtentPromotions map (promotionBinding _)) -> "promotion",
    "link" -> Link(group),
    "href" -> LinkAttr(group) -> "href")
    //    "name" -> Text(product.getName)

  def shoppingCartBinding(order : Order) : Binding = order bindWith params(
    "add" -> Text("add:" + BindAttr("product_tag", "product")),
    "link" -> Link("/shoppingcart"),
    "href" -> LinkAttr("/shoppingcart") -> "href")
      
  def propertyBinding(property: Property) : Binding = property bindWith params(  
    "id" -> Text(property.id.toString),
    "name" -> Text(property.name),
    "label" -> Text(property.name),
    "value" -> Value(property))
}
