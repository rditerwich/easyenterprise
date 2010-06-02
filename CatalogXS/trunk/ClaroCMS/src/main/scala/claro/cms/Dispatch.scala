package claro.cms

import java.util.Locale
import net.liftweb.http.{Req,LiftRules,LiftResponse,CSSResponse,StreamingResponse,InMemoryResponse,JavaScriptResponse,NotFoundResponse,ResourceServer}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.{CSSParser,Log}
import net.liftweb.util.TimeHelpers._
import claro.common.util.Conversions._

object Dispatch extends LiftRules.DispatchPF {

  val emptyResponse : () => Box[LiftResponse] = () => Empty
  
  def isDefinedAt(req : Req): Boolean = true
  
  def apply(req : Req) : () => Box[LiftResponse] = {
  
    // parse suffix
    val uri = req.uri 
    var suffix = ""
    var end = uri.length
    val start = uri.lastIndexOf('/', end - 1)
    if (start + 1 < end) {
      val dot = uri.lastIndexOf('.', end - 1)
      if (dot >= start) {
        suffix = uri.substring(dot + 1)
        end = dot
      }
    }
    
    // parse path
    var path : List[String] = Nil
    while (end >= 0) {
      val start = uri.lastIndexOf('/', end - 1)
      if (start + 1 < end) {
        path = uri.substring(start + 1, end) :: path
      }
      end = start
    }
    
    val locale = Cms.locale.is

    suffix match {
  	  case "css" => () => dispatchCSS(path, suffix, locale)
  	  case "js" => path match {
        case "classpath" :: rest => () => dispatchClasspathResource(path, suffix, locale, "text/javascript")
        case "ajax_request" :: "liftAjax" :: Nil => () => dispatchLiftAjax(path, suffix, locale)
        case _ => () => dispatchResource(path, suffix, locale, "text/javascript")
      }
  	  case "png" => () => dispatchResource(path, suffix, locale, "image/png")
  	  case "jpg" => () => dispatchResource(path, suffix, locale, "image/jpg")
  	  case "gif" => () => dispatchResource(path, suffix, locale, "image/gif")
  	  case "pdf" => () => dispatchLarge(path, suffix, locale, "application/pdf")
      case _ => emptyResponse
  	}
  }

  private def dispatchLiftAjax(path : List[String], suffix : String, locale : String) : Box[LiftResponse] = {
    val script = net.liftweb.http.js.ScriptRenderer.ajaxScript
    val modTime = System.currentTimeMillis
    Full(JavaScriptResponse(script,
                            List("Last-Modified" -> toInternetDate(modTime),
                            "Expires" -> toInternetDate(modTime + 60.minutes)),
                            Nil, 200))
  }
  
  private def dispatchClasspathResource(path : List[String], suffix : String, locale : String, mimeType : String) : Box[LiftResponse] = {
    val name = ResourceServer.baseResourceLocation + path.drop(1).mkString("/", "/", "") + "." + suffix
    def is = getClass.getClassLoader.getResourceAsStream(name)
    try {
      val bytes = is.readBytes
      val headers = List(
          "Cache-Control" -> "public, max-age=3600", 
          "Pragma" -> "public", 
          "Content-Length" -> bytes.length.toString,
          "Content-Type" -> mimeType) 
      Full(InMemoryResponse(bytes, headers, Nil, 200))
    } catch {
      case _ => Full(NotFoundResponse())
    }
  }
  
  private def dispatchResource(path : List[String], suffix : String, locale : String, mimeType : String) : Box[LiftResponse] = {
      val locator = ResourceLocator(path, suffix, List(Scope.global))
      Website.instance.resourceCache(locator, locale) match {
      case Some(resource) => 
      val bytes = Website.instance.contentCache(resource)
      val headers = List(
          "Cache-Control" -> "public, max-age=3600", 
          "Pragma" -> "public", 
          "Content-Length" -> bytes.length.toString,
          "Content-Type" -> mimeType) 
          Full(InMemoryResponse(bytes, headers, Nil, 200))
      case None => Empty
      }
  }
  
  private def dispatchCSS(path : List[String], suffix : String, locale : String) : Box[LiftResponse] = {
    val locator = ResourceLocator(path, "css", List(Scope.global))
    Website.instance.resourceCache(locator, locale) match {
      case Some(resource) =>
        val bytes = Website.instance.contentCache(resource, 
            CSSParser(Website.instance.context).fixCSS(resource.readString) match {
              case Full(content) => content.getBytes("UTF-8")
              case _ => new Array[Byte](0)
            })
        val headers = List(
          "Cache-Control" -> "public, max-age=3600", 
          "Pragma" -> "public", 
          "Content-Length" -> bytes.length.toString,
          "Content-Type" -> "text/css") 
        Full(InMemoryResponse(bytes, headers, Nil, 200))
      case None => Empty
    }
  }
  
  private def dispatchLarge(path : List[String], suffix : String, locale : String, mimeType : String) : Box[LiftResponse] = {
    val locator = ResourceLocator(path, suffix, List(Scope.global))
    Website.instance.resourceCache(locator, locale) match {
      case Some(resource) => {
        val is = resource.readStream
        val headers = List(
          "Cache-Control" -> "public, max-age=3600", 
          "Pragma" -> "public",
          "Content-Type" -> mimeType) 
        Full(StreamingResponse(is,() => is.close, -1, headers, Nil, 200))
      }
      case None => Empty
      }
  }
}
