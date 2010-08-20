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
    Cms.entityManager.createQuery("select shop from Shop shop").getResultList.asInstanceOf[java.util.List[jpa.shop.Shop]]
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
    catalog.getItems.toSet filter (!excludedItems.contains(_)) classFilter (classOf[jpa.catalog.ProductGroup])  

  val itemChildExtent : Map[jpa.catalog.Item, Set[jpa.catalog.Item]] = 
    (new mutable.HashMap[jpa.catalog.Item, Set[jpa.catalog.Item]] useIn 
	    (itemChildExtent(catalog.getItems.toSet, new mutable.HashSet[jpa.catalog.Item], _))).
	      toMap.withDefault(_ => Set.empty)  
    
  val itemParentExtent : Map[jpa.catalog.Item, Set[jpa.catalog.Item]] = 
    (new mutable.HashMap[jpa.catalog.Item, Set[jpa.catalog.Item]] useIn 
      (itemParentExtent(catalog.getItems.toSet classFilter(classOf[jpa.catalog.ProductGroup]), new mutable.HashSet[jpa.catalog.Item], _))).  
	      toMap.withDefault(_ => Set.empty)  
    
  val topLevelProductGroups : Set[jpa.catalog.ProductGroup] = 
	shop.getTopLevelProductGroups toSet

  val products : Set[jpa.catalog.Product] = 
    new mutable.HashSet[jpa.catalog.Product] useIn 
      (products(shop.getTopLevelProductGroups, new mutable.HashSet[jpa.catalog.ProductGroup], _)) toSet

  val items : Set[jpa.catalog.Item] = 
    topLevelProductGroups ++ topLevelProductGroups.map(itemChildExtent(_)).flatten
      
  val itemProperties : Map[jpa.catalog.Item, Seq[jpa.catalog.Property]] =
    Map(items.toSeq.map(item => (item, item.getProperties.toSeq.filterNot(excludedProperties))):_*).
      withDefault(_ => Seq.empty)
    
  val itemPropertyExtent : Map[jpa.catalog.Item, Seq[jpa.catalog.Property]] = Map(
    items.toSeq.map(item =>
      (item, (itemParentExtent(item).toSeq ++ Seq(item)).
          flatMap(itemProperties(_)))):_*
    ).withDefault(_ => Seq.empty)
    
  val itemPropertyValues : Map[jpa.catalog.Item, scala.Seq[jpa.catalog.PropertyValue]] =
    items.makeMapWithValues (
      _.getPropertyValues.toSeq.
        filter (v => v.getProperty != null && !excludedProperties.contains(v.getProperty))).
      withDefault(_ => Seq.empty)

  val itemPropertyValueExtent : Map[jpa.catalog.Item, scala.Seq[jpa.catalog.PropertyValue]] =
    (new mutable.HashMap[jpa.catalog.Item, Seq[jpa.catalog.PropertyValue]] useIn 
      (itemPropertyValueExtent(items, new mutable.HashSet[jpa.catalog.Item], _))).
        toMap.withDefault(_ => Seq.empty)  
        
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
	  
  private def itemChildExtent(items : Iterable[jpa.catalog.Item], visited : mutable.Set[jpa.catalog.Item], result : mutable.HashMap[jpa.catalog.Item, Set[jpa.catalog.Item]]) : Unit = {
    for (item <- items) if (!visited.contains(item)) {
      visited += item
      itemChildExtent(item.getChildren, visited, result)
      val direct = item.getChildren filterNot (excludedItems)
      val indirect = item.getChildren filterNot (excludedItems) flatMap (result.getOrElse(_, Set.empty))
      result(item) = direct ++ indirect toSet
    }
  }
  
  private def itemParentExtent(items : Iterable[jpa.catalog.Item], visited : mutable.Set[jpa.catalog.Item], result : mutable.HashMap[jpa.catalog.Item, Set[jpa.catalog.Item]]) : Unit = {
    for (item <- items) if (!visited.contains(item)) {
      visited += item
      itemParentExtent(item.getParents, visited, result)
      val direct = item.getParents filterNot (excludedItems)
      val indirect = item.getParents filterNot (excludedItems) flatMap (result.getOrElse(_, Set.empty))
      result(item) = direct ++ indirect toSet
    }
  }

  private def itemPropertyValueExtent(items : Iterable[jpa.catalog.Item], visited : mutable.Set[jpa.catalog.Item], result : mutable.HashMap[jpa.catalog.Item, Seq[jpa.catalog.PropertyValue]]) : Unit = {
    for (item <- items) if (!visited.contains(item)) {
      visited += item
      itemPropertyValueExtent(item.getParents, visited, result)
      val itemValues = itemPropertyValues(item)
      val itemProperties = itemValues.map(_.getProperty).toSet
      val parentValues = item.getParents.flatMap(result(_)).filter(v => !itemProperties.contains(v.getProperty)) 
      result(item) = itemValues ++ parentValues
    }
  }
    
  private def products(groups : Iterable[jpa.catalog.ProductGroup], visited : mutable.Set[jpa.catalog.ProductGroup], result : mutable.HashSet[jpa.catalog.Product]) : Unit = {
    for (group <- groups) if (!visited.contains(group)) {
      visited += group
      products(group.getChildren.toSet classFilter(classOf[jpa.catalog.ProductGroup]), visited, result)
      result ++= group.getChildren.toSet classFilter(classOf[jpa.catalog.Product]) 
    }
  }

  def template(obj : Object, template : String) : Option[NodeSeq] = {
    None
  }  
}
