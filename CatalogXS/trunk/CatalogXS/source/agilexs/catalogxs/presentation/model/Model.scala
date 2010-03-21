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
import claro.common.util.Conversions._
import claro.common.util.Lazy
import agilexs.catalogxs.jpa

object Model {
  
  val locales : Set[String] = Locale.getAvailableLocales map (_ toString) toSet
  
  var shopCache = Lazy(new ShopCache) 
  
  object shop extends RequestVar[Shop](shopCache.get.shopsById(S.param("shop") get)) 
  object language extends RequestVar[String](S.param("language") get) 
  object basePath extends RequestVar[String](S.param("basePath") get) 
  
  lazy val entityMangerProperties = System.getProperties.entrySet.makeMap (entry => 
    entry.getKey.toString.parsePrefix("entitymanager.") match {
      case Some(key) => Some((key, entry.getValue.toString))
      case None => None
    }) toJava
  
  object entityManagerFactory extends SessionVar[EntityManagerFactory]( 
	Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit", entityMangerProperties))
    		
  object entityManager extends RequestVar[EntityManager]( 
  	entityManagerFactory.createEntityManager)
  
  object shoppingCart extends SessionVar[Order](new Order(new jpa.shop.Order))

  def currentProductGroup : Option[ProductGroup] =
    S.param("group") match {
      case Full(id) => shop.productGroupsById.get(id.toLong) 
      case _ => None
    }

  def currentProduct : Option[Product] =
    S.param("product") match {
      case Full(id) => shop.productsById.get(id.toLong) 
      case _ => None
    }
      
  def currentSearchString : Option[String] =
	  S.param("search") match {
	  case Full(searchString) => Some(searchString) 
	  case _ => None
  }

  def currentSearchProducts : Iterable[Product] =
	  S.param("search") match {
	  case Full(searchString) => shop.keywordMap.find(searchString) 
	  case _ => Seq.empty
  }
}