package claro.cms

//import scala.xml.{NodeSeq,Text}
//import Cms.{Namespace,Tag}
//import net.liftweb.util.BindHelpers
//import net.liftweb.http.S
//import net.liftweb.util.BindHelpers.{AttrBindParam,FuncAttrBindParam,BindParam,TheBindParam,TheStrBindParam,FuncBindParam}
//import java.util.concurrent.ConcurrentHashMap
//
//object Attr {
//  def apply(f : String => String) = new Attr(f)
//  def apply(f : => String) = new Attr(_ => f)
////  def apply(f : => String) = new Attr(_ => Some(f))
//  def apply(value : => Boolean) = new FunAttr( (name,xml) => {
//    val input = xml.toString.trim
//    val index = input.indexOf('=')
//    NodeSeq.Empty
//  })
//}
//
//class Attr (f : String => String) {
//  def calcValue(xml : NodeSeq) = Text(f(xml.toString))
//}
//
//class FunAttr (f : (String,NodeSeq) => NodeSeq) {
//  def calcValue(xml : NodeSeq) = xml
//}
//
//case class Bindings(val bindings : Binding*) {
//  override def toString = "Bindings(" + (bindings mkString (",")) + ")"
//}
//
//class BindingCtor(name : Tag) {
//  def -> (attr : Attr) = new AttrBindingCtor2(name, attr) 
//  def -> (attr : FunAttr) = new FunAttrBindingCtor2(name, attr) 
//  def -> (f : => NodeSeq) = new XmlBinding(name, f) 
//  def -> (f : NodeSeq => NodeSeq) = new XmlFunBinding(name, f) 
//  def -> (f : => ConcreteTemplate) = new TemplateBinding(name, f) 
//  def -> (f : => Bindings) = new ComplexBindingCtor2(name, f) 
//  def -> (f : => Collection[Any]) = new CollectionBindingCtor2(name, f) 
//  def -> (f : => Option[Any]) = new OptionBindingCtor2(name, f) 
//  def -> (f : => Any) = new BindingCtor2(name, f) 
//}
//
//class AttrBindingCtor2(name : Tag, attr : Attr) {
//  def -> (targetName : Tag) = new ParamBinding(FuncAttrBindParam(name, attr.calcValue(_), targetName))
//}
//
//class FunAttrBindingCtor2(name : Tag, attr : FunAttr) {
//	def -> (targetName : Tag) = new ParamBinding(FuncAttrBindParam(name, attr.calcValue(_), targetName))
//}
//
//class ComplexBindingCtor2(name : Tag, f : => Bindings) {
//  def -> (defaultNamespace : Namespace) = new ComplexBinding(name, f, defaultNamespace)
//  def toRootBinding = new ComplexRootBinding(f, name)
//  def toBinding = new ComplexBinding(name, f, "") // tag attribute obliged!
//}
//
//class CollectionBindingCtor2[A](name : Tag, f : => Collection[A]) {
//  def -> (defaultNamespace : Namespace) = new ObjectCollectionBinding(name, f, defaultNamespace)  
//  def toBinding = new CollectionBinding(name, f)
//}
//
//class OptionBindingCtor2(name : Tag, f : => Option[Any]) {
//  def -> (defaultNamespace : Namespace) = new ObjectBinding(name, f getOrElse null, defaultNamespace)  
//  def toRootBinding = new ObjectRootBinding(f getOrElse null, name)
//  def toBinding = new ExprBinding(name, f getOrElse null)
//  def toTuple = (name,f)
//}
//
//class BindingCtor2(name : Tag, f : => Any) {
//  def -> (defaultNamespace : Namespace) = new ObjectBinding(name, f, defaultNamespace)  
//  def toRootBinding = new ObjectRootBinding(f, name)
//  def toBinding = new ExprBinding(name, f)
//  def toTuple = (name,f)
//}
//
//trait RootBinding {
//  def createBinder : Binder
//}
//
//class ComplexRootBinding(bindings : Bindings, namespace : Namespace) extends RootBinding {
//  def createBinder = new ComplexBinder(bindings, namespace)
//}
//
//class ObjectRootBinding(expr : => Any, namespace : Namespace) extends RootBinding {
//  def createBinder = {
//    if (expr != null) new ObjectBinder(expr, namespace)
//    else NullBinder
//  }
//}
//
//trait Binding {
//  def name : Tag
//  def param : BindParam 
//  override def toString = name
//}
//
//class ParamBinding(p : BindParam) extends Binding {
//  def name = param.name
//  def param = p
//}
//
//class AttrBinding(param : AttrBindParam) extends ParamBinding(param) {
//}
//
//class XmlBinding(val name : Tag, f : => NodeSeq) extends Binding {
//  def param = FuncBindParam(name, xml => { 
//    new XmlBinder(_ => f).bindAll(xml)
//  })
//}
//
//class TemplateBinding(val name : Tag, f : => ConcreteTemplate) extends Binding {
//  def param = FuncBindParam(name, new TemplateBinder(f).bindAll(_))
//} 
//
//class XmlFunBinding(val name : Tag, f : NodeSeq => NodeSeq) extends Binding {
//  def param = FuncBindParam(name, new XmlBinder(f).bindAll(_))
//}
//
//class ExprBinding(val name : Tag, expr : => Any) extends Binding {
//  def param = FuncBindParam(name, { xml => 
//    val value : Any = expr
//    if (value == null) NodeSeq.Empty
//    else Text(value.toString)
//  })
//}
//
//class ComplexBinding(val name : Tag, f : => Bindings, defaultNamespace : Namespace) extends Binding {
//  def param = {
//    FuncBindParam(name, xml => new ComplexBinder(f, defaultNamespace).bindAll(xml))
//  }
//}
//
//class ObjectBinding(val name : Tag, expr : => Any, defaultNamespace : Namespace) extends Binding {
//  def param = {
//    val value : Any = expr
//    if (value != null) FuncBindParam(name, xml => new ObjectBinder(expr, defaultNamespace).bindAll(xml))
//    else TheBindParam(name, NodeSeq.Empty)
//  }
//}
//
//class CollectionBinding(val name : Tag, expr : => Collection[Any]) extends Binding {
//  def param = FuncBindParam(name, xml => new CollectionBinder(expr, 
//		  obj => new StrBinder(obj.toString)).bindAll(xml))
//}
//
//class ObjectCollectionBinding(val name : Tag, expr : => Collection[Any], defaultNamespace : Namespace) extends Binding {
//  def param = FuncBindParam(name, xml => new CollectionBinder(expr, 
//		  new ObjectBinder(_, defaultNamespace)).bindAll(xml))
//}
//
//object BindAttr {
//  def apply(name : String) : String = 
//	BindHelpers.attr(name) match { 
//      case Some(attr) => attr.toString 
//      case None => error("Missing required tag: " + name)
//    }
//
//  def apply(name : String, default : => String) : String = 
//	  BindHelpers.attr(name) match { 
//	  case Some(attr) => attr.toString 
//	  case None => default
//  }
//}
//
//object IfAttr {
//  def apply[A](name : String, yes : => A, no : => A) = 
//    BindHelpers.attr(name) match { 
//      case Some(attr) => if (attr.toString.toLowerCase == "yes") yes else no 
//      case None => no
//    }
//}
//
//object BindingCache {
//    // TODO: Use google maps mapmaker to have weak keys
//  private val objectBindingCache = new ConcurrentHashMap[Any,Bindings]()
//  
//  // Don't care that method is not thread safe: the same binding might be
//  // created multiple times, which only impacts performance, and only slightly.
//  private[cms] def findObjectBindings(obj : Any) = {
//    var result = objectBindingCache.get(obj)
//    if (result == null) {
//      result = Cms.objectBindings.toList.find (_.isDefinedAt(obj)) match {
//		case Some(objectBindings) => objectBindings(obj)
//		case None => Bindings()
//      }
//      objectBindingCache.put(obj, result)
//    }
//    result
//  }
//}