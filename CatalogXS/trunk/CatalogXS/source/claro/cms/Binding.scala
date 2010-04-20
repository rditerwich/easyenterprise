package claro.cms

import claro.common.util.Conversions._
import xml.{Elem,Group,Node,NodeSeq,Text}
import collection.{mutable,jcl}
import java.util.concurrent.ConcurrentMap

case class BindingContext(root : RootBinding, parent : BindingContext, bindings : Map[String,Bindings]) {
  def +(binding : (String, Bindings)) = BindingContext(root, this, bindings + binding)
}

object Bindings {
  def apply(obj : Any, mappings: (String,Binding)*) = new Bindings(obj, Map(mappings:_*)) 
}

case class Bindings(obj : Any, bindings : Map[String,Binding]) {}

abstract class Bindable extends BindingHelpers {
  def bind(node : Node, context : BindingContext) = Binding.bind(node.child, context)
  def bindings = Bindings(null)
}

object Binding {
  
  def bind(xml : NodeSeq, context : BindingContext) : NodeSeq = {
    xml flatMap {
      case s : Elem => 
        context.root.currentElement = s
        context.root.currentContext = context
        try {
          context.root.currentElement = s
          val bindings = if (s.prefix == null) None else context.bindings.get(s.prefix)  
          bindings match {
            case Some(Bindings(obj, bindings)) => bindings.get(s.label) match {
              case Some(binding) => binding.bind(s, context)
              case None => Seq.empty
            }
            case None => Elem(s.prefix, s.label, s.attributes, s.scope, bind(s.child, context) :_*)
          }
        } catch {
          case e => <div>ERROR:{e.printStackTrace;e}</div> 
        }
      case Group(nodes) => Group(bind(nodes, context))
      case n => n
    }
  }
}

trait Binding extends BindingHelpers {
  def bind(node : Node, context : BindingContext) : NodeSeq
}

class AnyBinding(f : => Any) extends Binding {
  def bind(node : Node, context: BindingContext) : NodeSeq = {
    Text(f.toString)
  }
}

class OptionBinding(f : => Option[Any]) extends Binding {
  def bind(node : Node, context: BindingContext) : NodeSeq = {
    f match { 
      case Some(x) => Text(x.toString) 
      case None => Seq.empty 
    }
  }
}

object XmlBinding {
  val ident = new XmlBinding(xml => xml)
  val empty = new XmlBinding(_ => Seq.empty)
  def apply(identNotEmpty : Boolean) = if (identNotEmpty) ident else empty 
}

class XmlBinding(f : NodeSeq => NodeSeq) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = {
    Binding.bind(f(node.child), context)
  }
}

class AnyComplexBinding(defaultPrefix : String, f : => Any) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = {
    val prefix = attr(node, "prefix", defaultPrefix)
    Binding.bind(node.child, context + (prefix -> bindingsFor(f)))
  }  
}

class BindableBinding(f : => Bindable) extends Binding {
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    f.bind(node, context)
  }  
}

class BindableComplexBinding(defaultPrefix : String, f : => Bindable) extends Binding {
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    val bindable = f
    val prefix = attr(node, "prefix", defaultPrefix)
    bindable.bind(node, context + (prefix -> bindable.bindings))
  }  
}

class CollectionBindingBase(f : => Collection[Any], eltBinding : Any => Binding) extends Binding {

  def defaultTargetPrefix : String = "list"
  def groupBinding(elt : Any) : Binding = new CollectionBindingBase(List(elt), _ => XmlBinding.ident)
  
