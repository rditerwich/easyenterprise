package claro.common.util

//import scala.collection.{Map,Set}
import scala.collection.JavaConversions._

object Conversions {

  /**
   * Java collection conversions
   */
//  implicit def convertList[T](list : java.util.List[T]) = jcl.Buffer(list)
//  implicit def convertSet[T](set : java.util.Set[T]) = jcl.Set(set)
//  implicit def convertSortedSet[T](set : java.util.SortedSet[T]) = jcl.SortedSet(set)
//  implicit def convertMap[T,E](map : java.util.Map[T,E]) = jcl.Map(map)
//  implicit def convertSortedMap[T,E](map : java.util.SortedMap[T,E]) = jcl.SortedMap(map)
//  implicit def convertIterable[A](it : java.util.Collection[A]) = new scala.collection.jcl.IterableWrapper[A] { override def underlying = it }
  implicit def rich(properties : java.util.Properties) = new RichProperties(properties)
  
  implicit def rich[A](option : Option[A]) = new RichOption(option)
  implicit def rich(s: String) = new RichString(s)
  implicit def rich(ss: Seq[String]) = new RichStringSeq(ss)
  implicit def rich(ss: List[String]) = new RichStringList(ss)
  implicit def rich(ss: Array[String]) = new RichStringArray(ss)
  implicit def rich[A](value : A) = new RichObject(value)
  implicit def rich(value : java.lang.Integer) = new RichInt(value)
  implicit def rich(value : java.lang.Long) = new RichLong(value)
  implicit def rich(value : java.lang.Float) = new RichFloat(value)
  implicit def rich(value : java.lang.Double) = new RichDouble(value)
  implicit def rich[A](it : Traversable[A]) = new RichTraversable[A](it)
//  implicit def richCollection[A](collection : Collection[A]) = new RichCollection[A](collection)
  implicit def rich[A](seq : Seq[A]) = new RichSeq[A](seq)
  implicit def rich[A](list : List[A]) = new RichList[A](list)
  implicit def rich[A](set : Set[A]) = new RichSet[A](set)
  implicit def rich[A,B](map : Map[A,B]) = new RichMap[A,B](map)
  implicit def rich[A](array : Array[A]) = new RichArray[A](array)
  implicit def rich[A,B](it : Iterable[PartialFunction[A,B]]) = new RichPartialFunctionIterable[A,B](it)
  implicit def rich(uri : java.net.URI) = new RichUri(uri)
  implicit def rich(getInputStream : => java.io.InputStream) = new RichInputStream(getInputStream)
  implicit def rich(xml : scala.xml.NodeSeq) = new RichNodeSeq(xml)
}
