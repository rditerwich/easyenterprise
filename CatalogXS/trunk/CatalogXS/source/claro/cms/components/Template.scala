package claro.cms.components

import java.util.Locale
import xml.{Node,NodeSeq,Text}
import net.liftweb.util.{ThreadGlobal}
import claro.cms.Conversions._

class TemplateComponent extends Component {
  
  val prefix = "template"
  
  val RootTemplateMap = new TemplateMap(null, NodeSeq.Empty)

  bindings.append {
    case _ : TemplateComponent => Map(
      "include" -> "null"
    )
  }
  
  def incl : NodeSeq = {
    val template = @@("template")
//    Binder._currentBinder.value.findTemplate(template) match {
//      case Some(xml) => xml
//      case None => Text("Error: Template not found: " + template)
//    }
  NodeSeq.Empty
  }
  
  def extractNestedTemplates(xml : NodeSeq) = {
    val roots = xml filter (node => {println(node.prefix);node.prefix == "template" && node.label == "define"})
    val result = Map(roots map (node => (node.attribute("template"), node.child)):_*)
    println("NESTED:" + result)
  } 
  
//  def includeTemplate : ConcreteTemplate  = {
//    val template = @@("template")
//    Cms.site.templateCache(Template(template), Cms.locale.get) match {
//      case Some(template) => template
//      case None => ConcreteTemplate("template-not-found", Scope(), Locale.getDefault, Text("Error: Template not found: " + template)) 
//    }
//  }
}

class TemplateMap(parent : TemplateMap, xml : NodeSeq) {
  def roots = xml filter (isNested(_))
  val nested = Map(roots map (node => (node.attribute("template") getOrElse (""), node.child)):_*)
  def contents = xml filter (!isNested(_))
  def findNestedTemplate(name : String) : NodeSeq = nested.get(name) match {
    case Some(xml) => xml
    case None => if (parent != null) parent.findNestedTemplate(name) else Text("Error: Template not found: " + name)
  }
  private def isNested(node : Node) = node.prefix == "template" && node.label == "define"
  
}
