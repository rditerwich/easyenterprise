package agilexs.catalogxs.presentation.model

import scala.collection.{mutable, Set, Map}
import scala.xml.NodeSeq 

import agilexs.catalogxs.jpa
import agilexs.catalogxs.presentation.util.ProjectionMap
import agilexs.catalogxs.presentation.model.Conversions._ 
import claro.common.util.Conversions._

class TemplateCache private (val cacheData : WebshopCacheData, val catalog : jpa.catalog.Catalog, val shop : jpa.shop.Shop, val locale : String) {

  type Templates = Map[String, jpa.catalog.Template]
  
  val catalogTemplates : Set[jpa.catalog.Template] = 
    catalog.getTemplates toSet
  
  val viewTemplates : Set[jpa.catalog.Template] = 
    shop.getTemplates toSet
  
  val productGroupTemplates : Map[jpa.catalog.ProductGroup, Templates] =
    mutable.Map((for (group <- cacheData.productGroups toSeq) 
      yield (group -> byName(group.getTemplates))):_*)
  
  val productGroupTemplates2 : Map[jpa.catalog.ProductGroup, Templates] = 
    cacheData.productGroups makeMapWithValues ((g : jpa.catalog.ProductGroup) => byName(g.getTemplates))
  
  private def byName(templates : Iterable[jpa.catalog.Template]) : Templates =
    templates mapBy (_.getName)
}
