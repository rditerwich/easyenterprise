package claro.cms

import java.util.Locale
import net.liftweb.http.{Req,LiftRules,LiftResponse,CSSResponse,StreamingResponse,InMemoryResponse}
import net.liftweb.util.{CSSParser,Box,Full,Empty,Log}

object Dispatch extends LiftRules.DispatchPF {

  val emptyResponse : () => Box[LiftResponse] = () => Empty
  
  def isDefinedAt(req : Req): Boolean = {
    Request.website match {
      case null => false
      case website => dispatch(website, req) != emptyResponse
    }
  }
  def apply(req : Req) : () => Box[LiftResponse] = dispatch(Request.website, req) 

  def dispatch(website : Website, req : Req) : () => Box[LiftResponse] = {
  	req.path.suffix match {
  	  case "css" => () => dispatchCSS(website, req)
  	  case "js" => req.section match {
        case "classpath" => emptyResponse
        case "ajax_request" => emptyResponse
        case _ => () => dispatchResource(website, req, "text/javascript")
      }
  	  case "png" => () => dispatchResource(website, req, "image/png")
  	  case "jpg" => () => dispatchResource(website, req, "image/jpg")
  	  case "gif" => () => dispatchResource(website, req, "image/gif")
  	  case "pdf" => () => dispatchLarge(website, req, "application/pdf")
      case _ => emptyResponse
  	}
  }
  
  private def dispatchResource(website : Website, req : Req, mimeType : String) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(website.path.size)
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), req.path.suffix, List(Scope.global)), locale) match {
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
  
  private def dispatchCSS(website : Website, req : Req) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(website.path.size)
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), "css", List(Scope.global)), locale) match {
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
  
  private def dispatchLarge(website : Website, req : Req, mimeType : String) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(website.path.size)
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), req.path.suffix, List(Scope.global)), locale) match {
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
