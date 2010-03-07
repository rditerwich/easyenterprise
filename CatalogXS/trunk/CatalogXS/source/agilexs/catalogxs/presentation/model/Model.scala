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

object Model {
  
  val locales : Set[String] = Locale.getAvailableLocales map (_ toString) toSet
  
  object catalogName extends SessionVar[String]("default")
  object viewName extends SessionVar[String]("default")

  def webShop = S.attr("webShop") 
  
  def catalogBean =//extends RequestVar[agilexs.catalogxs.businesslogic.Catalog]( 
    new InitialContext().
    lookup("java:comp/env/ejb/CatalogBean").
    asInstanceOf[agilexs.catalogxs.businesslogic.Catalog]

  val entityManagerProperties = new java.util.HashMap[String, String]()
  
  object entityManagerFactory extends SessionVar[EntityManagerFactory]( 
	Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit", entityManagerProperties))
    		
  object entityManager extends RequestVar[EntityManager]( 
  	entityManagerFactory.createEntityManager)
  
 // exbject entityManager = Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit").createEntityManager

  lazy val webShops = new WebShopCache 
  
  def currentProductGroup : Option[ProductGroup] =
    S.param("currentProductGroup") match {
      case Full(id) => Model.catalog.productGroupsById.get(id.toLong) 
      case _ => None
    }

  def currentProduct : Option[Product] =
    S.param("currentProduct") match {
      case Full(id) => Model.catalog.productsById.get(id.toLong) 
      case _ => None
    }
      
  def currentSearchString : Option[String] =
	  S.param("searchString") match {
	  case Full(searchString) => Some(searchString) 
	  case _ => None
  }

  def currentSearchProducts : Iterable[Product] =
	  S.param("searchString") match {
	  case Full(searchString) => Model.catalog.keywordMap.find(searchString) 
	  case _ => Seq.empty
  }
}