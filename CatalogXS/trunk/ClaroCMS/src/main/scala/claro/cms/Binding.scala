package claro.cms

import claro.cms.util.Formatting
import net.liftweb.http.{RequestVar,SessionVar}
import claro.common.util.Conversions._
import collection.{mutable}
import java.util.concurrent.ConcurrentMap
import scala.collection.JavaConversions._

import xml.{Elem, Group, MetaData, Node, NodeSeq, Text, Attribute, UnprefixedAttribute, PrefixedAttribute, TopScope}

object Flag {
  def apply() = new Flag(false)
  def apply(value : Boolean) = new Flag(value)
  implicit def toBoolean(flag : Flag) : Boolean = flag.value
}

class Flag(var value : Boolean) {
  def unary_! : Boolean = !value
  def set(v : Boolean) = value = v
} 

case class Bindings(boundObject: Option[Any], bindings : Map[String,Binding]) {}

object Binding {
  
  def bind(xml : NodeSeq, context : BindingContext) : NodeSeq = {
    xml flatMap {
      case s : Elem => 
        BindingContext.current.set(context, s)
        try {
            val bindings = if (s.prefix == null) None else context.bindings.get(s.prefix)  
            bindings match {
            case Some(Bindings(_, bindings)) => bindings.get(s.label) match {
              case Some(binding) => binding.bind(s, context)
              case None => NodeSeq.Empty
            }
            case None => 
              var attrs : MetaData = s.attributes 
              for (attr <- s.attributes) attr match {
                case PrefixedAttribute(prefix, label, value, _) => context.bindings.get(prefix) match {
                  case Some(Bindings(_, bindings)) => bindings.get(label) match {
                    case Some(binding) => 
                      val str = value.toString
                      val i = str.indexOf('=')
                      val i2 = str.indexOf('|', i)
                      val l = if (i <= 0) str else str.substring(0, i) 
                      val v = if (i <= 0) "" else 
                        if (i2 < 0) str.substring(i + 1) 
                        else str.substring(i + 1, i2)
                      if (binding.toBoolean(attr, context))
                        attrs = new UnprefixedAttribute(l, v, attrs.filter(_ != attr))
                      else 
                        if (i2 < 0) attrs = attrs.filter(_ != attr)
                        else attrs = new UnprefixedAttribute(l, str.substring(i2 + 1), attrs.filter(_ != attr))
                    case None => 
                    }
                  case None => 
                }
                case _ => 
              }
              // concat classes
              val classes = attrs.filter(_.key == "class").map(_.value).mkString(" ") match {
            	  case "" =>
            	  case value => attrs = new UnprefixedAttribute("class", value, attrs)
              }
              attrs = MetaData.normalize(attrs, TopScope)
              Elem(s.prefix, s.label, attrs, s.scope, bind(s.child, context) :_*)
            }
        } catch {
          case e => <div>ERROR:{e.printStackTrace;e}</div> 
        }
      case Group(nodes) => Group(bind(nodes, context))
      case n => n
    }

  }

  object ident extends Binding {
    def bind(node : Node, context: BindingContext) = 
      Binding.bind(node.child, context)
    override def toBoolean(attr : MetaData, context: BindingContext) = true 
  }
  
  object empty extends Binding {
    def bind(node : Node, context: BindingContext) = 
      NodeSeq.Empty
    override def toBoolean(attr : MetaData, context: BindingContext) = false 
  }
  
  def apply(identNotEmpty : Boolean) = if (identNotEmpty) ident else empty 

  def prefix(node : Node, defaultPrefix : String) = node.attributes.find(a => !a.isPrefixed && a.key == "prefix") match {
      case Some(attr) => attr.value.toString
      case None => defaultPrefix
    }
    
  private def toBoolean(binding : Binding, s : Elem, context : BindingContext) : Boolean = {
    if (binding == ident) true
    else if (binding == empty) false
    else binding.bind(s, context) match {
      case Text(s) => 
        if (s.equalsIgnoreCase("true")) return true
        try {
          if (s.toInt > 0) return true
        } catch {
          case e : NumberFormatException =>
        }
        return false
      case NodeSeq.Empty => return false
      case _ => return true
    }
  }
}

trait Binding extends BindingHelpers {
  def bind(node : Node, context : BindingContext) : NodeSeq
  def toBoolean(attr : MetaData, context : BindingContext) : Boolean = false
  def toBoolean(a : Any) = {
    a match {
      case b : Boolean => b
      case i : Int => i > 0
      case n : Number => n.intValue > 0
      case it : Iterable[Any] => !it.isEmpty
      case null => false
      case obj : Any => 
        try {
          obj.toString.toInt > 0
        } catch {
          case e : NumberFormatException => false
        }
    }
  }
}

