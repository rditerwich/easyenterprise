package agilexs.catalogxs.presentation.model

import scala.collection.{mutable, Set, Map}
import scala.xml.NodeSeq 

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.util.ProjectionMap
import Conversions._ 

object WebShopCache {

  private var instance : WebShopCache = null
  
  def apply() : WebShopCache = {
    synchronized {
      if (instance == null) {
        instance = new WebShopCache
      }
      instance
    }
  }
    
  def reset = {
    instance = null
  }
}

class WebShopCache private {

  val shopCaches = findAllWebShops map (shop => new WebShop(new WebShopData(shop.getCatalog, shop)))
  val shopsByName = shopCaches makeMapWithKeys (_.webShopData.shop.getName)
  val shopsByPrefix = shopCaches makeMapWithKeys (_.webShopData.shop.getUrlPrefix)
    
  def findAllWebShops : Seq[jpa.WebShop] =
    Model.entityManager.is.createQuery("select shop from WebShop shop").getResultList.asInstanceOf[java.util.List[jpa.WebShop]]
}

class WebShopData (val catalog : jpa.Catalog, val shop : jpa.WebShop) {

  val templateObjectCache = new mutable.HashMap[Tuple2[Object, String], NodeSeq]
  val templateClassCache = new mutable.HashMap[Tuple2[Class[_], String], NodeSeq]

  val excludedItems : Set[jpa.Item] = 
	shop.getExcludedItems toSet
                                            	   
  val excludedProperties : Set[jpa.Property] = 
    shop.getExcludedProperties toSet

  val promotions : Set[jpa.Promotion] = 
    shop.getPromotions toSet
  
  val productGroups : Set[jpa.ProductGroup] = 
	catalog.getItems classFilter (classOf[jpa.ProductGroup]) filter (!excludedItems.contains(_)) toSet 

  val productGroupChildGroups : Map[jpa.ProductGroup, Set[jpa.ProductGroup]] = 
    new mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]] useIn 
	  (productGroupChildGroups(catalog.getItems classFilter(classOf[jpa.ProductGroup]), new mutable.HashSet[jpa.ProductGroup], _)) readOnly  
    
  val productGroupParents : Map[jpa.ProductGroup, Set[jpa.ProductGroup]] = 
    new mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]] useIn 
	  (productGroupParents(catalog.getItems classFilter(classOf[jpa.ProductGroup]), new mutable.HashSet[jpa.ProductGroup], _)) readOnly  
    
  val topLevelProductGroups : Set[jpa.ProductGroup] = 
	shop.getTopLevelProductGroups toSet

  val products : Set[jpa.Product] = 
	new mutable.HashSet[jpa.Product] useIn 
	 (products(shop.getTopLevelProductGroups, new mutable.HashSet[jpa.ProductGroup], _)) readOnly

  val productGroupPropertyValues : Map[jpa.ProductGroup, Seq[jpa.PropertyValue]] =
    productGroups makeMapWithValues (_.getPropertyValues filter(_.getProperty != null) toSeq)

  val productPropertyValues : Map[jpa.Product, Seq[jpa.PropertyValue]] =
    products makeMapWithValues (_.getPropertyValues filter(_.getProperty != null) toSeq)

  val mediaPropertyValues : Seq[jpa.PropertyValue] = 
    products flatMap (productPropertyValues(_)) filter (_.getProperty.getType == jpa.PropertyType.Media) filter(_.getMediaValue != null) toSeq
  
  val mediaValues : Map[Long, (String, Array[Byte])] =
    mutable.HashMap(mediaPropertyValues map (v => (v.getId.longValue, (v.getMimeType, v.getMediaValue))):_*)
    	
  val productGroupProducts : Map[jpa.ProductGroup, Set[jpa.Product]] =
    productGroups makeMapWithValues (_.getChildren classFilter(classOf[jpa.Product]) toSet)   
	  
  val productGroupProductExtent : Map[jpa.ProductGroup, Set[jpa.Product]] = 
    new mutable.HashMap[jpa.ProductGroup, Set[jpa.Product]] useIn 
    (productGroupProductExtent(shop.getTopLevelProductGroups, _)) readOnly  

  private def productGroupChildGroups(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], result : mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]]) : Unit = {
    for (group <- groups; if !visited.contains(group)) {
      visited += group
      val children = group.getChildren classFilter(classOf[jpa.ProductGroup])
      productGroupChildGroups(children, visited, result)
      var direct = children filter (!excludedItems.contains(_))
      val indirect = children filter (excludedItems.contains(_)) flatMap (result.getOrElse(_, Set.empty))
      result(group) = direct ++ indirect toSet
    }
  }
  
  private def productGroupParents(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], result : mutable.HashMap[jpa.ProductGroup, Set[jpa.ProductGroup]]) : Unit = {
    for (group <- groups; if !visited.contains(group)) {
      visited += group
      productGroupChildGroups(group.getParents, visited, result)
      var direct = group.getParents filter (!excludedItems.contains(_))
      val indirect = group.getParents filter (excludedItems.contains(_)) flatMap (result.getOrElse(_, Set.empty))
      result(group) = direct ++ indirect toSet
    }
  }

  private def products(groups : Iterable[jpa.ProductGroup], visited : mutable.Set[jpa.ProductGroup], result : mutable.HashSet[jpa.Product]) : Unit = {
    for (group <- groups; if !visited.contains(group)) {
      visited += group
      products(group.getChildren classFilter(classOf[jpa.ProductGroup]), visited, result)
      result ++= group.getChildren classFilter(classOf[jpa.Product]) 
    }
  }

  private def productGroupProductExtent(groups : Iterable[jpa.ProductGroup], result : mutable.Map[jpa.ProductGroup, Set[jpa.Product]]) : Set[jpa.Product] = {
    var allProducts = new mutable.HashSet[jpa.Product]
    for (group <- groups) {
      if (!result.contains(group)) {
	      var products = new mutable.HashSet[jpa.Product]
	      result += ((group, products))
	      products ++= productGroupProductExtent(group.getChildren classFilter(classOf[jpa.ProductGroup]), result)
	      products ++= group.getChildren classFilter(classOf[jpa.Product])
	      allProducts ++= products
      } else {
        allProducts ++= result(group)
      }
    }
    allProducts
  }

  def template(obj : Object, template : String) : Option[NodeSeq] = {
    None
  }  
}
