package claro.cms

import claro.cms.util.{Page,AllPages}
import scala.collection.mutable
import net.liftweb.common.Full
import net.liftweb.http.{S,RequestVar}
import javax.persistence.{EntityManager,EntityManagerFactory,Persistence}
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions._

object Dao {
  
  private val properties = new java.util.HashMap[String, String]
  private val factories = new mutable.HashMap[String, EntityManagerFactory]
  private object entityManagers extends RequestVar[mutable.Map[String, EntityManager]](mutable.Map()) {
    S.addCleanupFunc(() => is.map(_._2.close))
  }
  
  private object threadLocalEntityManagers extends ThreadLocal[Map[String,EntityManager]]
  
  def setProperties(properties : Map[String, String]) = factories.synchronized {
    for ((key, value) <- properties) {
      this.properties.put(key, value)
    }
    factories.clear
  }
  
  private def getFactory(dataSource : String) = factories.synchronized {
    factories.getOrElseUpdate(dataSource, Persistence.createEntityManagerFactory(dataSource, properties))
  }
  
  def getEntityManager(dataSource : String) = {
    entityManagers.is.getOrElseUpdate(dataSource, {
      val factory = factories.getOrElseUpdate(dataSource, Persistence.createEntityManagerFactory(dataSource, properties))
      factory.createEntityManager
    })
  }
}

trait Dao {

  val dataSource : String

  def entityManager[A](f : EntityManager => A) : A = {
    val entityManagers = Dao.threadLocalEntityManagers.get() match {
      case null => Map[String, EntityManager]()
      case map => map
    }
    val entityManager = entityManagers.get(dataSource) match {
      case Some(em) => em
      case None => {
        val em = Dao.getFactory(dataSource).createEntityManager
        Dao.threadLocalEntityManagers.set(entityManagers + ((dataSource, em)))
        em
      }
    }
    f(entityManager)
  }
  
  def transaction[A](f : EntityManager => A) : A = entityManager { em =>
    val tx = em.getTransaction
    val newTx = !tx.isActive
    try {
      if (newTx) {
        tx.begin
      }
      val result = f(em)
      if (newTx) {
        tx.commit
      }
      result
    } catch {
      case e => 
        if (newTx) 
          tx.rollback()
        throw e
    }
  }
  
  def querySingle[A](query : String, parameters : (String,Any)*) : Option[A] = transaction { em => 
    val q = em.createQuery(query)
    for ((parameter, value) <- parameters) 
      q.setParameter(parameter, value)
    if (q.getResultList.isEmpty) {
      None
    } else {
      Some(q.getResultList.get(0).asInstanceOf[A])
    }
  }
  
  def query[A](queryString : String, parameters : (String,Any)*) : Iterable[A] = query(AllPages, queryString, parameters:_*)
  
  def query[A](page : Page, queryString : String, parameters : (String,Any)*) : Iterable[A] = transaction { em => 
    val q = em.createQuery(queryString)
    for ((parameter, value) <- parameters) 
      q.setParameter(parameter, value)
    q.setFirstResult(page.start)
    q.setMaxResults(page.size)
    q.getResultList.asInstanceOf[java.util.List[A]]
  }
}
