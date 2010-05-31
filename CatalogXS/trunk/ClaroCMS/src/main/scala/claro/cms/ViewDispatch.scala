package claro.cms

import java.util.Locale
import net.liftweb.http.LiftRules
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.{Log}
import xml.NodeSeq

object ViewDispatch extends LiftRules.ViewDispatchPF {
  
  def isDefinedAt(path : List[String]): Boolean = Cms.website != null
  def apply(path : List[String]) = viewDispatch(Request.get, path)
  
  private def viewDispatch(request : Request, rawPath : List[String]) = {
    val tempPath = rawPath.drop(request.contextPath.size) match {
      case Nil => "index" :: Nil
      case path => path
    }
    val path = Cms.website.rewrite.foldLeft(tempPath)((b, a) => a(b)) 
    if (path != Nil) {
      val template = Template(path)
      Cms.website.templateCache(template, request.locale) match {
        case Some(template) => 
          request.template = Some(template)
          request.path = path
          Left(() => render(Cms.website, rawPath, template))
        case None => 
          Log.info("No template found for path: " + rawPath.mkString("/", "/", ""))
          Left(() => Empty)
      }
    } else Left(() => Empty)
  }

  private def render(website : Website, rawPath : List[String], template : ConcreteTemplate) : Box[NodeSeq] = {
    val result = Cms.website.rootBinding.bind(template.xml) 
    /*match {
      case xml if (xml.first.label == "html") => Full(xml)
      case _ => Empty
    }*/
    Log.info("Rendered template: " + template.resource.uri + " for path: " + rawPath.mkString("/", "/", ""))
    Full(result)
  }
}