  def bind(node : Node, context: BindingContext) : NodeSeq = {
    val targetPrefix = attr(node, "list-prefix", defaultTargetPrefix)
    val collection : Collection[Any] = f
    val size = collection.size
    if (!collection.isEmpty) {
      var index = 1
      var iterator = collection.elements
      val result = new mutable.ArrayBuffer[Node]
      while (iterator.hasNext) {
        val elt = iterator.next
        val binding = eltBinding(elt)
        result ++= binding.bind(node, context + (targetPrefix -> Bindings(elt, Map(
          "first" -> XmlBinding(index == 1),
          "last" -> XmlBinding(index == size),
          "skip-first" -> XmlBinding(index > 1),
          "skip-last" -> XmlBinding(index < size),
          "once" -> XmlBinding(index == 1),
          "single" -> XmlBinding(size == 1),
          "plural" -> XmlBinding(size != 1),
          "index" -> new AnyBinding(index),
          "size" -> new AnyBinding(size),
          "group" -> groupBinding(elt)))
        ))
        index += 1
      }
      result
    } else {
      node(child => child.prefix == "list" && child.label == "once").theSeq.toList match {
        case head :: tail => eltBinding(null).bind(head, context + (targetPrefix -> EmptyBindings.bindings))
        case Nil => Binding.bind(node.child, context + (targetPrefix -> EmptyBindings.bindings))
      }
    }
  } 
}

class CollectionBinding(f : => Collection[Any], eltBinding : Any => Binding) extends CollectionBindingBase(f, eltBinding) {

  override def bind(node : Node, context: BindingContext) : NodeSeq = {
    val collection : Collection[Any] = f
    val size = collection.size
    
    val groupSize = attr(node, "group-size", "1").toInt
    var groupCount = attr(node, "group-count", "-1").toInt
    if (groupCount < 0) {
      groupCount = Math.ceil(size / groupSize.asInstanceOf[Double]).round.toInt
    }
    
    if (groupSize > 1) {
      var pair : (List[Any],List[Any]) = collection.toList.splitAt(groupSize)
      val groups = new mutable.ArrayBuffer[List[Any]]
      while (!pair._1.isEmpty) {
        groups += pair._1 
        pair = pair._2.splitAt(groupSize)
      }
      
      val binding = new CollectionBindingBase(groups, _ => XmlBinding.ident) {
        override def groupBinding(elt : Any) = new CollectionBinding(elt.asInstanceOf[Collection[Any]], eltBinding) {
          override val defaultTargetPrefix = "group"
        }
      }
      binding.bind(node, context)
    } else {
      super.bind(node, context)
    }
  }
}

object EmptyBindings {
  val bindings = Bindings(null, Map(
  "first" -> XmlBinding.empty,
  "last" -> XmlBinding.empty,
  "skip-first" -> XmlBinding.empty,
  "skip-last" -> XmlBinding.empty,
  "once" -> XmlBinding.ident,
  "single" -> XmlBinding.empty,
  "plural" -> XmlBinding.ident,
  "index" -> new AnyBinding(""),
  "size" -> new AnyBinding(0),
  "group" -> new CollectionBindingBase(Seq.empty, _ => XmlBinding.empty)))
}

object RootBinding {
//  private val current = new ThreadLocal[RootBinding]
//  def apply() = current.get
  val emptyElem = new Elem(null, "", null, xml.TopScope, null)
}

class RootBinding(val website : Website) {

  val cache = new BindingCache(website)
  
  val context = BindingContext(this, null, Map(componentBindings:_*))
  
  var currentElement : Elem = null
  
  var currentContext : BindingContext = context
  
  def componentBindings = website.components map (component => (component.prefix, Bindings(component, cache(component))))
  
  def bind(xml : NodeSeq) : NodeSeq = {
    currentElement = RootBinding.emptyElem
    currentContext = context
    Binding.bind(xml, context)
  }
}

class BindingCtor(label : String) {
  def -> (binding : Binding) = (label, binding)
  def -> (bindings : Bindings) = (label, bindings)
  def -> (bindings : Map[String,Binding]) = (label, bindings)
  def -> (bindable : => Bindable) = new BindableBindingCtor(label, bindable)
  def -> (f : NodeSeq => NodeSeq) = (label, new XmlBinding(f))
  def -> (f : => NodeSeq) = (label, new XmlBinding(_ => f))
  def -> [A](f : => java.util.Collection[A]) = new JavaCollectionBindingCtor(label, f)
  def -> (f : => Collection[Any]) = new CollectionBindingCtor(label, f)
  def -> (f : => Option[Any]) = new OptionBindingCtor(label, f)
  def -> (f : => Any) = new AnyBindingCtor(label, f)
}

