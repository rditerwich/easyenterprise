package claro.cms.shop

import net.liftweb.http.{RequestVar,Req,S,LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import claro.cms.{Cms,Component,Template,ResourceLocator,Scope,Bindings}
import claro.cms.Conversions._
import scala.xml.{NodeSeq,Text}
import agilexs.catalogxs.presentation.util.Util
import agilexs.catalogxs.presentation.snippet.ShoppingCart

class ShopComponent extends Component {
  
  val prefix = "shop"

  bindings.append {
    case _ : ShopComponent => Map (
      "id" -> ShopModel.shop.get.id,
      "current_product_group" -> ShopModel.currentProductGroup -> "group",
      "current_product" -> ShopModel.currentProduct -> "product",
      "current_search_string" -> ShopModel.currentSearchString,
      "current_search_products" -> ShopModel.currentSearchProducts -> "product",
      "products" -> ShopModel.shop.get.products -> "product",
      "top_level_groups" -> ShopModel.shop.get.topLevelProductGroups -> "group",
      "promotions" -> ShopModel.shop.get.promotions -> "promotion",
      "shopping_cart" -> ShopModel.shoppingCart -> "shopping_cart")
    
    case promotion : VolumeDiscountPromotion => Map(         
      "id" -> promotion.id,
      "start_date" -> Util.slashDate.format(promotion.startDate),
      "end_date" -> Util.slashDate.format(promotion.endDate),
      "price" -> Util.formatMoney(promotion.priceCurrency, promotion.price.doubleValue),
      "volume_discount" -> promotion.volumeDiscount,
      "product" -> promotion.product -> "product")
    
    case product : Product => Map(   
      "id" -> product.id.toString,
      "properties" -> product.properties -> "property",
      "property" -> product.propertiesByName.get(@@("name")) -> "property",
      "value" -> Value(product.propertiesByName.get(@@("property"))),
      "groups" -> product.productGroups -> "group",
      "link" -> Link(product),
      "href" -> LinkAttr(product) -> "href")
    
    case group : ProductGroup => Map(   
      "id" -> group.id.toString,
      "sub_groups" -> group.children -> "group",
      "parent_groups" -> group.parents -> "group",
      "group_properties" -> group.groupProperties -> "property",
      "group_property" -> group.groupPropertiesByName.get(@@("name")) -> "property",
      "group_value" -> Value(group.groupPropertiesByName.get(@@("property"))),
      "properties" -> group.properties -> "property",
//      "products" -> IfAttr("include_sub_groups", group.productExtent, group.products) -> "product",
      "promotions" -> group.productExtentPromotions -> "promotion",
      )
//      "link" -> Link(group),
//      "href" -> LinkAttr(group) -> "href")
    
    case order : Order => Map(
      "add" -> ("add:" + @@("product_tag", "product")),
      "link" -> Link("/shoppingcart"),
      "href" -> LinkAttr("/shoppingcart") -> "href")
    
    case property: Property => Map(  
      "id" -> property.id.toString,
      "name" -> property.name,
      "label" -> property.name,
      "value" -> Value(property))
  }
  
  templateLocators.append {
    case Template(name, product : Product) => ResourceLocator(name + "-product", "html",
    	Scope(product.id), 
    	product.productGroupExtent map (g => 
    	  Scope("group" -> g.id)),
        Scope("shop" -> ShopModel.shop.id),
        Scope("catalog" -> ShopModel.shop.catalogId),
    	Scope())
  }
  
  entryPoints.append {
    case "index" :: Nil => Template("index")
    case "product" :: id :: Nil => Template("product-page", ShopModel.shop.productsById.get(id.toLong))
  }
  
  templateClasspath.append("claro.cms.shop.templates")
  
//  LiftRules.rewrite.append {
//    case RewriteRequest(
//      ParsePath("product" :: product :: Nil,_,_,_),_,_) =>
//      	RewriteResponse("product" :: Nil, Map("product" -> product))
//  }
}

