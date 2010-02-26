package agilexs.catalogxs.presentation.model

import scala.collection.jcl
import scala.collection.Set
import scala.collection.Map
import scala.collection.mutable
import scala.xml.NodeSeq 
import net.liftweb.util.BindHelpers.AttrBindParam
import net.liftweb.util.BindHelpers.FuncBindParam

object Conversions {
  implicit def convertList[T](list : java.util.List[T]) = jcl.Buffer(list)
  implicit def convertSet[T](set : java.util.Set[T]) = jcl.Set(set)
  implicit def convertSortedSet[T](set : java.util.SortedSet[T]) = jcl.SortedSet(set)
  implicit def convertMap[T,E](map : java.util.Map[T,E]) = jcl.Map(map)
  implicit def convertSortedMap[T,E](map : java.util.SortedMap[T,E]) = jcl.SortedMap(map)
//  implicit def convertCollection[A](collection : java.util.Collection[A]) : jcl.Buffer[A] = jcl.Buffer[A](collection.asInstanceOf[java.util.List[A]])
//  implicit def convertCollection[A](collection : java.util.Collection[A]) : Seq[A] = collection.toSeq
//  implicit def convertCollectionToSet[A](collection : java.util.Collection[A]) : Set[A] = Set(collection.toSeq:_*)
  implicit def convertIterableToSeq[A](it : Iterable[A]) : Seq[A] = it.toSeq
  implicit def convertIterableToSeq[A](it : java.lang.Iterable[A]) : Seq[A] = jcl.Buffer[A](it.asInstanceOf[java.util.List[A]])
//  implicit def convertIterableToSet[A](it : java.lang.Iterable[A]) : Set[A] = Set(it.toSeq:_*)
  
//  implicit def convertIterableToSet[A](it : Iterable[A]) : Set[A] = it match {
//    case set : Set[_] => set.asInstanceOf[Set[A]]
//    case _ => Set(it.toSeq:_*)
//  }

  class ToSet[A](it : java.lang.Iterable[A]) {
    def toSet : Set[A] = Set(it.toSeq:_*)
  }
  implicit def toSet[A](it : java.lang.Iterable[A]) = new ToSet(it)
  
  class SeqToSet[A](it : Seq[A]) {
	def toSet = Set(it:_*)
  }
  
//  implicit def seqToSet[A](seq : Seq[A]) = new SeqToSet(seq)
  class ItToSet[A](it : Iterable[A]) {
	  def toSet = Set(it:_*)
  }
  implicit def itToSet[A](seq : Iterable[A]) = new ItToSet(seq)
  
  
  class OptionalString(s: String) {
	def or (s2: String) = {
	 if (s == null || s.trim() == "") s2 else s
	}
  }
  implicit def optionalString(s: String) = new OptionalString(s)
  
//  implicit def stringToBindingWithTag[A](t : Tuple2[Tuple2[String,() => Binding[A]],String]) = 
//	FuncBindParam(t._1._1, (xml) => t._1._2().bind(t._2, xml))
//  implicit def stringToBindingWithTag[A](t : Tuple2[Tuple2[String,() => Binding[A]],String]) = 
//    FuncBindParam(t._1._1, (xml) => t._1._2().bind(t._2, xml))
//  
  implicit def stringToBindingsWithTag[A](t : Tuple2[Tuple2[String, Function2[String, NodeSeq, NodeSeq]],String]) = 
	FuncBindParam(t._1._1, (xml) => t._1._2(t._2, xml))

  implicit def linkAttrBindParam[A](t : Tuple2[Tuple2[String, LinkAttr],String]) = 
	AttrBindParam(t._1._1, t._1._2.value, t._2)
  

  implicit def toBindableObject(obj : Object) = new BindableObject(obj)

  class OrNull[A](option : Option[A]) {
    def orNull : A = option match {
      case Some(value) => value
      case None => null.asInstanceOf[A]
    }
  }
  
  implicit def orNull[A](option : Option[A]) = new OrNull(option)

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

  class MakeMap[A](it : Iterable[A]) {
	def makeMap[B](map : A => B) : Map[A, B] = 
	  mutable.Map((for (a <- it.toSeq; b = map(a)) yield (a, b)):_*)
  }
  class MakeMapReverse[B](it : Iterable[B]) {
	def makeMapReverse[A](map : B => A) : Map[A, B] = 
	  mutable.Map((for (b <- it.toSeq; a = map(b)) yield (a -> b)):_*)
  }
                
  implicit def makeMap[A, B](it : Iterable[A]) = new MakeMap[A](it)
  implicit def makeMapReverse[B](it : Iterable[B]) = new MakeMapReverse[B](it)
  
  implicit def toScala[A](it : java.util.Collection[A]) = new scala.collection.jcl.CollectionWrapper[A] { override def underlying = it }
  
  class RichIterable[A](it : Iterable[A]) {
      
      def filterInstancesOf[B<:A](c : Class[A]) : Seq[B] = 
    	  it filter (c.isInstance(_)) map (_.asInstanceOf[B])

  }
  
  class RichCollection[A](col : Collection[A]) {
	  def filterInstancesOf[B<:A](c : Class[A]) : Seq[B] = 
		  col filter (c.isInstance(_)) map (_.asInstanceOf[B])
  }
  
  class RichSeq[A](seq : Seq[A]) {
    def filterInstancesOf[B<:A](c : java.lang.Class[A]) : Seq[B] = 
      seq filter (c.isInstance(_)) map (_.asInstanceOf[B])
  }
  
  implicit def richJavaCollection[A](collection : java.util.Collection[A]) = new RichCollection[A](collection)
  implicit def richCollection[A](collection : Collection[A]) = new RichCollection[A](collection)
  implicit def richSeq[A](seq : Seq[A]) = new RichSeq[A](seq)
}
