package claro.cms

import java.util.Locale
import net.liftweb.http.LiftRules
import net.liftweb.util.{Box,Full,Empty,Log}
import xml.NodeSeq

object ViewDispatch extends LiftRules.ViewDispatchPF {
  
  def isDefinedAt(path : List[String]): Boolean = Request.website != null
  def apply(path : List[String]) = viewDispatch(Request.website, path)
  
  private def viewDispatch(website : Website, rawPath : List[String]) = {
    val tempPath = rawPath.drop(website.path.size) match {
      case Nil => "index" :: Nil
      case path => path
    }
    val path = website.rewrite.foldLeft(tempPath)((b, a) => a(b)) 
    if (path != Nil) {
      val template = Template(path)
      val locale = Locale.getDefault
      website.templateCache(template, locale) match {
        case Some(template) => 
          val request = Request.is
          request.template = Some(template)
          request.path = path
          Left(() => render(website, rawPath, template))
        case None => 
          Log.info("No template found for path: " + rawPath.mkString("/", "/", ""))
          Left(() => Empty)
      }
    } else Left(() => Empty)
  }

  private def render(website : Website, rawPath : List[String], template : ConcreteTemplate) : Box[NodeSeq] = {
    val result = Request.website.rootBinding.bind(template.xml) 
    /*match {
      case xml if (xml.first.label == "html") => Full(xml)
      case _ => Empty
    }*/
    Log.info("Rendered template: " + template.resource.uri + " for path: " + rawPath.mkString("/", "/", ""))
    Full(result)
  }
}
