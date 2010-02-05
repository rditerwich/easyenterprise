package agilexs.catalogxs.presentation.model

import scala.collection.jcl._ 
import net.liftweb.util.BindHelpers.FuncBindParam

object Conversions {
  implicit def convertSet[T](set : java.util.Set[T]) = Set(set)
  implicit def convertList[T](set : java.util.List[T]) = Buffer(set)
  implicit def convertSortedSet[T](set : java.util.SortedSet[T]) = SortedSet(set)
  implicit def convertMap[T,E](set : java.util.Map[T,E]) = Map(set)
  implicit def convertSortedMap[T,E](set : java.util.SortedMap[T,E]) = SortedMap(set)
  implicit def convertCollection[A](collection : java.util.Collection[A]) = Buffer[A](collection.asInstanceOf[java.util.List[A]])

  case class OptionalString(s: String) {
	def or (s2: String) = {
	 if (s == null || s.trim() == "") s2 else s
	}
  }
  implicit def optionalString(s: String) = OptionalString(s)
  
  implicit def stringToBindingWithTag[A](t : Tuple2[Tuple2[String, Binding[A]],String]) = 
    FuncBindParam(t._1._1, (xml) => t._1._2.bind(t._2, xml))
  
  implicit def stringToBindingsWithTag[A](t : Tuple2[Tuple2[String, Seq[Binding[A]]],String]) = 
	FuncBindParam(t._1._1, (xml) => t._1._2 flatMap (_ bind(t._2, xml)))
  

}
