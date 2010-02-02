package agilexs.catalogxs.presentation.model

import scala.collection.jcl._ 

object Conversions {
  implicit def convertSet[T](set : java.util.Set[T]) = Set(set)
  implicit def convertList[T](set : java.util.List[T]) = Buffer(set)
  implicit def convertSortedSet[T](set : java.util.SortedSet[T]) = SortedSet(set)
  implicit def convertMap[T,E](set : java.util.Map[T,E]) = Map(set)
  implicit def convertSortedMap[T,E](set : java.util.SortedMap[T,E]) = SortedMap(set)
  implicit def converCollection[A](collection : java.util.Collection[A]) = new IterableWrapper[A]{override def underlying = collection}

}
