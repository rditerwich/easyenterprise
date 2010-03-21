package claro.cms

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers.{BindParam,TheBindParam,TheStrBindParam,IntBindParam,FuncBindParam}

import CMS.{Namespace,Tag}

trait Binder {
  val parent : Binder
  def bind(current : Binder, xml : NodeSeq) : NodeSeq
  def bindAll(current : Binder, xml : NodeSeq) : NodeSeq = parent.bindAll(current, bind(current, xml)) 
}

trait ConstBinder extends Binder {
  val parent = null
  override def bindAll(current : Binder, xml : NodeSeq) : NodeSeq = bind(current, xml)
}

object NullBinder extends ConstBinder {
  def bind(current : Binder, xml : NodeSeq) : NodeSeq = NodeSeq.Empty
}

class StrBinder(s : String) extends ConstBinder {
  def bind(current : Binder, xml : NodeSeq) : NodeSeq = Text(s)
}

/**
 * D
 */
class XmlBinder(val parent : Binder, f : NodeSeq => NodeSeq) extends Binder {
  def bind(current : Binder, xml : NodeSeq) : NodeSeq = f(xml)
}

class ComplexBinder(val parent : Binder, bindings : Bindings, defaultNamespace : Namespace) extends Binder {

  val namespace = BindAttr("tag", defaultNamespace)

  val params = bindings.bindings map (_.param(this) )

  def bind(current : Binder, xml : NodeSeq) : NodeSeq = {
	BindHelpers.bind(namespace, xml, params:_*)
  }
}

class ObjectBinder(parent : Binder, obj : Any, defaultNamespace : Namespace) 
	extends ComplexBinder(parent, Binding.findObjectBindings(obj), defaultNamespace) {
}
	  
class CollectionBinder(val parent : Binder, objs : Collection[Any], objBinder : Any => Binder) extends Binder {

  val namespace = BindAttr("list-tag", "list")

  val iterator = objs.elements
  val size = objs.size
  var index = 0
  val binders = new Array[Binder](if (size == 0) 1 else size)
  if (!iterator.hasNext) {
    binders(0) = new CollectionNoItemsBinder(parent, namespace)
  }
  while (iterator.hasNext) {
    val obj = iterator.next
    binders(index) = new CollectionItemBinder(parent, namespace, index, size, objBinder(obj))
    index += 1
  }

  def bind(current : Binder, xml : NodeSeq) : NodeSeq = {
    binders flatMap (_.bindAll(current, xml))
  }
}

object CollectionItemBinder {
  val first = (new XmlFunBinding("first", xml => xml), TheBindParam("first", NodeSeq.Empty))
  val skipFirst = (new XmlFunBinding("skip-first", xml => xml), TheBindParam("skip-first", NodeSeq.Empty))
  val last = (new XmlFunBinding("last", xml => xml), TheBindParam("last", NodeSeq.Empty))
  val skipLast = (new XmlFunBinding("skip-last", xml => xml), TheBindParam("skip-last", NodeSeq.Empty))
  val once = (new XmlFunBinding("once", xml => xml), TheBindParam("once", NodeSeq.Empty))
  val single = (new XmlFunBinding("single", xml => xml), TheBindParam("single", NodeSeq.Empty))
  val plural = (new XmlFunBinding("plural", xml => xml), TheBindParam("plural", NodeSeq.Empty))
}

class CollectionNoItemsBinder(val parent : Binder, namespace : String) extends Binder {
  import CollectionItemBinder._
  val params = List(
    first._2,
    last._2, 
    skipFirst._2, 
    skipLast._2,
    once._1.param(this),
    single._2,
    plural._1.param(this),
    TheStrBindParam("index", ""),
    IntBindParam("size", 0)
  ) 
  def bind(current : Binder, xml : NodeSeq) : NodeSeq = {
    BindHelpers.bind(namespace, xml, params:_*)
  }
}

class CollectionItemBinder(val parent : Binder, namespace : String, index : Int, size : Int, objBinder : Binder) extends Binder {
  import CollectionItemBinder._
  val params = List(
    if (index == 0) first._1.param(this) else first._2,
    if (index >= size - 1) last._1.param(this) else last._2, 
    if (index > 0) skipFirst._1.param(this) else skipFirst._2, 
    if (index < size - 1) skipLast._1.param(this) else skipLast._2,
    if (index == 0) once._1.param(this) else once._2,
    if (size == 1) single._1.param(this) else single._2,
    if (size != 1) plural._1.param(this) else plural._2,
    IntBindParam("index", index + 1),
    IntBindParam("size", size)
  ) 
  def bind(current : Binder, xml : NodeSeq) : NodeSeq = {
    BindHelpers.bind(namespace, objBinder.bind(current, xml), params:_*)
  }
}


object SuperRootBinder extends Binder {
  val parent = null
  def bind(current : Binder, xml : NodeSeq) : NodeSeq = {
    var result = xml
    var parent : Binder = this
    for (binding <- CMS.bindings.toList) {
      parent = binding.createBinder(parent)
    }
    parent.bind(null,xml)

  }
  override def bindAll(current : Binder, xml : NodeSeq) : NodeSeq = xml
}
