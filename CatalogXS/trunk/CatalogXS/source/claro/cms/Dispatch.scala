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
      case website => dispatch(website, request.contextPath, req) != emptyResponse
    }
  }
  def apply(req : Req) : () => Box[LiftResponse] = {
    val request = Request.get
    dispatch(request.website, request.contextPath, req)
  }

  def dispatch(website : Website, contextPath : String, req : Req) : () => Box[LiftResponse] = {
    
    def path = req.request.getServletPath.beforeLast('.').drop(contextPath.size)
    
  	req.request.getServletPath.afterLast('.') match {
  	  case "css" => () => dispatchCSS(website, path, req)
  	  case "js" => req.section match {
        case "classpath" => emptyResponse
        case "ajax_request" => emptyResponse
        case _ => () => dispatchResource(website, path, "js", req, "text/javascript")
      }
  	  case "png" => () => dispatchResource(website, path, "png", req, "image/png")
  	  case "jpg" => () => dispatchResource(website, path, "jpg", req, "image/jpg")
  	  case "gif" => () => dispatchResource(website, path, "gif", req, "image/gif")
  	  case "pdf" => () => dispatchLarge(website, path, "pdf", req, "application/pdf")
      case _ => emptyResponse
  	}
  }
  
  private def dispatchResource(website : Website, path : String, suffix: String, req : Req, mimeType : String) : Box[LiftResponse] = {
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path, suffix, List(Scope.global)), locale) match {
      case Some(resource) => 
        val bytes = website.contentCache(resource)
        val headers = List(
            "Cache-Control" -> "public, max-age=3600", 
            "Pragma" -> "public", 
            "Content-Length" -> bytes.length.toString,
            "Content-Type" -> mimeType) 
        Full(InMemoryResponse(bytes, headers, Nil, 200))
      case None => Empty
      }
  }
  
  private def dispatchCSS(website : Website, path : String, req : Req) : Box[LiftResponse] = {
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path, "css", List(Scope.global)), locale) match {
      case Some(resource) =>
        val bytes = website.contentCache(resource, 
            CSSParser(website.contextPath).fixCSS(resource.readString) match {
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
  
  private def dispatchLarge(website : Website, path : String, suffix : String, req : Req, mimeType : String) : Box[LiftResponse] = {
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), suffix, List(Scope.global)), locale) match {
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
