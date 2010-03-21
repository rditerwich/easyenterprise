package claro.cms

import scala.xml.{NodeSeq,Text}
import net.liftweb.util.BindHelpers
import net.liftweb.http.S

import claro.cms.Conversions._

object Calc {
  def apply(f : NodeSeq => NodeSeq) = f
}

object CmsComponent {

  def boot = {
    CMS.bindings.append("cms" -> CmsComponent)
    CMS.objectBindings.append {
      case CmsComponent => Bindings(
        "match-url" -> matchUrl _,
        "match-url" -> Attr(matchUrl(NodeSeq Empty)) -> "matched"
      )
    }
  }
  
  def matchUrl(xml : NodeSeq) = {
	BindHelpers.attr("prefix") match { 
      case Some(attr) => Text(S.request.toString) 
      case None => Text("NO PREFIX ATTR")
    }
  } 
}


class CmsComponent {
  
}