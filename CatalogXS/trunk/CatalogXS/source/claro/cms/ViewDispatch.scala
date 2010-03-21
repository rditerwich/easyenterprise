package claro.cms

import net.liftweb.http.LiftView
import net.liftweb.util.Full

object ViewDispatch {
  def unapply(path : List[String]) : Option[ConcreteTemplate] = {
    CMS.entryPoints.toList find (_.isDefinedAt(path)) match {
      case Some(entryPoint) => TemplateCache.findTemplate(entryPoint(path), CMS.locale.get) 
      case None => None
    }
  }
}
