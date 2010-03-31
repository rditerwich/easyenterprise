package claro.cms

//import scala.xml.{NodeSeq,Text}
//import net.liftweb.util.{BindHelpers,ThreadGlobal}
//import net.liftweb.util.BindHelpers.{BindParam,TheBindParam,TheStrBindParam,IntBindParam,FuncBindParam}
//
//import Cms.{Namespace,Tag}
//
//object Binder {
//  val _currentBinder = new ThreadGlobal[Binder]
//}
//
//trait Binder {
//  val par = Binder._currentBinder.value
//  def bind(xml : NodeSeq) : NodeSeq 
//  
//  def bindAll(xml : NodeSeq) : NodeSeq = {
//    val current = Binder._currentBinder.value
//	Binder._currentBinder.doWith(this)(bindAll2(xml))
//  }
//  
//  def bindAll2(xml : NodeSeq) : NodeSeq = {
//    var result = xml
//    var p = this
//    while (p != null) {
//      result = p.bind(result)
//      p = p.par
//    }
//    result
//  }
//  
//  def findTemplate(name : String) : Option[NodeSeq] = par.findTemplate(name)
//}
//
//trait ConstBinder extends Binder {
//  val parent = null
//  override def bindAll(xml : NodeSeq) : NodeSeq = bind(xml)
//}
//
//object NullBinder extends ConstBinder {
//  def bind(xml : NodeSeq) : NodeSeq = xml
//}
//
//class StrBinder(s : String) extends ConstBinder {
//  def bind(xml : NodeSeq) : NodeSeq = Text(s)
//}
//
///**
// * D
// */
//class XmlBinder(f : NodeSeq => NodeSeq) extends Binder {
//  def bind(xml : NodeSeq) : NodeSeq = f(xml)
//}
//
//class TemplateBinder(template : ConcreteTemplate) extends XmlBinder(_ => template.xml) {
//  override def findTemplate(name : String) = template.nestedTemplates.get(name)
//}
//
//class ComplexBinder(val bindings : Bindings, defaultNamespace : Namespace) extends Binder {
//
//  val namespace = BindAttr("tag", defaultNamespace)
//  
//  val params = bindings.bindings map (_.param )
//
//  def bind(xml : NodeSeq) : NodeSeq = {
//    BindHelpers.bind(namespace, xml, params:_*)
//  }
//}
//
//class ObjectBinder(obj : Any, defaultNamespace : Namespace) extends ComplexBinder(BindingCache.findObjectBindings(obj), defaultNamespace) {
//}
// 
//class ObjectXMLBinder(obj : Any, f : NodeSeq => NodeSeq, defaultNamespace : Namespace) extends ObjectBinder(obj, defaultNamespace) {
//	override def bind(xml : NodeSeq) : NodeSeq = super.bind(f(xml))
//}
//	  
//class CollectionBinder(val objs : Collection[Any], objBinder : Any => Binder) extends Binder {
//
//  val namespace = BindAttr("list-tag", "list")
//
//  val iterator = objs.elements
//  val size = objs.size
//  var index = 0
//  val binders = new Array[Binder](if (size == 0) 1 else size)
//  if (!iterator.hasNext) {
//    binders(0) = new CollectionNoItemsBinder(namespace)
//  }
//  while (iterator.hasNext) {
//    val obj = iterator.next
//    binders(index) = new CollectionItemBinder(namespace, index, size, objBinder(obj))
//    index += 1
//  }
//
//  def bind(xml : NodeSeq) : NodeSeq = {
//    binders flatMap (_.bindAll(xml))
//  }
//}
//
//object CollectionItemBinder {
//  val first = (new XmlFunBinding("first", xml => xml), TheBindParam("first", NodeSeq.Empty))
//  val skipFirst = (new XmlFunBinding("skip-first", xml => xml), TheBindParam("skip-first", NodeSeq.Empty))
//  val last = (new XmlFunBinding("last", xml => xml), TheBindParam("last", NodeSeq.Empty))
//  val skipLast = (new XmlFunBinding("skip-last", xml => xml), TheBindParam("skip-last", NodeSeq.Empty))
//  val once = (new XmlFunBinding("once", xml => xml), TheBindParam("once", NodeSeq.Empty))
//  val single = (new XmlFunBinding("single", xml => xml), TheBindParam("single", NodeSeq.Empty))
//  val plural = (new XmlFunBinding("plural", xml => xml), TheBindParam("plural", NodeSeq.Empty))
//}
//
//class CollectionNoItemsBinder(val namespace : String) extends Binder {
//  import CollectionItemBinder._
//  val params = List(
//    first._2,
//    last._2, 
//    skipFirst._2, 
//    skipLast._2,
//    once._1.param,
//    single._2,
//    plural._1.param,
//    TheStrBindParam("index", ""),
//    IntBindParam("size", 0)
//  ) 
//  def bind(xml : NodeSeq) : NodeSeq = {
//    BindHelpers.bind(namespace, xml, params:_*)
//  }
//}
//
//class CollectionItemBinder(val namespace : String, index : Int, size : Int, objBinder : Binder) extends Binder {
//  import CollectionItemBinder._
//  val params = List(
//    if (index == 0) first._1.param else first._2,
//    if (index >= size - 1) last._1.param else last._2, 
//    if (index > 0) skipFirst._1.param else skipFirst._2, 
//    if (index < size - 1) skipLast._1.param else skipLast._2,
//    if (index == 0) once._1.param else once._2,
//    if (size == 1) single._1.param else single._2,
//    if (size != 1) plural._1.param else plural._2,
//    IntBindParam("index", index + 1),
//    IntBindParam("size", size)
//  ) 
//  def bind(xml : NodeSeq) : NodeSeq = {
//    BindHelpers.bind(namespace, objBinder.bind(xml), params:_*)
//  }
//}
//
//object RootBinder extends Binder {
//
//  override val par = null
//
//  def apply(xml : NodeSeq) : NodeSeq = {
//    var result = xml
//    Binder._currentBinder.doWith(this) {
//      var bindings = Cms.bindings.toList 
//      while (bindings != Nil) {
//        result = bindings.head.createBinder.bind(result)
//        bindings = bindings.tail
//      }
//      val cur = Binder._currentBinder.value
//      Binder._currentBinder.value.bindAll(xml)
//    }
//    result
//  }
////  
////  def rootBinders = Binder._currentBinder.doWith(this) {
////    Cms.bindings.toList map (_.createBinder)
////  }
////  
////  def bind(xml : NodeSeq) : NodeSeq = {
////    Binder._currentBinder.value.
////    for  
////      var result = xml
////      var binders = rootBinders
////      while (binders != Nil) {
////        result = binders.head.bind(result)
////        binders = binders.tail
////      }
////      result;
////  }
//  override def bind(xml : NodeSeq) : NodeSeq = xml
////  override def bindAll(xml : NodeSeq) : NodeSeq = xml
//  override def findTemplate(name : String) = {
//    Cms.site.templateCache.findTemplate(Template(name), Cms.locale.get) match {
//      case Some(template) => Some(template.xml)
//      case None => Some(Text("Error: Template not found: " + name)) 
//    }
//
//  }
//}
