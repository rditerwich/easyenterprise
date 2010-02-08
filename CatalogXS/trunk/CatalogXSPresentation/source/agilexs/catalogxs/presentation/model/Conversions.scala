package agilexs.catalogxs.presentation.model

import scala.collection.jcl._
import scala.collection.mutable
import net.liftweb.util.BindHelpers.FuncBindParam

object Conversions {
  implicit def convertList[T](list : java.util.List[T]) = Buffer(list)
  implicit def convertSet[T](set : java.util.Set[T]) = Set(set)
  implicit def convertSortedSet[T](set : java.util.SortedSet[T]) = SortedSet(set)
  implicit def convertMap[T,E](map : java.util.Map[T,E]) = Map(map)
  implicit def convertSortedMap[T,E](map : java.util.SortedMap[T,E]) = SortedMap(map)
  implicit def convertCollection[A](collection : java.util.Collection[A]) = Buffer[A](collection.asInstanceOf[java.util.List[A]])

  class OptionalString(s: String) {
	def or (s2: String) = {
	 if (s == null || s.trim() == "") s2 else s
	}
  }
  implicit def optionalString(s: String) = new OptionalString(s)
  
  implicit def stringToBindingWithTag[A](t : Tuple2[Tuple2[String, Binding[A]],String]) = 
    FuncBindParam(t._1._1, (xml) => t._1._2.bind(t._2, xml))
  
  implicit def stringToBindingsWithTag[A](t : Tuple2[Tuple2[String, Seq[Binding[A]]],String]) = 
	FuncBindParam(t._1._1, (xml) => t._1._2 flatMap (_ bind(t._2, xml)))
  
  class SeqWrapper[A](elements : Iterable[A]) {
    def seqFlatMap[B](f : A => Iterable[B]) : Seq[B] = {
      val buf = new mutable.ArrayBuffer[B]
      for (element <- elements) {
        buf ++= f(element)
      }
      buf.readOnly
    }
  }
   
  implicit def seqWrapper[A](elements : Iterable[A]) = new SeqWrapper[A](elements)
  
  class UseInWrapper[A](obj : A) {
    def useIn(f: A => Any) : A = {
      f(obj)
      obj
    }
  }
  
  implicit def useInWrapper[A](obj : A) = new UseInWrapper[A](obj)
}
