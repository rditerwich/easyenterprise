package claro.cms

import java.util.Locale
import javax.servlet.http.HttpServletRequest
import xml.NodeSeq
import net.liftweb.http.{Req,LiftRules,LiftSession,LiftResponse,RulesSeq,RequestVar,S,InMemoryResponse,OkResponse,XhtmlResponse,CSSResponse,NotFoundResponse,StreamingResponse}
import net.liftweb.util.{CSSParser,Box,Full,Empty}

object Dispatch {
  
  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
    Webwebsite.findWebwebsite(httpRequest.getServerName, httpRequest.getServletPath) match {
      case Some(website) => 
        val request = Request.is
        request.httpRequest = httpRequest 
        request.website = website
        Full(website.contextPath)
      case None => Empty
    }
  }
  
  def unapply(req : Req) : Option[() => Box[LiftResponse]] = {
    Request.website match {
      case null => None 
      case website => dispatch(website, req)
    }
  }
  
  def unapply(path : List[String]) : Option[() => Box[NodeSeq]] = {
    Request.website match {
      case null => None 
      case website => viewDispatch(website, path)
    }
  }
  
  private def dispatch(website : Webwebsite, req : Req) : Option[() => Box[LiftResponse]] = {
  	req.path.suffix match {
  	  case "css" => Some(() => dispatchCSS(website, req))
  	  case "png" => Some(() => dispatchImage(website, req, "image/png"))
  	  case "jpg" => Some(() => dispatchImage(website, req, "image/jpg"))
  	  case "gif" => Some(() => dispatchImage(website, req, "image/gif"))
      case _ => None
  	}
  }
  
  private def dispatchCSS(website : Webwebsite, req : Req) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(website.path.size)
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), "css", List(Scope.global)), locale) match {
      case Some(resource) => 
        val content = resource.readString
        val fixedContent = CSSParser(website.contextPath).fixCSS(content)
        fixedContent match {
            case Full(content) => Full(CSSResponse(website.contentCache(resource, content)))
            case _ => Empty
        }
      case None => Empty
    }
  }
  
  private def dispatchImage(website : Webwebsite, req : Req, mimeType : String) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(website.path.size)
    val locale = Locale.getDefault
    website.resourceCache(ResourceLocator(path.mkString("/"), req.path.suffix, List(Scope.global)), locale) match {
      case Some(resource) => {
        val is = resource.readStream
        Full(StreamingResponse(is,() => is.close, -1, ("Content-Type", mimeType) :: Nil, Nil, 200))
      }
      case None => Empty
      }
  }
  
  private def viewDispatch(website : Webwebsite, rawPath : List[String]) : Option[() => Box[NodeSeq]] = {
    val path = rawPath.drop(website.path.size) match {
	  case Nil => "index" :: Nil
      case path => path
    }
    val locale = Locale.getDefault
    findTemplate(website.templateCache, locale, path.head, Nil, path.tail) match {
//    case Some((template, path, pars)) => Some(Request(req.request, website, template.resource, Some(template), path, pars))
      case Some((template, path, pars)) => 
        val request = Request.is
        request.template = Some(template)
        request.path = path
        request.pathTail = pars
        Some(() => render(website, template))
	  case None => None
	}
  }
  
  private def render(website : Webwebsite, template : ConcreteTemplate) : Box[NodeSeq] = {
    Request.website.rootBinding.bind(template.xml) match {
	  case xml if (xml.first.label == "html") => Full(xml)
	  case _ => Empty
    }
  }
  
  private def findTemplate(templates : TemplateCache, locale : Locale, name : String, path : List[String], pars : List[String]) : Option[(ConcreteTemplate, List[String], List[String])] = {
    val result = pars match {
      case head :: tail => findTemplate(templates, locale, name + "/" + head, head :: path, tail)
      case _ => None
    }
    result match {
      case None => 
        templates(Template(name), locale) match {
  	      case Some(template) => Some(template, path, pars)
	      case None => println("Template not found: " + name);None
        }
      case _ => result
    }
  }
}
