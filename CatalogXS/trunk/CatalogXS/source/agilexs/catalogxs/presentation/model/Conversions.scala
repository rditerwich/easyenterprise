package agilexs.catalogxs.presentation.model

import scala.collection.jcl
import scala.collection.Set
import scala.collection.Map
import scala.collection.immutable
import scala.collection.mutable
import scala.xml.NodeSeq 
import net.liftweb.util.BindHelpers.AttrBindParam
import net.liftweb.util.BindHelpers.FuncBindParam

object Conversions {
  
  /**
   * Java collection conversions
   */
  implicit def convertList[T](list : java.util.List[T]) = jcl.Buffer(list)
  implicit def convertSet[T](set : java.util.Set[T]) = jcl.Set(set)
  implicit def convertSortedSet[T](set : java.util.SortedSet[T]) = jcl.SortedSet(set)
  implicit def convertMap[T,E](map : java.util.Map[T,E]) = jcl.Map(map)
  implicit def convertSortedMap[T,E](map : java.util.SortedMap[T,E]) = jcl.SortedMap(map)
  implicit def convertIterable[A](it : java.util.Collection[A]) = new scala.collection.jcl.IterableWrapper[A] { override def underlying = it }

  /**
   * Extensions to the Object class
   */
  class RichObject[A](obj : A) {
    
	/** 
	 * Convert null values to Option
	 */
    def asOption = if (obj == null) None else Some(obj)
    
    
    def useIn(f: A => Any) : A = {
      f(obj)
      obj
    }
  }
  implicit def richObject[A](value : A) = new RichObject(value)

  /**
   * Extensions to the Option class
   */
  class RichOption[A](option : Option[A]) {
    def getOrNull : A = option match {
      case Some(value) => value
      case None => null.asInstanceOf[A]
    }
  }
  
  implicit def richOption[A](option : Option[A]) = new RichOption(option)

  /**
   * Extensions to the String class
   */
  class RichString(s: String) {
	def getOrElse (s2: String) = {
	 if (s == null || s.trim() == "") s2 else s
	}
    def parsePrefix (prefix : String) : Option[String] = {
      if (s.startsWith(prefix)) Some(s.substring(prefix.length))
      else None
    }
  }
  implicit def richString(s: String) = new RichString(s)

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
    
    def groupBy[K](f: A => K): Map[K, Set[A]] =
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

  implicit def richIterable[A](it : Iterable[A]) = new RichIterable[A](it)
  implicit def richCollection[A](collection : java.util.Collection[A]) = new RichCollection[A](collection)
  implicit def richCollection[A](collection : Collection[A]) = new RichCollection[A](collection)
  implicit def richSeq[A](seq : Seq[A]) = new RichSeq[A](seq)
  implicit def richSet[A](set : Set[A]) = new RichSet[A](set)
  implicit def richMap[A,B](map : Map[A,B]) = new RichMap[A,B](map)
  implicit def richArray[A](array : Array[A]) = new RichArray[A](array)

  /**
   * Implicit conversions for binding objects
   */
  implicit def stringToBindingsWithTag[A](t : Tuple2[Tuple2[String, Function2[String, NodeSeq, NodeSeq]],String]) = 
	FuncBindParam(t._1._1, (xml) => t._1._2(t._2, xml))

  implicit def linkAttrBindParam[A](t : Tuple2[Tuple2[String, LinkAttr],String]) = 
	AttrBindParam(t._1._1, t._1._2.value, t._2)

  implicit def toBindableObject(obj : Object) = new BindableObject(obj)

  
}
