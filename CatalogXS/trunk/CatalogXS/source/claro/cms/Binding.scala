package claro.cms

import claro.common.util.Conversions._
import xml.{Elem,Group,Node,NodeSeq,Text}
import collection.mutable
import java.util.concurrent.ConcurrentMap

class BindingCtor(label : String) {
  def -> (binding : Binding) = (label, binding)
  def -> (bindings : Map[String,Binding]) = (label, bindings)
  def -> (f : NodeSeq => NodeSeq) = (label, new XmlBinding(f))
  def -> (f : => NodeSeq) = (label, new XmlBinding(_ => f))
  def -> (f : => Collection[Any]) = new CollectionBindingCtor(label, f)
  def -> (f : => Option[Any]) = new OptionBindingCtor(label, f)
  def -> (f : => Any) = new AnyBindingCtor(label, f)
}

class CollectionBindingCtor(label : String, f : => Collection[Any]) {
  def -> (targetPrefix : String) = (label, new CollectionBinding(f, new ComplexBinding(_, targetPrefix)))
  def toLabeledBinding : Tuple2[String,Binding] = (label, new CollectionBinding(f, new AnyBinding(_)))
}

class OptionBindingCtor(label : String, f : => Option[Any]) {
	def -> (targetPrefix : String) = (label, new ComplexBinding(f getOrNull, targetPrefix))
	def toLabeledBinding = (label, new OptionBinding(f))
}

class AnyBindingCtor(label : String, f : => Any) {
  def -> (targetPrefix : String) = (label, new ComplexBinding(f, targetPrefix))
  def toLabeledBinding = (label, new AnyBinding(f))
}

class BindingContext(val root : RootBinding, val parent : BindingContext, val bindings : Map[String,Map[String,Binding]]) {
  def +(binding: (String,Map[String,Binding])) = new BindingContext(root, this, bindings + binding)
}

object Binding {
  
  def bind(xml : NodeSeq, context : BindingContext) : NodeSeq = {
    xml flatMap {
      case s : Elem =>
        try {
          context.root.currentElement = s
          val bindings = if (s.prefix == null) None else context.bindings.get(s.prefix)  
          bindings match {
            case Some(bindings) => bindings.get(s.label) match {
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

trait Binding extends BindingHelpers {
  def bind(node : Node, context : BindingContext) : NodeSeq
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

class ComplexBinding(f : => Any, defaultTargetPrefix : String) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = {
    val targetPrefix = attr(node, "prefix", defaultTargetPrefix)
    val childBindings = f match {
      case null => Map[String,Binding]()
      case obj => RootBinding().cache(f)
    }
    Binding.bind(node.child, context + (targetPrefix -> childBindings))
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
        result ++= binding.bind(node, context + (targetPrefix -> Map(
          "first" -> XmlBinding(index == 1),
          "last" -> XmlBinding(index == size),
		  "skip-first" -> XmlBinding(index > 1),
		  "skip-last" -> XmlBinding(index < size),
		  "once" -> XmlBinding(index == 1),
		  "single" -> XmlBinding(size == 1),
		  "plural" -> XmlBinding(size != 1),
		  "index" -> new AnyBinding(index),
		  "size" -> new AnyBinding(size),
		  "group" -> groupBinding(elt))
        ))
        index += 1
      }
      result
    } else {
      node(child => child.prefix == "list" && child.label == "once") theSeq match {
        case head :: tail => eltBinding(null).bind(head, context + (targetPrefix -> EmptyBindings.bindings))
        case Nil => NodeSeq.Empty
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
  val bindings = Map(
	"first" -> XmlBinding.empty,
	"last" -> XmlBinding.empty,
	"skip-first" -> XmlBinding.empty,
	"skip-last" -> XmlBinding.empty,
	"once" -> XmlBinding.ident,
	"single" -> XmlBinding.empty,
	"plural" -> XmlBinding.ident,
	"index" -> new AnyBinding(""),
	"size" -> new AnyBinding(0),
	"group" -> new CollectionBindingBase(Seq.empty, _ => XmlBinding.empty))
}

class OptionBinding(f : => Option[Any]) extends Binding {
	def bind(node : Node, context: BindingContext) : NodeSeq = {
	  f match { 
	    case Some(x) => Text(x.toString) 
	    case None => Seq.empty 
	  }
	}
}

class AnyBinding(f : => Any) extends Binding {
  def bind(node : Node, context: BindingContext) : NodeSeq = {
    Text(f.toString)
  }
}

object RootBinding {
  private val current = new ThreadLocal[RootBinding]
  def apply() = current.get
  val emptyElem = new Elem(null, "", null, xml.TopScope, null)
}

class RootBinding(val website : Webwebsite) {

  val cache = new BindingCache(website)
  
  val context = new BindingContext(this, null, Map(componentBindings:_*))
  
  var currentElement : Elem = null
  
  def componentBindings = website.components map (component => (component.prefix, cache(component)))
  
  def bind(xml : NodeSeq) : NodeSeq = {
    RootBinding.current.set(this)
    currentElement = RootBinding.emptyElem
    Binding.bind(xml, context)
  }
}

trait BindingHelpers {
  implicit def ctor(label : String) = new BindingCtor(label)
  implicit def labeledBinding(ctor : AnyBindingCtor) = ctor.toLabeledBinding
  implicit def labeledBinding(ctor : CollectionBindingCtor) = ctor.toLabeledBinding
 
  def website : Webwebsite = RootBinding().website
 
  def locale = Cms.locale.get
 
  def current : Elem = RootBinding().currentElement
  
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
  
}

class BindingCache(website : Webwebsite) {
  
  def apply(obj : Any) : Map[String,Binding] = objectBindings.get(obj)
  
  private val objectBindings : ConcurrentMap[Any,Map[String,Binding]] = 
    new com.google.common.collect.MapMaker().concurrencyLevel(32).weakKeys().makeComputingMap[Any,Map[String,Binding]](
       new com.google.common.base.Function[Any,Map[String,Binding]] {
         def apply(obj : Any) : Map[String,Binding] = {
           website.bindings findFirst(obj) getOrElse Map()
         }});
}