trait Bindable extends Binding {
	val defaultPrefix : String
	lazy val bindings : Map[String,Binding] = bindingsFor(this)
	def childContext(context : BindingContext) = context + (defaultPrefix -> Bindings(Some(this), bindings))
	def bind(node : Node, context : BindingContext) : NodeSeq = Binding.bind(node.child, childContext(context))
}

class XmlBinding(f : NodeSeq => NodeSeq) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = {
    Binding.bind(f(node.child), context)
  }
}

abstract class OptionBinding[A](f : => A) extends Binding {
  def bind(node : Node, context : BindingContext) : NodeSeq = f match {
		case null =>
  		val nodes = node.child.filter(child => child.prefix == "object" && child.label == "none")
  		Binding.bind(nodes.flatMap(_.child), context)
		case value =>
  		bind(value, node, context).filter(child => child.prefix != "object" || child.label != "none")
	}
  def bind(value : A, node : Node, context : BindingContext) : NodeSeq
}

class AnyBinding[A](f : => A, xml : A => Node => NodeSeq) extends OptionBinding(f) {
	def bind(value : A, node : Node, context : BindingContext) = {
		if (node.child.isEmpty) xml(value)(node)
		else Binding.bind(node.child, context + ("object" -> Bindings(Some(value), Map("value" -> new Binding {
			def bind(node : Node, context : BindingContext) = xml(value)(node)
		}))))
	}
}
private class AnyOptionBinding[A](f : => Option[A], xml : A => Node => NodeSeq) extends AnyBinding[A](f.getOrNull, xml)
private class AnyCollectionBinding(f : => Collection[Any], xml : Any => Node => NodeSeq) extends CollectionBinding(f, _ => obj => new AnyBinding[Any](obj, xml))
private class BooleanBinding(f : => Boolean) extends AnyBinding[String](if (f) "true" else null.asInstanceOf[String], _ => _ => NodeSeq.Empty)

class ComplexBinding[A](f : => A, defaultPrefix : String) extends OptionBinding(f) {
	def bindings(value : A) : Map[String,Binding] = bindingsFor(value)
	def bind(value : A, node : Node, context : BindingContext) = {
		val value : A = f
		Binding.bind(node.child, context + (attr(node, "prefix", defaultPrefix) -> Bindings(Some(value), bindings(value))))
	}
}
private class ComplexOptionBinding[A](f : => Option[A], defaultPrefix : String) extends ComplexBinding[A](f.getOrNull, defaultPrefix)
private class ComplexCollectionBinding(f : => Collection[Any], defaultPrefix : String) extends CollectionBinding(f, _ => obj => new ComplexBinding(obj, defaultPrefix))
private class ComplexGroupBinding(f : => Collection[Collection[Any]], defaultPrefix : String) extends GroupedCollectionBinding(f, _ => obj => new ComplexBinding(obj, defaultPrefix))

