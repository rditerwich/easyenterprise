package claro.common.util

import scala.collection.{immutable,mutable,Map,Set}
import claro.common.util.Conversions._

/**
 * Extensions to the collection classes
 */
class RichIterable[A](it : Iterable[A]) {

  /** 
   * Convert the iterable to an immutable set
   */
  def toSet = immutable.Set(it toSeq:_*)

  /**
   * Filters element that are not of specified type.
   */
  def classFilter[B <: A](c : java.lang.Class[B]) : Iterable[B] = 
    it filter (c.isInstance(_)) map (_.asInstanceOf[B])
 
  /**
   * Convert elements to a immutable hash map. 
   */
  def makeMap[B,C](f : A => Option[(B,C)]) : Map[B, C] = 
   immutable.Map((for (a <- it.toSeq; elt = f(a); if (elt != None)) yield elt.get ):_*)
 
  def makeMapWithValues[B](map : A => B) : Map[A, B] = 
    immutable.Map((for (a <- it.toSeq; b = map(a)) yield (a, b)):_*)

  def mapBy[K](map : A => K) : Map[K, A] = 
    immutable.Map((for (a <- it.toSeq; k = map(a)) yield (k -> a)):_*)

  def groupBy[K](f: A => K): scala.collection.Map[K, Set[A]] =
    new mutable.HashMap[K, mutable.Set[A]] with mutable.MultiMap[K, A] useIn 
      (m => it foreach { a => m add (f(a), a) }) //add is defined in MultiMap
}

class RichCollection[A](col : Collection[A]) extends RichIterable[A](col) {
}

class RichSeq[A](seq : Seq[A]) extends RichIterable[A](seq) {
}

class RichSet[A](set : Set[A]) {
  def classFilter[B <: A](c : java.lang.Class[B]) : Set[B] = 
   Set((set filter (c.isInstance(_)) toSeq) map (_.asInstanceOf[B]):_*)
}

class RichMap[A,B](map : Map[A,B]) {
  def toJava = new java.util.HashMap[A,B] useIn (result => for ((a,b) <- map) result.put(a,b))
}

class RichArray[A](array : Array[A]) {
  def toSet = immutable.Set(array toSeq:_*)
}

