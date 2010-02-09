package agilexs.catalogxs.presentation.model

import javax.naming.InitialContext;
import javax.persistence.{EntityManager,EntityManagerFactory,Persistence}
import net.liftweb.http.RequestVar
import net.liftweb.http.SessionVar
import scala.collection.jcl.{BufferWrapper,SetWrapper,IterableWrapper} 
import scala.collection.mutable.{HashMap, SynchronizedMap}
import Conversions._

object Model {
  object catalogName extends SessionVar[String]("default")
  object viewName extends SessionVar[String]("default")

  object catalog extends RequestVar[Catalog](
    new Catalog(CatalogCache(catalogName, viewName, "nl")))
  
  object catalogBean extends RequestVar[agilexs.catalogxs.businesslogic.Catalog]( 
    new InitialContext().
    lookup("java:comp/env/ejb/CatalogBean").
    asInstanceOf[agilexs.catalogxs.businesslogic.Catalog])

  object entityManagerFactory extends SessionVar[EntityManagerFactory]( 
	Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit"))
    		
  object entityManager extends RequestVar[EntityManager]( 
  	entityManagerFactory.createEntityManager)
  
  object v extends RequestVar[Any](null)
}