class BindableBindingCtor(label : String, f : => Bindable) {
  def -> (defaultPrefix : String) = (label, new BindableComplexBinding(defaultPrefix, f))
  def toLabeledBinding : Tuple2[String,Binding] = (label, new BindableBinding(f))
} 

class CollectionBindingCtor(label : String, f : => Collection[Any]) {
  def -> (defaultPrefix : String) = (label, new CollectionBinding(f, new AnyComplexBinding(defaultPrefix, _)))
  def toLabeledBinding : Tuple2[String,Binding] = (label, new CollectionBinding(f, new AnyBinding(_)))
}

class JavaCollectionBindingCtor(label : String, f : => Collection[Any]) extends CollectionBindingCtor(label, f) {
}

class OptionBindingCtor(label : String, f : => Option[Any]) {
  def -> (defaultPrefix : String) = (label, new AnyComplexBinding(defaultPrefix, f getOrNull))
  def toLabeledBinding = (label, new OptionBinding(f))
}

class AnyBindingCtor(label : String, f : => Any) {
  def -> (defaultPrefix : String) = (label, new AnyComplexBinding(defaultPrefix, f))
  def toLabeledBinding = (label, new AnyBinding(f))
}

trait BindingHelpers {
  implicit def toCtor(label : String) = new BindingCtor(label)
  implicit def toBinding(ctor : BindableBindingCtor) = ctor.toLabeledBinding
  implicit def toBinding(ctor : CollectionBindingCtor) = ctor.toLabeledBinding
  implicit def toBinding(ctor : OptionBindingCtor) = ctor.toLabeledBinding
  implicit def toBinding(ctor : AnyBindingCtor) = ctor.toLabeledBinding
  
  def website : Website = Request.website
 
  def locale = Cms.locale.get
 
  def current : Elem = Request.website.rootBinding.currentElement
  
  def currentContext : BindingContext = Request.website.rootBinding.currentContext
  
  def @@(name : String) : String = attr(current, name)
  
  def @@(name : String, default : => Any) : String = attr(current, name, default) 
  
  def @@?[A](name : String, yes : => A, no : => A) = ifAttr(name, yes, no)
  
  def attr(node : Node, name : String) : String = attr(node, name, throw new Exception("Missing attribute: " + name))
    
  def attr(node : Node, name : String, default : => Any) : String = 
    node.attributes.find(at => at.key == name && !at.isPrefixed) match {
      case Some(attr) => attr.value.toString
      case None => default.toString
    }

  def ifAttr[A](name : String, yes : => A, no : => A) = if (attr(current, name, "false") == "true") yes else no
 
  def bindingsFor(obj : Any) = obj match {
    case null => Bindings(obj, Map[String,Binding]())
    case obj => Bindings(obj, Request.website.rootBinding.cache(obj))
  }
  
  def findBoundObject(prefix : String) = currentContext.bindings.get(prefix) match {
    case Some(Bindings(obj, _)) => Some(obj)
    case None => None
  }

}

class BindingCache(website : Website) {
  
  def apply(obj : Any) : Map[String,Binding] = objectBindings.get(obj)
  
  private val objectBindings : ConcurrentMap[Any,Map[String,Binding]] = 
    new com.google.common.collect.MapMaker().concurrencyLevel(32).weakKeys().makeComputingMap[Any,Map[String,Binding]](
       new com.google.common.base.Function[Any,Map[String,Binding]] {
         def apply(obj : Any) : Map[String,Binding] = {
           website.bindings findFirst(obj) getOrElse Map()
         }});
}
