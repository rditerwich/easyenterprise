package agilexs.catalogxs.presentation.cache

import scala.collection.{mutable, Set, Map}

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.util.ProjectionMap
import agilexs.catalogxs.presentation.model.Conversions._ 
import agilexs.catalogxs.presentation.model.Model 

case class Catalog private (catalog : jpa.Catalog, view : jpa.CatalogView, locale : String) {

  private[cache] val productGroupMap = ProjectionMap((group : jpa.ProductGroup) => ProductGroup(this, group))
  private[cache] val productMap = ProjectionMap((product : jpa.Product) => Product(this, product))

  val excludedProductGroups : Set[ProductGroup] = 
   Set(view.getExcludedProductGroups map (productGroupMap):_*)
  
  val excludedProperties : Set[Property] = 
    Set(view.getExcludedProperties map (Property(this, _)):_*)

  val promotions : Set[Promotion] = 
    Set(Model.catalogBean.findAllPromotions map (Promotion(this, _)):_*)
  
  val productGroups : Set[ProductGroup] = 
	Set(catalog.getProductGroups map (productGroupMap) filter (!excludedProductGroups.contains(_)):_*)

  val topLevelProductGroups : Set[ProductGroup] = 
	Set(view.getTopLevelProductGroups map (productGroupMap):_*)

  val products : Set[Product] = 
	new mutable.HashSet[Product] useIn 
	 (products(view.getTopLevelProductGroups, new mutable.HashSet[jpa.ProductGroup], _)) readOnly

   val productGroupProducts : Map[jpa.ProductGroup, Set[Product]] = 
	 mutable.HashMap[jpa.ProductGroup, Set[Product]](
	   (for (group <- productGroups.toSeq map(_.productGroup)) 
         yield (group, Set(group.getProducts map(productMap):_*))):_*) 
		 
  val productGroupProductExtent : Map[jpa.ProductGroup, Set[Product]] = 
	new mutable.HashMap[jpa.ProductGroup, Set[Product]] useIn 
	  (productGroupProductExtent(view.getTopLevelProductGroups, _)) readOnly  

  private def products(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], result : mutable.HashSet[Product]) : Unit = {
    for (group <- groups; if !visited.contains(group)) {
      visited += group
      products(group.getChildren, visited, result)
      result ++= group.getProducts map (productMap)
    }
  }

  private def productGroupProductExtent(groups : Iterable[jpa.ProductGroup], result : mutable.Map[jpa.ProductGroup, Set[Product]]) : Set[Product] = {
    var allProducts = new mutable.HashSet[Product]
    for (group <- groups; if !result.contains(group)) {
      var products = new mutable.HashSet[Product]
      result(group) = products
      products ++= productGroupProductExtent(group.getChildren, result)
      products ++= group.getProducts map (productMap)
      allProducts ++= products
    }
    allProducts
  }
    
//  private def productPropertiesByProductGroupML(groups : Iterable[ProductGroup], result : mutable.Map[Product, Map[ProductGroup, List[PropertyValue]]]) : Unit = {
//    for (group <- groups; if !result.contains(group)) {
//      
//    }
  }

//}

object Catalog {

  private val viewCaches = new mutable.HashMap[(String, String, String), Catalog] with mutable.SynchronizedMap[(String, String, String), Catalog]

  def apply(catalogName: String, viewName: String, locale: String) : Catalog = {
   	viewCaches.getOrElseUpdate((catalogName, viewName, locale), {
   	  val catalog = Model.catalogBean.is.findAllCatalogs.find(_.getName == catalogName) match {
	   	  case Some(catalog) => catalog
	   	  case None => error("Catalog not found: " + catalogName)
   	  }
   	  val view = catalog.getViews.find(_.getName == viewName) match {
	   	  case Some(view) => view
	   	  case None => new jpa.CatalogView
   	  }
   	  new Catalog(catalog, view, locale)
    })
  }
}

