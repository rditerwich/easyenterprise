package claro.cms.webshop

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.xml.NodeSeq 
import java.util.Locale

import claro.jpa
import claro.cms.Cms
import claro.common.util.Conversions._

class WebshopCache {

  val shops = findAllShops map (shop => new Shop(new WebshopCacheData(shop.getCatalog, shop)))
  val shopsById = shops mapBy (_.id.toString)
  val shopsByName = shops mapBy (_.shop.getName)
  val shopsByServerName : collection.Map[String, Seq[Shop]] = shops groupBy (_.serverName)
  
  def findAllShops : Seq[jpa.shop.Shop] =
    Cms.entityManager("claro.jpa.PersistenceUnit").createQuery("select shop from Shop shop").getResultList.asInstanceOf[java.util.List[jpa.shop.Shop]]
}

class WebshopCacheData (val catalog : jpa.catalog.Catalog, val shop : jpa.shop.Shop) {

  val templateObjectCache = new mutable.HashMap[Tuple2[Object, String], NodeSeq]
  val templateClassCache = new mutable.HashMap[Tuple2[Class[_], String], NodeSeq]

  val excludedItems : Set[jpa.catalog.Item] = 
    shop.getExcludedItems toSet
                                            	   
  val excludedProperties : Set[jpa.catalog.Property] = 
    shop.getExcludedProperties toSet

  val promotions : Set[jpa.shop.Promotion] = 
    shop.getPromotions toSet
  
  val productGroups : Set[jpa.catalog.ProductGroup] = 
	catalog.getItems.toSet classFilter (classOf[jpa.catalog.ProductGroup]) filter (!excludedItems.contains(_)) toSet 

  val productGroupChildGroups : Map[jpa.catalog.ProductGroup, Set[jpa.catalog.ProductGroup]] = 
    new mutable.HashMap[jpa.catalog.ProductGroup, Set[jpa.catalog.ProductGroup]] useIn 
	  (productGroupChildGroups(catalog.getItems.toSet classFilter(classOf[jpa.catalog.ProductGroup]), new mutable.HashSet[jpa.catalog.ProductGroup], _)) toMap  
    
  val productGroupParents : Map[jpa.catalog.ProductGroup, Set[jpa.catalog.ProductGroup]] = 
    new mutable.HashMap[jpa.catalog.ProductGroup, Set[jpa.catalog.ProductGroup]] useIn 
	  (productGroupParents(catalog.getItems.toSet classFilter(classOf[jpa.catalog.ProductGroup]), new mutable.HashSet[jpa.catalog.ProductGroup], _)) toMap  
    
  val topLevelProductGroups : Set[jpa.catalog.ProductGroup] = 
	shop.getTopLevelProductGroups toSet

  val products : Set[jpa.catalog.Product] = 
    new mutable.HashSet[jpa.catalog.Product] useIn 
      (products(shop.getTopLevelProductGroups, new mutable.HashSet[jpa.catalog.ProductGroup], _)) toSet

  val productGroupPropertyValues : Map[jpa.catalog.ProductGroup, Seq[jpa.catalog.PropertyValue]] =
    productGroups makeMapWithValues (_.getPropertyValues filter(_.getProperty != null) toSeq)

  val productPropertyValues : Map[jpa.catalog.Product, Seq[jpa.catalog.PropertyValue]] =
    products makeMapWithValues (_.getPropertyValues filter(_.getProperty != null) toSeq)

  val allPropertyValues : Set[jpa.catalog .PropertyValue] = 
    products.flatMap(productPropertyValues(_)) ++ productGroups.flatMap(productGroupPropertyValues(_))
  
  val mediaPropertyValues : Seq[jpa.catalog.PropertyValue] = 
      allPropertyValues filter (_.getProperty.getType == jpa.catalog.PropertyType.Media) filter(_.getMediaValue != null) toSeq
  
  val mediaValues : Map[Long, (String, Array[Byte])] =
    Map(mediaPropertyValues map (v => (v.getId.longValue, (v.getMimeType, v.getMediaValue))):_*)
    	
  val productGroupProducts : Map[jpa.catalog.ProductGroup, Set[jpa.catalog.Product]] =
    productGroups makeMapWithValues (_.getChildren.toSet classFilter(classOf[jpa.catalog.Product]) toSet)   
	  
  val productGroupProductExtent : Map[jpa.catalog.ProductGroup, collection.Set[jpa.catalog.Product]] = 
    new mutable.HashMap[jpa.catalog.ProductGroup, mutable.Set[jpa.catalog.Product]] useIn 
    (productGroupProductExtent(shop.getTopLevelProductGroups, _)) toMap

  private def productGroupChildGroups(groups : Iterable[jpa.catalog.ProductGroup], visited : mutable.Set[jpa.catalog.ProductGroup], result : mutable.HashMap[jpa.catalog.ProductGroup, Set[jpa.catalog.ProductGroup]]) : Unit = {
    for (group <- groups) if (!visited.contains(group)) {
      visited += group
      val children = group.getChildren.toSet classFilter(classOf[jpa.catalog.ProductGroup])
      productGroupChildGroups(children, visited, result)
      var direct = children filter (!excludedItems.contains(_))
      val indirect = children filter (excludedItems.contains(_)) flatMap (result.getOrElse(_, Set.empty))
      result(group) = direct ++ indirect toSet
    }
  }
  
  private def productGroupParents(groups : Iterable[jpa.catalog.ProductGroup], visited : mutable.Set[jpa.catalog.ProductGroup], result : mutable.HashMap[jpa.catalog.ProductGroup, Set[jpa.catalog.ProductGroup]]) : Unit = {
    for (group <- groups) if (!visited.contains(group)) {
      visited += group
      productGroupChildGroups(group.getParents, visited, result)
      var direct = group.getParents filter (!excludedItems.contains(_))
      val indirect = group.getParents filter (excludedItems.contains(_)) flatMap (result.getOrElse(_, Set.empty))
      result(group) = direct ++ indirect toSet
    }
  }

  private def products(groups : Iterable[jpa.catalog.ProductGroup], visited : mutable.Set[jpa.catalog.ProductGroup], result : mutable.HashSet[jpa.catalog.Product]) : Unit = {
    for (group <- groups) if (!visited.contains(group)) {
      visited += group
      products(group.getChildren.toSet classFilter(classOf[jpa.catalog.ProductGroup]), visited, result)
      result ++= group.getChildren.toSet classFilter(classOf[jpa.catalog.Product]) 
    }
  }

  private def productGroupProductExtent(groups : Iterable[jpa.catalog.ProductGroup], result : mutable.Map[jpa.catalog.ProductGroup, mutable.Set[jpa.catalog.Product]]) : mutable.Set[jpa.catalog.Product] = {
    var allProducts = new mutable.HashSet[jpa.catalog.Product]
    for (group <- groups) {
      if (!result.contains(group)) {
	      var products = new mutable.HashSet[jpa.catalog.Product]
	      result += ((group, products))
	      products ++= productGroupProductExtent(group.getChildren.toSet classFilter(classOf[jpa.catalog.ProductGroup]), result)
	      products ++= group.getChildren.toSet classFilter(classOf[jpa.catalog.Product])
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
