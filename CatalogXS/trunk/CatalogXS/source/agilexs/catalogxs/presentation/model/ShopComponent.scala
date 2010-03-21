package agilexs.catalogxs.presentation.model

import net.liftweb.http.{RequestVar,Req}
import claro.cms.{CMS,Template,TemplateLocator,Scope,Bindings}
import claro.cms.Conversions._
import scala.xml.{NodeSeq,Text}

object ShopComponent {
  
  CMS.objectBindings.append {
    case shop : Shop => Bindings(
      "id" -> shop.id,
      "current_product_group" -> currentProductGroup -> "group",
      "current_product" -> currentProduct -> "product",
      "current_search_string" -> currentSearchString,
      "current_search_products" -> currentSearchProducts -> "product",
      "products" -> shop.products -> "product",
      "top_level_groups" -> shop.topLevelProductGroups -> "group",
      "promotions" -> shop.promotions -> "promotion")
  } 

  CMS.bindings.append("shop" -> Some(shop)) 

  CMS.objectTemplates.append {
    case Template(name,product:Product) => TemplateLocator(name + "-product",
    	Scope(product.id), 
    	product.productGroupExtent map (g => 
    	  Scope("group" -> g.id)),
        Scope("shop" -> shop.id),
        Scope("catalog" -> shop.catalogId),
    	Scope())
  }
  
  CMS.entryPoints.append {
    case "index" :: Nil => Template("index")
    case "product" :: id :: Nil => Template("product-page", null)
  }
  
  private object requestData extends RequestVar[RequestData](new RequestData)

  def basePath = requestData.get.basePath
  def shop = requestData.get.shop
  def currentProductGroup = requestData.get.currentProductGroup
  def currentProduct = requestData.get.currentProduct
  def currentSearchString = requestData.get.currentSearchString
  def currentSearchProducts = requestData.get.currentSearchProducts
}

class RequestData {
  val basePath = ""
  val shop : Shop = null//Model.shop
  val currentProductGroup : Option[ProductGroup] = None
  val currentProduct : Option[Product] = None
  val currentSearchString : Option[String] = None
  val currentSearchProducts : Collection[Product] = List()
}
  