private class BindingBinding(f : => Binding) extends OptionBinding[Binding](f) {
	def bind(binding : Binding, node : Node, context : BindingContext) = {
		binding.bind(node, context)
	}
}
private class BindingOptionBinding(f : => Option[Binding]) extends BindingBinding(f.getOrNull)
private class BindingCollectionBinding(f : => Collection[Binding]) extends CollectionBinding(f, _ => obj => obj.asInstanceOf[Binding]) 
private class BindingGroupBinding(f : => Collection[Collection[Binding]]) extends GroupedCollectionBinding(f, _ => obj => obj.asInstanceOf[Binding])

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
        val listBindings = listPrefix -> Bindings(Some(elt), Map(
          "first" -> Binding(index == 1),
          "last" -> Binding(index == size),
          "even" -> Binding(index % 2 == 0),
          "odd" -> Binding(index % 2 != 0),
          "skip-first" -> Binding(index > 1),
          "skip-last" -> Binding(index < size),
          "none" -> Binding.empty,
          "once" -> Binding(index == 1),
          "single" -> Binding(size == 1),
          "plural" -> Binding(size != 1),
          "index" -> new XmlBinding(_ => new Text(index.toString)),
          "size" -> new XmlBinding(_ => new Text(size.toString)),
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

private class CollectionBinding(f : => Collection[Any], eltBinding : Node => Any => Binding) extends Binding {

  override def bind(node : Node, context: BindingContext) : NodeSeq = {

    // get the collection
    var collection = f
    var size = collection.size
    val listPrefix = attr(node, "list-prefix", "list")
    
    // paging
    val pageSize = attr(node, "page-size", "0").toInt
    if (pageSize > 0 && pageSize < size) {
    	Paging.sizeEstimate = size
    	Paging.pageSize = pageSize
    	val startIndex = (Paging.currentPage - 1) * pageSize
    	collection = collection slice (startIndex, startIndex + pageSize)
    	size = collection.size
    }
    
    // determine groups
    var groupSize = attr(node, "group-size", "1").toInt
    var groupCount = attr(node, "group-count", "-1").toInt
    var groupInvert = attr(node, "group-invert", "false").toBoolean
    
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
    if (groupSize <= 1) {
    	
    	CollectionBinding.bind(collection, eltBinding, eltBinding, listPrefix, node, context)
    	
  	} else {

      // pad collection with null values
      val totalSize = groupCount * groupSize
      val padding = totalSize - collection.size
      val array = if (padding > 0) collection.toSeq.padTo(padding, null) else collection.toSeq
      
      // partition in groups
      val groups = if (!groupInvert) {
        for (i <- 0 until groupCount; start = i * groupSize) 
          yield array.slice(start, start + groupSize)
      } else {
        for (i <- 0 until groupCount) 
          yield for (j <- i until(totalSize, groupCount)) 
            yield(array(j))
      }
      
      val groupBinding = (_ : Node) => (elt : Any) => new CollectionGroupBinding(elt.asInstanceOf[Collection[Any]], eltBinding)
      CollectionBinding.bind(groups, eltBinding, groupBinding, listPrefix, node, context)
    }
    
  }  
}

class GroupedCollectionBinding(f : => Collection[Collection[Any]], eltBinding : Node => Any => Binding) extends Binding {

  override def bind(node : Node, context: BindingContext) : NodeSeq = {

    // get the collection
    var collection = f
    var size = collection.size
    val listPrefix = attr(node, "list-prefix", "list")
    
    // determine groups
    var groupInvert = attr(node, "group-invert", "false").toBoolean
    
    if (groupInvert) {
      val groupCount = collection.foldLeft(0)((x,y) => Math.max(x, y.size))
      val totalSize = groupCount * size
      val padding = totalSize - collection.size
      val array = if (padding > 0) collection.toSeq ++ Array.make(padding, null) else collection.toSeq
      collection =  for (i <- 0 until groupCount) 
          yield for (j <- i until(totalSize, groupCount)) 
            yield(array(j))
    }
    
    val groupBinding = (node : Node) => (elt : Any) => new CollectionGroupBinding(elt.asInstanceOf[Collection[Any]], eltBinding)
    CollectionBinding.bind(collection, _ => _ => Binding.ident, groupBinding, listPrefix, node, context)
  }  
}

object EmptyBindings {
  val bindings = Bindings(None, Map(
  "first" -> Binding.empty,
  "last" -> Binding.empty,
  "even" -> Binding.ident,
  "odd" -> Binding.empty,
  "skip-first" -> Binding.empty,
  "skip-last" -> Binding.empty,
  "once" -> Binding.empty,
  "none" -> Binding.empty,
  "single" -> Binding.empty,
  "plural" -> Binding.ident,
  "index" -> new XmlBinding(_ => NodeSeq.Empty),
  "size" -> new XmlBinding(_ => new Text("0")),
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

class CollectionGroupBinding(collection : Collection[Any], eltBinding : Node => Any => Binding) extends Binding {
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
  
  val context = BindingContext(null, Map(bindings:_*), Flag())
  
  def componentBindings = website.components map (component => (component.prefix, Bindings(Some(component), cache(component))))

  def bindings = componentBindings ++ Map("object" -> Bindings(None, Map("none" -> Binding.empty)))
  
  def bind(xml : NodeSeq) : NodeSeq = {
    BindingContext.current.set((context, RootBinding.emptyElem))
    Binding.bind(xml, context)
  }
}

object BindingContext {
  val current = new ThreadLocal[(BindingContext, Elem)]
}

case class BindingContext(parent : BindingContext, bindings : Map[String,Bindings], repeatSeen : Flag) {
  def + (binding : (String, Bindings)) = BindingContext(this, bindings + binding, repeatSeen)
  def initRepeatSeen = BindingContext(this, bindings, Flag())
}

class BindingCache(website : Website) {
  
  def apply(obj : Any) : Map[String,Binding] = Bindings.get(obj)
  
  private val Bindings : ConcurrentMap[Any,Map[String,Binding]] = 
    new com.google.common.collect.MapMaker().concurrencyLevel(32).weakKeys().makeComputingMap[Any,Map[String,Binding]](
       new com.google.common.base.Function[Any,Map[String,Binding]] {
         def apply(obj : Any) : Map[String,Binding] = {
           website.bindings findFirst(obj) getOrElse Map()
         }});
}
