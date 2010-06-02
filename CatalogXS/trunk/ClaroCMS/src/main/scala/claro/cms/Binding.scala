package claro.cms

import claro.common.util.Conversions._
import xml.{Elem,Group,Node,NodeSeq,Text}
import collection.{mutable}
import java.util.concurrent.ConcurrentMap
import scala.collection.JavaConversions._

class BindingCtor(label : String) {
  def -> (binding : Binding) = (label, binding)
  def -> (bindings : Bindings) = (label, bindings)
  def -> (bindings : Map[String,Binding]) = (label, bindings)
  def -> (bindable : => Bindable) = new BindableBindingCtor(label, bindable)
  def -> (f : NodeSeq => NodeSeq) = (label, new XmlBinding(f))
  def -> (f : => NodeSeq) = (label, new XmlBinding(_ => f))
  def -> (f : => java.util.Collection[Any]) = new JavaCollectionBindingCtor(label, f)
  def -> (f : => Collection[Any]) = new CollectionBindingCtor(label, f)
  def -> (f : => Option[Any]) = new OptionBindingCtor(label, f)
  def -> (f : => Any) = new AnyBindingCtor(label, f)
}

class BindableBindingCtor(label : String, f : => Bindable) {
  def -> (defaultPrefix : String) = (label, new BindableComplexBinding(defaultPrefix, f))
  def toLabeledBinding : Tuple2[String,Binding] = (label, new BindableBinding(f))
} 

class CollectionBindingCtor(label : String, f : => Collection[Any]) {
  def -> (defaultPrefix : String) = (label, new CollectionBinding(f, node => obj => 
    new AnyComplexBinding(_ => Binding.prefix(node, defaultPrefix), obj)))
  def toLabeledBinding : Tuple2[String,Binding] = (label, new CollectionBinding(f, _ => obj => new AnyBinding(obj)))
}

class JavaCollectionBindingCtor(label : String, f : => java.util.Collection[Any]) 
	extends CollectionBindingCtor(label, f) 

class OptionBindingCtor(label : String, f : => Option[Any]) extends BindingHelpers {
  def -> (defaultPrefix : String) = (label, new AnyComplexBinding(node => Binding.prefix(node, defaultPrefix), f getOrNull))
  def toLabeledBinding = (label, new AnyBinding(f getOrNull))
}

class AnyBindingCtor(label : String, f : => Any) {
  def -> (defaultPrefix : String) = (label, new AnyComplexBinding(node => Binding.prefix(node, defaultPrefix), f))
  def toLabeledBinding = (label, new AnyBinding(f))
}

case class BindingContext(root : RootBinding, parent : BindingContext, bindings : Map[String,Bindings], repeatSeen : Flag) {
  def + (binding : (String, Bindings)) = BindingContext(root, this, bindings + binding, repeatSeen)
  def initRepeatSeen = BindingContext(root, this, bindings, Flag())
}

object Flag {
  def apply() = new Flag(false)
  def apply(value : Boolean) = new Flag(value)
  implicit def toBoolean(flag : Flag) : Boolean = flag.value
}

