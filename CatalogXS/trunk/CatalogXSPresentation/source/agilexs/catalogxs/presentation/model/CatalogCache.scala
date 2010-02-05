package agilexs.catalogxs.presentation.model

import net.liftweb.http.RequestVar

import scala.xml.NodeSeq 
import scala.collection.mutable.{HashMap, SynchronizedMap}
import agilexs.catalogxs.jpa.catalog._
import Conversions._

object CatalogCache {
  
  private val viewCaches = new HashMap[(String, String, String), CatalogCache] with SynchronizedMap[(String, String, String), CatalogCache]
  
  def apply(catalogName: String, viewName: String, locale: String) : CatalogCache = {
   	viewCaches.getOrElseUpdate((catalogName, viewName, locale), {
   	  val catalog = Model.catalogBean.is.findAllCatalogs.find(_.getName == catalogName) match {
	   	  case Some(catalog) => catalog
	   	  case None => error("Catalog not found: " + catalogName)
   	  }
   	  val view = catalog.getViews.find(_.getName == viewName) match {
	   	  case Some(view) => view
	   	  case None => new CatalogView
   	  }
   	  new CatalogCache(catalog, view, locale)
    })
  }
}

class CatalogCache private (catalog: Catalog, view: CatalogView, locale: String) {
  Model.catalogBean.findAllProductGroups
  
  private val templateObjectCache = new HashMap[Tuple2[Object, String], NodeSeq]()
  private val templateClassCache = new HashMap[Tuple2[Class[_], String], NodeSeq]()
  
  val excludedProductGroups = Set(view.getExcludedProductGroups)
  val excludedProperties = Set(view.getExcludedProperties)
  val promotions = Seq(Model.catalogBean.findAllPromotions:_*)
  
  def template(obj : Object, template : String) : Option[NodeSeq] = {
    templateObjectCache.get(obj, template) match {
      case Some(xml) => Some(xml)
      case None => templateClassCache.get((obj.getClass, template)) match {
	      case Some(xml) => Some(xml)
	      case None => None
	    }
    }
  }  
}
