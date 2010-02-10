package agilexs.catalogxs.presentation.model

import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers.BindParam
import net.liftweb.util.BindHelpers.FuncBindParam
import scala.xml.NodeSeq 

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
  def apply[A](f : => Binding[A]) = new Complex(f)
  def apply(f : => Iterable[Binding[_]]) = new ComplexList(f)
}

class Complex(f : => Binding[_]) extends Function2[String, NodeSeq, NodeSeq] {
  override def apply(tag : String, xml : NodeSeq) : NodeSeq = f.bind(tag, xml)
}

class ComplexList(f : => Iterable[Binding[_]]) extends Function2[String, NodeSeq, NodeSeq] {
  override def apply(tag : String, xml : NodeSeq) : NodeSeq = f.toSeq flatMap (_ bind(tag, xml))
}

case class Binding[A](obj : Object, params : BindParam*)  {
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
  