class Flag(var value : Boolean) {
  def unary_! : Boolean = !value
  def set(v : Boolean) = value = v
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
            val bindings = if (s.prefix == null) None else context.bindings.get(s.prefix)  
            bindings match {
            case Some(Bindings(obj, bindings)) => bindings.get(s.label) match {
            case Some(binding) => binding.bind(s, context)
            case None => NodeSeq.Empty
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

  val ident = new Binding {
    def bind(node : Node, context: BindingContext) = 
      Binding.bind(node.child, context)
  }
  
  val empty = new Binding {
    def bind(node : Node, context: BindingContext) = 
      NodeSeq.Empty
  }
  
  def apply(identNotEmpty : Boolean) = if (identNotEmpty) ident else empty 

  def prefix(node : Node, defaultPrefix : String) = node.attributes.find(a => !a.isPrefixed && a.key == "prefix") match {
      case Some(attr) => attr.value.toString
      case None => defaultPrefix
    }
}

trait Binding extends BindingHelpers {
  def bind(node : Node, context : BindingContext) : NodeSeq
}

class AnyBinding(f : => Any) extends Binding {
  def bind(node : Node, context: BindingContext) : NodeSeq = {
    f match {
      case null => NodeSeq.Empty
      case value => Text(f.toString)
    }
  }
}

class XmlBinding(f : NodeSeq => NodeSeq) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = {
    Binding.bind(f(node.child), context)
  }
}

class AnyComplexBinding(prefix : Node => String, f : => Any) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = {
    def none = {
      val nodes = node.child.filter(child => child.prefix == "object" && child.label == "none").flatMap(_.child)
      nodes.flatMap(Binding.bind(_, context))
    }
    f match {
      case null => none 
      case false => none 
      case java.lang.Boolean.FALSE => none 
      case value =>
        Binding.bind(node.child, context + (prefix(node) -> bindingsFor(value)))
    }
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

object CollectionBinding {
  
  def bind(collection : Collection[Any], eltBinding : Node => Any => Binding, groupBinding : Node => Any => Binding, listPrefix : String, node : Node, context: BindingContext) : NodeSeq = {
    val size = collection.size
    val context2 = context.initRepeatSeen
    if (!collection.isEmpty) {
      var index = 1
      var iterator = collection.elements
      val result = new mutable.ArrayBuffer[Node]
      while (iterator.hasNext && !context2.repeatSeen) {
        val elt = iterator.next
        
        // list bindings 
        val listBindings = listPrefix -> Bindings(elt, Map(
          "first" -> Binding(index == 1),
          "last" -> Binding(index == size),
          "skip-first" -> Binding(index > 1),
          "skip-last" -> Binding(index < size),
          "none" -> Binding.empty,
          "once" -> Binding(index == 1),
          "single" -> Binding(size == 1),
          "plural" -> Binding(size != 1),
          "index" -> new AnyBinding(index),
          "size" -> new AnyBinding(size),
          "group" -> groupBinding(node)(elt),
          "fill" -> Binding.empty,
          "repeat" -> new CollectionRepeatBinding(node, collection, eltBinding, groupBinding, listPrefix)))

        if (elt == null) {
          val nodes = node.child.filter(child => child.prefix == listPrefix && child.label == "fill").flatMap(_.child)
          result ++= nodes.flatMap(Binding.bind(_, context2 + listBindings))
        }
        else {
          result ++= eltBinding(node)(elt).bind(node, context2 + listBindings)
        }
        index += 1
      }
      result
    } else {
      val nodes = node.child.filter(child => child.prefix == listPrefix && (child.label == "once" || child.label == "none")).flatMap(_.child)
      nodes.flatMap(Binding.bind(_, context + (listPrefix -> EmptyBindings.bindings)))
    }
  } 
}

class CollectionBinding(f : => Collection[Any], eltBinding : Node => Any => Binding) extends Binding {

