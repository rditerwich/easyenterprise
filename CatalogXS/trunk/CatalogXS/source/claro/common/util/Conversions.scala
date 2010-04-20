package claro.common.util

import scala.collection.{jcl,Map,Set}

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
  implicit def convertCollection[A](collection : java.util.Collection[A]) = new RichCollection[A](collection)
  implicit def convertProperties(properties : java.util.Properties) = new RichProperties(properties)
  
  implicit def richOption[A](option : Option[A]) = new RichOption(option)
  implicit def richString(s: String) = new RichString(s)
  implicit def richStringSeq(ss: Seq[String]) = new RichStringSeq(ss)
  implicit def richStringList(ss: List[String]) = new RichStringList(ss)
  implicit def richObject[A](value : A) = new RichObject(value)
  implicit def richInt(value : java.lang.Integer) = new RichInt(value)
  implicit def richLong(value : java.lang.Long) = new RichLong(value)
  implicit def richFloat(value : java.lang.Float) = new RichFloat(value)
  implicit def richDouble(value : java.lang.Double) = new RichDouble(value)
  implicit def richIterable[A](it : Iterable[A]) = new RichIterable[A](it)
  implicit def richCollection[A](collection : Collection[A]) = new RichCollection[A](collection)
  implicit def richSeq[A](seq : Seq[A]) = new RichSeq[A](seq)
  implicit def richList[A](list : List[A]) = new RichList[A](list)
  implicit def richSet[A](set : Set[A]) = new RichSet[A](set)
  implicit def richMap[A,B](map : Map[A,B]) = new RichMap[A,B](map)
  implicit def richArray[A](array : Array[A]) = new RichArray[A](array)
  implicit def richPartialFunctionIterable[A,B](it : Iterable[PartialFunction[A,B]]) = new RichPartialFunctionIterable[A,B](it)
  implicit def richUri(uri : java.net.URI) = new RichUri(uri)
  implicit def richInputStream(getInputStream : => java.io.InputStream) = new RichInputStream(getInputStream)
}
