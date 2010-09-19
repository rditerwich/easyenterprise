package claro.cms

import java.util.Locale
import net.liftweb.http.LiftRules
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.{Log}
import xml.NodeSeq
import claro.common.util.Locales

object ViewDispatch extends LiftRules.ViewDispatchPF {
  
  def isDefinedAt(path : List[String]): Boolean = true
 
  def apply(path0 : List[String]) = {
  
    val website = Website.instance
    var path = path0
    var locale = Cms.locale.is 
      
    // default path
    if (path.isEmpty) 
      path = "index" :: Nil 
    
    // extract locale
    if (website.config.locales.contains(path.head)) {
      locale = Locales(path.head)
      path = path.tail
      Cms.locale.set(locale);
    }
    
    // component rewrite
    path = Website.instance.rewrite.foldLeft(path)((b, a) => a(b)) 

    if (path != Nil) {
      val template = Template(path)
      Website.instance.templateCache(template, locale) match {
        case Some(template) => 
          Left(() => render(Website.instance, path0, template))
        case None => 
          Log.info("No template found for path: " + path0.mkString("/", "/", ""))
          Left(() => Empty)
      }
    } else Left(() => Empty)
  }

  private def render(website : Website, path0 : List[String], template : ConcreteTemplate) : Box[NodeSeq] = {
    val result = Website.instance.rootBinding.bind(template.xml) 
    /*match {
      case xml if (xml.first.label == "html") => Full(xml)
      case _ => Empty
    }*/
//    Log.info("Rendered template: " + template.resource.uri + " for path: " + path0.mkString("/", "/", ""))
    Full(Postprocessor.postprocess(result))
  }
}
