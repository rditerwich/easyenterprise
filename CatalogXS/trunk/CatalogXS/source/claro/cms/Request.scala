package claro.cms

import java.util.Locale
import net.liftweb.http.{RequestVar,SessionVar}
import net.liftweb.util.{Box,Full,Empty,Log}
import javax.servlet.http.HttpServletRequest
import claro.common.util.Conversions._

object Request extends RequestVar[Request](new Request) {

  var lastLocalhostServer = ""
  
  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
    val servletPath = httpRequest.getServletPath
    val (server, path, localhost) = httpRequest.getServerName match {
      case "localhost" => servletPath.indexOf('/', 1) match {
        case -1 => ("", servletPath, false)
        case i => (servletPath.substring(1, i), servletPath.substring(i), true)
      }
      case server => (server, servletPath, false)
    }
     
    Website.findWebsite(server, path) match {
      case Some(website) => 
        val request = Request.is
        website.caching = !localhost
        request.httpRequest = httpRequest 
        request.website = website
        if (localhost) {
          request.context = server :: website.path 
          request.contextPath = "/" + server + (if (website.contextPath == "/") "" else website.contextPath)  
          Full(request.contextPath)
        } else {
          request.context = website.path 
          request.contextPath = website.contextPath 
          website.contextPath match {
            case "/" => Empty
            case contextPath => Full(contextPath)
          }
        }
      case None => Empty
    }
  }
}

class Request {
  var httpRequest : HttpServletRequest = null
  var context = List[String]()
  var contextPath = ""
  var website : Website = null
  var template : Option[ConcreteTemplate] = None
  var path : List[String] = Nil
}

