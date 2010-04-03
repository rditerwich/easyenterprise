package claro.cms

import java.util.Locale
import net.liftweb.http.{RequestVar}
import javax.servlet.http.HttpServletRequest

object Request extends RequestVar[Request](null) {
  
  def process(httpRequest : HttpServletRequest) : Option[Request] = {
    val request = parseRequest(httpRequest)
    this(request getOrElse null)
    request
  }
  
  def site = get.site
  def pathTail = get.pathTail
  
  private def parseRequest(request : HttpServletRequest) : Option[Request] = {
    Site.findSite(request.getServerName, parsePath(request.getServletPath)) match {
      case Some((site, p)) => 
	    val path = p match {
	      case Nil => "index" :: Nil
	      case path => path
	    }
        val locale = Locale.getDefault
        findTemplate2(site.templateStore, locale, "", Nil, path) match {
          case Some((template, path, pars)) => Some(Request(request, site, template, path, pars))
          case None => None
        }
      case None => None
    }
  }

  private def parsePath(s : String) : List[String] = {
    var result = List[String]();
    var end = s.size
    while (end >= 0) {
      val index = s.lastIndexOf('/', end - 1)
      val start = if (index < 0) 0 else index + 1
      if (end > start) {
        result = s.substring(start, end) :: result
      }
      end = index
    }
    result
  }
  
  private def findTemplate(templates : TemplateStore, locale : Locale, path : List[String], pars : List[String]) : Option[(ConcreteTemplate, List[String], List[String])] = {
    if (path != Nil) {
      templates(Template(path.mkString("/")), locale) match {
	    case Some(template) => Some(template, path, pars)
	    case None => findTemplate(templates, locale, path.dropRight(1), path.head :: pars)
      }
	 } else None
  }

  private def findTemplate2(templates : TemplateStore, locale : Locale, name : String, path : List[String], pars : List[String]) : Option[(ConcreteTemplate, List[String], List[String])] = {
    val result = pars match {
      case head :: tail => findTemplate2(templates, locale, name + "/" + head, head :: path, tail)
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

case class Request(request : HttpServletRequest, site : Site, template: ConcreteTemplate, path : List[String], pathTail : List[String]) {
}

