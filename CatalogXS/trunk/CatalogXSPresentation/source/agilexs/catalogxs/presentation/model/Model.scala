package agilexs.catalogxs.presentation.model

import javax.naming.InitialContext;
import javax.persistence.{EntityManager,EntityManagerFactory,Persistence}
import net.liftweb.http.RequestVar
import net.liftweb.http.SessionVar
import net.liftweb.http.S
import net.liftweb.util.Box
import net.liftweb.util.Full
import scala.collection.jcl.{BufferWrapper,SetWrapper,IterableWrapper} 
import scala.collection.mutable.{HashMap, SynchronizedMap}
import Conversions._

object Model {
  object catalogName extends SessionVar[String]("default")
  object viewName extends SessionVar[String]("default")

  object catalog extends RequestVar[Catalog](
    Catalog(catalogName, viewName, "nl"))
  
  object catalogBean extends RequestVar[agilexs.catalogxs.businesslogic.Catalog]( 
    new InitialContext().
    lookup("java:comp/env/ejb/CatalogBean").
    asInstanceOf[agilexs.catalogxs.businesslogic.Catalog])

  object entityManagerFactory extends SessionVar[EntityManagerFactory]( 
	Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit"))
    		
  object entityManager extends RequestVar[EntityManager]( 
  	entityManagerFactory.createEntityManager)
  
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
      
  def currentSearchProducts : Seq[Product] =
	  S.param("searchString") match {
	  case Full(searchString) => Model.catalog.keywordMap.find(searchString) 
	  case _ => Seq.empty
  }
}