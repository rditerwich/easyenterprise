package agilexs.catalogxs.presentation.model

import scala.collection.jcl.{BufferWrapper,SetWrapper,IterableWrapper} 
import javax.persistence.{EntityManager,Persistence}

object Model {
  val factory = Persistence.createEntityManagerFactory("AgileXS.CatalogXS.Jpa.PersistenceUnit")

  // Temporarily using ThreadLocal until we get lifecycle handling in RequestVar
  val emVar = new ThreadLocal[EntityManager]
  def em = emVar.get()

//  implicit def setToWrapper[A](set : java.util.Set[A]) = new SetWrapper[A]{override def underlying = set}
//  implicit def listToWrapper[A](list : java.util.List[A]) = new BufferWrapper[A]{override def underlying = list}
//  implicit def collectionToWrapper[A](collection : java.util.Collection[A]) = new IterableWrapper[A]{override def underlying = collection}
}