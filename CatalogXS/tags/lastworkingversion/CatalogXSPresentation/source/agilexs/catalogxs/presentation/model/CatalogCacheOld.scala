package agilexs.catalogxs.presentation.model

import scala.xml.NodeSeq 

import scala.collection.{Set, Map}
import scala.collection.immutable.{HashSet, HashMap}
import scala.collection.mutable
import agilexs.catalogxs.jpa.catalog._
import Conversions._

//class CatalogCache private (catalog: Catalog, view: CatalogView, locale: String) {
//  
//  val templateObjectCache = new HashMap[Tuple2[Object, String], NodeSeq]
//  val templateClassCache = new HashMap[Tuple2[Class[_], String], NodeSeq]
//
//  val excludedProductGroups : Set[ProductGroup] = 
//	Set(view.getExcludedProductGroups:_*)
//  
//  val excludedProperties : Set[Property] = 
//    Set(view.getExcludedProperties:_*)
//  
//  val promotions : Set[Promotion] = 
//    Set(Model.catalogBean.findAllPromotions:_*)
//  
//  val productGroups : Set[ProductGroup] = 
//    Set(catalog.getProductGroups filter (!excludedProductGroups.contains(_)):_*)
//  
//  val products : Set[Product] = 
//    new mutable.HashSet[Product] useIn 
//      (CatalogCache.products(view.getTopLevelProductGroups, new mutable.HashSet[ProductGroup], _)) readOnly
//  
//  val productGroupProducts : Map[ProductGroup, Set[Product]] = 
//    new mutable.HashMap[ProductGroup, Set[Product]] useIn 
//      (CatalogCache.productGroupProducts(productGroups, _)) readOnly
//    
//  val productPropertiesByGroupML = null
//}
//
//object CatalogCache {
//
//  private val viewCaches = new mutable.HashMap[(String, String, String), CatalogCache] with mutable.SynchronizedMap[(String, String, String), CatalogCache]
//  
//  def apply(catalogName: String, viewName: String, locale: String) : CatalogCache = {
//   	viewCaches.getOrElseUpdate((catalogName, viewName, locale), {
//   	  val catalog = Model.catalogBean.is.findAllCatalogs.find(_.getName == catalogName) match {
//	   	  case Some(catalog) => catalog
//	   	  case None => error("Catalog not found: " + catalogName)
//   	  }
//   	  val view = catalog.getViews.find(_.getName == viewName) match {
//	   	  case Some(view) => view
//	   	  case None => new CatalogView
//   	  }
//   	  new CatalogCache(catalog, view, locale)
//    })
//  }
//
//  private def products(groups : Iterable[ProductGroup], visited : mutable.Set[ProductGroup], result : mutable.HashSet[Product]) : Unit = {
//    for (group <- groups; if !visited.contains(group)) {
//      visited += group
//      products(group.getChildren, visited, result)
//      result ++= group.getProducts
//    }
//  }
//  
//  private def productGroupProducts(groups : Iterable[ProductGroup], result : mutable.Map[ProductGroup, Set[Product]]) : Set[Product] = {
//    var allProducts = new mutable.HashSet[Product]
//    for (group <- groups; if !result.contains(group)) {
//      var products = new mutable.HashSet[Product]
//      result(group) = products
//      products ++= productGroupProducts(group.getChildren, result)
//      products ++= group.getProducts
//      allProducts ++= products
//    }
//    Set(allProducts.toArray:_*)
//  }
//  
//}




//   val productGroupProducts : Map[jpa.ProductGroup, Set[Product]] = 
//	 mutable.HashMap[jpa.ProductGroup, Set[Product]](
//	   (for (group <- productGroups.toSeq map(_.productGroup)) 
//         yield (group, Set(group.getProducts map(productMap):_*))):_*) 
//		 
//  val productGroupProductExtent : Map[jpa.ProductGroup, Set[Product]] = 
//	new mutable.HashMap[jpa.ProductGroup, Set[Product]] useIn 
//	  (productGroupProductExtent(cache.topLevelProductGroups, _)) readOnly  
//
//  private def products(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], result : mutable.HashSet[Product]) : Unit = {
//    for (group <- groups; if !visited.contains(group)) {
//      visited += group
//      products(group.getChildren, visited, result)
//      result ++= group.getProducts map (productMap)
//    }
//  }
//
//  private def productGroupProductExtent(groups : Iterable[jpa.ProductGroup], result : mutable.Map[jpa.ProductGroup, Set[Product]]) : Set[Product] = {
//    var allProducts = new mutable.HashSet[Product]
//    for (group <- groups; if !result.contains(group)) {
//      var products = new mutable.HashSet[Product]
//      result(group) = products
//      products ++= productGroupProductExtent(group.getChildren, result)
//      products ++= group.getProducts map (productMap)
//      allProducts ++= products
//    }
//    allProducts
//  }
//  private def productPropertiesByProductGroupML(groups : Iterable[ProductGroup], result : mutable.Map[Product, Map[ProductGroup, List[PropertyValue]]]) : Unit = {
//    for (group <- groups; if !result.contains(group)) {
//      
//    }

//}

//object Catalog {
//
//  private val viewCaches = new mutable.HashMap[(String, String, String), Catalog] with mutable.SynchronizedMap[(String, String, String), Catalog]
//
//  def apply(catalogName: String, viewName: String, locale: String) : Catalog = {
//   	viewCaches.getOrElseUpdate((catalogName, viewName, locale), {
//   	  val catalog = Model.catalogBean.is.findAllCatalogs.find(_.getName == catalogName) match {
//	   	  case Some(catalog) => catalog
//	   	  case None => error("Catalog not found: " + catalogName)
//   	  }
//   	  val view = catalog.getViews.find(_.getName == viewName) match {
//	   	  case Some(view) => view
//	   	  case None => new jpa.CatalogView
//   	  }
//   	  new Catalog(catalog, view, locale)
//    })
//  }
//}
