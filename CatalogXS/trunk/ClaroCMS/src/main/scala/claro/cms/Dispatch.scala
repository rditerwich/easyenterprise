package claro.cms

import java.util.Locale
import net.liftweb.http.{Req,LiftRules,LiftResponse,CSSResponse,StreamingResponse,InMemoryResponse,JavaScriptResponse,NotFoundResponse,ResourceServer}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.{CSSParser,Log}
import net.liftweb.util.TimeHelpers._
import claro.common.util.Conversions._

object Dispatch extends LiftRules.DispatchPF {

  val response = new ThreadLocal[() => Box[LiftResponse]]
  
  val emptyResponse : () => Box[LiftResponse] = () => Empty
  
  def isDefinedAt(req : Req): Boolean = {
    dispatch(req) match {
      case Some(response) =>
        this.response.set(response)
        true
      case _ => false
    }
  }
  
  def apply(req : Req) : () => Box[LiftResponse] = this.response.get() 
    
  
  private def fixSuffix(path : List[String], suffix : String) : (List[String], String) = {
    if (suffix == "" && !path.isEmpty) {
      val r = path.reverse
      val s = r(0)
      val i = s.lastIndexOf('.')
      if (i > 0) {
        return ((s.substring(0, i) :: r.tail).reverse, s.substring(i + 1))
      }
    }
    return (path, suffix)
  }
  
  private def dispatch(req : Req) : Option[() => Box[LiftResponse]] = {
    
    val (path, suffix) = fixSuffix(req.path.partPath, req.path.suffix)
    
    // too bad, locales are not available, since urlDecorate does not append locale string to images, css etc
    val locale = Cms.locale.is

    suffix match {
  	  case "css" => Some(() => dispatchCSS(path, suffix, locale))
  	  case "js" => path match {
        case "classpath" :: rest => Some(() => dispatchClasspathResource(path, suffix, locale, "text/javascript"))
        case "ajax_request" :: "liftAjax" :: Nil => Some(() => dispatchLiftAjax(path, suffix, locale))
        case _ => Some(() => dispatchResource(path, suffix, locale, "text/javascript"))
      }
  	  case "png" => Some(() => dispatchResource(path, suffix, locale, "image/png"))
  	  case "jpg" => Some(() => dispatchResource(path, suffix, locale, "image/jpg"))
  	  case "gif" => Some(() => dispatchResource(path, suffix, locale, "image/gif"))
  	  case "pdf" => Some(() => dispatchLarge(path, suffix, locale, "application/pdf"))
      case _ => None
  	}
  }

  private def dispatchLiftAjax(path : List[String], suffix : String, locale : Locale) : Box[LiftResponse] = {
    val script = net.liftweb.http.js.ScriptRenderer.ajaxScript
    val modTime = System.currentTimeMillis
    Full(JavaScriptResponse(script,
                            List("Last-Modified" -> toInternetDate(modTime),
                            "Expires" -> toInternetDate(modTime + 60.minutes)),
                            Nil, 200))
  }
  
  private def dispatchClasspathResource(path : List[String], suffix : String, locale : Locale, mimeType : String) : Box[LiftResponse] = {
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
  
  private def dispatchResource(path : List[String], suffix : String, locale : Locale, mimeType : String) : Box[LiftResponse] = {
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
  
  private def dispatchCSS(path : List[String], suffix : String, locale : Locale) : Box[LiftResponse] = {
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
  
  private def dispatchLarge(path : List[String], suffix : String, locale : Locale, mimeType : String) : Box[LiftResponse] = {
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
