package claro.cms

import java.util.Locale
import javax.servlet.http.HttpServletRequest
import xml.NodeSeq
import net.liftweb.http.{Req,LiftRules,LiftSession,LiftResponse,RulesSeq,RequestVar,S,InMemoryResponse,OkResponse,XhtmlResponse,CSSResponse,NotFoundResponse}
import net.liftweb.util.{CSSParser,Box,Full,Empty}

object Dispatch {
  
  def calculateContextPath(httpRequest : HttpServletRequest) : Box[String] = {
    Site.findSite(httpRequest.getServerName, httpRequest.getServletPath) match {
      case Some(site) => 
        val request = Request.is
        request.httpRequest = httpRequest 
        request.site = site
        Full(site.contextPath)
      case None => Empty
    }
  }
  
  def unapply(req : Req) : Option[() => Box[LiftResponse]] = {
    Request.site match {
      case null => None 
      case site => dispatch(site, req)
    }
  }
  
  def unapply(path : List[String]) : Option[() => Box[NodeSeq]] = {
    Request.site match {
      case null => None 
      case site => viewDispatch(site, path)
    }
  }
  
  private def dispatch(site : Site, req : Req) : Option[() => Box[LiftResponse]] = {
	req.path.suffix match {
	  case "css" => Some(() => dispatchCSS(site, req))
      case _ => None
	}
  }
  
  private def dispatchCSS(site : Site, req : Req) : Box[LiftResponse] = {
    val path = req.path.partPath.drop(site.path.size)
    val locale = Locale.getDefault
    site.resourceCache(ResourceLocator(path.mkString("/"), "css", List(Scope.global)), locale) match {
      case Some(resource) => 
        Full(CSSResponse(site.contentCache(resource, 
          CSSParser(site.contextPath).fixCSS(resource.readString) match {
            case Full(content) => content
            case _ => ""
        })))
      case None => Empty
    }
  }
  
  private def viewDispatch(site : Site, rawPath : List[String]) : Option[() => Box[NodeSeq]] = {
    val path = rawPath.drop(site.path.size) match {
	  case Nil => "index" :: Nil
      case path => path
    }
    val locale = Locale.getDefault
    findTemplate(site.templateCache, locale, path.head, Nil, path.tail) match {
//    case Some((template, path, pars)) => Some(Request(req.request, site, template.resource, Some(template), path, pars))
      case Some((template, path, pars)) => 
        val request = Request.is
        request.template = Some(template)
        request.path = path
        request.pathTail = pars
        Some(() => render(site, template))
	  case None => None
	}
  }
  
  private def render(site : Site, template : ConcreteTemplate) : Box[NodeSeq] = {
    Request.site.rootBinding.bind(template.xml) match {
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
