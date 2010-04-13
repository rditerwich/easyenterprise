package claro.cms

import java.util.Locale
import net.liftweb.http.{RequestVar}
import javax.servlet.http.HttpServletRequest

object Request extends RequestVar[Request](new Request) {
  
  def httpRequest = get.httpRequest
  def site = get.site
  def template = get.template
  def path = get.path
  def pathTail = get.pathTail
}

class Request {
  var httpRequest : HttpServletRequest = null
  var site : Site = null
  var template : Option[ConcreteTemplate] = None
  var path : List[String] = Nil
  var pathTail : List[String] = Nil
}

