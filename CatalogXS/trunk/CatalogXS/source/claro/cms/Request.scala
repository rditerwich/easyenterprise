package claro.cms

import java.util.Locale
import net.liftweb.http.{RequestVar,SessionVar}
import net.liftweb.util.{Box,Full,Empty,Log}
import javax.servlet.http.HttpServletRequest
import claro.common.util.Conversions._

object Request extends RequestVar[Request](new Request) {

  var lastLocalhostServer = ""
  
  def httpRequest = get.httpRequest
  def website = get.website
  def template = get.template
  def path = get.path
  
  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
    val (server, caching) = httpRequest.getServerName match {
      case "localhost" => httpRequest.getParameter("server") match {
        case null => (lastLocalhostServer, false)
        case server => 
          lastLocalhostServer = server
          (server, httpRequest.getParameter("caching") != null)
      }
      case server => (server, true)
    }
    val path = httpRequest.getServletPath
    if (path.startsWith("/classpath/") || path.startsWith("/ajax_request/")) {
//      return Empty
    }
    Website.findWebsite(server, path) match {
      case Some(website) => 
        val request = Request.is
        request.httpRequest = httpRequest 
        request.website = website
        website.caching = caching
        website.contextPath match {
          case "/" => Empty
          case contextPath => Full(contextPath)
        }
      case None => Empty
    }
  }
}

class Request {
  var httpRequest : HttpServletRequest = null
  var website : Website = null
  var template : Option[ConcreteTemplate] = None
  var path : List[String] = Nil
}

