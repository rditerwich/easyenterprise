package claro.cms

import java.util.Locale

import net.liftweb.http.{RequestVar,SessionVar,S,Req}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.{Log}
import javax.servlet.http.HttpServletRequest
import claro.common.util.Locales
import claro.common.util.Conversions._

//object Request extends RequestVar[Request](null) {
//
//  private object sessionVar extends SessionVar[Request](null)
//  private val defaultLocale = Locale.getDefault
//
//  def apply(req : Req) = {
//    if (req.uri.startsWith("/ajax_request/")) {
//      sessionVar.get
//    }
//    else {
//      var request = is
//      if (request == null)
//        request = new Request(req)
//        sessionVar.set(request)
//        set(request)
//    }
//  }
  
//  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
//    val r = S.request
//    val request = Request.is
//    if (request.httpRequest == null) {
//      request.fill(httpRequest)
//    }
//    request.context
//  }
   
//  def fillRequest(request : Request) = {
//    val servletPath = httpRequest.getServletPath
//    val (server, path0, localhost) = httpRequest.getServerName match {
//      case "localhost" => servletPath.indexOf('/', 1) match {
//        case -1 => ("", servletPath, false)
//        case i => (servletPath.substring(1, i), servletPath.substring(i), true)
//      }
//      case server => (server, servletPath, false)
//    }
//    val (path, locale) = parsePath(servletPath) match {
//      case head :: tail => Locales.availableLocales.get(head) match {
//        case Some(locale) => (tail, locale)
//        case None => (head :: tail, defaultLocale)
//      }
//      case path => (path, defaultLocale)
//    }
//    Website.findWebsite(server, path) match {
//      case Some(website) => 
//        val request = Request.is
//        website.caching = false
//        request.httpRequest = httpRequest 
//        Website.instance = website
//        request.locale = locale
//        request.path = path
//        if (localhost) {
//          request.context = server :: website.path 
////          request.contextPath = "/" + server + (if (website.contextPath == "/") "" else website.contextPath)
//          // TODO localhost not supported now
//          Full(website.contextPath)
//        } else {
//          request.context = website.path 
//          website.contextPath match {
//            case "/" => Empty
//            case contextPath => Full(contextPath)
//          }
//        }
//      case None => Empty
//    }
//  }
//}
//
//class Request(val req : Req) {
//  var contextPath = List[String]()
//  var website : Website = null
//  var template : Option[ConcreteTemplate] = None
//  var path : List[String] = Nil
//  var suffix = ""
//  var locale : Locale = Request.defaultLocale
//  var context : Box[String] = Empty
//
//  fill
//  def fill = {
//    
//    val servletPath = req.uri//getServletPath
//    var disableCaching = false
//  
//    // parse suffix
//    var end = servletPath.length
//    val start = servletPath.lastIndexOf('/', end - 1)
//    if (start + 1 < end) {
//      val dot = servletPath.lastIndexOf('.', end - 1)
//      if (dot >= start) {
//        suffix = servletPath.substring(dot + 1)
//        end = dot
//      }
//    }
//    
//    // parse path
//    while (end >= 0) {
//      val start = servletPath.lastIndexOf('/', end - 1)
//      if (start + 1 < end) {
//        path = servletPath.substring(start + 1, end) :: path
//      }
//      end = start
//    }
//
//    // parse server
//    val server = req.request.serverName match {
//      case "localhost" => path match {
//        case server :: rest => 
//          contextPath = server :: contextPath
//          disableCaching = true
//          path = rest
//          server
//        case _ => "" 
//      }
//      case server => server
//    }
//  
//    // parse locale
//    path match {
//      case localeString :: rest =>
//        Locales.availableLocales.get(localeString) match {
//          case Some(locale) => 
//            this.locale = locale
//            contextPath = localeString :: contextPath
//            path = rest
//          case None => 
//        }
//     case _ =>
//    }
//   
//    // find website
//    contextPath = contextPath.reverse
//    website = Website.find(server, contextPath) match {
//      case Some(website) =>
//        if (disableCaching) {
//          Cms.caching = false
//        }
//        website
//      case _ => null
//    }
//    
//    // context
//    context = 
//      if (contextPath.isEmpty) Empty
//      else Full(contextPath.mkString("/", "/", ""))
//  }
//}

