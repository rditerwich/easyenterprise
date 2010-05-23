package claro.cms

import java.util.Locale
import net.liftweb.http.{RequestVar,SessionVar}
import net.liftweb.util.{Box,Full,Empty,Log}
import javax.servlet.http.HttpServletRequest
import claro.common.util.Locales
import claro.common.util.Conversions._

object Request extends RequestVar[Request](new Request) {

  private val defaultLocale = Locale.getDefault
  
  var lastLocalhostServer = ""
  
  private def parsePath(path : String) : List[String] = {
    var result : List[String] = Nil
    var end = path.length
    while (end >= 0) {
      val start = path.lastIndexOf('/', end - 1)
      if (start + 1 < end) {
        result = path.substring(start + 1, end) :: result
      }
      end = start
    }
    result
  }
  
  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
    val request = Request.is
    if (request.httpRequest == null) {
      request.httpRequest = httpRequest
      fillRequest(request)
    }
  }
   
  def fillRequest(request : Request)
    val servletPath = httpRequest.getServletPath
    val (server, path0, localhost) = httpRequest.getServerName match {
      case "localhost" => servletPath.indexOf('/', 1) match {
        case -1 => ("", servletPath, false)
        case i => (servletPath.substring(1, i), servletPath.substring(i), true)
      }
      case server => (server, servletPath, false)
    }
    val (path, locale) = parsePath(servletPath) match {
      case head :: tail => Locales.availableLocales.get(head) match {
        case Some(locale) => (tail, locale)
        case None => (head :: tail, defaultLocale)
      }
      case path => (path, defaultLocale)
    }
    Website.findWebsite(server, path) match {
      case Some(website) => 
        val request = Request.is
        website.caching = false
        request.httpRequest = httpRequest 
        request.website = website
        request.locale = locale
        request.path = path
        if (localhost) {
          request.context = server :: website.path 
//          request.contextPath = "/" + server + (if (website.contextPath == "/") "" else website.contextPath)
          // TODO localhost not supported now
          Full(website.contextPath)
        } else {
          request.context = website.path 
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
  var website : Website = null
  var template : Option[ConcreteTemplate] = None
  var path : List[String] = Nil
  var locale : Locale = null
}

