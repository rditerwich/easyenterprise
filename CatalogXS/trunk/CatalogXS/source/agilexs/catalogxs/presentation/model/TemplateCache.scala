package agilexs.catalogxs.presentation.model

import scala.collection.{mutable, Set, Map}
import scala.xml.NodeSeq 

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.util.ProjectionMap
import agilexs.catalogxs.presentation.model.Conversions._ 

class TemplateCache private (val cacheData : WebShopCacheData, val catalog : jpa.Catalog, val shop : jpa.WebShop, val locale : String) {

  type Templates = Map[String, jpa.Template]
  
  val catalogTemplates : Set[jpa.Template] = 
    catalog.getTemplates toSet
  
  val viewTemplates : Set[jpa.Template] = 
    shop.getTemplates toSet
  
  val productGroupTemplates : Map[jpa.ProductGroup, Templates] =
    mutable.Map((for (group <- cacheData.productGroups toSeq) 
      yield (group -> byName(group.getTemplates))):_*)
  
  val productGroupTemplates2 : Map[jpa.ProductGroup, Templates] = 
    cacheData.productGroups makeMapWithValues ((g : jpa.ProductGroup) => byName(g.getTemplates))
  
  private def byName(templates : Iterable[jpa.Template]) : Templates =
    templates mapBy (_.getName)
}
