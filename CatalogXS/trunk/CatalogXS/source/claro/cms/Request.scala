package claro.cms

import java.util.Locale
import net.liftweb.http.{RequestVar}
import net.liftweb.util.{Box,Full,Empty,Log}
import javax.servlet.http.HttpServletRequest

object Request extends RequestVar[Request](new Request) {
  
  def httpRequest = get.httpRequest
  def website = get.website
  def template = get.template
  def path = get.path
//  def pathTail = get.pathTail
  
  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
    Website.findWebsite(httpRequest.getServerName, httpRequest.getServletPath) match {
      case Some(website) => 
        val request = Request.is
        request.httpRequest = httpRequest 
        request.website = website
        Full(website.contextPath)
      case None => Empty
    }
  }
}

class Request {
  var httpRequest : HttpServletRequest = null
  var website : Website = null
  var template : Option[ConcreteTemplate] = None
  var path : List[String] = Nil
//  var pathTail : List[String] = Nil
}

