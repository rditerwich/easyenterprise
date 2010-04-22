package claro.cms.webshop

import net.liftweb.http.{RequestVar,Req,S,SHtml,LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import claro.cms.{Cms,Component,Template,ResourceLocator,Scope}
import scala.xml.{Node,NodeSeq,Text}
import agilexs.catalogxs.jpa

class WebshopComponent extends Component with WebshopBindingHelpers {
  
  val prefix = "webshop"

  bindings.append {
    case _ : WebshopComponent => Map (
      "id" -> WebshopModel.shop.get.id,
      "current-product-group" -> WebshopModel.currentProductGroup -> "group",
      "current-product" -> WebshopModel.currentProduct -> "product",
      "current-search-string" -> WebshopModel.currentSearchStringVar.is,
      "current-search-products" -> WebshopModel.currentSearchProducts -> "product",
      "products" -> WebshopModel.shop.get.products -> "product",
      "group" -> WebshopModel.shop.get.productGroupsByName.get(@@("name")) -> "group",
      "top-level-groups" -> WebshopModel.shop.get.topLevelProductGroups -> "group",
      "promotions" -> WebshopModel.shop.get.promotions -> "promotion",
      "shopping-cart" -> ShoppingCart -> "shopping-cart",
      "search-form" -> new SearchForm -> "search")
    
    case promotion : VolumeDiscountPromotion => Map(         
      "id" -> promotion.id,
      "start-date" -> WebshopUtil.slashDate.format(promotion.startDate),
      "end-date" -> WebshopUtil.slashDate.format(promotion.endDate),
      "price" -> money(promotion.price, promotion.priceCurrency),
      "volume-discount" -> promotion.volumeDiscount,
      "product" -> promotion.product -> "product")
    
    case product : Product => Map(   
      "id" -> product.id.toString,
      "properties" -> product.properties -> "property",
      "property" -> product.propertiesByName.get(@@("name")) -> "property",
      "value" -> value(product.propertiesByName.get(@@("property"))),
      "groups" -> product.productGroups -> "group",
      "link" -> Link(product),
      "href" -> LinkAttr(product) -> "href")
    
    case group : ProductGroup => Map(   
      "id" -> group.id.toString,
      "sub-groups" -> group.children -> "group",
      "parent-groups" -> group.parents -> "group",
      "group-properties" -> group.groupProperties -> "property",
      "group-property" -> group.groupPropertiesByName.get(@@("name")) -> "property",
      "group-value" -> value(group.groupPropertiesByName.get(@@("property"))),
      "properties" -> group.properties -> "property",
      "products" -> @@?("include_sub-groups", group.productExtent, group.products) -> "product",
      "promotions" -> group.productExtentPromotions -> "promotion",
      "link" -> Link(group))
      
    case cart : ShoppingCart => Map(
      "items" -> cart.order.productOrders -> "item",
      "add" -> cart.addProduct(@@("product-prefix", "product")),
      "clear" -> cart.clear,
      "link" -> Link("/cart"))
    
    case order : Order => Map(
      "items" -> order.order.getProductOrders -> "item",
      "link" -> Link("/order"))

   case productOrder : ProductOrder => Map(   
      "id" -> productOrder.productOrder.getId.toString,
      "product" -> productOrder.product -> "product",
      "price" -> money(productOrder.price, productOrder.currency),
      "total-price" -> money(productOrder.totalPrice, productOrder.currency),
      "currency" -> productOrder.currency,
      "volume" -> productOrder.volume.toString,
      "volume-edit" -> ShoppingCart.updateVolume(productOrder),
      "remove" -> ShoppingCart.removeProductOrder(productOrder))

    case property: Property => Map(  
      "id" -> property.id.toString,
      "name" -> property.name,
      "label" -> property.name,
      "value" -> value(property))
  }
  
  templateLocators.append {
    case Template(name, product : Product) => ResourceLocator(name + "-product", "html",
    	Scope(product.id), 
    	product.productGroupExtent map (g => 
    	  Scope("group" -> g.id)),
        Scope("shop" -> WebshopModel.shop.id),
        Scope("catalog" -> WebshopModel.shop.catalogId),
    	Scope.global)
  }

  rewrite.append {
    case "index" :: Nil => "index" :: Nil
    case "product" :: id :: Nil => WebshopModel.currentProductVar(Some(id)); "product" :: Nil
    case "group" :: id :: Nil => WebshopModel.currentProductGroupVar(Some(id)); "group" :: Nil
    case "group" :: id :: "search" :: s :: Nil => 
      WebshopModel.currentProductGroupVar(Some(id))
      WebshopModel.currentSearchStringVar(Some(s))
      "group_search" :: Nil
    case "search" :: s :: Nil => WebshopModel.currentSearchStringVar(Some(s)); "search" :: Nil
    case "cart" :: Nil => "shopping_cart" :: Nil
    case path => path
  }
}

