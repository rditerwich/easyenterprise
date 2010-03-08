package agilexs.catalogxs.presentation.model

import java.util.Locale;
import javax.naming.InitialContext;
import javax.persistence.{EntityManager,EntityManagerFactory,Persistence}
import net.liftweb.http.RequestVar
import net.liftweb.http.SessionVar
import net.liftweb.http.S
import net.liftweb.util.Box
import net.liftweb.util.Full
import scala.collection.jcl.{BufferWrapper,SetWrapper,IterableWrapper} 
import scala.collection.{mutable}
import Conversions._
import agilexs.catalogxs.presentation.util.LazyValue

object Model {
  
  val locales : Set[String] = Locale.getAvailableLocales map (_ toString) toSet
  
  var webShopCache = new LazyValue(new WebShopCache) 
  
  object webShop extends RequestVar[WebShop](webShopCache.get.webShopsById(S.attr("webShop") get)) 
  
  lazy val entityMangerProperties = System.getProperties.entrySet.makeMap (entry => 
    entry.getKey.toString.parsePrefix("entitymanager.") match {
      case Some(key) => Some((key, entry.getValue.toString))
      case None => None
    }) toJava
  
  object entityManagerFactory extends SessionVar[EntityManagerFactory]( 
	Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit", entityMangerProperties))
    		
  object entityManager extends RequestVar[EntityManager]( 
  	entityManagerFactory.createEntityManager)
  
 // exbject entityManager = Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit").createEntityManager

  def currentProductGroup : Option[ProductGroup] =
    S.param("group") match {
      case Full(id) => webShop.productGroupsById.get(id.toLong) 
      case _ => None
    }

  def currentProduct : Option[Product] =
    S.param("product") match {
      case Full(id) => webShop.productsById.get(id.toLong) 
      case _ => None
    }
      
  def currentSearchString : Option[String] =
	  S.param("search") match {
	  case Full(searchString) => Some(searchString) 
	  case _ => None
  }

  def currentSearchProducts : Iterable[Product] =
	  S.param("search") match {
	  case Full(searchString) => webShop.keywordMap.find(searchString) 
	  case _ => Seq.empty
  }
}