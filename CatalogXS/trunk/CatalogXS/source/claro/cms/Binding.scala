package claro.cms

import claro.common.util.Conversions._
import xml.{Elem,Group,Node,NodeSeq,Text}
import collection.mutable
import java.util.concurrent.ConcurrentMap

class BindingCtor(label : String) {
  def -> (f : NodeSeq => NodeSeq) = (label, new XmlBinding(f))
  def -> (f : => Collection[Any]) = new CollectionBindingCtor(label, f)
  def -> (f : => Any) = new AnyBindingCtor(label, f)
}

class CollectionBindingCtor(label : String, f : => Collection[Any]) {
  def -> (targetPrefix : String) = (label, new CollectionBinding(f, new ComplexBinding(_, targetPrefix)))
  def toLabeledBinding : Tuple2[String,Binding] = (label, new CollectionBinding(f, new AnyBinding(_)))
}

class AnyBindingCtor(label : String, f : => Any) {
  def -> (targetPrefix : String) = (label, new ComplexBinding(f, targetPrefix))
  def toLabeledBinding = (label, new AnyBinding(f))
}

trait Bindings {
  val childBindings : Map[String,Binding]
}

class BindingContext(val root : RootBinding, val parent : BindingContext, val bindings : Map[String,Bindings]) {
  def findTemplate(template : String) : Option[NodeSeq] = None
  def apply(bindings : Map[String,Bindings]) = new BindingContext(root, this, bindings)
}

abstract class Binding {
  def bind(elem : Elem, xml : NodeSeq, context : BindingContext) : NodeSeq = {
    xml flatMap {
      case s : Elem =>
        val bindings = if (s.prefix == null) None else context.bindings.get(s.prefix)  
        bindings match {
          case Some(bindings) => bindings.childBindings.get(s.label) match {
            case Some(binding) => binding.bind(s, s.child, context)
            case None => NodeSeq.Empty
          }
          case None => Elem(s.prefix, s.label, s.attributes, s.scope, bind(s, s.child, context) :_*)
        }
      case Group(nodes) => Group(bind(null, nodes, context))
      case n => n
    }
  }
}

object XmlBinding {
  val ident = new XmlBinding(xml => xml)
  val empty = new XmlBinding(_ => NodeSeq.Empty)
  def apply(identNotEmpty : Boolean) = if (identNotEmpty) ident else empty 
}

class XmlBinding(f : NodeSeq => NodeSeq) extends Binding {
  override def bind(elem : Elem, xml : NodeSeq, context : BindingContext) : NodeSeq = {
    super.bind(elem, f(xml), context)
  }
}

class ComplexBinding(f : => Any, targetPrefix : String) extends Binding with Bindings {
  override def bind(elem : Elem, xml : NodeSeq, context : BindingContext) : NodeSeq = {
    super.bind(elem, xml, context(context.bindings + (targetPrefix -> this)))
  }
  lazy val childBindings = RootBinding().cache(f)
}

class CollectionBinding(f : => Collection[Any], eltBinding : Any => Binding) extends Binding {
  override def bind(elem : Elem, xml : NodeSeq, context: BindingContext) : NodeSeq = {
    val collection : Collection[Any] = f
    val size = collection.size
    if (!collection.isEmpty) {
      var index = 1
      var iterator = collection.elements
      val result = new mutable.ArrayBuffer[Node]
      while (iterator.hasNext) {
        val binding = eltBinding(iterator.next)
        result ++= binding.bind(elem, xml, context(context.bindings + ("list" -> new Bindings{
		  lazy val childBindings = Map(
		    "first" -> XmlBinding(index == 1),
		    "last" -> XmlBinding(index == size),
		    "skip-first" -> XmlBinding(index > 1),
		    "skip-last" -> XmlBinding(index < size),
		    "once" -> XmlBinding(index == 1),
		    "single" -> XmlBinding(size == 1),
		    "plural" -> XmlBinding(size != 1),
		    "index" -> new AnyBinding(index),
		    "size" -> new AnyBinding(size))
        })))
        index += 1
      }
      result
    } else {
      super.bind(elem, xml, context(context.bindings + ("list" -> EmptyBindings)))
    }
  } 
}

object EmptyBindings extends Bindings {
  val childBindings = Map(
	"first" -> XmlBinding.empty,
	"last" -> XmlBinding.empty,
	"skip-first" -> XmlBinding.empty,
	"skip-last" -> XmlBinding.empty,
	"once" -> XmlBinding.ident,
	"single" -> XmlBinding.empty,
	"plural" -> XmlBinding.ident,
	"index" -> new AnyBinding(""),
	"size" -> new AnyBinding(0))
}

class AnyBinding(f : => Any) extends Binding {
  override def bind(elem : Elem, xml : NodeSeq, context: BindingContext) : NodeSeq = {
    Text(f.toString)
  }
}

object RootBinding {
  private val current = new ThreadLocal[RootBinding]
  def apply() = current.get
}

class RootBinding(site : Site) extends Binding {

  val cache = new BindingCache(site)
  
  def componentBindings = site.components map (component => (component.prefix, new Bindings {
    val childBindings = cache(component)
  }))
  
  val context = new BindingContext(this, null, Map(componentBindings:_*))
  
  def bind(xml : NodeSeq) : NodeSeq = {
    RootBinding.current.set(this)
    bind(null, xml, context)
  }
}

class BindingCache(site : Site) {
  
  def apply(obj : Any) : Map[String,Binding] = objectBindings.get(obj)
  
  private val objectBindings : ConcurrentMap[Any,Map[String,Binding]] = 
    new com.google.common.collect.MapMaker().concurrencyLevel(32).weakKeys().makeComputingMap[Any,Map[String,Binding]](
       new com.google.common.base.Function[Any,Map[String,Binding]] {
         def apply(obj : Any) : Map[String,Binding] = {
           site.bindings findFirst(obj) getOrElse Map()
         }});
}