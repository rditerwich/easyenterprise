package agilexs.catalogxs.presentation.model

import javax.naming.InitialContext;
import scala.collection.mutable.{HashMap, SynchronizedMap}
import agilexs.catalogxs.jpa.catalog._
import Conversions._

object CatalogCache {

  private val viewCaches = new HashMap[(String, String, String), CatalogCache] with SynchronizedMap[(String, String, String), CatalogCache]

  def catalogBean = 
    new InitialContext().
    lookup("java:comp/env/ejb/CatalogBean").
    asInstanceOf[agilexs.catalogxs.businesslogic.Catalog]

  def apply(catalogName: String, locale: String) : CatalogCache = 
    apply(catalogName, null, locale)

  def apply(catalogName: String, viewName: String, locale: String) : CatalogCache = {
   	viewCaches.getOrElseUpdate((catalogName, viewName, locale), {
   	  val catalog = catalogBean.findAllCatalogs.find(_.getName == catalogName) match {
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

class CatalogCache(catalog: Catalog, view: CatalogView, locale: String) {
  CatalogCache.catalogBean.findAllProductGroups
  
  val excludedProductGroups = Set(view.getExcludedProductGroups)
  val excludedProperties = Set(view.getExcludedProperties)
  val promotions = Seq(CatalogCache.catalogBean.findAllPromotions:_*)
  
}


