package claro.cms

import java.util.Locale
import net.liftweb.http.{Req,LiftRules,LiftResponse,CSSResponse,StreamingResponse}
import net.liftweb.util.{CSSParser,Box,Full,Empty,Log}

object Dispatch extends LiftRules.DispatchPF {

  val emptyResponse : () => Box[LiftResponse] = () => Empty
  
  def isDefinedAt(req : Req): Boolean = dispatch(Request.website, req) != emptyResponse
  def apply(req : Req) : () => Box[LiftResponse] = dispatch(Request.website, req) 

  def dispatch(website : Website, req : Req) : () => Box[LiftResponse] = {
  	req.path.suffix match {
  	  case "css" => () => dispatchCSS(website, req)
  	  case "png" => () => dispatchImage(website, req, "image/png")
  	  case "jpg" => () => dispatchImage(website, req, "image/jpg")
  	  case "gif" => () => dispatchImage(website, req, "image/gif")
      case _ => emptyResponse
  	}
  }
  
  private def dispatchCSS(website : Website, req : Req) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(website.path.size)
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), "css", List(Scope.global)), locale) match {
      case Some(resource) => 
        Full(CSSResponse(
          website.contentCache(resource, 
            CSSParser(website.contextPath).fixCSS(resource.readString) match {
              case Full(content) => content
              case _ => ""
            })))
      case None => Empty
    }
  }
  
  private def dispatchImage(website : Website, req : Req, mimeType : String) : Box[LiftResponse] = {
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
