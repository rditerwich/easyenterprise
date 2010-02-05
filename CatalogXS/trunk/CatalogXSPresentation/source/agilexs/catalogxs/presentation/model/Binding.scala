package agilexs.catalogxs.presentation.model

import net.liftweb.util.BindHelpers
import net.liftweb.util.BindHelpers.BindParam
import net.liftweb.util.BindHelpers.FuncBindParam
import scala.xml.NodeSeq 

case class Binding[A](obj : Object, params : BindParam*)  {
  def bind(tag : String, xml: NodeSeq) = {
	val actualTag = determineTag(tag)
	val template = determineTemplate(obj, xml)
	val parent = (xml: NodeSeq) => BindHelpers.bind(actualTag, xml, params:_*)
	val paramsWithParent = params.map(addParentBindings(_, parent)) 
	BindHelpers.bind(actualTag, xml, paramsWithParent:_*)
  } 
  
  private def addParentBindings(param : BindParam, parent : NodeSeq => NodeSeq) = {
    param match {
      case param : FuncBindParam => 
        FuncBindParam(param.name, xml => parent(param.calcValue(xml))) 
      case _ => param
    }
  }
  
  private def determineTag(default: String) = {
	BindHelpers.attr("tag") match { 
      case Some(explicitTag) => explicitTag.toString 
      case None => default
    }
  }

  private def determineTemplate(obj : Object, default : NodeSeq) = {
	BindHelpers.attr("template") match { 
      case Some(explicitTag) => 
        Model.catalogCache.template(obj, explicitTag.toString) 
      case None => default
    }
  }
}
  