  override def bind(node : Node, context: BindingContext) : NodeSeq = {

    // get the collection
    var collection = f
    var size = collection.size
    val listPrefix = attr(node, "list-prefix", "list")
    
    // determine groups
    var groupSize = attr(node, "group-size", "1").toInt
    var groupCount = attr(node, "group-count", "-1").toInt
    var groupScatter = attr(node, "group-scatter", "false").toBoolean
    
    if (groupCount < 0) {
      groupCount = Math.ceil(size / groupSize.asInstanceOf[Double]).round.toInt
    } else {
      groupSize = Math.ceil(size / groupCount.asInstanceOf[Double]).round.toInt
    }
    val fillCount = (size % groupSize) match {
      case 0 => 0
      case mod => groupSize - mod
    }
    
    // grouping?
    val (groups,groupBinding) = if (groupSize <= 1) (collection, eltBinding)
    else {

      // pad collection with null values
      val totalSize = groupCount * groupSize
      val padding = totalSize - collection.size
      val array = if (padding > 0) collection.toSeq ++ Array.make(padding, null) else collection.toSeq
      
      // partition in groups
      val groups = if (!groupScatter) {
        for (i <- 0 until groupCount; start = i * groupSize) 
          yield array.slice(start, start + groupSize)
      } else {
        for (i <- 0 until groupCount) 
          yield for (j <- i until(totalSize, groupCount)) 
            yield(array(j))
      }
      
      (groups, (node : Node) => (elt : Any) => new CollectionGroupBinding(elt.asInstanceOf[Collection[Any]], groupSize, eltBinding))
    }
    
    CollectionBinding.bind(groups, eltBinding, groupBinding, listPrefix, node, context)
  }  
}

object EmptyBindings {
  val bindings = Bindings(null, Map(
  "first" -> Binding.empty,
  "last" -> Binding.empty,
  "skip-first" -> Binding.empty,
  "skip-last" -> Binding.empty,
  "once" -> Binding.empty,
  "none" -> Binding.empty,
  "single" -> Binding.empty,
  "plural" -> Binding.ident,
  "index" -> new AnyBinding(""),
  "size" -> new AnyBinding(0),
  "group" -> EmptyEltBinding(null)(null),
  "fill" -> Binding.empty,
  "repeat" -> new CollectionRepeatBinding(null, List(), EmptyEltBinding, EmptyEltBinding, "list")))
}

object EmptyEltBinding extends Function1[Node, Function[Any, Binding]] {
  def apply(node : Node) = (_ : Any) => Binding.empty
}

class CollectionRepeatBinding(collectionNode : Node, collection : Collection[Any], eltBinding : Node => Any => Binding, groupBinding : Node => Any => Binding, listPrefix : String) extends Binding {
  override def bind(node : Node, context: BindingContext) : NodeSeq = {
    context.repeatSeen.set(true)
    CollectionBinding.bind(collection, _ => obj => eltBinding(collectionNode)(obj), groupBinding, listPrefix, node, context)
  }  
}

class CollectionGroupBinding(collection : Collection[Any], groupSize : Int, eltBinding : Node => Any => Binding) extends Binding {
  override def bind(node : Node, context: BindingContext) : NodeSeq = {
    val groupPrefix = attr(node, "list-prefix", "group")
    CollectionBinding.bind(collection, eltBinding, EmptyEltBinding, groupPrefix, node, context)
  }  
}

object RootBinding {
  val emptyElem = new Elem(null, "", null, xml.TopScope, null)
}

class RootBinding(val website : Website) {

  val cache = new BindingCache(website)
  
  val context = BindingContext(this, null, Map(bindings:_*), Flag())
  
  var currentElement : Elem = null
  
  var currentContext : BindingContext = context
  
  def componentBindings = website.components map (component => (component.prefix, Bindings(component, cache(component))))

  def bindings = componentBindings ++ Map("object" -> Bindings(null, "none" -> Binding.empty))
  
  def bind(xml : NodeSeq) : NodeSeq = {
    currentElement = RootBinding.emptyElem
    currentContext = context
    Binding.bind(xml, context)
  }
}

trait BindingHelpers {
  
  implicit def toCtor(label : String) = new BindingCtor(label)
  implicit def toBinding(ctor : BindableBindingCtor) = ctor.toLabeledBinding
  implicit def toBinding(ctor : CollectionBindingCtor) = ctor.toLabeledBinding
  implicit def toBinding(ctor : OptionBindingCtor) = ctor.toLabeledBinding
  implicit def toBinding(ctor : AnyBindingCtor) = ctor.toLabeledBinding
  
  def locale = Cms.locale
 
  def website = Website.instance 
  
  def current : Elem = website.rootBinding.currentElement
  
  def currentContext : BindingContext = website.rootBinding.currentContext

  def currentAttributes(excl : String*) = 
    current.attributes filter(a => !excl.contains(a.key))

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
    case obj => Bindings(obj, Website.instance.rootBinding.cache(obj))
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
