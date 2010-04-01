package claro.cms

import claro.common.util.Conversions._
import xml.{Elem,Group,Node,Text}
import collection.mutable
import java.util.concurrent.ConcurrentMap

class BindingCtor(label : String) {
  def -> (binding : Binding) = (label, binding)
  def -> (bindings : Bindings) = (label, bindings)
  def -> (f : Seq[Node] => Seq[Node]) = (label, new XmlBinding(f))
  def -> (f : => Seq[Node]) = (label, new XmlBinding(_ => f))
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
  def +(binding: (String,Bindings)) = new BindingContext(root, this, bindings + binding)
}

object Binding {
  
  def bind(xml : Seq[Node], context : BindingContext) : Seq[Node] = {
    xml flatMap {
      case s : Elem =>
        try {
          context.root.currentElement = s
          val bindings = if (s.prefix == null) None else context.bindings.get(s.prefix)  
          bindings match {
            case Some(bindings) => bindings.childBindings.get(s.label) match {
              case Some(binding) => binding.bind(s, context)
              case None => Seq.empty
            }
            case None => Elem(s.prefix, s.label, s.attributes, s.scope, bind(s.child, context) :_*)
          }
        } catch {
          case e => <div>ERROR:{e.getMessage}</div> 
        }
      case Group(nodes) => Group(bind(nodes, context))
      case n => n
    }
  }
}

trait Binding {
  def bind(node : Node, context : BindingContext) : Seq[Node]
}

object XmlBinding {
  val ident = new XmlBinding(xml => xml)
  val empty = new XmlBinding(_ => Seq.empty)
  def apply(identNotEmpty : Boolean) = if (identNotEmpty) ident else empty 
}

class XmlBinding(f : Seq[Node] => Seq[Node]) extends Binding {
  def bind(node : Node, context : BindingContext) : Seq[Node] = {
    Binding.bind(f(node.child), context)
  }
}

class ComplexBinding(f : => Any, targetPrefix : String) extends Binding with Bindings {
  def bind(node : Node, context : BindingContext) : Seq[Node] = {
    Binding.bind(node.child, context + (targetPrefix -> this))
  }
  lazy val childBindings = RootBinding().cache(f)
}

class CollectionBinding(f : => Collection[Any], eltBinding : Any => Binding) extends Binding {
  def bind(node : Node, context: BindingContext) : Seq[Node] = {
    val collection : Collection[Any] = f
    val size = collection.size
    if (!collection.isEmpty) {
      var index = 1
      var iterator = collection.elements
      val result = new mutable.ArrayBuffer[Node]
      while (iterator.hasNext) {
        val binding = eltBinding(iterator.next)
        result ++= binding.bind(node, context + ("list" -> new Bindings{
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
        }))
        index += 1
      }
      result
    } else {
      Binding.bind(node.child, context + ("list" -> EmptyBindings))
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
  def bind(node : Node, context: BindingContext) : Seq[Node] = {
    Text(f.toString)
  }
}


object RootBinding {
  private val current = new ThreadLocal[RootBinding]
  def apply() = current.get
  val emptyElem = new Elem(null, "", null, xml.TopScope, null)
}

class RootBinding(val site : Site) {

  val cache = new BindingCache(site)
  
  val context = new BindingContext(this, null, Map(componentBindings:_*))
  
  var currentElement : Elem = null
  
  def componentBindings = site.components map (component => (component.prefix, new Bindings {
    val childBindings = cache(component)
  }))
  
  def bind(xml : Seq[Node]) : Seq[Node] = {
    RootBinding.current.set(this)
    currentElement = RootBinding.emptyElem
    Binding.bind(xml, context)
  }
  
  def findAttr(attr : String, default : => Any) : String = {
    currentElement.attributes.find(at => at.key == attr && !at.isPrefixed) match {
      case Some(attr) => attr.value.toString
      case None => default.toString
    }
  }
}

trait BindingHelpers {
  implicit def ctor(label : String) = new BindingCtor(label)
  implicit def labeledBinding(ctor : AnyBindingCtor) = ctor.toLabeledBinding
  implicit def labeledBinding(ctor : CollectionBindingCtor) = ctor.toLabeledBinding
 
  def site : Site = RootBinding().site
 
  def locale = Cms.locale.get
 
  def current : Elem = RootBinding().currentElement
  
  def @@(name : String) : String = attr(current, name)
  
  def @@(name : String, default : => Any) : String = attr(current, name, default) 
  
  def attr(node : Node, name : String) : String = attr(node, name, throw new Exception("Missing attribute: " + name))
    
  def attr(node : Node, name : String, default : => Any) : String = 
    node.attributes.find(at => at.key == name && !at.isPrefixed) match {
      case Some(attr) => attr.value.toString
      case None => default.toString
    }

  def find(node : Node, prefix : String, label : String) : Seq[Node] = 
    node.child filter(child => child.prefix == prefix && child.label == label)
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