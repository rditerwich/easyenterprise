package agilexs.catalogxs.presentation.model

import net.liftweb.http.SHtml
import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers.BindParam
import net.liftweb.util.BindHelpers.FuncBindParam
import net.liftweb.util.Bindable
import net.liftweb.util.Full
import scala.xml.{NodeSeq, Text} 
import scala.collection.mutable 
import agilexs.catalogxs.jpa.{catalog => jpa}

object BindAttr {
  def apply(name : String) : String = 
	BindHelpers.attr(name) match { 
      case Some(attr) => attr.toString 
      case None => error("Missing required tag: " + name)
    }

  def apply(name : String, default : => String) : String = 
	  BindHelpers.attr(name) match { 
	  case Some(attr) => attr.toString 
	  case None => default
  }
}

object Complex {
  def apply(f : => Binding) = new Complex(f)
  def apply(f : => Iterable[Binding]) = new ComplexList(f)
}

class Complex(f : => Binding) extends Function2[String, NodeSeq, NodeSeq] {
  override def apply(tag : String, xml : NodeSeq) : NodeSeq = f.bind(tag, xml)
}

class ComplexList(f : => Iterable[Binding]) extends Function2[String, NodeSeq, NodeSeq] {
  override def apply(tag : String, xml : NodeSeq) : NodeSeq = f.toSeq flatMap (_ bind(tag, xml))
}

object Value {
  def apply(property : => Option[Property]) = new Value(property)
  def apply(property : => Property) = new Value2(property)
}

class Value(property : => Option[Property]) extends Bindable {
  override def asHtml = property match { 
    case Some(property) => property.propertyType match {
      case jpa.PropertyType.Media => 
        if (property.mediaValue == null) 
          Text("")
	    else if (property.mimeType.startsWith("image/")) 
		  Text("<img src=\"image/"+ property.valueId + "\" />")
	     else
  		   Text(property.mediaValue.toString());
      case _ => Text(property.valueAsString)
    }
    case None => Text("")
  }
}

class Value2(property : => Property) extends Value(if (property != null) Some(property) else None) {
}

object Link {
  def apply(group : ProductGroup) = (xml : NodeSeq) => <a href={"/group/" + group.id}>{xml}</a>
  def apply(product : Product) = (xml : NodeSeq) => <a href={"/product/" + product.id}>{xml}</a>
}

class BindableObject(obj : Object) {
  def bindWith(params : => Seq[BindParam]) = if (obj != null) new Binding(obj, params) else NullBinding
}

object params {
	def apply(params : BindParam*) : Seq[BindParam] = params
}

class Binding(obj : Object, params : Seq[BindParam])  {
  def bind(tag : String, xml: NodeSeq) = {
    val actualTag = BindAttr("tag", tag)
    val template = determineTemplate(obj, xml)
    val parent = (xml: NodeSeq) => BindHelpers.bind(actualTag, xml, params:_*)
    val paramsWithParent = params.map(addParentBindings(_, parent)) 
    BindHelpers.bind(actualTag, template, paramsWithParent:_*)
  } 
  
  private def addParentBindings(param : BindParam, parent : NodeSeq => NodeSeq) = {
    param match {
      case param : FuncBindParam => 
        FuncBindParam(param.name, xml => param.calcValue(parent(xml))) 
      case _ => param
    }
  }

  private def determineTemplate(obj : Object, default : NodeSeq) : NodeSeq = {
	BindHelpers.attr("template") match { 
      case Some(explicitTag) => 
        Model.catalog.cache.template(obj, explicitTag.toString) match {
          case Some(xml) => xml
          case None => default
        } 
      case None => default
    }
  }
}

object NullBinding extends Binding(null, null) {
  override def bind(tag : String, xml: NodeSeq) = NodeSeq.Empty
}
  
