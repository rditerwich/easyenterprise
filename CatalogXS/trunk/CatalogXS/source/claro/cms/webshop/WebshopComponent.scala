package claro.cms.webshop

import net.liftweb.http.{RequestVar,Req,S,LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import claro.cms.{Cms,Component,Template,ResourceLocator,Scope}
import scala.xml.{NodeSeq,Text}
import agilexs.catalogxs.presentation.snippet.ShoppingCart

class WebshopComponent extends Component with WebshopBindingHelpers {
  
  val prefix = "webshop"

  bindings.append {
    case _ : WebshopComponent => Map (
      "id" -> WebshopModel.shop.get.id,
      "current_product_group" -> WebshopModel.currentProductGroup -> "group",
      "current_product" -> WebshopModel.currentProduct -> "product",
      "current_search_string" -> WebshopModel.currentSearchString,
      "current_search_products" -> WebshopModel.currentSearchProducts -> "product",
      "products" -> WebshopModel.shop.get.products -> "product",
      "top_level_groups" -> WebshopModel.shop.get.topLevelProductGroups -> "group",
      "promotions" -> WebshopModel.shop.get.promotions -> "promotion",
      "shopping_cart" -> WebshopModel.shoppingCart -> "shopping_cart")
    
    case promotion : VolumeDiscountPromotion => Map(         
      "id" -> promotion.id,
      "start_date" -> WebshopUtil.slashDate.format(promotion.startDate),
      "end_date" -> WebshopUtil.slashDate.format(promotion.endDate),
      "price" -> WebshopUtil.formatMoney(promotion.priceCurrency, promotion.price.doubleValue),
      "volume_discount" -> promotion.volumeDiscount,
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
      "sub_groups" -> group.children -> "group",
      "parent_groups" -> group.parents -> "group",
      "group_properties" -> group.groupProperties -> "property",
      "group_property" -> group.groupPropertiesByName.get(@@("name")) -> "property",
      "group_value" -> value(group.groupPropertiesByName.get(@@("property"))),
      "properties" -> group.properties -> "property",
      "products" -> @@?("include_sub_groups", group.productExtent, group.products) -> "product",
      "promotions" -> group.productExtentPromotions -> "promotion",
      "link" -> Link(group))
    
    case order : Order => Map(
      "add" -> ("add:" + @@("product_tag", "product")),
      "link" -> Link("/shoppingcart"),
      "href" -> LinkAttr("/shoppingcart") -> "href")
    
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
    	Scope())
  }
  
  entryPoints.append {
    case "index" :: Nil => Template("index")
    case "product" :: id :: Nil => Template("product-page", WebshopModel.shop.productsById.get(id.toLong))
  }
  
  templateClasspath.append("claro.cms.shop.templates")
  
//  LiftRules.rewrite.append {
//    case RewriteRequest(
//      ParsePath("product" :: product :: Nil,_,_,_),_,_) =>
//      	RewriteResponse("product" :: Nil, Map("product" -> product))
//  }
}

