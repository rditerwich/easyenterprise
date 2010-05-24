package claro.cms

import java.util.Locale
import net.liftweb.http.{Req,LiftRules,LiftResponse,CSSResponse,StreamingResponse,InMemoryResponse}
import net.liftweb.util.{CSSParser,Box,Full,Empty,Log}
import claro.common.util.Conversions._

object Dispatch extends LiftRules.DispatchPF {

  val emptyResponse : () => Box[LiftResponse] = () => Empty
  
  def isDefinedAt(req : Req): Boolean = {
    val request = Request.get
    request.website match {
      case null => false
      case website => dispatch(request) != emptyResponse
    }
  }
  def apply(req : Req) : () => Box[LiftResponse] = dispatch(Request.get)

  def dispatch(request : Request) : () => Box[LiftResponse] = {
    
  	request.suffix match {
  	  case "css" => () => dispatchCSS(request)
  	  case "js" => request.path match {
        case "classpath" :: rest => emptyResponse
        case "ajax_request" :: rest => emptyResponse
        case _ => () => dispatchResource(request, "text/javascript")
      }
  	  case "png" => () => dispatchResource(request, "image/png")
  	  case "jpg" => () => dispatchResource(request, "image/jpg")
  	  case "gif" => () => dispatchResource(request, "image/gif")
  	  case "pdf" => () => dispatchLarge(request, "application/pdf")
      case _ => emptyResponse
  	}
  }
  
  private def dispatchResource(request : Request, mimeType : String) : Box[LiftResponse] = {
    val locale = Locale.getDefault
    val locator = ResourceLocator(request.path, request.suffix, List(Scope.global))
    request.website.resourceCache(locator, locale) match {
      case Some(resource) => 
        val bytes = request.website.contentCache(resource)
        val headers = List(
            "Cache-Control" -> "public, max-age=3600", 
            "Pragma" -> "public", 
            "Content-Length" -> bytes.length.toString,
            "Content-Type" -> mimeType) 
        Full(InMemoryResponse(bytes, headers, Nil, 200))
      case None => Empty
      }
  }
  
  private def dispatchCSS(request : Request) : Box[LiftResponse] = {
    val locator = ResourceLocator(request.path, "css", List(Scope.global))
    request.website.resourceCache(locator, request.locale) match {
      case Some(resource) =>
        val bytes = request.website.contentCache(resource, 
            CSSParser(request.website.contextPath).fixCSS(resource.readString) match {
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
  
  private def dispatchLarge(request : Request, mimeType : String) : Box[LiftResponse] = {
    val locator = ResourceLocator(request.path, request.suffix, List(Scope.global))
    request.website.resourceCache(locator, request.locale) match {
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
