package agilexs.catalogxs.presentation.model

import scala.collection.{mutable, Set, Map}
import scala.xml.NodeSeq 

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.util.ProjectionMap
import Conversions._ 

class CatalogCache private (val catalog : jpa.Catalog, val view : jpa.CatalogView, val locale : String) {

  val templateObjectCache = new mutable.HashMap[Tuple2[Object, String], NodeSeq]
  val templateClassCache = new mutable.HashMap[Tuple2[Class[_], String], NodeSeq]

  val excludedProductGroups : Set[jpa.ProductGroup] = 
   view.getExcludedProductGroups toSet
  
  val excludedProperties : Set[jpa.Property] = 
    view.getExcludedProperties toSet

  val promotions : Set[jpa.Promotion] = 
    view.getPromotions toSet
  
  val productGroups : Set[jpa.ProductGroup] = 
	catalog.getProductGroups filter (!excludedProductGroups.contains(_)) toSet

  val productGroupChildren : Map[jpa.ProductGroup, Set[jpa.ProductGroup]] = 
    new mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]] useIn 
	  (productGroupGetRelated(catalog.getProductGroups, new mutable.HashSet[jpa.ProductGroup], g => g.getChildren,  _)) readOnly  
    
  val productGroupParents : Map[jpa.ProductGroup, Set[jpa.ProductGroup]] = 
    new mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]] useIn 
	  (productGroupGetRelated(catalog.getProductGroups, new mutable.HashSet[jpa.ProductGroup], g => g.getParents,  _)) readOnly  
    
  val topLevelProductGroups : Set[jpa.ProductGroup] = 
	view.getTopLevelProductGroups toSet

  val productGroupPropertyValues : Map[jpa.ProductGroup, Seq[jpa.PropertyValue]] =
    productGroups makeMap (_.getGroupPropertyValues filter(_.getProperty != null))

  val products : Set[jpa.Product] = 
	new mutable.HashSet[jpa.Product] useIn 
	 (products(view.getTopLevelProductGroups, new mutable.HashSet[jpa.ProductGroup], _)) readOnly

  val productPropertyValues : Map[jpa.Product, Seq[jpa.PropertyValue]] =
    products makeMap (_.getPropertyValues filter(_.getProperty != null))

  val mediaPropertyValues : Seq[jpa.PropertyValue] = 
    products flatMap (productPropertyValues(_)) filter (_.getProperty.getType == jpa.PropertyType.Media) filter(_.getMediaValue != null)
  
  val mediaValues : Map[Long, (String, Array[Byte])] =
    mutable.HashMap(mediaPropertyValues map (v => (v.getId.longValue, (v.getMimeType, v.getMediaValue))):_*)
    	
  val productGroupProductExtent : Map[jpa.ProductGroup, Set[jpa.Product]] = 
	new mutable.HashMap[jpa.ProductGroup, Set[jpa.Product]] useIn 
	  (productGroupProductExtent(view.getTopLevelProductGroups, _)) readOnly  

  private def productGroupGetRelated(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], getRelated : jpa.ProductGroup => Seq[jpa.ProductGroup], result : mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]]) : Unit = {
    for (group <- groups; if !visited.contains(group)) {
      visited += group
      productGroupGetRelated(getRelated(group), visited, getRelated, result)
      var direct = getRelated(group).filter(!excludedProductGroups.contains(_))
      val indirect = getRelated(group).filter(excludedProductGroups.contains(_)) flatMap (result.getOrElse(_, Set.empty))
      result(group) = direct ++ indirect toSet
    }
  }
  
  private def products(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], result : mutable.HashSet[jpa.Product]) : Unit = {
    for (group <- groups; if !visited.contains(group)) {
      visited += group
      products(group.getChildren, visited, result)
      result ++= group.getProducts 
    }
  }

  private def productGroupProductExtent(groups : Iterable[jpa.ProductGroup], result : mutable.Map[jpa.ProductGroup, Set[jpa.Product]]) : Set[jpa.Product] = {
    var allProducts = new mutable.HashSet[jpa.Product]
    for (group <- groups; if !result.contains(group)) {
      var products = new mutable.HashSet[jpa.Product]
      result(group) = products
      products ++= productGroupProductExtent(group.getChildren, result)
      products ++= group.getProducts 
      allProducts ++= products
    }
    allProducts
  }

  def template(obj : Object, template : String) : Option[NodeSeq] = {
    None
//    cache.templateObjectCache.get(obj, template) match {
//      case Some(xml) => Some(xml)
//      case None => cache.templateClassCache.get((obj.getClass, template)) match {
//	      case Some(xml) => Some(xml)
//	      case None => None
//	    }
//    }
  }  

//  private def productPropertiesByProductGroupML(groups : Iterable[ProductGroup], result : mutable.Map[Product, Map[ProductGroup, List[PropertyValue]]]) : Unit = {
//    for (group <- groups; if !result.contains(group)) {
//      
//    }
  }

//}

object CatalogCache {

  private val viewCaches = new mutable.HashMap[(String, String, String), CatalogCache] with mutable.SynchronizedMap[(String, String, String), CatalogCache]

  def apply(catalogName: String, viewName: String, locale: String) : CatalogCache = {
   	viewCaches.getOrElseUpdate((catalogName, viewName, locale), {
   	  val catalog = Model.catalogBean.is.findAllCatalogs.find(_.getName == catalogName) match {
	   	  case Some(catalog) => catalog
	   	  case None => error("Catalog not found: " + catalogName)
   	  }
   	  val view = catalog.getViews.find(_.getName == viewName) match {
	   	  case Some(view) => view
	   	  case None => new jpa.CatalogView
   	  }
   	  new CatalogCache(catalog, view, locale)
    })
  }
}
